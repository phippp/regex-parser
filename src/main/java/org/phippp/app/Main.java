package org.phippp.app;

import com.beust.jcommander.JCommander;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.antlr4.RegExLexer;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.RegEx;
import org.phippp.logic.Acyclic;
import org.phippp.logic.BruteForce;
import org.phippp.logic.ConjunctiveTree;
import org.phippp.logic.Spanner;
import org.phippp.util.BinaryTree;
import org.phippp.util.Logging;
import org.phippp.util.Optimizer;
import org.phippp.util.Renderer;
import org.phippp.visitors.ObjectVisitor;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

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
        Logging.log(String.format("Input: %s", input), LOG, arguments);
        CharStream stream = CharStreams.fromString(input);
        RegEx re = parse(stream);
        Logging.log(String.format("Before Optimization:%n%s", re.toString(true)), LOG, arguments);

        // optimize
        re = Optimizer.optimize(re, getOptimizations(arguments));
        Logging.log(String.format("After Optimization:%n%s", re.toString(true)), LOG, arguments);

        // convert
        Spanner spanner = Spanner.fromRegEx(re);
        Pair<Boolean, Acyclic.Graph<Integer>> acyclic = Acyclic.bracket(spanner);
        Logging.log(String.format("%s have acyclic representation", acyclic.getLeft() ? "Does" : "Doesn't"), LOG, arguments);
        Logging.log(String.format("%s", spanner), LOG, arguments);

        // visualize
        render(arguments, re);
        if(acyclic.getLeft()) {
            BinaryTree<Spanner> bTree = Acyclic.tree(spanner, acyclic.getRight(), arguments);
            ConjunctiveTree tree = ConjunctiveTree.fromTree(bTree);
            render(arguments, tree);
            if(arguments.query != null) {
                boolean match = BruteForce.testString(arguments.query, tree, arguments);
                String m = match ? "does" : "doesn't";
                Logging.log(String.format("%s %s match the input %s", arguments.query, m, arguments.input), LOG, arguments);
                long start = System.currentTimeMillis();
                Pattern p = Pattern.compile(String.format("^%s$", arguments.input.replace('$', '\\')));
                match = p.matcher(arguments.query).matches();
                Logging.log(String.format("Took %dms", (System.currentTimeMillis() - start)), LOG, arguments);
            }
        }
    }

    protected static byte getOptimizations(Args args) {
        if(args.all) return (byte)(Optimizer.CONCAT | Optimizer.SIMPLIFY);
        byte opt = 0;
        // if(args.reorder || args.gyo) opt |= Optimizer.ORDER;
        if(args.simplify)   opt |= Optimizer.SIMPLIFY;
        if(args.concat)     opt |= Optimizer.CONCAT;
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
            Logging.log(String.format("File saved at : %s", location), LOG, args);
        } catch (IOException e){
            LOG.fatal(e);
        }
    }

    protected static void render(Args args, ConjunctiveTree tree){
        try{
            String location = Renderer.makeConjunctiveGraph(tree, args.rough, args.title, args.file);
            Logging.log(String.format("File saved at : %s", location), LOG, args);
        } catch (IOException e){
            LOG.fatal(e);
        }
    }
}