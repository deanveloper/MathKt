package com.deanveloper.mathkt.expression

import com.deanveloper.mathkt.expression.twopartexpression.AdditionExpression
import com.deanveloper.mathkt.expression.twopartexpression.DivisionExpression
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.mathkt.expression.twopartexpression.SubtractionExpression
import java.util.*

/**
 * Represents anything that returns a value.
 *
 * @author Dean
 * @since 1.0
 */
abstract class Expression(vars: CharArray, val isNegative: Boolean = false) {
    val vars: CharArray

    init {
        this.vars = vars.distinct().toCharArray()
    }

    operator fun invoke(vararg args: Expression): Expression {
        return execute(vars.zip(args).toMap())
    }

    abstract fun execute(args: Map<Char, Expression>): Expression

    abstract fun derive(variable: Char): Expression

    abstract fun simplify(): Expression

    abstract operator fun unaryMinus(): Expression

    open operator fun plus(e: Expression): Expression = AdditionExpression(this.vars + e.vars, this, e)

    open operator fun minus(e: Expression): Expression = SubtractionExpression(this.vars + e.vars, this, e)

    open operator fun times(e: Expression): Expression = MultiplicationExpression(this.vars + e.vars, this, e)

    open operator fun div(e: Expression): Expression = DivisionExpression(this.vars + e.vars, this, e)

    abstract class TwoPartExpression(
            variables: CharArray,
            val f: Expression,
            val g: Expression,
            isNegative: Boolean = false
    ) : Expression(variables, isNegative) {
        private val hashCode = f.hashCode() xor g.hashCode() xor this.javaClass.name.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other is TwoPartExpression && this.javaClass.name == other.javaClass.name) {
                return Arrays.equals(vars, other.vars) && f == other.f && g == other.g && isNegative == other.isNegative
            }

            return false
        }

        override fun hashCode(): Int {
            return hashCode
        }
    }

    abstract class TrigExpression(
            variables: CharArray,
            val f: Expression,
            isNegative: Boolean = false
    ) : Expression(variables, isNegative) {
        private val hashCode = f.hashCode() xor this.javaClass.name.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other is TrigExpression && this.javaClass.name == other.javaClass.name) {
                return Arrays.equals(vars, other.vars) && f == other.f && isNegative == other.isNegative
            }

            return false
        }

        override fun hashCode(): Int {
            return hashCode
        }
    }
}

