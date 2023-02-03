package org.phippp.app;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.phippp.antlr4.RegExLexer;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.RegEx;
import org.phippp.util.Optimizer;
import org.phippp.visitors.ObjectVisitor;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("RegEx please: ");
        String userinput = scanner.nextLine();
        CharStream input = CharStreams.fromString(userinput);
        RegExLexer lexer = new RegExLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RegExParser parser = new RegExParser(tokens);
        ParseTree tree = parser.regex();

        RegEx re = new ObjectVisitor().visit(tree);

        Optimizer.optimize(re, Optimizer.CONCAT);

        System.out.println("-------------------");
        System.out.println(re.toString());
    }
}