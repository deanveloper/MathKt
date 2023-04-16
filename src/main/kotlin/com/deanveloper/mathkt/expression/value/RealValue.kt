package com.deanveloper.mathkt.expression.value

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.rational.IntValue
import java.math.BigDecimal

abstract class RealValue(isNegative: Boolean) : Expression(charArrayOf(), isNegative) {
    abstract val approx: BigDecimal

    override abstract fun simplify(): RealValue
    override abstract fun unaryMinus(): RealValue
    override abstract fun toString(): String
    override abstract fun hashCode(): Int
    override abstract fun equals(other: Any?): Boolean
    abstract fun floor(): IntValue
    abstract fun onPlus(o: RealValue): RealValue
    abstract fun onTimes(o: RealValue): RealValue
    abstract fun onPow(o: RealValue): RealValue

    override final fun insertValues(args: Map<Char, Expression>) = this
    override final fun derive(variable: Char) = IntValue[0]

    override operator fun times(e: Expression): Expression {
        return if (e is RealValue) onTimes(e) else super.times(e)
    }

    override operator fun plus(e: Expression): Expression {
        return if (e is RealValue) onPlus(e) else super.plus(e)
    }

    override operator fun minus(e: Expression): Expression {
        // Create a SubtractionExpression and simplify it to avoid recursion
        return super.minus(e).simplify()
    }

    override operator fun div(e: Expression): Expression {
        // Create a DivisionExpression and simplify it to avoid recursion
        return super.div(e).simplify()
    }

    override infix fun pow(e: Expression): Expression {
        return if (e is RealValue) onPow(e) else super.pow(e)
    }

    override infix fun root(e: Expression): Expression {
        return super.root(e).simplify()
    }

    operator fun mod(o: RealValue): RealValue = (this - ((this / o) as RealValue).floor() * o) as RealValue
}