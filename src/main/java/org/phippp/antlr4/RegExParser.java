package org.phippp.antlr4;// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class RegExParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, OR=5, PLUS=6, NUMBER=7, CHAR=8, WS=9;
	public static final int
		RULE_regex = 0, RULE_group = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"regex", "group"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'$'", "'('", "')'", "'(?:'", "'|'", "'+'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "OR", "PLUS", "NUMBER", "CHAR", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RegExParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RegexContext extends ParserRuleContext {
		public RegexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_regex; }
	 
		public RegexContext() { }
		public void copyFrom(RegexContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReferenceContext extends RegexContext {
		public RegexContext left;
		public Token ref;
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(RegExParser.NUMBER, 0); }
		public ReferenceContext(RegexContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitReference(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CharacterContext extends RegexContext {
		public TerminalNode CHAR() { return getToken(RegExParser.CHAR, 0); }
		public CharacterContext(RegexContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitCharacter(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AlternationContext extends RegexContext {
		public RegexContext left;
		public RegexContext right;
		public TerminalNode OR() { return getToken(RegExParser.OR, 0); }
		public List<RegexContext> regex() {
			return getRuleContexts(RegexContext.class);
		}
		public RegexContext regex(int i) {
			return getRuleContext(RegexContext.class,i);
		}
		public AlternationContext(RegexContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitAlternation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GroupsContext extends RegexContext {
		public GroupContext group() {
			return getRuleContext(GroupContext.class,0);
		}
		public GroupsContext(RegexContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitGroups(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConcatContext extends RegexContext {
		public RegexContext left;
		public RegexContext right;
		public List<RegexContext> regex() {
			return getRuleContexts(RegexContext.class);
		}
		public RegexContext regex(int i) {
			return getRuleContext(RegexContext.class,i);
		}
		public ConcatContext(RegexContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitConcat(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlusContext extends RegexContext {
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(RegExParser.PLUS, 0); }
		public PlusContext(RegexContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitPlus(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RegexContext regex() throws RecognitionException {
		return regex(0);
	}

	private RegexContext regex(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RegexContext _localctx = new RegexContext(_ctx, _parentState);
		RegexContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_regex, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR:
				{
				_localctx = new CharacterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(5);
				match(CHAR);
				}
				break;
			case T__1:
			case T__3:
				{
				_localctx = new GroupsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(6);
				group();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(21);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(19);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new ConcatContext(new RegexContext(_parentctx, _parentState));
						((ConcatContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_regex);
						setState(9);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(10);
						((ConcatContext)_localctx).right = regex(4);
						}
						break;
					case 2:
						{
						_localctx = new AlternationContext(new RegexContext(_parentctx, _parentState));
						((AlternationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_regex);
						setState(11);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(12);
						match(OR);
						setState(13);
						((AlternationContext)_localctx).right = regex(3);
						}
						break;
					case 3:
						{
						_localctx = new PlusContext(new RegexContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_regex);
						setState(14);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(15);
						match(PLUS);
						}
						break;
					case 4:
						{
						_localctx = new ReferenceContext(new RegexContext(_parentctx, _parentState));
						((ReferenceContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_regex);
						setState(16);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(17);
						match(T__0);
						setState(18);
						((ReferenceContext)_localctx).ref = match(NUMBER);
						}
						break;
					}
					} 
				}
				setState(23);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GroupContext extends ParserRuleContext {
		public GroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group; }
	 
		public GroupContext() { }
		public void copyFrom(GroupContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SimpleGroupContext extends GroupContext {
		public RegexContext inner;
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public SimpleGroupContext(GroupContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitSimpleGroup(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NonCapturingContext extends GroupContext {
		public RegexContext inner;
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public NonCapturingContext(GroupContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RegExVisitor) return ((RegExVisitor<? extends T>)visitor).visitNonCapturing(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupContext group() throws RecognitionException {
		GroupContext _localctx = new GroupContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_group);
		try {
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new SimpleGroupContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(24);
				match(T__1);
				setState(25);
				((SimpleGroupContext)_localctx).inner = regex(0);
				setState(26);
				match(T__2);
				}
				break;
			case T__3:
				_localctx = new NonCapturingContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(28);
				match(T__3);
				setState(29);
				((NonCapturingContext)_localctx).inner = regex(0);
				setState(30);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return regex_sempred((RegexContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean regex_sempred(RegexContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\t#\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0003\u0000\b\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u0014\b\u0000\n\u0000\f\u0000"+
		"\u0017\t\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001!\b\u0001\u0001\u0001"+
		"\u0000\u0001\u0000\u0002\u0000\u0002\u0000\u0000&\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0002 \u0001\u0000\u0000\u0000\u0004\u0005\u0006\u0000\uffff"+
		"\uffff\u0000\u0005\b\u0005\b\u0000\u0000\u0006\b\u0003\u0002\u0001\u0000"+
		"\u0007\u0004\u0001\u0000\u0000\u0000\u0007\u0006\u0001\u0000\u0000\u0000"+
		"\b\u0015\u0001\u0000\u0000\u0000\t\n\n\u0003\u0000\u0000\n\u0014\u0003"+
		"\u0000\u0000\u0004\u000b\f\n\u0002\u0000\u0000\f\r\u0005\u0005\u0000\u0000"+
		"\r\u0014\u0003\u0000\u0000\u0003\u000e\u000f\n\u0004\u0000\u0000\u000f"+
		"\u0014\u0005\u0006\u0000\u0000\u0010\u0011\n\u0001\u0000\u0000\u0011\u0012"+
		"\u0005\u0001\u0000\u0000\u0012\u0014\u0005\u0007\u0000\u0000\u0013\t\u0001"+
		"\u0000\u0000\u0000\u0013\u000b\u0001\u0000\u0000\u0000\u0013\u000e\u0001"+
		"\u0000\u0000\u0000\u0013\u0010\u0001\u0000\u0000\u0000\u0014\u0017\u0001"+
		"\u0000\u0000\u0000\u0015\u0013\u0001\u0000\u0000\u0000\u0015\u0016\u0001"+
		"\u0000\u0000\u0000\u0016\u0001\u0001\u0000\u0000\u0000\u0017\u0015\u0001"+
		"\u0000\u0000\u0000\u0018\u0019\u0005\u0002\u0000\u0000\u0019\u001a\u0003"+
		"\u0000\u0000\u0000\u001a\u001b\u0005\u0003\u0000\u0000\u001b!\u0001\u0000"+
		"\u0000\u0000\u001c\u001d\u0005\u0004\u0000\u0000\u001d\u001e\u0003\u0000"+
		"\u0000\u0000\u001e\u001f\u0005\u0003\u0000\u0000\u001f!\u0001\u0000\u0000"+
		"\u0000 \u0018\u0001\u0000\u0000\u0000 \u001c\u0001\u0000\u0000\u0000!"+
		"\u0003\u0001\u0000\u0000\u0000\u0004\u0007\u0013\u0015 ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}