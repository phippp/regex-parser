import org.junit.jupiter.api.Test;
import org.phippp.grammar.RegEx;
import org.phippp.util.Optimizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimizationTests {

    @Test
    public void optimizationGroups() {
        String str = "((?:(a)))";
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r,  (byte) 0);
        assertEquals(1, r.traverse().size());
    }

    @Test
    public void optimizationConcat() {
        String str = "qwerty";
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, Optimizer.CONCAT);
        assertEquals(1, r.traverse().size());
    }

    @Test
    public void optimizationSimplify1() {
        String str = "(a)+";
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, Optimizer.SIMPLIFY);
        assertEquals(1, r.traverse().size());
    }

    @Test
    public void optimizationSimplify2() {
        String str = "(a+)+";
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, Optimizer.SIMPLIFY);
        assertEquals(2, r.traverse().size());
    }

    @Test
    public void optimizationSimplify() {
        String str = "(a)|(b)";

    }

    @Test
    public void optimizationAll1() {
        String str = "(((abc)))|b";
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, (byte) (Optimizer.SIMPLIFY | Optimizer.CONCAT));
        assertEquals(3, r.traverse().size());
    }

    @Test
    public void optimizationAll2() {
        String str = "aa?";
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, (byte) (Optimizer.SIMPLIFY | Optimizer.CONCAT));
        assertEquals(3, r.traverse().size());
    }

}
