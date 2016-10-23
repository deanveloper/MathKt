package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

class Variable(val variable: Char, negative: Boolean = false) : Expression(charArrayOf(variable), negative) {
    override fun execute(args: Map<Char, Expression>) = args[variable]!!.execute(emptyMap())
    override fun derive() = Value(BigDecimal.ONE)
    override fun simplify() = this // do nothing
    override fun toString() = "${if (isNegative) "-" else ""}$variable"
    override fun equals(other: Any?): Boolean {
        if (other is Variable) {
            if (other.variable === variable && other.isNegative === isNegative) {
                return true
            }
        }

        return false
    }

    override fun unaryMinus(): Expression {
        return Variable(variable, !isNegative)
    }

    override fun hashCode(): Int {
        return variable.hashCode()
    }
}