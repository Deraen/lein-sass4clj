# lein-less4j

A Leiningen plugin to do many wonderful things.

## Usage

FIXME: Use this for user-level plugins:

Put `[lein-less4j "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-less4j 0.1.0-SNAPSHOT`.

FIXME: Use this for project-level plugins:

Put `[lein-less4j "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

FIXME: and add an example usage that actually makes sense:

    $ lein less4j

## FAQ

### Log configuration

If you are using some logging stuff it might be that library used by
less4j will write lots of stuff to your log, then you should add the following
rule to your `logback.xml`:

```xml
  <logger name="org.apache.commons.beanutils.converters" level="INFO"/>
```

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
