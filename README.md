# lein-sass4clj
[![Clojars Project](http://clojars.org/deraen/lein-sass4clj/latest-version.svg)](http://clojars.org/deraen/lein-less4clj)

Leiningen task to compile Less.

* Provides the `sass4clj` task
* For each `.main.sass` in source-dirs creates equivalent `.css` file.
* Uses jsass

## Usage

```clj
:sass4clj {:source-paths ["src/scss"]
           :target-path "target/generated/public/css"}
```

## Features

- Load imports from classpath
  - Loading order. `@import "{name}";` at `{path}`.
    1. check if file `{path}/{name}.scss` exists
    2. try `(io/resource "{name}.scss")`
    3. try `(io/resource "{path}/{name}.scss")`
    4. check if webjars asset map contains `{name}`
      - Resource `META-INF/resources/webjars/{package}/{version}/{path}` can be referred using `{package}/{path}`
      - E.g. `bootstrap/scss/bootstrap.scss` => `META-INF/resources/webjars/bootstrap/4.0.0-alpha/scss/bootstrap.scss`
  - You should be able to depend on `[org.webjars.bower/bootstrap "4.0.0-alpha"]`
    and use `@import "bootstrap/scss/bootstrap";`

## License

Copyright © 2015 Juho Teperi

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
