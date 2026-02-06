package com.brag.agentscope.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Calculator工具的单元测试
 */
public class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    void testAdd() {
        assertEquals(5, calculator.add(2, 3));
        assertEquals(0, calculator.add(-2, 2));
        assertEquals(-5, calculator.add(-2, -3));
        assertEquals(100, calculator.add(50, 50));
    }

    @Test
    void testSubtract() {
        assertEquals(1, calculator.subtract(3, 2));
        assertEquals(-1, calculator.subtract(2, 3));
        assertEquals(0, calculator.subtract(5, 5));
        assertEquals(-10, calculator.subtract(-5, 5));
    }

    @Test
    void testMultiply() {
        assertEquals(6L, calculator.multiply(2, 3));
        assertEquals(-6L, calculator.multiply(2, -3));
        assertEquals(0L, calculator.multiply(0, 100));
        assertEquals(25L, calculator.multiply(5, 5));
    }

    @Test
    void testDivide() {
        assertEquals(2, calculator.divide(6, 3));
        assertEquals(3, calculator.divide(9, 3));
        assertEquals(-2, calculator.divide(-6, 3));

        // 测试除数为0的情况
        assertThrows(IllegalArgumentException.class, () -> calculator.divide(10, 0));
    }

    @Test
    void testDivideDouble() {
        assertEquals(2.5, calculator.divideDouble(5, 2), 0.001);
        assertEquals(3.333, calculator.divideDouble(10, 3), 0.001);
        assertEquals(-2.0, calculator.divideDouble(-6, 3), 0.001);

        // 测试除数为0的情况
        assertThrows(IllegalArgumentException.class, () -> calculator.divideDouble(10, 0));
    }

    @Test
    void testPower() {
        assertEquals(8.0, calculator.power(2, 3), 0.001);
        assertEquals(1.0, calculator.power(5, 0), 0.001);
        assertEquals(0.25, calculator.power(2, -2), 0.001);
        assertEquals(9.0, calculator.power(3, 2), 0.001);
    }

    @Test
    void testSquareRoot() {
        assertEquals(3.0, calculator.squareRoot(9), 0.001);
        assertEquals(5.0, calculator.squareRoot(25), 0.001);
        assertEquals(1.414, calculator.squareRoot(2), 0.001);

        // 测试负数平方根
        assertThrows(IllegalArgumentException.class, () -> calculator.squareRoot(-1));
    }

    @Test
    void testAbsolute() {
        assertEquals(5, calculator.absolute(5));
        assertEquals(5, calculator.absolute(-5));
        assertEquals(0, calculator.absolute(0));
        assertEquals(100, calculator.absolute(-100));
    }
}



