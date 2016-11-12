package com.deanveloper.mathkt.expression.trigexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.twopartexpression.ExponentialExpression
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.mathkt.expression.value.rational.IntValue
import com.deanveloper.mathkt.expression.value.irrational.IrrationalValue
import com.deanveloper.mathkt.expression.value.RealValue

/**
 * @author Dean
 */
class TangentExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun insertValues(args: Map<Char, Expression>): TangentExpression {
        return TangentExpression(vars, f.insertValues(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                ExponentialExpression(vars,
                        SecantExpression(vars, f),
                        IntValue[2]
                ),
                f.derive(variable)
        )
    }

    override fun simplify(): Expression {
        val simp = TangentExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f.isNegative) {
                return TangentExpression(vars, -f, !isNegative).simplify()
            }

            if (f is RealValue) {
                val sin = calcSin(f)
                val cos = calcSin((f + (IrrationalValue.PI / IntValue[2])) as RealValue)

                if (sin !== null && cos !== null && cos !== IntValue[0]) {
                    return sin / cos
                }
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return TangentExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "tan($f)"
    }
}