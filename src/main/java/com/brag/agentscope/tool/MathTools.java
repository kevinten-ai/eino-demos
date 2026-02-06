package com.brag.agentscope.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.stereotype.Service;

/**
 * 高级数学工具类
 * 提供复杂的数学运算功能
 */
@Service
public class MathTools {

    /**
     * 计算阶乘
     */
    @Tool(description = "计算正整数的阶乘（n!）")
    public long factorial(
            @ToolParam(name = "n", description = "要求阶乘的正整数，最大支持20") int n) {
        if (n < 0) {
            throw new IllegalArgumentException("阶乘不能为负数");
        }
        if (n > 20) {
            throw new IllegalArgumentException("数字过大，可能导致溢出，最大支持20");
        }

        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * 判断是否为质数
     */
    @Tool(description = "判断一个正整数是否为质数")
    public boolean isPrime(
            @ToolParam(name = "number", description = "要判断的正整数") int number) {
        if (number <= 1) {
            return false;
        }
        if (number <= 3) {
            return true;
        }
        if (number % 2 == 0 || number % 3 == 0) {
            return false;
        }

        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算斐波那契数列的第n项
     */
    @Tool(description = "计算斐波那契数列的第n项")
    public long fibonacci(
            @ToolParam(name = "n", description = "斐波那契数列的位置（从0开始），最大支持50") int n) {
        if (n < 0) {
            throw new IllegalArgumentException("位置不能为负数");
        }
        if (n > 50) {
            throw new IllegalArgumentException("数字过大，可能导致溢出，最大支持50");
        }

        if (n == 0) return 0;
        if (n == 1) return 1;

        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    /**
     * 计算最大公约数
     */
    @Tool(description = "计算两个正整数的最大公约数")
    public int gcd(
            @ToolParam(name = "a", description = "第一个正整数") int a,
            @ToolParam(name = "b", description = "第二个正整数") int b) {
        if (a <= 0 || b <= 0) {
            throw new IllegalArgumentException("参数必须为正整数");
        }

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * 计算最小公倍数
     */
    @Tool(description = "计算两个正整数的最小公倍数")
    public long lcm(
            @ToolParam(name = "a", description = "第一个正整数") int a,
            @ToolParam(name = "b", description = "第二个正整数") int b) {
        if (a <= 0 || b <= 0) {
            throw new IllegalArgumentException("参数必须为正整数");
        }

        int gcd = gcd(a, b);
        return (long) a * b / gcd;
    }

    /**
     * 计算调和级数的前n项和
     */
    @Tool(description = "计算调和级数1 + 1/2 + 1/3 + ... + 1/n的前n项和")
    public double harmonicSum(
            @ToolParam(name = "n", description = "项数，最大支持10000") int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("项数必须为正整数");
        }
        if (n > 10000) {
            throw new IllegalArgumentException("项数过大，最大支持10000");
        }

        double sum = 0.0;
        for (int i = 1; i <= n; i++) {
            sum += 1.0 / i;
        }
        return sum;
    }

    /**
     * 计算排列数 P(n, k)
     */
    @Tool(description = "计算排列数P(n, k) = n! / (n-k)!")
    public long permutation(
            @ToolParam(name = "n", description = "总元素数") int n,
            @ToolParam(name = "k", description = "选择的元素数") int k) {
        if (n < 0 || k < 0 || k > n) {
            throw new IllegalArgumentException("参数无效：要求 n >= k >= 0");
        }
        if (n > 20) {
            throw new IllegalArgumentException("数字过大，最大支持20");
        }

        long result = 1;
        for (int i = 0; i < k; i++) {
            result *= (n - i);
        }
        return result;
    }

    /**
     * 计算组合数 C(n, k)
     */
    @Tool(description = "计算组合数C(n, k) = n! / (k! * (n-k)! )")
    public long combination(
            @ToolParam(name = "n", description = "总元素数") int n,
            @ToolParam(name = "k", description = "选择的元素数") int k) {
        if (n < 0 || k < 0 || k > n) {
            throw new IllegalArgumentException("参数无效：要求 n >= k >= 0");
        }
        if (n > 20) {
            throw new IllegalArgumentException("数字过大，最大支持20");
        }

        // 使用对称性减少计算
        if (k > n - k) {
            k = n - k;
        }

        long result = 1;
        for (int i = 0; i < k; i++) {
            result *= (n - i);
            result /= (i + 1);
        }
        return result;
    }
}



