package com.deanveloper.mathkt.expression.trigexpression

import com.deanveloper.mathkt.cos
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression

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

            if (f is RealValue) {
                return RealValue(f.value.cos()).simplify()
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