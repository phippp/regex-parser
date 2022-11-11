package org.phippp.logic;

public class Parts {

    /**
     * Current development setup does not use Unicode for console so
     * unicode characters (mathematical symbols) can't be used, they have
     * been replaced by String representations for the time being.
     */

    public static final String PREFIX = "φ_α";
    public static final String IN = "IN" ; //'\u2208';
    public static final String DOT_EQ = "="; //'\u2250';
    public static final String AND = "AND"; //'\u2227';

    public static String constructTerminal(int curr, String value){
        return "\n\t" + PREFIX + "(x_" + curr + ") := x_" + curr +
                " " + DOT_EQ + " " + value;
    }

    public static String constructAnd(int term){
        return constructAnd("x_" + term);
    }

    public static String constructAnd(String term){
        return " " + AND + " " + PREFIX + "(" + term + ")";
    }


}
