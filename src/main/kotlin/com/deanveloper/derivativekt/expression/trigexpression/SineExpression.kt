package com.deanveloper.derivativekt.expression.trigexpression

import com.deanveloper.derivativekt.expression.Expression
import com.deanveloper.derivativekt.expression.Value
import com.deanveloper.derivativekt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.derivativekt.sin

/**
 * @author Dean
 */
class SineExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun execute(args: Map<Char, Expression>): SineExpression {
        return SineExpression(vars, f.execute(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                CosineExpression(vars, f, isNegative),
                f.derive(variable)
        )
    }

    override fun simplify(): Expression {
        val simp = SineExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f.isNegative) {
                return SineExpression(vars, -f, !isNegative).simplify()
            }

            if (f is Value) {
                return Value(f.value.sin()).simplify()
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return SineExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "sin($f)"
    }
}