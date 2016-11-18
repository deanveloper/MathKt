package com.deanveloper.mathkt

import com.deanveloper.mathkt.value.RealValue

/**
 * @author Dean
 */
internal class ExpressionTree(private val root: Node) {

    internal inner class Node(
            val exp1: Node,
            val op: Operator,
            val exp2: Node
    )
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