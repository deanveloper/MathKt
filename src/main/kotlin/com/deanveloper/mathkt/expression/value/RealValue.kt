package com.deanveloper.mathkt.expression.value

import com.deanveloper.mathkt.expression.Expression
import java.math.BigInteger

abstract class RealValue(isNegative: Boolean) : Expression(charArrayOf(), isNegative) {

    override final fun execute(args: Map<Char, Expression>) = this

    override final fun derive(variable: Char) = IntValue[0]

    abstract fun floor(): IntValue

    override abstract fun simplify(): RealValue
    override abstract fun unaryMinus(): RealValue
    override abstract fun toString(): String
    override abstract fun hashCode(): Int
    override abstract fun equals(other: Any?): Boolean

    abstract fun onPlus(o: RealValue): RealValue
    abstract fun onMinus(o: RealValue): RealValue
    abstract fun onTimes(o: RealValue): RealValue
    abstract fun onDiv(o: RealValue): RealValue
    abstract fun onPow(o: RealValue): RealValue

    override operator fun times(e: Expression): Expression {
        return if (e is RealValue) onTimes(e) else super.times(e)
    }

    override operator fun plus(e: Expression): Expression {
        return if (e is RealValue) onPlus(e) else super.plus(e)
    }

    override operator fun div(e: Expression): Expression {
        return if (e is RealValue) onDiv(e) else super.div(e)
    }

    override operator fun minus(e: Expression): Expression {
        return if (e is RealValue) onTimes(e) else super.times(e)
    }

    override infix fun pow(e: Expression): Expression {
        return if (e is RealValue) onPow(e) else super.times(e)
    }

    operator fun mod(o: RealValue): RealValue = this.onMinus(this.onDiv(o)).floor().onTimes(o)
}

val Byte.toValue: IntValue
    get() = IntValue[this]

val Short.toValue: IntValue
    get() = IntValue[this]

val Int.toValue: IntValue
    get() = IntValue[this]

val Long.toValue: IntValue
    get() = IntValue[this]

val BigInteger.toValue: IntValue
    get() = IntValue[this]