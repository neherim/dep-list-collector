package com.github.neherim;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {
    public static final String LOCAL_REPOSITORY_FOLDER = "local-repository";

    public static void main(String[] args) throws IOException {
        var cliArgs = parseArgs(args);
        var collector = new DepListCollector(new LocalRepository(LOCAL_REPOSITORY_FOLDER));
        var formatter = new OutputFormatter();

        var artifact = FileUtils.readLines(cliArgs.inputFile);

        var resolved = collector.collect(artifact).stream()
                .map(formatter::format)
                .sorted()
                .collect(Collectors.toList());

        FileUtils.writeLines(cliArgs.outputFile, resolved);
    }

    private static CliArgs parseArgs(String[] args) {
        var options = new Options()
                .addOption(Option.builder("i")
                        .longOpt("input")
                        .hasArg()
                        .argName("INPUT_FILE")
                        .required()
                        .desc("Path to input file with artifact list")
                        .build())
                .addOption(Option.builder("o")
                        .longOpt("output")
                        .hasArg()
                        .argName("OUTPUT_FILE")
                        .required(false)
                        .desc("Path to output file, default: output.txt")
                        .build());
        var parser = new DefaultParser();
        try {
            var cmd = parser.parse(options, args);
            var input = cmd.getOptionValue("i");
            var output = cmd.hasOption("o") ? cmd.getOptionValue("o") : "output.txt";
            return new CliArgs(input, output);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar dep-list-collector.jar", options, true);
            System.exit(1);
            return null;
        }
    }

    private static class CliArgs {
        File inputFile;
        File outputFile;

        public CliArgs(String input, String output) {
            this.inputFile = new File(input);
            this.outputFile = new File(output);
        }
    }
}