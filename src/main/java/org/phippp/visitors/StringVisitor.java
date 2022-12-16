package org.phippp.visitors;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;

import java.util.*;
import java.lang.String;

import static org.phippp.logic.Parts.*;

public class StringVisitor extends RegExBaseVisitor<String> {

    private final List<String> references = new ArrayList<>();
    private final LinkedList<Integer> terms = new LinkedList<>();
    private final Set<Integer> visited = new HashSet<>();

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

    // ----------------------------- 0 additions ----------------------------- //

    @Override public String visitCharacter(RegExParser.CharacterContext ctx) {
        int term = visitNextTerm();
        return constructTerminal(term, ctx.getText());
    }

    @Override public String visitGroups(RegExParser.GroupsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public String visitSimpleGroup(RegExParser.SimpleGroupContext ctx) {
        int term = terms.peek() != null ? terms.peek() : 1;
        this.references.add("x_" + term);
        return visit(ctx.inner);
    }

    @Override public String visitNonCapturing(RegExParser.NonCapturingContext ctx) {
        return "Non Capture : " + visit(ctx.inner);
    }

    // ----------------------------- 1 addition  ----------------------------- //

    @Override public String visitReference(RegExParser.ReferenceContext ctx) {
        int term = visitNextTerm();
        int[] arr = {getNextFreeTerm(term)};
        String lhs = visit(ctx.left);
        String ref = this.references.get(Integer.parseInt(ctx.ref.getText()) - 1);
        return "\n\t\t" + PREFIX + "(x_" + term + ") := x_" + term + " " + DOT_EQ +
                " x_" + arr[0] + ref +
                constructAnd(arr[0]) + constructAnd(ref)
                + lhs;
    }

    @Override public String visitPlus(RegExParser.PlusContext ctx) {
        return "Plus : " + visit(ctx.main) + "+";
    }

    // ----------------------------- 2 additions ----------------------------- //

    /**
     * The expansion of the tree under concatenation always favours the left node.
     * This means any x_(n+1) should be on the right and x_(n+2) will be on the left,
     * any n-sequence of concatenation will have x_(2n) being the first character in
     * the sequence.
     */

    @Override public String visitAlternation(RegExParser.AlternationContext ctx) {
        int term = visitNextTerm();
        int[] arr = {getNextFreeTerm(term), getNextFreeTerm(term)};
        return "\n " + PREFIX + "(x_" + term + ") := x_" + term + " " + IN +
                " (x_" + arr[1] + "|x_" + arr[0] + ")" +
                constructAnd(arr[1]) + constructAnd(arr[0])
                + visit(ctx.left) + visit(ctx.right);
    }

    @Override public String visitConcat(RegExParser.ConcatContext ctx) {
        int term = visitNextTerm();
        int[] arr = {getNextFreeTerm(term), getNextFreeTerm(term)};
        return "\n" + PREFIX + "(x_" + term + ") := x_" + term + " " + DOT_EQ + " x_" + arr[1] + "x_" + arr[0]
                + constructAnd(arr[1]) + constructAnd(arr[0])
                + visit(ctx.left) + visit(ctx.right);
    }

    // --------------------------- Helper Functions -------------------------- //

    private int getNextFreeTerm(int curr){
        for( ; visited.contains(++curr) || terms.contains(curr) ; );
        terms.push(curr);
        return curr;
    }

    private int visitNextTerm(){
        int term = terms.peek() != null ? terms.pop() : 0;
        visited.add(term);
        return term;
    }

}
