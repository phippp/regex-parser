package org.phippp.grammar;

// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RegExParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RegExVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code anchors}
	 * labeled alternative in {@link RegExParser#exp()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnchors(RegExParser.AnchorsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code reference}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference(RegExParser.ReferenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code character}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter(RegExParser.CharacterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dot}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDot(RegExParser.DotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code alternation}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlternation(RegExParser.AlternationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code groups}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroups(RegExParser.GroupsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code concat}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcat(RegExParser.ConcatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plus}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlus(RegExParser.PlusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code kleene}
	 * labeled alternative in {@link RegExParser#regex()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKleene(RegExParser.KleeneContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simple_group}
	 * labeled alternative in {@link RegExParser#group()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_group(RegExParser.Simple_groupContext ctx);
	/**
	 * Visit a parse tree produced by the {@code non_capturing}
	 * labeled alternative in {@link RegExParser#group()}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNon_capturing(RegExParser.Non_capturingContext ctx);
}