# The umbj library

The `umbj` Java library provides support for reading and writing files in the [UMB](https://pmc-tools.github.io/umb) (Unified Markov Binary) format, which is an efficient, extensible format for storage and communication of probabilistic models.

It is used within the [PRISM](http://prismmodelchecker.org/) model checker to provide support for import and export of models in UMB, but is provided as standalone library, for use by other Java-based implementations.

To use, you need `umbj.jar` as well as the following dependencies:

* Apache Commons Compress
* fastutil
* Gson
* XZ for Java

To compile from source, use

```bash
gradle build
```

To also extract the required JAR files for dependencies:

``` bash
gradle packageLibrary
```

To build everything into a single JAR, use:

``` bash
gradle fatJar
```
