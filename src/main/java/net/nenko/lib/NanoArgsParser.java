package net.nenko.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * NanoArgsParser - helper class to parse array of arguments of command line
 *
 * It's created with the array of all possible options descriptions;
 * This array is used to parse input arguments, and also to generate
 * synopsis of command line.
 *
 * NOTE1: only this single class is needed to use this functionality.
 * That is why no library (jar) provided, just copy this file into your project.
 * Keep eye on the version for the updated source.
 *
 * v1.00 2024-04-24 initial version
 */
public class NanoArgsParser {

    public static final String FLAG = "NANO-ARGS-PARSER-FLAG";
    public static final String REQUIRED = "NANO-ARGS-PARSER-REQUIRED-OPTION";

    public NanoArgsParser(Option[] options) {
        this.options = options;
    }

    /**
     * parses the array of input command line arguments
     *
     * This method recognizes all options, processes its, and removes from args[] array
     * All other arguments remains in array and returned as output array
     *
     * @param args - array of all input arguments
     * @return String[] - array of all unprocessed arguments (that is all options are removed)
     * @throws NanoArgsParserException - identifies the error of parsing
     */
    public String[] parse(String[] args) throws NanoArgsParserException {
        List<String> result = new ArrayList<>();
        if(args.length > 0) {
            for (int ix = 0; ix < args.length; ix++) {
                if (args[ix].startsWith("-")) {
                    String key = args[ix].substring(1);
                    Optional<Option> opt = Arrays.stream(options).filter(o -> o.key.equals(key)).findFirst();
                    if (opt.isEmpty()) {
                        throw new NanoArgsParserException(null, 22, "Unrecognized option " + args[ix]);
                    }
                    Option option = opt.get();
                    if (option.processed) {
                        throw new NanoArgsParserException(option, 33, "Duplicated option -" + option.key);
                    }
                    if (option.value == FLAG) {
                        option.processed = true;
                    } else {    // next argument is the value of this option
                        if (ix < args.length - 1) {
                            option.value = args[++ix];
                            option.processed = true;
                        } else {
                            throw new NanoArgsParserException(option, 44, "The value of option -" + option.key + " (next argument) is missing");
                        }
                    }
                } else {        // simple argument, should be moved to output array
                    result.add(args[ix]);
                }
            }
        }
        // Check that all mandatory options are present in command line
        Optional<Option> opt = Arrays.stream(options).filter(o -> o.value == REQUIRED).findFirst();
        if (opt.isPresent()) {
            throw new NanoArgsParserException(opt.get(), 55, "Required option -" + opt.get().key + " is missing");
        }
        return result.toArray(new String[0]);
    }

    public String argsSynopsis() {
        return Arrays.stream(options).map(opt -> opt.synopsis).collect(Collectors.joining());
    }

    private final Option[] options;

    public static final class NanoArgsParserException extends RuntimeException {
        public final Option option;
        public final int error;
        public NanoArgsParserException(Option option, int error, String message) {
            super(message);
            this.error = error;
            this.option = option;
        }

        @Override
        public String toString() {
            return "Problem detected when parsing the option -" + option.key +
                    " - " + super.getMessage() + " (code " + error + ')';
        }
    }

    public static class Option {
        public final String key;
        // Normally an application parses commmand line one time in its run, so we use the same field
        // 'value' for initial value of the option and for saving the result of parsing
        public String value;
        public boolean processed = false;
        public final String synopsis;

        public Option(String keyWithoutDash, String defaultValue, String valueMeaning, String description) {
            key = keyWithoutDash;
            value = defaultValue;
            synopsis = synopsis(valueMeaning, description);
        }

        public boolean isOn() {
            if(value != FLAG) {
                throw new NanoArgsParserException(this, 66, "isOn() is applicable only for flag option");
            }
            return processed;
        }

        private String synopsis(String valueMeaningForAutogeneratedSynopsis, String optionDescriptionForAutogeneratedSynopsis) {
            String extra = "";
            String description = "\t-" + key;
            if(value != FLAG) {
                description = description + ' ' + valueMeaningForAutogeneratedSynopsis;
                extra = value == REQUIRED ? " (required option)" : " (default is '" + value + "')";
            }
            description = description + " - " + optionDescriptionForAutogeneratedSynopsis + extra + '\n';
            return description;
        }
    }
}
