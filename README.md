Dep List Collector
==
A small and simple jvm tool that lists all transitive dependencies for maven artifact.

This can be useful if your company is using a private self-hosted maven repository instead of maven central.
And all artifacts must be manually uploaded to this repository by the Ops team after legal or security review.
The Dep List Collector can help you collect a list of all the `.jar` and `.pom` files you need to upload to company's
repository to build your application.

Dep List Collector uses the [jcabi-aether](https://github.com/jcabi/jcabi-aether) library to resolve all artifact
dependencies.

## Building

To build the source you need to install JDK 11+ and then run the maven package command from the project's root
directory:

```shell
./mvnw package
```

The artifact `dep-list-collector` will appear in `target` folder.

## Usage

Create an input file listing all your artifacts in the format: `<groupId>:<artifactId>:<version>`,
then pass this file to dep-list-collector.jar with the command:

```shell
java -jar dep-list-collector.jar -i <INPUT_FILE> [-o <OUTPUT_FILE>]
 -i,--input <INPUT_FILE>     Path to input file with artifact list
 -o,--output <OUTPUT_FILE>   Path to output file, default: output.txt
```

## Example

For example, you want to list all transitive dependencies for artifacts: `spring-boot-starter-web`
and `spring-boot-starter-jdbc`

1) Make input.txt file:
    ```text
    org.springframework.boot:spring-boot-starter-web:2.7.5
    org.springframework.boot:spring-boot-starter-jdbc:2.7.5
    ```

2) Run dep-list-collector:
    ```shell
    java -jar dep-list-collector.jar -i input.txt
    ```

3) After some time it will create output.txt with all dependencies in the current folder:
    ```text
    ...
    https://repo1.maven.org/maven2/com/sun/xml/bind/mvn/jaxb-runtime-parent/2.3.6/, #2022-01-27
    https://repo1.maven.org/maven2/org/ow2/asm/asm-analysis/7.1/, #2019-03-03
    https://repo1.maven.org/maven2/org/glassfish/jersey/ext/jersey-bean-validation/2.35/, #2021-09-03
    https://repo1.maven.org/maven2/org/jboss/jboss-parent/12/, #2013-11-22
    ...
    ```
