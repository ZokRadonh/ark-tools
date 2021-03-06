package qowyn.ark.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import qowyn.ark.ReadingOptions;
import qowyn.ark.WritingOptions;
import qowyn.ark.tools.options.BooleanValueConverter;
import qowyn.ark.tools.options.IntegerValueConverter;

public class OptionHandler {

  private final OptionParser parser;

  private final OptionSpec<String> nonOptionsSpec;

  private final OptionSpec<Integer> asyncSizeSpec;

  private final OptionSpec<Boolean> asyncSpec;
  
  private final OptionSpec<String> langSpec;

  private final OptionSpec<Void> mmapSpec;

  private final OptionSpec<Void> parallelSpec;

  private final OptionSpec<Void> prettySpec;

  private final OptionSpec<Void> stopwatchSpec;

  private final OptionSpec<Void> quietSpec;

  private final OptionSpec<Void> helpSpec;

  private final OptionSpec<Void> verboseSpec;

  private final String[] originalArgs;

  private final OptionSet initialOptions;

  private final List<String> nonOptions;

  private final FilteredHelpFormatter helpFormatter;

  private Command commandObject;

  public OptionHandler(String... args) {
    parser = new OptionParser();
    parser.allowsUnrecognizedOptions();

    nonOptionsSpec = parser.nonOptions();

    WritingOptions options = WritingOptions.create();

    asyncSizeSpec = parser.accepts("async-size", "Size of buffer for asynchronous I/O, higher values increase used memory but can reduce total processing time with slow I/O.")
        .withRequiredArg().withValuesConvertedBy(new IntegerValueConverter()).defaultsTo(options.getAsyncBufferSize());

    asyncSpec = parser.acceptsAll(Arrays.asList("async", "a"), "Wether asynchronous I/O should be used.")
        .withRequiredArg().withValuesConvertedBy(new BooleanValueConverter()).defaultsTo(options.isAsynchronous());
    
    langSpec = parser.accepts("lang", "Load data for specified language, needs appropriate ark_data_lang.json.").withRequiredArg();

    mmapSpec = parser.acceptsAll(Arrays.asList("mmap", "m"), "If set memory mapping will be used. Efficency depends on available RAM and OS.");

    parallelSpec = parser.acceptsAll(Arrays.asList("parallel", "p"), "If set files will be processed by multiple threads.");

    prettySpec = parser.accepts("pretty-printing", "If set all JSON output will use pretty printing.");

    stopwatchSpec = parser.acceptsAll(Arrays.asList("stopwatch", "s"), "Measure time spent.");

    helpSpec = parser.acceptsAll(Arrays.asList("help", "h"), "Displays this help screen, use with a command to get contextual help.")
        .forHelp();

    quietSpec = parser.acceptsAll(Arrays.asList("quiet", "q"), "Suppresses output to stdout.");

    verboseSpec = parser.acceptsAll(Arrays.asList("verbose", "v"), "Prints stack traces for potentially corrupt files.");

    initialOptions = parser.parse(args);
    originalArgs = args;
    nonOptions = initialOptions.valuesOf(nonOptionsSpec);
    helpFormatter = new FilteredHelpFormatter(parser.recognizedOptions().keySet());
  }

  public boolean hasCommand() {
    return nonOptions.size() > 0;
  }

  public String getCommand() {
    return nonOptions.get(0);
  }

  public Command getCommandObject() {
    return commandObject;
  }

  public void setCommandObject(Command commandObject) {
    this.commandObject = commandObject;
  }

  public List<String> getParams() {
    return nonOptions.subList(1, nonOptions.size());
  }

  public OptionSet reparse() {
    return parser.parse(originalArgs);
  }

  public List<String> getParams(OptionSet options) {
    List<String> params = options.valuesOf(nonOptionsSpec);
    return params.subList(1, params.size());
  }

  public int asyncSize() {
    return initialOptions.valueOf(asyncSizeSpec);
  }

  public boolean useAsync() {
    return initialOptions.valueOf(asyncSpec);
  }
  
  public String lang() {
    return initialOptions.valueOf(langSpec);
  }

  public boolean useMmap() {
    return initialOptions.has(mmapSpec);
  }

  public boolean useParallel() {
    return initialOptions.has(parallelSpec);
  }

  public boolean usePretty() {
    return initialOptions.has(prettySpec);
  }

  public boolean useStopwatch() {
    return initialOptions.has(stopwatchSpec);
  }

  public boolean wantsHelp() {
    return initialOptions.has(helpSpec);
  }

  public boolean isQuiet() {
    return initialOptions.has(quietSpec);
  }

  public boolean isVerbose() {
    return initialOptions.has(verboseSpec);
  }

  public void printHelp() {
    try {
      System.err.println();
      parser.printHelpOn(System.err);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void printCommandHelp() {
    try {
      if (!wantsHelp()) {
        int paramCount = getParams(reparse()).size();

        if (paramCount > commandObject.getOptions().length) {
          System.err.println("Too many arguments.");
        } else if (paramCount == commandObject.getOptions().length) {
          // Should never happen
          throw new RuntimeException("Derp.");
        } else {
          System.err.println("Missing argument " + commandObject.getOptions()[paramCount] + ".");
        }
      }
      System.err.println(commandObject.getDescription());
      System.err.println("Usage: ark-tools " + getCommand() + (commandObject.getOptionSummary().isEmpty() ? "" : " " + commandObject.getOptionSummary()) + " [OPTIONS]");
      System.err.println();
      parser.formatHelpWith(helpFormatter);
      parser.printHelpOn(System.err);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public OptionSpecBuilder accepts(String option, String description) {
    return parser.accepts(option, description);
  }

  public OptionSpecBuilder acceptsAll(List<String> options, String description) {
    return parser.acceptsAll(options, description);
  }

  public WritingOptions writingOptions() {
    return WritingOptions.create()
        .asyncBufferSize(asyncSize())
        .asynchronous(useAsync())
        .parallel(useParallel())
        .withMemoryMapping(useMmap());
  }

  public ReadingOptions readingOptions() {
    return ReadingOptions.create()
        .asynchronous(useAsync())
        .parallel(useParallel())
        .withMemoryMapping(useMmap());
  }

}
