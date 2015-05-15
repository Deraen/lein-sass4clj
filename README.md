# lein-less4j
[![Clojars Project](http://clojars.org/deraen/lein-less4j/latest-version.svg)](http://clojars.org/deraen/lein-less4j)

Leiningen task to compile Less.

* Provides the `less4j` task
* For each `.main.less` in source-dirs creates equivalent `.css` file.
* Uses [Less4j](https://github.com/SomMeri/less4j) Java implementation of Less compiler

## Usage

```clj
:less {:source-map true
       :compression true}
```

## Features

- Load imports from classpath
  - Loading order. `@import "{name}";` at `{path}`.
    1. check if file `{path}/{name}.less` exists
    2. try `(io/resource "{name}.less")`
    3. try `(io/resource "{path}/{name}.less")`
    4. check if webjars asset map contains `{name}`
      - Resource `META-INF/resources/webjars/{package}/{version}/{path}` can be referred using `{package}/{path}`
      - E.g. `bootstrap/less/bootstrap.less` => `META-INF/resources/webjars/bootstrap/3.3.1/less/bootstrap.less`
  - You should be able to depend on `[org.webjars/bootstrap "3.3.1"]`
    and use `@import "bootstrap/less/bootstrap";`

## FAQ

### Log configuration

If you are using some logging stuff it might be that library used by
less4j will write lots of stuff to your log, then you should add the following
rule to your `logback.xml`:

```xml
  <logger name="org.apache.commons.beanutils.converters" level="INFO"/>
```

## License

Copyright © 2015 Juho Teperi

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
