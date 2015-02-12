(ns leiningen.less4j
  (:require
    [leiningen.help]
    [leiningen.core.eval :as leval]
    [leiningen.core.project :as project]
    [clojure.java.io :as io]))

(defn main-file? [file]
  (.endsWith (.getName file) ".main.less"))

(defn find-main-files [source-paths]
  (->> source-paths
       (map (fn [source-path]
              (let [file (io/file source-path)]
                (->> (file-seq file)
                     (filter main-file?)
                     (map (fn [x] [(.getPath x) (.toString (.relativize (.toURI file) (.toURI x)))]))))))
       (apply concat)))

(def less4j-profile {:dependencies '[[deraen/less4clj "0.1.2-SNAPSHOT"]
                                     [watchtower "0.1.1"]]})

(defn- run-compiler
  "Run the lesscss compiler."
  [project
   {:keys [source-paths target-path source-map compression]
    :or {source-map false
         compression false}}
   watch?]
  (let [project' (project/merge-profiles project [less4j-profile])]
    (doseq [[path relative-path] (find-main-files source-paths)]
      (leval/eval-in-project
        project'
        `(let [~'f (fn [& ~'_]
                     (println (format "Compiling {less}... %s" ~relative-path))
                     (less4clj.core/less-compile
                       ~path
                       ~(.getPath (io/file target-path))
                       ~relative-path
                       {:source-map ~source-map
                        :compression ~compression
                        :source-paths ~source-paths}))]
           (if ~watch?
             (watchtower.core/watcher ~source-paths
               (watchtower.core/rate 100)
               (watchtower.core/file-filter watchtower.core/ignore-dotfiles)
               (watchtower.core/file-filter (watchtower.core/extensions :less))
               (watchtower.core/on-change ~'f))
             (~'f)))
        '(require 'less4clj.core 'watchtower.core)))))

(defn- once
  "Compile less files once."
  [project config]
  (run-compiler project config false))

(defn- auto
  "Compile less files, then watch for changes and recompile until interrupted."
  [project config]
  (run-compiler project config true))

(defn less4j
  "Run the {less} css compiler plugin."
  {:help-arglists '([once auto])
   :subtasks      [#'once #'auto]}
  ([project]
   (println (leiningen.help/help-for "less4j"))
   (leiningen.core.main/abort))
  ([project subtask & args]
   (let [config (:less project)]
     (case subtask
       "once" (apply once project config args)
       "auto" (apply auto project config args)))))
