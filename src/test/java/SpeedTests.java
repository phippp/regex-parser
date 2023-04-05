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
import org.phippp.util.Logging;
import org.phippp.util.Optimizer;

import java.util.regex.Pattern;

public class SpeedTests {

    private static final Args ARGS = new Args();

    /**
     * This isn't strictly for 'testing', it is more useful for analysing
     * the performance of string matching in my model, this won't include
     * any image generation, so we will see the true performance.
     */

    @ParameterizedTest
    @CsvSource({
            "(a)+, aaaaaaaaaaaaa",
            "(a+)+, aaaaaaaaaaaaa",
            "(a+)$1, aaaaaaaaaaaa"
    })
    public void speedTest(String str, String query){
        long start = System.currentTimeMillis(), middle;
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, (byte) (Optimizer.SIMPLIFY | Optimizer.CONCAT));
        Spanner sp = Spanner.fromRegEx(r);
        Pair<Boolean, Acyclic.Graph<Integer>> acyclic = Acyclic.bracket(sp);
        BinaryTree<Spanner> bTree = Acyclic.tree(sp, acyclic.getRight(), ARGS);
        ConjunctiveTree tree = ConjunctiveTree.fromTree(bTree);
        middle = System.currentTimeMillis();
        boolean match = BruteForce.testString(query, tree, ARGS);
        Logging.log(String.format("Building took %dms. Matching took %dms. Output : %s", (middle - start), (System.currentTimeMillis() - middle), match), SpeedTests.class, ARGS);
        start = System.currentTimeMillis();
        Pattern p = Pattern.compile(String.format("^%s$", str.replace('$', '\\')));
        match = p.matcher(query).matches();
        Logging.log(String.format("Java matching took %dms", (System.currentTimeMillis() - start)), SpeedTests.class, ARGS);
    }
}
