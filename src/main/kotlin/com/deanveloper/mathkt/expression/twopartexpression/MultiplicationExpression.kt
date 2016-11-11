package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.IntValue
import com.deanveloper.mathkt.expression.value.RealValue
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

    override fun execute(args: Map<Char, Expression>): MultiplicationExpression {
        return MultiplicationExpression(vars, f.execute(args), g.execute(args))
    }

    override fun derive(variable: Char): Expression {
        // (f * g)' == f * g' + f' * g
        return AdditionExpression(vars,
                MultiplicationExpression(vars, f.derive(variable), g),
                MultiplicationExpression(vars, f, g.derive(variable))
        )
    }

    override fun simplify(): Expression {
        val simp = MultiplicationExpression(vars, f.simplify(), g.simplify())
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