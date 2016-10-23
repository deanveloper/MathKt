package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

/**
 * Represents anything that returns a value.
 *
 * @author Dean
 * @since 1.0
 */
abstract class Expression(val vars: CharArray, val isNegative: Boolean = false) {

    operator fun invoke(vararg args: Expression): BigDecimal {
        require(args.size === vars.size) { "This expression takes ${vars.size} arguments, not ${args.size}" }

        return execute(vars.zip(args).toMap())
    }

    internal abstract fun execute(args: Map<Char, Expression>): BigDecimal

    abstract fun derive(): Expression

    abstract class TwoPartExpression(
            variables: CharArray,
            val f: Expression,
            val g: Expression,
            negative: Boolean = false)
    : Expression(variables, negative)

    abstract fun simplify(): Expression

    abstract operator fun unaryMinus(): Expression
}

