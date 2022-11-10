package org.phippp.logic;

public class Parts {

    public static final String PREFIX = "φ_α";
    public static final String IN = "IN" ; //'\u2208';
    public static final String DOT_EQ = "="; //'\u2250';
    public static final String AND = "AND"; //'\u2227';

    public static String constructTerminal(int curr, Character value){
        return "\n\t" + PREFIX + "(x_" + curr + ") := x_" + curr +
                " " + DOT_EQ + " " + value;
    }

    public static String constructAnd(int term){
        return " " + AND + " " + PREFIX + "(x_" + term + ")";
    }

}
