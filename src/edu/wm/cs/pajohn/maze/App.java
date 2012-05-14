package edu.wm.cs.pajohn.maze;

import java.util.Arrays;
import java.util.Iterator;

public class App {

    public static final String USAGE = getUsage();
    public static final String HELP = getHelp();

    public static final int DISPLAY_ASCII = 0;
    public static final int DISPLAY_2D = 1;
    public static final int DISPLAY_3D = 2;

    public static final int DEFAULT_DISPLAY = DISPLAY_ASCII;
    public static final int DEFAULT_WIDTH = 10;
    public static final int DEFAULT_HEIGHT = 10;

    private static String getUsage() {
        return "usage: ./run.sh [-d display] [-s w h] width height";
    }

    private static String getHelp() {
        return USAGE;
    }

    private static void parseError(String message) {
        System.out.println(HELP);
        System.out.println("error: " + message);
        System.exit(2);
    }

    private static void printHelp() {
        System.out.println(HELP);
        System.exit(0);
    }

    private static int parseDisplay(Iterator<String> iter) {
        String arg = iter.next().toLowerCase();
        if (arg.equals("ascii")) {
            return DISPLAY_ASCII;
        } else if (arg.equals("2d")) {
            return DISPLAY_2D;
        } else if (arg.equals("3d")) {
            return DISPLAY_3D;
        }
        parseError("valid displays are ascii, 2d, and 3d");
        return -1;
    }

    public static void main(String[] argv) {
        Iterator<String> iter = Arrays.asList(argv).iterator();
        String arg;

        int display = DEFAULT_DISPLAY;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        int pos = 0;

        while (iter.hasNext()) {
            arg = iter.next();
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
                if (arg.equals("help")) {
                    printHelp();
                } else if (arg.equals("display")) {
                    display = parseDisplay(iter);
                } else {
                    parseError("unrecognized option '--" + arg + "'");
                }
            } else if (arg.startsWith("-")) {
                arg = arg.substring(1);
                for (char ch : arg.toCharArray()) {
                    if (ch == 'h') {
                        printHelp();
                    } else if (ch == 'd') {
                        display = parseDisplay(iter);
                    } else {
                        parseError("unrecognized option '-" + ch + "'");
                    }
                }
            } else {
                if (pos == 0) {
                    width = Integer.parseInt(arg);
                } else if (pos == 1) {
                    height = Integer.parseInt(arg);
                    pos++;
                } else {
                    parseError("only accepts " + pos + " positional arguments");
                }
                pos++;
            }
        }

        System.out.println("gui " + display + " width " + width + " height " + height);
    }

}
