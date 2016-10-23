package com.deanveloper.derivativekt.expression.trigexpression

import com.deanveloper.derivativekt.cos
import com.deanveloper.derivativekt.expression.Expression
import com.deanveloper.derivativekt.expression.Value
import com.deanveloper.derivativekt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.derivativekt.sin

/**
 * @author Dean
 */
class CosExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun execute(args: Map<Char, Expression>): SinExpression {
        return SinExpression(vars, f.execute(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                SinExpression(vars, f, !isNegative),
                f.derive(variable)
        )
    }

    override fun simplify(): Expression {
        val simp = CosExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f is Value) {
                return Value(f.value.cos())
            }

            if (f.isNegative) {
                return -this
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return SinExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "cos($f)"
    }
}