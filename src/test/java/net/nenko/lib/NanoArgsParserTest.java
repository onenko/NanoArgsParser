package net.nenko.lib;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NanoArgsParserTest {

    private static final String SOME_DEF_VAL = "some default value";
    private static final String[] THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN = new String[0];
    private NanoArgsParser parser;
    private NanoArgsParser.Option[] options;

    @Before
    public void setUp() throws Exception {
        options = new NanoArgsParser.Option[4];
        options[0] = new NanoArgsParser.Option("p", NanoArgsParser.REQUIRED, "<password>", "sets the password for encryption");
        options[1] = new NanoArgsParser.Option("rev", NanoArgsParser.FLAG, null, "if set, activates reverse order of processing");
        options[2] = new NanoArgsParser.Option("d", ".", "<directory>", "sets current directory for processing");
        options[3] = new NanoArgsParser.Option("x", SOME_DEF_VAL, "<arg-value-meaning>", "explanation of the option for autogeneration command line format");
        parser = new NanoArgsParser(options);
    }

    @Test
    public void parseHappyPathTest() {
        String[] args = List.of("command", "-p", "abracadabra", "-rev", "input", "output", "-d", "another directory").toArray(new String[0]);
        String[] out = parser.parse(args);
        assertNotNull(out);
        assertEquals(3, out.length);
        assertEquals("command", out[0]);
        assertEquals("input", out[1]);
        assertEquals("output", out[2]);
        assertEquals("abracadabra", options[0].value);
        assertTrue(options[1].isOn());
        assertEquals("another directory", options[2].value);
    }

    @Test
    public void parseUnknownArgTest() {
        String[] args = List.of("command", "-Z", "abracadabra", "-rev", "input", "output", "-d", "another directory").toArray(new String[0]);
        String[] out = THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN;
        try {
            out = parser.parse(args);
        } catch(NanoArgsParser.NanoArgsParserException napx) {
            assertNull(napx.option);
            assertEquals(22, napx.error);
            assertEquals("Unrecognized option -Z", napx.getMessage());
        }
        assertTrue(THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN == out);
    }

    @Test
    public void parseMissingRequiredArgTest() {
        String[] args = List.of("command", "-rev", "input", "output", "-d", "another directory").toArray(new String[0]);
        String[] out = THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN;
        try {
            out = parser.parse(args);
        } catch(NanoArgsParser.NanoArgsParserException napx) {
            assertEquals(options[0], napx.option);
            assertEquals(55, napx.error);
            assertEquals("Required option -p is missing", napx.getMessage());
        }
        assertTrue(THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN == out);
    }

    @Test
    public void parseDuplicateArgTest() {
        String[] args = List.of("command", "-p", "first-occurance", "-rev", "-p", "duplicate (appear 2nd time)",
                "input", "output", "-d", "another directory").toArray(new String[0]);
        String[] out = THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN;
        try {
            out = parser.parse(args);
        } catch(NanoArgsParser.NanoArgsParserException napx) {
            assertEquals(options[0], napx.option);
            assertEquals(33, napx.error);
            assertEquals("Duplicated option -p", napx.getMessage());
        }
        assertTrue(THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN == out);
    }

    @Test
    public void parseMissingArgValueTest() {
        String[] args = List.of("command", "-p", "abracadabra", "-rev", "input", "output", "-d").toArray(new String[0]);
        String[] out = THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN;
        try {
            out = parser.parse(args);
        } catch(NanoArgsParser.NanoArgsParserException napx) {
            assertEquals(options[2], napx.option);
            assertEquals(44, napx.error);
            assertEquals("The value of option -d (next argument) is missing", napx.getMessage());
        }
        assertTrue(THIS_VALUE_SIGNIFY_THAT_EXCEPTION_WAS_THROWN == out);
    }

    @Test
    public void synopsisTest() {
        assertEquals("\t-p <password> - sets the password for encryption (required option)\n", options[0].synopsis);
        assertEquals("\t-rev - if set, activates reverse order of processing\n", options[1].synopsis);
        assertEquals("\t-d <directory> - sets current directory for processing (default is '.')\n", options[2].synopsis);
        assertEquals("\t-x <arg-value-meaning> - explanation of the option for autogeneration command line format (default is '" + SOME_DEF_VAL + "')\n", options[3].synopsis);
    }

}