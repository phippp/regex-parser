package org.phippp.app;

import com.beust.jcommander.JCommander;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.antlr4.RegExLexer;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.RegEx;
import org.phippp.util.Optimizer;
import org.phippp.util.Renderer;
import org.phippp.visitors.ObjectVisitor;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    // log4j logger should only include info or higher (check log4j2.xml config)
    private final static Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String... args) {

        // use JCommander to load params
        Args arguments = new Args();
        JCommander cmd = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        cmd.parse(args);
        // add usage when passed with -h or --help flag
        if(arguments.help){
            cmd.usage();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("RegEx please: ");
        String userinput = scanner.nextLine();
        CharStream input = CharStreams.fromString(userinput);
        RegExLexer lexer = new RegExLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RegExParser parser = new RegExParser(tokens);
        ParseTree tree = parser.regex();

        RegEx re = new ObjectVisitor().visit(tree);

        re = Optimizer.optimize(re, getOptimizations(arguments));

        LOG.info(re.toString(true));

        try {
            String location = Renderer.makeGraph(re.traverse(), arguments.rough, arguments.title, arguments.file);
            LOG.info("File saved! " + location);
        } catch (IOException e){
            LOG.error(e);
        }

    }

    protected static byte getOptimizations(Args args) {
        if(args.all) return (byte)(Optimizer.CONCAT | Optimizer.SIMPLIFY | Optimizer.ORDER);
        byte opt = 0;
        if(args.reorder) opt |= Optimizer.ORDER;
        if(args.simplify) opt |= Optimizer.SIMPLIFY;
        if(args.concat) opt |= Optimizer.CONCAT;
        return opt;
    }
}