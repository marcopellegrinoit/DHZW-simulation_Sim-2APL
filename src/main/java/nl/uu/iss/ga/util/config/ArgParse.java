package main.java.nl.uu.iss.ga.util.config;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ArgParse {
    private static final Logger LOGGER = Logger.getLogger(ArgParse.class.getName());

    @Arg(dest = "configuration")
    private File configuration;

    private ConfigModel configModel;

    @Arg(dest = "threads")
    private int threads;

    private long iterations;
    private LocalDate startdate;

    private String outputDir;

    private int node;

    private String descriptor;

    @Arg(dest = "logproperties")
    private File logproperties;

    @Arg(dest = "parametersetindex")
    private int parameterSetIndex;

    @Arg(dest = "outputPath")
    private String outputPath;

    @Arg(dest = "parametersPath")
    private String parametersPath;

    private Random random;

    public ArgParse(String[] args) {
        ArgumentParser p = getParser();
        try {
            p.parseArgs(args, this);
            verifyLogProperties();
            try {
                this.configuration = findFile(this.configuration);
                processConfigFile(p);
            } catch (IOException e) {
                throw new ArgumentParserException(e.getMessage(), p);
            }
        } catch (ArgumentParserException e) {
            p.handleError(e);
            System.exit(1);
        }
    }

    /**
     * The method read the TOML configuration file and initialise variables
     * @param p
     * @throws ArgumentParserException
     */
    private void processConfigFile(ArgumentParser p) throws ArgumentParserException {
        TomlParseResult result = null;
        try {
            result = Toml.parse(Paths.get(this.configuration.getAbsolutePath()));
        } catch (IOException e) {
            throw new ArgumentParserException(e.getMessage(), p);
        }
        if(result.errors().size() > 0) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Error(s) encountered parsing configuration file:\n");
            result.errors().forEach(x -> {
                errorMessage.append(x.getMessage()).append(" ").append(x.position()).append("\n");
            });
            throw new ArgumentParserException(errorMessage.toString(), p);
        } else {
            try {
                this.startdate = getDateFromTable(result, "simulation.startdate");
                if(result.contains("simulation.iterations")) {
                    LocalDate d = getDateFromTable(result, "simulation.iterations");
                    if(d == null) {
                        this.iterations = result.getLong("simulation.iterations");
                    } else {
                        this.iterations = ChronoUnit.DAYS.between(this.startdate, d) + 1;
                    }
                } else {
                    this.iterations = Long.MAX_VALUE;
                }

                if (result.contains("simulation.seed")) {
                    this.random = new Random(result.getLong("simulation.seed"));
                } else {
                    this.random = new Random();
                }

                this.descriptor = result.getString("output.descriptor");
                this.node = (int) result.getLong("output.node", () -> -1);

                TomlTable table = result.getTable("config");

                this.configModel = new ConfigModel(this, "config", table);

            } catch (Exception e) {
                throw new ArgumentParserException(e.getMessage(), p);
            }
        }
    }

    private LocalDate getDateFromTable(TomlParseResult parseResult, String dottedKey) {
        if(parseResult.contains(dottedKey)) {
            if(parseResult.isLocalDate(dottedKey)) {
                return parseResult.getLocalDate(dottedKey);
            } else if (parseResult.isString(dottedKey)) {
                return LocalDate.parse(parseResult.getString(dottedKey), DateTimeFormatter.ISO_DATE);
            }
        }
        return null;
    }

    public ConfigModel getConfigModel() {
        return this.configModel;
    }

    public Random getSystemWideRandom() {
        return random;
    }

    public int getNode() {
        return node;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public int getThreads() {
        return threads;
    }

    public int getParameterSetIndex() {
        return parameterSetIndex;
    }

    public File getParameterSetFile() {
        return new File(this.parametersPath);
    }

    public File getOutputFile() {
        return new File(this.outputPath);
    }

    public long getIterations() {
        return iterations;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public static File findFile(File f) throws FileNotFoundException {
        File existingFile = null;
        if(f.getAbsoluteFile().exists()) {
            existingFile = f;
        } else if (!f.isAbsolute()) {
            URL r = ArgParse.class.getClassLoader().getResource(f.toString());
            if (r != null) {
                f = new File(r.getFile()).getAbsoluteFile();
                if (f.exists())
                    existingFile = f;
            }
        }
        if(existingFile != null) {
            return existingFile;
        } else {
            throw new FileNotFoundException("File not found: " + f.toString());
        }
    }

    private void verifyLogProperties() {
        if(this.logproperties != null) {
            try {
                this.logproperties = findFile(this.logproperties);
                InputStream stream = new FileInputStream(this.logproperties);
                LogManager.getLogManager().readConfiguration(stream);
                LOGGER.log(Level.INFO, "Properties file for logger loaded");
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Properties file for logger not found. Using defaults");
            }
        }
    }

    /**
     * The method reads the command line arguments and initialise the correspondent variables
     * @return
     */
    private ArgumentParser getParser() {
        ArgumentParser parser = ArgumentParsers.newFor("2APL/SimpleEpiDemic Disease Simulation").build()
                .defaultHelp(true)
                .description("Agent-based simulation of mode choices of a population");

        parser.addArgument("--config")
                .type(File.class)
                .required(true)
                .dest("configuration")
                .help("Specify the TOML configuration file");

        parser.addArgument("--parameterset_index")
                .type(Integer.class)
                .required(true)
                .dest("parametersetindex")
                .help("Specify the index of the parameter set to use from the parameter file");

        parser.addArgument("--output_file")
                .type(String.class)
                .required(true)
                .dest("outputPath")
                .help("Specify the file where a new line will be added with the mode choice distribution");

        parser.addArgument("--parameter_file")
                .type(String.class)
                .required(true)
                .dest("parametersPath")
                .help("specify the file containing the values of the parameters");

        ArgumentGroup optimization = parser.addArgumentGroup("Runtime optimization");

        optimization.addArgument("--threads", "-t")
                .type(Integer.class)
                .required(false)
                .setDefault(8)
                .dest("threads")
                .help("Specify the number of threads to use for execution");

        optimization.addArgument("--log-properties")
                .type(File.class)
                .required(false)
                .dest("logproperties")
                .setDefault(new File("logging.properties"));

        return parser;
    }
}