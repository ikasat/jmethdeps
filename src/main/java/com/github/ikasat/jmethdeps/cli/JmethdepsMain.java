package com.github.ikasat.jmethdeps.cli;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.github.ikasat.jmethdeps.Jmethdeps;
import com.github.ikasat.jmethdeps.Jmethdeps.OutputFormat;

public class JmethdepsMain {
    private static Options defineOptions() {
        Options options = new Options();
        options.addOption(Option.builder("d").longOpt("allow-dup").build());
        options.addOption(Option.builder("j").longOpt("json").build());
        options.addOption(Option.builder("h").longOpt("help").build());
        options.addOption(Option.builder("V").longOpt("version").build());
        return options;
    }

    public static void main(String[] args) throws Exception {
        DefaultParser parser = new DefaultParser();
        Options options = defineOptions();
        CommandLine cl = parser.parse(options, args);
        if (cl.hasOption("help")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("jmethdeps [-d] [-j] JAR...", options);
            return;
        } else if (cl.hasOption("version")) {
            System.out.println(Jmethdeps.VERSION);
            return;
        }

        Jmethdeps jmethdeps = new Jmethdeps();
        jmethdeps.setDuplicationAllowed(cl.hasOption("allow-dup"));
        jmethdeps.setOutputFormat(cl.hasOption("json") ? OutputFormat.JSON : OutputFormat.TEXT);
        List<File> jarFiles = cl.getArgList().stream()
                .map(s -> Paths.get(s).toFile())
                .collect(Collectors.toList());
        jmethdeps.run(jarFiles);
    }
}
