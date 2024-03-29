package org.phippp.app;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names={"--rough", "-r"}, description="Use rough processor to make graphviz images appear hand-drawn")
    public boolean rough = false;

    @Parameter(names={"--file", "-f"}, description="File to save the resulting graphviz image as")
    public String file = "images/image.png";

    @Parameter(names={"--title", "-t"}, description="Title of the graphviz image")
    public String title = "";

    @Parameter(names={"-a"}, description="All optimizations will be used, equivalent to -cs flags")
    public boolean all = false;

    @Parameter(names={"--concat", "--concatenation", "-c"}, description="Concatenation optimization")
    public boolean concat = false;

    @Parameter(names={"--simplify", "-s"}, description="Simplification optimization")
    public boolean simplify = false;

    // @Parameter(names={"--reorder", "-o"}, description="Reorder variables to ascend rather than descend")
    // public boolean reorder = false;

    // @Parameter(names={"--gyo", "-g"}, description="Perform GYO optimization, requires -so, will automatically use flag.")
    // public boolean gyo = false;

    @Parameter(names={"--verbose", "-v"}, description="Include extra outputs in the command line")
    public boolean verbose = false;

    @Parameter(names={"-i", "--input"}, description="The RegEx formula")
    public String input = null;

    @Parameter(names={"-q", "--query"}, description="The string to test against the value from -i")
    public String query = null;

    @Parameter(names={"--help", "-h"}, help = true)
    public boolean help = false;
}
