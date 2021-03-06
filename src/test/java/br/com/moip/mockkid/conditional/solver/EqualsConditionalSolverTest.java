package br.com.moip.mockkid.conditional.solver;

import br.com.moip.mockkid.conditional.solver.EqualsConditionalSolver;
import br.com.moip.mockkid.model.Conditional;
import br.com.moip.mockkid.model.ConditionalType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualsConditionalSolverTest {

    private EqualsConditionalSolver equalsConditionalExpression;

    @Before
    public void setUp() {
        equalsConditionalExpression = new EqualsConditionalSolver();
    }

    @Test
    public void shouldHandleOnlyTheContainsType() {
        assertEquals(equalsConditionalExpression.type(), ConditionalType.EQUALS);
    }

    @Test
    public void evalShouldReturnTrue() {
        Map<String, String> variables = of("myvar", "value");
        Conditional conditional = new Conditional(ConditionalType.EQUALS, "myvar", "value");

        assertTrue(equalsConditionalExpression.eval(conditional, variables));
    }

    @Test
    public void evalShouldReturnFalse() {
        Map<String, String> variables = of("myvar", "nope");
        Conditional conditional = new Conditional(ConditionalType.EQUALS, "myvar", "value");

        assertFalse(equalsConditionalExpression.eval(conditional, variables));
    }

    @Test
    public void evalShouldReturnFalseOnContains() {
        Map<String, String> variables = of("myvar", "thevalueishere");
        Conditional conditional = new Conditional(ConditionalType.EQUALS, "myvar", "value");

        assertFalse(equalsConditionalExpression.eval(conditional, variables));
    }

    @Test
    public void evalShouldReturnFalseOnUnknownVariable() {
        Map<String, String> variables = of("anothervar", "nope");
        Conditional conditional = new Conditional(ConditionalType.EQUALS, "myvar", "value");

        assertFalse(equalsConditionalExpression.eval(conditional, variables));
    }

}
