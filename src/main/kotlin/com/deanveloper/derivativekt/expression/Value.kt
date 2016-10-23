package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

class Value(val value: BigDecimal) : Expression(charArrayOf(), value.signum() === -1) {
    override fun execute(args: Map<Char, Expression>) = this
    override fun derive(variable: Char) = Value(BigDecimal.ZERO)
    override fun simplify(): Value = this // do nothing
    override fun toString() = value.toPlainString()!!
    override fun hashCode() = value.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other is Value) {
            return value.compareTo(other.value) === 0
        }

        return false
    }

    override fun unaryMinus(): Expression {
        return Value(-value)
    }
}