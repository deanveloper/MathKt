package com.deanveloper.derivativekt.expression.twopartexpression

import com.deanveloper.derivativekt.expression.Expression
import com.deanveloper.derivativekt.expression.Value
import java.math.BigDecimal

class AdditionExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): AdditionExpression {
        return AdditionExpression(vars, f.execute(args), g.execute(args))
    }

    override fun derive(variable: Char): Expression {
        return AdditionExpression(vars, f.derive(variable), g.derive(variable))
    }

    override fun simplify(): Expression {
        val simp = AdditionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is Value && g is Value) {
                return Value(f.value + g.value)
            }

            if (f is Value) {
                if (f.isNegative) {
                    return SubtractionExpression(vars, g, f)
                }
                if (f.value.signum() === 0) {
                    return g
                }
            }

            if (g is Value) {
                if (g.isNegative) {
                    return SubtractionExpression(vars, f, g)
                }
                if (g.value.signum() === 0) {
                    return f
                }
            }

            //TODO: cx + dx = (c+d)x (only perform if c+d makes a nice number)
            return this
        }
    }

    override fun unaryMinus(): Expression {
        return AdditionExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f + $g)"
}