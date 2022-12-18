package org.phippp.visitors;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.RegEx;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectVisitor extends RegExBaseVisitor<RegEx> {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final List<RegEx> references = new ArrayList<>();

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

    @Override public RegEx visitCharacter(RegExParser.CharacterContext ctx) {
        int term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.CHARACTER, new ArrayList<>(), ctx.getText());
    }

    @Override public RegEx visitGroups(RegExParser.GroupsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public RegEx visitSimpleGroup(RegExParser.SimpleGroupContext ctx) {
        RegEx[] inner = {visit(ctx.inner)};
        Integer term = counter.getAndAdd(1);
        RegEx next = new RegEx(term, RegEx.Rule.SIMPLE_GROUP, List.of(inner), "");
        this.references.add(next);
        return next;
    }

    @Override public RegEx visitNonCapturing(RegExParser.NonCapturingContext ctx) {
        return visit(ctx.inner);
    }

    // ----------------------------- 1 addition  ----------------------------- //

    @Override public RegEx visitReference(RegExParser.ReferenceContext ctx) {
        RegEx[] children = {visit(ctx.left), this.references.get(Integer.parseInt(ctx.ref.getText()) - 1)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.REFERENCE, List.of(children), "");
    }

    @Override public RegEx visitPlus(RegExParser.PlusContext ctx) {
        RegEx[] children = {visit(ctx.main)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.PLUS, List.of(children), "");
    }

    // ----------------------------- 2 additions ----------------------------- //

    /**
     * The expansion of the tree under concatenation always favours the left node.
     * This means any x_(n+1) should be on the right and x_(n+2) will be on the left,
     * any n-sequence of concatenation will have x_(2n) being the first character in
     * the sequence.
     */

    @Override public RegEx visitAlternation(RegExParser.AlternationContext ctx) {
        RegEx[] children = {visit(ctx.left), visit(ctx.right)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.ALTERNATION, List.of(children), "");
    }

    @Override public RegEx visitConcat(RegExParser.ConcatContext ctx) {
        RegEx[] children = {visit(ctx.left), visit(ctx.right)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.CONCAT, List.of(children), "");
    }

    // --------------------------- Helper Functions -------------------------- //

}
