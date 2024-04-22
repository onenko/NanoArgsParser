package net.nenko.lib;

/**
 * NanoArgsParser's demo application
 */
public class NanoArgsParserDemoMainApp
{
    public static void main( String[] args ) {
        NanoArgsParser.Option[] options = new NanoArgsParser.Option[4];
        options[0] = new NanoArgsParser.Option("p", NanoArgsParser.REQUIRED, "<password>", "sets the password for encryption");
        options[1] = new NanoArgsParser.Option("rev", NanoArgsParser.FLAG, null, "if set, activates reverse order of processing");
        options[2] = new NanoArgsParser.Option("d", ".", "<directory>", "sets current directory for processing");
        options[3] = new NanoArgsParser.Option("h", NanoArgsParser.FLAG, null, "displays command line format and list of options");
        NanoArgsParser parser = new NanoArgsParser(options);

        System.out.println("NanoArgsParser Demo Application");
        boolean toPrintHelp;
        try {
            String[] nonOptionArguments = parser.parse(args);
            System.out.println("\nThe result of parsing:");
            System.out.print("\nNonoptional arguments to be processed separately by the application: ");
            if(nonOptionArguments.length == 0) {
                System.out.println("None");
            } else {
                for(String arg: nonOptionArguments) {
                    System.out.print("\n" + arg);
                }
                System.out.println("");
            }
            System.out.println("Password: " + options[0].value);
            System.out.println("Reverse order: " + (options[1].isOn() ? "true" : "false"));
            System.out.println("Directory: " + options[2].value);
            toPrintHelp = options[3].isOn();
        } catch (NanoArgsParser.NanoArgsParserException napx) {
            System.out.println(napx.toString());
            toPrintHelp = true;
        }
        if(toPrintHelp) {
            System.out.println("\nCommand line format:");
            System.out.println("NanoArgsParserDemoMainApp <options> <other-app-specific-nonoptional-arguments>");
            System.out.println("\tOptional and nonoptional arguments can be intermixed");
            System.out.println(parser.argsSynopsis());
        }
    }
}
