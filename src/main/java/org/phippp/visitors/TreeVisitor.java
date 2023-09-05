package org.phippp.visitors;

import org.phippp.antlr4.RegExBaseVisitor;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.Expression;
import org.phippp.grammar.Rule;
import org.phippp.model.Tree;

public class TreeVisitor extends RegExBaseVisitor<Tree<Expression>> {

    public final Tree<Expression> tree = Tree.binary();

    @Override
    public Tree<Expression> visitReference(RegExParser.ReferenceContext ctx) {
        return super.visitReference(ctx);
        new Expression(Rule.REFERENCE, null);
    }

    @Override
    public Tree<Expression> visitCharacter(RegExParser.CharacterContext ctx) {
        return super.visitCharacter(ctx);
    }

    @Override
    public Tree<Expression> visitAlternation(RegExParser.AlternationContext ctx) {
        return super.visitAlternation(ctx);
    }

    @Override
    public Tree<Expression> visitGroups(RegExParser.GroupsContext ctx) {
        return super.visitGroups(ctx);
    }

    @Override
    public Tree<Expression> visitOptional(RegExParser.OptionalContext ctx) {
        return super.visitOptional(ctx);
    }

    @Override
    public Tree<Expression> visitConcat(RegExParser.ConcatContext ctx) {
        return super.visitConcat(ctx);
    }

    @Override
    public Tree<Expression> visitPlus(RegExParser.PlusContext ctx) {
        return super.visitPlus(ctx);
    }

    @Override
    public Tree<Expression> visitSimpleGroup(RegExParser.SimpleGroupContext ctx) {
        return super.visitSimpleGroup(ctx);
    }

    @Override
    public Tree<Expression> visitNonCapturing(RegExParser.NonCapturingContext ctx) {
        return super.visitNonCapturing(ctx);
    }
}
