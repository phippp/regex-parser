import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({"qwerty, 1", "qwertyuiop, 1", "abcdef(abcdef), 1"})
    public void optimizationConcat(String str, int num) {
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, Optimizer.CONCAT);
        assertEquals(num, r.traverse().size());
    }

    @ParameterizedTest
    @CsvSource({"(a)+, 1", "(a+)+, 2"})
    public void optimizationSimplify(String str, int num) {
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, Optimizer.SIMPLIFY);
        assertEquals(num, r.traverse().size());
    }

    @ParameterizedTest
    @CsvSource({"(((abc)))|b, 3", "aa?, 3", "(a+)+, 2", "(a|b)+, 4"})
    public void optimizationAll(String str, int num) {
        RegEx r = ParseTests.parse(str);
        r = Optimizer.optimize(r, (byte) (Optimizer.SIMPLIFY | Optimizer.CONCAT));
        assertEquals(num, r.traverse().size());
    }
}
