package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

/**
 * Represents anything that returns a value.
 *
 * @author Dean
 * @since 1.0
 */
abstract class Expression(val vars: CharArray, val isNegative: Boolean = false) {

    operator fun invoke(vararg args: Expression): Expression {
        return execute(vars.zip(args).toMap())
    }

    internal abstract fun execute(args: Map<Char, Expression>): Expression

    abstract fun derive(variable: Char): Expression

    abstract fun simplify(): Expression

    abstract operator fun unaryMinus(): Expression

    abstract class TwoPartExpression(
            variables: CharArray,
            val f: Expression,
            val g: Expression,
            isNegative: Boolean = false
    ) : Expression(variables, isNegative)

    abstract class TrigExpression(
            variables: CharArray,
            val f: Expression,
            isNegative: Boolean = false
    ) : Expression(variables, isNegative)
}

