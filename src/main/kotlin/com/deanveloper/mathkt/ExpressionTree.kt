package com.deanveloper.mathkt

import com.deanveloper.mathkt.value.RealValue

/**
 * @author Dean
 */
internal class ExpressionTree(val root: Node) {

    internal inner class Node(
            val exp1: Expression,
            val op: Operator,
            val exp2: Expression
    )

    internal class Expression(
            val op: Operator?,
            val num: RealValue?
    ) {
        constructor(op: Operator) : this(op, null)
        constructor(num: RealValue) : this(null, num)
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