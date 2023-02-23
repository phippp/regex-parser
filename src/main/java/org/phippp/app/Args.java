package org.phippp.app;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names={"--rough", "-r"}, description="Use rough processor to make graphviz images appear hand-drawn")
    public boolean rough = false;

    @Parameter(names={"--file", "-f"}, description="File to save the resulting graphviz image as")
    public String file = "images/image.png";

    @Parameter(names={"--title", "-t"}, description="Title of the graphviz image")
    public String title = "";

}
