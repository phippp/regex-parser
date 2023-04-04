import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.phippp.antlr4.RegExLexer;
import org.phippp.antlr4.RegExParser;
import org.phippp.grammar.RegEx;
import org.phippp.visitors.ObjectVisitor;

public class ParseTests {

    @ParameterizedTest
    @ValueSource(strings = {"abc", "(a)$1", "(a|b)c", "(a+|b+)c", "(a+)b$1c", "(a+)b$1c$1"})
    void parseString(String str){
        parse(str);
    }

    public static RegEx parse(String str){
        CharStream stream = CharStreams.fromString(str);
        RegExLexer lexer = new RegExLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RegExParser parser = new RegExParser(tokens);
        ParseTree tree = parser.regex();
        return new ObjectVisitor().visit(tree);
    }

}
