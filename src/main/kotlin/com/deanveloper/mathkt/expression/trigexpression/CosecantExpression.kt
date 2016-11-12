package com.deanveloper.mathkt.expression.trigexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.mathkt.expression.value.rational.IntValue
import com.deanveloper.mathkt.expression.value.irrational.IrrationalValue
import com.deanveloper.mathkt.expression.value.rational.RationalValue
import com.deanveloper.mathkt.expression.value.RealValue

/**
 * @author Dean
 */
class CosecantExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun insertValues(args: Map<Char, Expression>): CosecantExpression {
        return CosecantExpression(vars, f.insertValues(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        return MultiplicationExpression(vars,
                MultiplicationExpression(vars,
                        -this,
                        CotangentExpression(vars, f, false)
                ),
                f.derive(variable)
        )
    }

    override fun simplify(): Expression {
        val simp = CosecantExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f.isNegative) {
                return CosecantExpression(vars, -f, !isNegative).simplify()
            }

            if (f is RealValue) {
                val sin = calcSin(f)
                if (sin !== null) {
                    return IntValue[1] / sin
                }
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return SineExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "csc($f)"
    }
}