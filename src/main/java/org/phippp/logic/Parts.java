package org.phippp.logic;

public class Parts {

    /**
     * Current development setup does not use Unicode for console so
     * unicode characters (mathematical symbols) can't be used, they have
     * been replaced by String representations for the time being.
     */

    public static final String ALPHA    = "α";          //'α';
    public static final String PREFIX   = "φ_α";
    public static final String IN       = " IN " ;      //'∈';
    public static final String DOT_EQ   = " = ";        //'≐';
    public static final String AND      = " AND ";      //'∧';
    public static final String EXISTS   = "EXISTS ";    //'∃';
    public static final String JOIN     = "JOIN";       //'⋈';

    @Deprecated
    public static String constructTerminal(int curr, String value){
        return "\n\t" + PREFIX + "(x_" + curr + ") := x_" + curr +
                " " + DOT_EQ + " " + value;
    }

    @Deprecated
    public static String constructAnd(int term){
        return constructAnd("x_" + term);
    }

    @Deprecated
    public static String constructAnd(String term){
        return " " + AND + " " + PREFIX + "(" + term + ")";
    }


}
