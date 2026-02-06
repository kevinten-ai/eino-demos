package com.brag.agentscope.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MathTools工具的单元测试
 */
public class MathToolsTest {

    private MathTools mathTools;

    @BeforeEach
    void setUp() {
        mathTools = new MathTools();
    }

    @Test
    void testFactorial() {
        assertEquals(1, mathTools.factorial(0));
        assertEquals(1, mathTools.factorial(1));
        assertEquals(2, mathTools.factorial(2));
        assertEquals(6, mathTools.factorial(3));
        assertEquals(24, mathTools.factorial(4));
        assertEquals(120, mathTools.factorial(5));

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.factorial(-1));

        // 测试大数字（应该抛出异常）
        assertThrows(IllegalArgumentException.class, () -> mathTools.factorial(25));
    }

    @Test
    void testIsPrime() {
        // 质数测试
        assertTrue(mathTools.isPrime(2));
        assertTrue(mathTools.isPrime(3));
        assertTrue(mathTools.isPrime(5));
        assertTrue(mathTools.isPrime(7));
        assertTrue(mathTools.isPrime(11));
        assertTrue(mathTools.isPrime(13));

        // 非质数测试
        assertFalse(mathTools.isPrime(0));
        assertFalse(mathTools.isPrime(1));
        assertFalse(mathTools.isPrime(4));
        assertFalse(mathTools.isPrime(6));
        assertFalse(mathTools.isPrime(8));
        assertFalse(mathTools.isPrime(9));
        assertFalse(mathTools.isPrime(10));
    }

    @Test
    void testFibonacci() {
        assertEquals(0, mathTools.fibonacci(0));
        assertEquals(1, mathTools.fibonacci(1));
        assertEquals(1, mathTools.fibonacci(2));
        assertEquals(2, mathTools.fibonacci(3));
        assertEquals(3, mathTools.fibonacci(4));
        assertEquals(5, mathTools.fibonacci(5));
        assertEquals(8, mathTools.fibonacci(6));
        assertEquals(13, mathTools.fibonacci(7));

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.fibonacci(-1));

        // 测试大数字（应该抛出异常）
        assertThrows(IllegalArgumentException.class, () -> mathTools.fibonacci(55));
    }

    @Test
    void testGcd() {
        assertEquals(1, mathTools.gcd(2, 3));
        assertEquals(2, mathTools.gcd(4, 6));
        assertEquals(3, mathTools.gcd(9, 12));
        assertEquals(5, mathTools.gcd(10, 15));
        assertEquals(7, mathTools.gcd(14, 21));
        assertEquals(12, mathTools.gcd(24, 36));

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.gcd(0, 5));
        assertThrows(IllegalArgumentException.class, () -> mathTools.gcd(5, 0));
    }

    @Test
    void testLcm() {
        assertEquals(6, mathTools.lcm(2, 3));
        assertEquals(12, mathTools.lcm(4, 6));
        assertEquals(36, mathTools.lcm(9, 12));
        assertEquals(30, mathTools.lcm(10, 15));
        assertEquals(42, mathTools.lcm(14, 21));
        assertEquals(24, mathTools.lcm(8, 12));

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.lcm(0, 5));
        assertThrows(IllegalArgumentException.class, () -> mathTools.lcm(5, 0));
    }

    @Test
    void testHarmonicSum() {
        assertEquals(1.0, mathTools.harmonicSum(1), 0.001);
        assertEquals(1.5, mathTools.harmonicSum(2), 0.001);
        assertEquals(1.833, mathTools.harmonicSum(3), 0.001);
        assertEquals(2.083, mathTools.harmonicSum(4), 0.001);

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.harmonicSum(0));
        assertThrows(IllegalArgumentException.class, () -> mathTools.harmonicSum(-1));
        assertThrows(IllegalArgumentException.class, () -> mathTools.harmonicSum(10001));
    }

    @Test
    void testPermutation() {
        assertEquals(6, mathTools.permutation(3, 2)); // P(3,2) = 6
        assertEquals(24, mathTools.permutation(4, 3)); // P(4,3) = 24
        assertEquals(1, mathTools.permutation(5, 0)); // P(5,0) = 1
        assertEquals(120, mathTools.permutation(5, 5)); // P(5,5) = 120

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.permutation(2, 3));
        assertThrows(IllegalArgumentException.class, () -> mathTools.permutation(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> mathTools.permutation(25, 2));
    }

    @Test
    void testCombination() {
        assertEquals(1, mathTools.combination(3, 0)); // C(3,0) = 1
        assertEquals(3, mathTools.combination(3, 1)); // C(3,1) = 3
        assertEquals(3, mathTools.combination(3, 2)); // C(3,2) = 3
        assertEquals(1, mathTools.combination(3, 3)); // C(3,3) = 1
        assertEquals(10, mathTools.combination(5, 2)); // C(5,2) = 10
        assertEquals(6, mathTools.combination(4, 2)); // C(4,2) = 6

        // 测试无效输入
        assertThrows(IllegalArgumentException.class, () -> mathTools.combination(2, 3));
        assertThrows(IllegalArgumentException.class, () -> mathTools.combination(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> mathTools.combination(25, 2));
    }
}



