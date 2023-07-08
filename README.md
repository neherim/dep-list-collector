Dep List Collector
==
Small app that lists all transitive dependencies for for an artifact from the maven central.

This can be useful if your company is using a private self-hosted maven repository instead of maven central.
And all artifacts must be manually uploaded to this repository by the Ops team after legal or security review.
By passing the list of libraries you need from the maven central to the Dep List Collector input,
you will receive a list of all `.jar` and `.pom` files that you need to upload to the company's repository.

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
usage: java -jar dep-list-collector.jar [-e <PRIVATE_MAVEN_REPO_URL>] -i <INPUT_FILE> [-o <OUTPUT_FILE>]
 -e,--exclude <PRIVATE_MAVEN_REPO_URL>   Exclude artifacts from the output list if they exists in the private maven repository
 -i,--input <INPUT_FILE>                 Path to the input file with an artifact list
 -o,--output <OUTPUT_FILE>               Path to the output file, default: output.txt
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
   Or, if you want to exclude from the output all dependencies that already exist in your private repository, you can
   run:
    ```shell
    java -jar dep-list-collector.jar -i input.txt -e https://nexus.my-company.org/maven/
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
