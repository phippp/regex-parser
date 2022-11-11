package org.phippp.grammar;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;

import java.util.*;
import java.lang.String;

import static org.phippp.logic.Parts.*;

public class Visitor extends RegExBaseVisitor<String> {

    private final Map<Integer, String> references = new HashMap<>();
    private final LinkedList<Integer> terms = new LinkedList<>();

    /**
     * Because each function can increase the number of nodes by 1 or 2, we need to be
     * able to track operations so that we can match up counters later.
     *      2 | concatenation, alternation,
     *      1 | reference, plus
     *      0 | character, groups
     * Think this relation can be modelled with push/pull so that we don't have to keep
     * tracking multiple values, just what comes next. Decided to use linked list instead
     * of a stack as it handles null pointers better and the decrease in performance
     * should be so slight.
     */

    @Override public String visitReference(RegExParser.ReferenceContext ctx) {
        return "Reference : " + visit(ctx.left) + "$" + ctx.ref;
    }

    @Override public String visitCharacter(RegExParser.CharacterContext ctx) {
        int term = terms.peek() != null ? terms.pop() : 0;
        return constructTerminal(term, ctx.getText().charAt(0));
    }

    @Override public String visitAlternation(RegExParser.AlternationContext ctx) {
        int term = terms.peek() != null ? terms.pop() : 0;
        int[] arr = {term + 1, term + 2};
        for(int t : arr) terms.push(t);
        return "\n " + PREFIX + "(x_" + term + ") := x_" + term + " " + IN +
                " (x_" + arr[1] + "|x_" + arr[0] + ")" +
                constructAnd(arr[0]) + constructAnd(arr[1])
                + visit(ctx.left) + visit(ctx.right);
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
        int term = terms.peek() != null ? terms.pop() : 0;
        int[] arr = {term + 1, term + 2};
        for(int t : arr) terms.push(t);
        return "\n" + PREFIX + "(x_" + term + ") := x_" + term + " " + DOT_EQ + " x_" + arr[1] + "x_" + arr[0]
                + constructAnd(arr[1]) + constructAnd(arr[0])
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
