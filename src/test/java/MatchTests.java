import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.phippp.app.Args;
import org.phippp.grammar.RegEx;
import org.phippp.logic.Acyclic;
import org.phippp.logic.BruteForce;
import org.phippp.logic.ConjunctiveTree;
import org.phippp.logic.Spanner;
import org.phippp.util.BinaryTree;
import org.phippp.util.Optimizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchTests {

    private static final Args ARGS = new Args();

    @ParameterizedTest
    @CsvSource({
            "a+, , false", "a+, aaaaaaaaaa, true",
            "(a)+, a, true", "(a)+, , false",
            "a?, a, true", "a?, , true", "a?, aa, false",
            "a|b, a, true", "a|b, b, true", "a|b, ab, false", "a|b, c, false", "a|b, , false",
            "a+|b+, a, true", "a+|b+, bbbb, true", "a+|b+, , false",
            "a?|b+, a, true", "a?|b+, bbbb, true", "a?|b+, , true",
            "(a+)?, a, true", "(a+)?, aaaaaa, true", "(a+)?, , true", "(a+)?, b, false",
    })
    public void testParts(String str, String query, boolean expected) {
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, (byte) (Optimizer.SIMPLIFY));
        if(query == null) query = "";
        boolean match = r.match(query);
        assertEquals(expected, match);
    }

    @ParameterizedTest
    @CsvSource({
            "(a+)b$1c, abac, true", "(a+)b$1c, aaaabaaaac, true", "(a+)b$1c, bac, false",
            "aa?aa?aa?, aaaa, true", "aa?aa?aa?, aaa, true", "aa?aa?aa?, aa, false", "aa?aa?aa?, aaaaaaa, false",
            "(a+|b+)+, aaaa, true", "(a+|b+)+, abab, false", "(a+|b+)+, , false",
    })
    public void testMatches(String str, String query, boolean expected) {
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, (byte) (Optimizer.CONCAT | Optimizer.SIMPLIFY));
        Spanner sp = Spanner.fromRegEx(r);
        Pair<Boolean, Acyclic.Graph<Integer>> acyclic = Acyclic.bracket(sp);
        BinaryTree<Spanner> bTree = Acyclic.tree(sp, acyclic.getRight(), ARGS);
        ConjunctiveTree tree = ConjunctiveTree.fromTree(bTree);
        if(query == null) query = "";
        boolean matches = BruteForce.testString(query, tree, ARGS);
        assertEquals(expected, matches);
    }
}
