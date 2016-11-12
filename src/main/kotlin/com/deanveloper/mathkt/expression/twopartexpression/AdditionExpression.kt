package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.IntValue
import com.deanveloper.mathkt.expression.value.RealValue
import java.math.BigDecimal

class AdditionExpression(
        variables: CharArray,
        f: Expression,
        g: Expression,
        isNegative: Boolean = false
) : Expression.TwoPartExpression(variables, f, g, isNegative) {
    constructor(
            variable: Char,
            f: Expression,
            g: Expression,
            isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, g, isNegative)

    override fun insertValues(args: Map<Char, Expression>): AdditionExpression {
        return AdditionExpression(vars, f.insertValues(args), g.insertValues(args))
    }

    override fun derive(variable: Char): Expression {
        return AdditionExpression(vars, f.derive(variable), g.derive(variable))
    }

    override fun simplify(): Expression {
        val simp = AdditionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is RealValue && g is RealValue) {
                return f + g
            }

            if (f is RealValue) {
                if (f.isNegative) {
                    return SubtractionExpression(vars, g, f)
                }
                if (f == IntValue[0]) {
                    return g
                }
            }

            if (g is RealValue) {
                if (g.isNegative) {
                    return SubtractionExpression(vars, f, g)
                }
                if (g == IntValue[0]) {
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