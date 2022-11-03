package org.phippp.app;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.phippp.grammar.RegExLexer;
import org.phippp.grammar.RegExParser;

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
        ParseTree tree = parser.exp();
        System.out.println(tree.toStringTree(parser));
    }
}