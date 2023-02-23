package org.phippp.visitors;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.RegEx;

import java.util.ArrayList;
import java.util.List;
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
     */

    // ----------------------------- 0 additions ----------------------------- //

    @Override public RegEx visitCharacter(RegExParser.CharacterContext ctx) {
        int term = counter.getAndAdd(1);
        return new RegEx(term, ctx.getText());
    }

    @Override public RegEx visitGroups(RegExParser.GroupsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public RegEx visitSimpleGroup(RegExParser.SimpleGroupContext ctx) {
        RegEx[] inner = {visit(ctx.inner)};
        Integer term = counter.getAndAdd(1);
        RegEx next = new RegEx(term, RegEx.Rule.SIMPLE_GROUP, inner);
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
        return new RegEx(term, RegEx.Rule.REFERENCE, children);
    }

    @Override public RegEx visitPlus(RegExParser.PlusContext ctx) {
        RegEx[] children = {visit(ctx.main)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.PLUS, children);
    }

    @Override public RegEx visitOptional(RegExParser.OptionalContext ctx) {
        RegEx[] children = {visit(ctx.main)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.OPTIONAL, children);
    }

    // ----------------------------- 2 additions ----------------------------- //

    /**
     * When a tree includes concatenation, at any point it must have visited both
     * nodes thus the left if x_n, the right is x_(n+1) and the concatenation
     * node is x_(n+2).
     */

    @Override public RegEx visitAlternation(RegExParser.AlternationContext ctx) {
        RegEx[] children = {visit(ctx.left), visit(ctx.right)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.ALTERNATION, children);
    }

    @Override public RegEx visitConcat(RegExParser.ConcatContext ctx) {
        RegEx[] children = {visit(ctx.left), visit(ctx.right)};
        Integer term = counter.getAndAdd(1);
        return new RegEx(term, RegEx.Rule.CONCAT, children);
    }

    // --------------------------- Helper Functions -------------------------- //

}
