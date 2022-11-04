package org.phippp.grammar;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;

public class Visitor<T> extends RegExBaseVisitor<T> {

    private Boolean anchored = false;

    @Override public T visitAnchors(RegExParser.AnchorsContext ctx) {
        this.anchored = (ctx.start != null || ctx.end != null);
        return visitChildren(ctx);
    }
    @Override public T visitReference(RegExParser.ReferenceContext ctx) { return visitChildren(ctx); }
    @Override public T visitCharacter(RegExParser.CharacterContext ctx) { return visitChildren(ctx); }
    @Override public T visitDot(RegExParser.DotContext ctx) { return visitChildren(ctx); }
    @Override public T visitAlternation(RegExParser.AlternationContext ctx) { return visitChildren(ctx); }
    @Override public T visitGroups(RegExParser.GroupsContext ctx) { return visitChildren(ctx); }
    @Override public T visitConcat(RegExParser.ConcatContext ctx) { return visitChildren(ctx); }
    @Override public T visitPlus(RegExParser.PlusContext ctx) { return visitChildren(ctx); }
    @Override public T visitKleene(RegExParser.KleeneContext ctx) { return visitChildren(ctx); }
    @Override public T visitSimpleGroup(RegExParser.SimpleGroupContext ctx) { return visitChildren(ctx); }
    @Override public T visitNonCapturing(RegExParser.NonCapturingContext ctx) { return visitChildren(ctx); }
}
