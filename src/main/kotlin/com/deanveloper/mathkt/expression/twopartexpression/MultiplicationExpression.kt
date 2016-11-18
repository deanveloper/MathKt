package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.value.rational.IntValue
import com.deanveloper.mathkt.value.RealValue
import java.math.BigDecimal

class MultiplicationExpression(
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

    override fun insertValues(args: Map<Char, Expression>): MultiplicationExpression {
        return MultiplicationExpression(vars, f.insertValues(args), g.insertValues(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        // (f * g)' == f * g' + f' * g
        return f * g.derive(variable) + f.derive(variable) * g
    }

    override fun simplify(): Expression {
        val simp = f.simplify() * g.simplify()
        with(simp) {
            if (f is RealValue && g is RealValue) {
                return f * g
            }
            if (f is IntValue) {
                if (f == IntValue[1]) { // 1 * g == g
                    return g
                } else if (f == IntValue[0]) { // 0 * g == 0
                    return IntValue[0]
                }
            }
            if (g is RealValue) {
                if (g == IntValue[1]) { // 1 * g == g
                    return f
                } else if (g == IntValue[0]) { // 0 * g == 0
                    return IntValue[0]
                }
            }
            if (f is DivisionExpression) {
                return ((g * f.f).simplify() / f.g).simplify()
            }
            if (g is DivisionExpression) {
                return ((f * g.f).simplify() / g.g).simplify()
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