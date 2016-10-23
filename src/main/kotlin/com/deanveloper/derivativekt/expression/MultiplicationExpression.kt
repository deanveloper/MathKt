package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

class MultiplicationExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) * g.execute(args)
    }

    override fun derive(): Expression {
        // (f * g)' == f * g' + f' * g
        return AdditionExpression(vars,
                MultiplicationExpression(vars, f.derive(), g),
                MultiplicationExpression(vars, f, g.derive())
        )
    }

    override fun simplify(): Expression {
        val simp = MultiplicationExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is Value && g is Value) {
                return Value(f.value * g.value)
            }
            if (f is Value) {
                if (f.value === BigDecimal.ONE) { // 1 * g == g
                    return g
                } else if (f.value === BigDecimal.ZERO) { // 0 * g == 0
                    return f // value 0
                }
            }
            if (g is Value) {
                if (g.value === BigDecimal.ONE) { // f * 1 == f
                    return f
                } else if (g.value === BigDecimal.ZERO) { // f * 0 == 0
                    return g // value 0
                }
            }
            if (f is DivisionExpression) {
                return DivisionExpression(vars, MultiplicationExpression(vars, g, f.f).simplify(), f.g)
            }
            if (g is DivisionExpression) {
                return DivisionExpression(vars, MultiplicationExpression(vars, f, g.f).simplify(), g.g)
            }

            //TODO: x^c * x^d == x^(c+d)

            return this
        }
    }

    override operator fun unaryMinus(): Expression {
        return MultiplicationExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f * $g)"
}