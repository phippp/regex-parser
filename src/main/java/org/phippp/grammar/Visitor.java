package org.phippp.grammar;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;

import java.util.HashMap;
import java.util.Map;
import java.lang.String;

import static org.phippp.logic.Parts.*;

public class Visitor extends RegExBaseVisitor<String> {

    private final Map<Integer, String> references = new HashMap<>();
    private int concatCounter = 0;
    private int termCounter = 0;

    @Override public String visitReference(RegExParser.ReferenceContext ctx) {
        return "Reference : " + visit(ctx.left) + "$" + ctx.ref;
    }

    @Override public String visitCharacter(RegExParser.CharacterContext ctx) {
        if(termCounter < 1 ) termCounter = concatCounter;
        int sub = (termCounter % 2 == 0) ? 1 : 2;
        int curr = termCounter;
        termCounter -= sub;
        return "\n\t" + PREFIX + "(x_" + curr + ") := x_" + curr + " " + DOT_EQ + " " + ctx.getText();
    }

    @Override public String visitAlternation(RegExParser.AlternationContext ctx) {
        return "Alternation : " + visit(ctx.left) + "|" + visit(ctx.right);
    }

    @Override public String visitGroups(RegExParser.GroupsContext ctx) {
        return "Group : " + visit(ctx.group());
    }

    @Override public String visitConcat(RegExParser.ConcatContext ctx) {
        int curr = concatCounter;
        concatCounter += 2;
        return "\n" + PREFIX + "(x_" + curr + ") := x_" + curr + " " + DOT_EQ + "x_" + (curr + 2) + "x_" + (curr + 1) + " " +
                AND + " " + PREFIX + "(x_" + (curr + 2) + ") " + AND + " " + PREFIX + "(x_" + (curr + 1) + "); "
                + visit(ctx.left) + visit(ctx.right);
    }

    @Override public String visitPlus(RegExParser.PlusContext ctx) {
        return "Plus : " + visitChildren(ctx);
    }

    @Override public String visitSimpleGroup(RegExParser.SimpleGroupContext ctx) {
//        String eval = visit(ctx.inner);
//        Integer next = 1 + this.references.keySet()
//                .stream()
//                .max(Integer::compareTo)
//                .orElse(0);
//        this.references.put(next, eval);
        return "Simple : " + visit(ctx.inner);
    }

    @Override public String visitNonCapturing(RegExParser.NonCapturingContext ctx) {
        return "Non Capture : " + visit(ctx.inner);
    }

}
