package org.phippp.util;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizProcessor;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.rough.Roughifyer;
import org.phippp.grammar.RegEx;
import org.phippp.logic.Conjunctive;
import org.phippp.logic.Parts;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class Renderer {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final GraphvizProcessor PROCESSOR = new Roughifyer().bowing(2).curveStepCount(6).roughness(1).font("*serif", "Comic Sans MS");

    public static List<LinkSource> makeLinks(List<RegEx> regexes) {
        List<LinkSource> sources = new ArrayList<>();
        for(RegEx regex : regexes) {
            sources.add(node(String.valueOf(regex.getTerm()))
                    .with(
                            Label.lines(
                                    "Term: " + regex.getTerm(),
                                    "Rule: " + regex.getRule(),
                                    regex.isTerminal() ? "Str: " + regex.getText()
                                            : "Not Terminal"
                                    )
                    ).link(
                            regex.getChildren().stream()
                                    .map(r -> node(String.valueOf(r.getTerm())))
                                    .toList()
                    )
            );
        }
        return sources;
    }

    public static List<LinkSource> makeLinks(Conjunctive.Node node) {
        List<LinkSource> sources = new ArrayList<>();
        List<Conjunctive.Node> list = node.toList();

        for(int i = 0; i < list.size(); i++){
            Conjunctive.Node local = list.get(i);
            boolean isJoin = local.isJoin();

            sources.add(node(isJoin ? "j_" + i : "t_" + i)
                    .with(
                            Label.lines(local.toString())
                    ).link(
                            local.getChildren().stream()
                                    .map(n -> {
                                        int index = list.indexOf(n);
                                        return node(n.isJoin() ? "j_" + index : "t_" + index);
                                    }).toList()
                    )
            );
        }

        return sources;
    }

    public static String makeGrammarGraph(List<RegEx> r, boolean rough) throws IOException {
        return makeGrammarGraph(r, rough, "", FORMATTER.format(new Date()));
    }

    public static String makeGrammarGraph(List<RegEx> r, boolean rough, String... params) throws IOException {
        // assume that array has graph name and file name
        if(params.length < 2) throw new InvalidPropertiesFormatException("Expected at least 2 parameters");

        File output = new File(params[1]);

        Graph g = graph(params[0]).directed().with(
                makeLinks(r)
        );

        Graphviz graphviz = Graphviz.fromGraph(g);

        if (rough) graphviz =  graphviz.processor(PROCESSOR);

        graphviz.width(1024)
                .render(Format.PNG)
                .toFile(output);

        return output.getAbsolutePath();
    }

    public static String makeConjunctiveGraph(Conjunctive.Node node) throws IOException {
        // assume that array has graph name and file name
//        if(params.length < 2) throw new InvalidPropertiesFormatException("Expected at least 2 parameters");

//        File output = new File(params[1]);
        File output = new File("images/lol.png");

        Graph g = graph("").directed().with(
                makeLinks(node)
        );

        Graphviz graphviz = Graphviz.fromGraph(g);

//        if (rough) graphviz =  graphviz.processor(PROCESSOR);

        graphviz.width(1024)
                .render(Format.PNG)
                .toFile(output);

        return output.getAbsolutePath();
    }
}
