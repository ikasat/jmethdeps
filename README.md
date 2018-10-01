# jmethdeps

## Install

```sh
git clone --depth 1 https://github.com/ikasat/jmethdeps.git
cd jmethdeps/
./gradlew distTar
tar xvf build/distributions/jmethdeps.tar -C <target directory>
```

## Usage

* `jmethdeps [-d] [-j] JAR...`
    * `-d`, `--allow-dup`: retain the order and the number of invocation
        * otherwise destinations will be uniquified and sorted
    * `-j`, `--json`: print dependencies as JSON
    * `-h`, `--help`: show help message
    * `-V`, `--version`: show version

```
$ jmethdeps jmethdeps.jar
com.github.ikasat.jmethdeps.Jmethdeps.<init> java.lang.Object.<init>
com.github.ikasat.jmethdeps.Jmethdeps.run com.github.ikasat.jmethdeps.Jmethdeps.processJar
com.github.ikasat.jmethdeps.Jmethdeps.run java.util.Iterator.hasNext
com.github.ikasat.jmethdeps.Jmethdeps.run java.util.Iterator.next
com.github.ikasat.jmethdeps.Jmethdeps.run java.util.List.iterator
...
```

```
$ jmethdeps -j jmethdeps.jar | jq .
{
  "com.github.ikasat.jmethdeps.Jmethdeps.<init>": [
    "java.lang.Object.<init>"
  ],
  "com.github.ikasat.jmethdeps.Jmethdeps.run": [
    "com.github.ikasat.jmethdeps.Jmethdeps.processJar",
    "java.util.Iterator.hasNext",
    "java.util.Iterator.next",
    "java.util.List.iterator"
  ],
  ...
}
```
