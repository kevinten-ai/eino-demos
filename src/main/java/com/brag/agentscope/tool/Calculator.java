package com.brag.agentscope.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.stereotype.Service;

/**
 * 计算器工具类
 * 提供基础的数学运算功能
 */
@Service
public class Calculator {

    /**
     * 计算两个整数的和
     */
    @Tool(description = "计算两个整数的和")
    public int add(
            @ToolParam(name = "a", description = "第一个加数") int a,
            @ToolParam(name = "b", description = "第二个加数") int b) {
        return a + b;
    }

    /**
     * 计算两个整数的差
     */
    @Tool(description = "计算两个整数的差（第一个数减去第二个数）")
    public int subtract(
            @ToolParam(name = "a", description = "被减数") int a,
            @ToolParam(name = "b", description = "减数") int b) {
        return a - b;
    }

    /**
     * 计算两个整数的乘积
     */
    @Tool(description = "计算两个整数的乘积")
    public long multiply(
            @ToolParam(name = "a", description = "第一个乘数") long a,
            @ToolParam(name = "b", description = "第二个乘数") long b) {
        return a * b;
    }

    /**
     * 计算两个整数的商（整数除法）
     */
    @Tool(description = "计算两个整数的商（整数除法，结果向下取整）")
    public int divide(
            @ToolParam(name = "a", description = "被除数") int a,
            @ToolParam(name = "b", description = "除数，不能为0") int b) {
        if (b == 0) {
            throw new IllegalArgumentException("除数不能为0");
        }
        return a / b;
    }

    /**
     * 计算两个数的商（浮点数除法）
     */
    @Tool(description = "计算两个数的商（浮点数除法）")
    public double divideDouble(
            @ToolParam(name = "a", description = "被除数") double a,
            @ToolParam(name = "b", description = "除数，不能为0") double b) {
        if (b == 0.0) {
            throw new IllegalArgumentException("除数不能为0");
        }
        return a / b;
    }

    /**
     * 计算整数的幂
     */
    @Tool(description = "计算整数的幂（base的exponent次方）")
    public double power(
            @ToolParam(name = "base", description = "底数") double base,
            @ToolParam(name = "exponent", description = "指数") double exponent) {
        return Math.pow(base, exponent);
    }

    /**
     * 计算平方根
     */
    @Tool(description = "计算非负数的平方根")
    public double squareRoot(
            @ToolParam(name = "number", description = "要求平方根的非负数") double number) {
        if (number < 0) {
            throw new IllegalArgumentException("不能计算负数的平方根");
        }
        return Math.sqrt(number);
    }

    /**
     * 计算绝对值
     */
    @Tool(description = "计算整数的绝对值")
    public int absolute(
            @ToolParam(name = "number", description = "要求绝对值的整数") int number) {
        return Math.abs(number);
    }
}



