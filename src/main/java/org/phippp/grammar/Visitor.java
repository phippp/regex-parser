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

    /**
     * Because each function can increase the number of nodes by 1 or 2, we need to be
     * able to track operations so that we can match up counters later.
     *      2 | concatenation, alternation,
     *      1 | reference, plus
     *      0 | character, groups
     * Think this relation can be modelled with push/pull so that we don't have to keep
     * tracking multiple values, just what comes next.
     */

    @Override public String visitReference(RegExParser.ReferenceContext ctx) {
        return "Reference : " + visit(ctx.left) + "$" + ctx.ref;
    }

    @Override public String visitCharacter(RegExParser.CharacterContext ctx) {
        if(termCounter < 1 ) termCounter = concatCounter;
        String result = constructTerminal(termCounter, ctx.getText().charAt(0));
        termCounter -= (termCounter % 2 == 0) ? 1 : 2;
        return  result;
    }

    @Override public String visitAlternation(RegExParser.AlternationContext ctx) {
        return "\n " + PREFIX + "(x_" + concatCounter + ") := x_" + (concatCounter++) + " " + IN +
                " (x_" + (concatCounter + 1) + "|x_" + concatCounter + ") " + AND + " " +
                PREFIX + "(x_" + (++concatCounter) + ") " + AND + " " +
                PREFIX + "(x_" + (concatCounter - 1) + ");" + visit(ctx.left) + visit(ctx.right);
    }

    @Override public String visitGroups(RegExParser.GroupsContext ctx) {
        return "Group : " + visit(ctx.group());
    }

    /**
     * The expansion of the tree under concatenation always favours the left node.
     * This means any x_(n+1) should be on the right and x_(n+2) will be on the left,
     * any n-sequence of concatenation will have x_(2n) being the first character in
     * the sequence.
     */

    @Override public String visitConcat(RegExParser.ConcatContext ctx) {
        int children = 2; // this rule will always add 2 new terms in our FC logic
        int curr = concatCounter;
        concatCounter += 2;
        return "\n" + PREFIX + "(x_" + curr + ") := x_" + curr + " " + DOT_EQ + " x_" + (curr + 2) + "x_" + (curr + 1)
                + constructAnd(curr + 2) + constructAnd(curr + 1)
                + visit(ctx.left) + visit(ctx.right);
    }

    @Override public String visitPlus(RegExParser.PlusContext ctx) {
        return "Plus : " + visit(ctx.main) + "+";
    }

    @Override public String visitSimpleGroup(RegExParser.SimpleGroupContext ctx) {
        return "Simple : " + visit(ctx.inner);
    }

    @Override public String visitNonCapturing(RegExParser.NonCapturingContext ctx) {
        return "Non Capture : " + visit(ctx.inner);
    }

}
