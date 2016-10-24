package com.deanveloper.derivativekt.expression.trigexpression

import com.deanveloper.derivativekt.cos
import com.deanveloper.derivativekt.expression.Expression
import com.deanveloper.derivativekt.expression.Value
import com.deanveloper.derivativekt.expression.twopartexpression.MultiplicationExpression

/**
 * @author Dean
 */
class CosineExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun execute(args: Map<Char, Expression>): CosineExpression {
        return CosineExpression(vars, f.execute(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                SineExpression(vars, f, !isNegative),
                f.derive(variable)
        )
    }

    override fun simplify(): Expression {
        val simp = CosineExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f.isNegative) {
                return CosineExpression(vars, -f, isNegative).simplify()
            }

            if (f is Value) {
                return Value(f.value.cos()).simplify()
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return CosineExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "cos($f)"
    }
}