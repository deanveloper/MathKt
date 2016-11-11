package com.deanveloper.mathkt.expression.trigexpression

import com.deanveloper.mathkt.cos
import com.deanveloper.mathkt.defaultScale
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.value.toValue
import com.deanveloper.mathkt.expression.twopartexpression.ExponentialExpression
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.mathkt.sin

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

    override fun execute(args: Map<Char, Expression>): TangentExpression {
        return TangentExpression(vars, f.execute(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                ExponentialExpression(vars,
                        SecantExpression(vars, f),
                        2.asExp
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
                return RealValue(f.value.sin().divide(f.value.cos(), defaultScale)).simplify()
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