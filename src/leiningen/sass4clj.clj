(ns leiningen.sass4clj
  (:require
    [leiningen.help]
    [leiningen.core.eval :as leval]
    [leiningen.core.project :as project]
    [clojure.java.io :as io]))

(defn main-file? [file]
  (and (.endsWith (.getName file) ".scss")
       (not (.startsWith (.getName file) "_"))))

(defn find-main-files [source-paths]
  (->> source-paths
       (map (fn [source-path]
              (let [file (io/file source-path)]
                (->> (file-seq file)
                     (filter main-file?)
                     (map (fn [x] [(.getPath x) (.toString (.relativize (.toURI file) (.toURI x)))]))))))
       (apply concat)))

(def sass4j-profile {:dependencies '[[deraen/sass4clj "0.1.0-SNAPSHOT"]
                                     [watchtower "0.1.1"]]})

; From lein-cljsbuild
(defn- eval-in-project [project form requires]
  (leval/eval-in-project
    project
    ; Without an explicit exit, the in-project subprocess seems to just hang for
    ; around 30 seconds before exiting.  I don't fully understand why...
    `(try
       (do
         ~form
         (System/exit 0))
       (catch Exception e#
         (do
           (.printStackTrace e#)
           (System/exit 1))))
    requires))

(defn- run-compiler
  "Run the sasscss compiler."
  [project
   {:keys [source-paths target-path]}
   watch?]
  (let [project' (project/merge-profiles project [sass4j-profile])
        main-files (vec (find-main-files source-paths))]
    (eval-in-project
      project'
      `(let [f# (fn compile-sass [& ~'_]
                  (doseq [[path# relative-path#] ~main-files]
                    (println (format "Compiling {sass}... %s" relative-path#))
                    (sass4clj.core/sass-compile-to-file
                      path#
                      ~(.getPath (io/file target-path))
                      relative-path#
                      {:source-paths ~source-paths})))]
         (if ~watch?
           @(watchtower.core/watcher
             ~source-paths
             (watchtower.core/rate 100)
             (watchtower.core/file-filter watchtower.core/ignore-dotfiles)
             (watchtower.core/file-filter (watchtower.core/extensions :scss))
             (watchtower.core/on-change f#))
           (f#)))
      '(require 'sass4clj.core 'watchtower.core))))

(defn- once
  "Compile sass files once."
  [project config]
  (run-compiler project config false))

(defn- auto
  "Compile sass files, then watch for changes and recompile until interrupted."
  [project config]
  (run-compiler project config true))

(defn sass4clj
  "Run the {sass} css compiler plugin."
  {:help-arglists '([once auto])
   :subtasks      [#'once #'auto]}
  ([project]
   (println (leiningen.help/help-for "sass4j"))
   (leiningen.core.main/abort))
  ([project subtask & args]
   (let [config (:sass4clj project)]
     (case subtask
       "once" (apply once project config args)
       "auto" (apply auto project config args)))))
