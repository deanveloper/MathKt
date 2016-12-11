package com.deanveloper.mathkt

import com.deanveloper.mathkt.value.RealValue

/**
 * @author Dean
 */
internal class ExpressionTree(private val root: Node) {

    internal inner class Node(
            val exp1: Node?,
            val op: Operator?,
            val value: RealValue?,
            val exp2: Node?
    ) {
        val isValue = value != null
        val isOperator = op != null
        
        
    }
}

internal enum class Operator {
    VALUE,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    POWER,
    ROOT,
    LOG;
}