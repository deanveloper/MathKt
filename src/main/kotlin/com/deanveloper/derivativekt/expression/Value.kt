package com.deanveloper.derivativekt.expression

import java.math.BigDecimal
import java.math.BigInteger

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

val Byte.asExp: Value
    get() = Value(BigDecimal.valueOf(this.toLong()))

val Short.asExp: Value
    get() = Value(BigDecimal.valueOf(this.toLong()))

val Int.asExp: Value
    get() = Value(BigDecimal.valueOf(this.toLong()))

val Long.asExp: Value
    get() = Value(BigDecimal.valueOf(this))

val Float.asExp: Value
    get() = Value(BigDecimal.valueOf(this.toDouble()))

val Double.asExp: Value
    get() = Value(BigDecimal.valueOf(this))

val BigInteger.asExp: Value
    get() = Value(BigDecimal(this))

val BigDecimal.asExp: Value
    get() = Value(this)