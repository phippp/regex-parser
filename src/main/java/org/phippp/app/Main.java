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
import org.phippp.logic.Conjunctive;
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

        // load inputs and parse grammar
        String input = getInput(arguments);
        if(arguments.verbose) System.out.println(input);
        CharStream stream = CharStreams.fromString(input);
        RegEx re = parse(stream);
        LOG.info("BEFORE OPTIMIZATION:\n" + re.toString(true));

        // optimize
        re = Optimizer.optimize(re, getOptimizations(arguments));
        LOG.info("AFTER OPTIMIZATION:\n" + re.toString(true));

        // convert
        Conjunctive.Node conj = Conjunctive.fromRegEx(re);

        // visualize
        render(arguments, re);
        render(arguments, conj);
    }

    protected static byte getOptimizations(Args args) {
        if(args.all) return (byte)(Optimizer.CONCAT | Optimizer.SIMPLIFY | Optimizer.ORDER);
        byte opt = 0;
        if(args.reorder || args.gyo) opt |= Optimizer.ORDER;
        if(args.simplify || args.gyo) opt |= Optimizer.SIMPLIFY;
        if(args.concat) opt |= Optimizer.CONCAT;
        return opt;
    }

    protected static String getInput(Args args) {
        if(args.input != null) return args.input;
        // read input from console
        Scanner scanner = new Scanner(System.in);
        System.out.print("RegEx:");
        return scanner.nextLine();
    }

    protected static RegEx parse(CharStream stream) {
        RegExLexer lexer = new RegExLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RegExParser parser = new RegExParser(tokens);
        ParseTree tree = parser.regex();
        return new ObjectVisitor().visit(tree);
    }

    protected static void render(Args args, RegEx r) {
        try{
            String location = Renderer.makeGrammarGraph(r.traverse(), args.rough, args.title, args.file);
            LOG.info("File saved! " + location);
        } catch (IOException e){
            LOG.fatal(e);
        }
    }

    protected static void render(Args args, Conjunctive.Node conj){
        try{
            conj = Optimizer.optimize(conj);
            String location = Renderer.makeConjunctiveGraph(conj, args.rough, args.title, args.file);
            LOG.info("File saved! " + location);
        } catch (IOException e){
            LOG.fatal(e);
        }
    }
}