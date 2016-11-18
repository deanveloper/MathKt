package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.value.rational.IntValue
import com.deanveloper.mathkt.value.RealValue
import java.math.BigDecimal

class SubtractionExpression(
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

    override fun insertValues(args: Map<Char, Expression>): SubtractionExpression {
        return SubtractionExpression(vars, f.insertValues(args), g.insertValues(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return f.derive(variable) - g.derive(variable)
    }

    override fun simplify(): Expression {
        val simp = f.simplify() - g.simplify()
        with(simp) {
            if (g.isNegative) {
                return AdditionExpression(vars, f, -g).simplify()
            }
            if (f is RealValue && g is RealValue) {
                return f - g
            }
            if (f is RealValue) {
                if (f == IntValue[0]) {
                    return -g
                }
            }

            if (g is RealValue) {
                if (g.isNegative) {
                    return AdditionExpression(vars, f, g)
                }
                if (g == IntValue[0]) {
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