package com.github.neherim;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static final String LOCAL_REPOSITORY_FOLDER = "local-repository";
    public static final String MAVEN_BASE_URL = "https://repo1.maven.org/maven2/";

    public static void main(String[] args) throws IOException {
        var cliArgs = parseArgs(args);
        var collector = new DepListCollector(new LocalRepository(LOCAL_REPOSITORY_FOLDER), MAVEN_BASE_URL);
        var formatter = new ArtifactOutputFormatter(MAVEN_BASE_URL);
        var filter = new ArtifactFilter(cliArgs.privateMavenRepoUrl);

        var artifacts = readArtifactsFromInputFile(cliArgs.inputFile);

        System.out.println("Trying to resolve dependencies for " + artifacts.size() + " artifacts");
        var formattedArtifacts = collector.collectTransitiveDependencies(artifacts).stream()
                .filter(filter::include)
                .map(formatter::format)
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Save output to " + cliArgs.outputFile);
        saveArtifactsToOutputFile(cliArgs.outputFile, formattedArtifacts);
    }

    private static void saveArtifactsToOutputFile(File outputFile, List<String> formattedArtifacts) throws IOException {
        FileUtils.writeLines(outputFile, formattedArtifacts);
    }

    private static Set<Artifact> readArtifactsFromInputFile(File inputFile) throws IOException {
        return FileUtils.readLines(inputFile).stream()
                .map(Artifact::fromString)
                .collect(Collectors.toSet());
    }

    private static CliArgs parseArgs(String[] args) {
        var options = new Options()
                .addOption(Option.builder("i")
                        .longOpt("input")
                        .hasArg()
                        .argName("INPUT_FILE")
                        .required()
                        .desc("Path to the input file with an artifact list")
                        .build())
                .addOption(Option.builder("o")
                        .longOpt("output")
                        .hasArg()
                        .argName("OUTPUT_FILE")
                        .required(false)
                        .desc("Path to the output file, default: output.txt")
                        .build())
                .addOption(Option.builder("e")
                        .longOpt("exclude")
                        .hasArg()
                        .argName("PRIVATE_MAVEN_REPO_URL")
                        .required(false)
                        .desc("Exclude artifacts from the output list if they exists in the private maven repository")
                        .build());
        var parser = new DefaultParser();
        try {
            var cmd = parser.parse(options, args);
            var input = cmd.getOptionValue("i");
            var output = cmd.hasOption("o") ? cmd.getOptionValue("o") : "output.txt";
            var privateMavenRepoUrl = cmd.getOptionValue("e");
            return new CliArgs(input, output, privateMavenRepoUrl);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);
            formatter.printHelp("java -jar dep-list-collector.jar", options, true);
            System.exit(1);
            return null;
        }
    }

    private static class CliArgs {
        final File inputFile;
        final File outputFile;
        final String privateMavenRepoUrl;

        public CliArgs(String input, String output, String privateMavenRepoUrl) {
            this.inputFile = new File(input);
            this.outputFile = new File(output);
            this.privateMavenRepoUrl = privateMavenRepoUrl;
        }
    }
}