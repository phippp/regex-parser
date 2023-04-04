import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.phippp.grammar.RegEx;
import org.phippp.logic.Acyclic;
import org.phippp.logic.Spanner;
import org.phippp.util.Optimizer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AcyclicTests {

    public static final String ACYCLIC = "(a+)b$1c", CYCLIC = "(a+)b$1c$1";

    @Test
    public void validateAcyclic() {
        RegEx r = ParseTests.parse(ACYCLIC);
        r = Optimizer.optimize(r, (byte) (Optimizer.CONCAT | Optimizer.SIMPLIFY));
        Spanner sp = Spanner.fromRegEx(r);
        Pair<Boolean, Acyclic.Graph<Integer>> pair = Acyclic.bracket(sp);
        assertTrue(pair.getLeft());
    }

    @Test
    public void validateCyclic() {
        RegEx r = ParseTests.parse(CYCLIC);
        r = Optimizer.optimize(r, (byte) (Optimizer.CONCAT | Optimizer.SIMPLIFY));
        Spanner sp = Spanner.fromRegEx(r);
        Pair<Boolean, Acyclic.Graph<Integer>> pair = Acyclic.bracket(sp);
        assertFalse(pair.getLeft());
    }

}
