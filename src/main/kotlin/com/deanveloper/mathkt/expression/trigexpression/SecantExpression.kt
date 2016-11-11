package com.deanveloper.mathkt.expression.trigexpression

import com.deanveloper.mathkt.cos
import com.deanveloper.mathkt.defaultScale
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import java.math.BigDecimal

/**
 * @author Dean
 */
class SecantExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun execute(args: Map<Char, Expression>): SecantExpression {
        return SecantExpression(vars, f.execute(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                MultiplicationExpression(vars,
                        this,
                        TangentExpression(vars, f, false)
                ),
                f.derive(variable)
        )
    }

    override fun simplify(): Expression {
        val simp = SecantExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f.isNegative) {
                return SecantExpression(vars, -f, isNegative).simplify()
            }

            if (f is RealValue) {
                return RealValue(BigDecimal.ONE.divide(f.value.cos(), defaultScale)).simplify()
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return SineExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "sec($f)"
    }
}