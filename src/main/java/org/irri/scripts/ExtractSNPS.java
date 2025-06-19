package org.irri.scripts;

import picocli.CommandLine;

public class ExtractSNPS {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new RunGetSNPS()).execute(args);
        System.exit(exitCode);
    }
}
