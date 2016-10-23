package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

class SubtractionExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) - g.execute(args)
    }

    override fun derive(): Expression {
        return SubtractionExpression(vars, f.derive(), g.derive())
    }

    override fun simplify(): Expression {
        val simp = SubtractionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (g.isNegative) {
                return AdditionExpression(vars, f, -g).simplify()
            }
            if (f is Value && g is Value) {
                return Value(f.value - g.value)
            }
            if (f is Value) {
                if (f.value.signum() === 0) {
                    return -g
                }
            }

            if (g is Value) {
                if (g.value.signum() < 0) {
                    return AdditionExpression(vars, f, g)
                }
                if (g.value.signum() === 0) {
                    return f
                }
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return SubtractionExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f - $g)"
}