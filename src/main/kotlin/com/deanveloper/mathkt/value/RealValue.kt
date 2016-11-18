package com.deanveloper.mathkt.value

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.value.rational.IntValue
import java.math.BigDecimal

abstract class RealValue(isNegative: Boolean) {
    abstract val approx: BigDecimal

    abstract fun simplify(): RealValue
    abstract fun unaryMinus(): RealValue
    override abstract fun toString(): String
    override abstract fun hashCode(): Int
    override abstract fun equals(other: Any?): Boolean
    abstract fun floor(): IntValue
    abstract fun onPlus(o: RealValue): RealValue
    abstract fun onTimes(o: RealValue): RealValue
    abstract fun onPow(o: RealValue): RealValue

    final fun derive(variable: Char) = IntValue[0]

    operator fun times(e: Expression): Expression {
        return if (e is RealValue) onTimes(e) else super.times(e)
    }

    operator fun plus(e: Expression): Expression {
        return if (e is RealValue) onPlus(e) else super.plus(e)
    }

    operator fun minus(e: RealValue): Expression {
        // Create a SubtractionExpression and simplify it to avoid recursion
        return minus(e).simplify()
    }

    operator fun div(e: Expression): Expression {
        // Create a DivisionExpression and simplify it to avoid recursion
        return div(e).simplify()
    }

    infix fun pow(e: Expression): Expression {
        return if (e is RealValue) onPow(e) else pow(e)
    }

    infix fun root(e: Expression): Expression {
        return root(e).simplify()
    }

    operator fun mod(o: RealValue): RealValue = (this - ((this / o) as RealValue).floor() * o) as RealValue
}