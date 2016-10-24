package com.deanveloper.derivativekt.expression.trigexpression

import com.deanveloper.derivativekt.cos
import com.deanveloper.derivativekt.defaultScale
import com.deanveloper.derivativekt.expression.Expression
import com.deanveloper.derivativekt.expression.Value
import com.deanveloper.derivativekt.expression.asExp
import com.deanveloper.derivativekt.expression.twopartexpression.ExponentialExpression
import com.deanveloper.derivativekt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.derivativekt.sin

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

            if (f is Value) {
                return Value(f.value.sin().divide(f.value.cos(), defaultScale)).simplify()
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