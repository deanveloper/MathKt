package com.deanveloper.mathkt.expression

import java.math.BigDecimal

class Variable(val variable: Char, negative: Boolean = false) : Expression(charArrayOf(variable), negative) {
    override fun execute(args: Map<Char, Expression>) = args[variable]?.execute(args) ?: this
    override fun derive(variable: Char): Expression {
        if(variable === this.variable) {
            return Value(BigDecimal.ONE)
        } else {
            throw IllegalArgumentException("Cannot take derivative of $this with respect to $variable, " +
                    "define $this")
        }
    }
    override fun simplify() = this // do nothing
    override fun toString() = "${if (isNegative) "-" else ""}$variable"
    override fun hashCode() = variable.hashCode()
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
}