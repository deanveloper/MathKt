package com.deanveloper.mathkt.expression.trigexpression

import com.deanveloper.mathkt.defaultScale
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.mathkt.expression.value.rational.IntValue
import com.deanveloper.mathkt.expression.value.irrational.IrrationalValue
import com.deanveloper.mathkt.sin
import java.math.BigDecimal

/**
 * @author Dean
 */
class CotangentExpression(
        variables: CharArray,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TrigExpression(variables, f, isNegative) {
    constructor(variable: Char,
                f: Expression,
                isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, isNegative)

    override fun insertValues(args: Map<Char, Expression>): CotangentExpression {
        return CotangentExpression(vars, f.insertValues(args), isNegative)
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
        val simp = CotangentExpression(vars, f.simplify(), isNegative)
        with(simp) {
            if (f.isNegative) {
                return CotangentExpression(vars, -f, !isNegative).simplify()
            }

            if (f is RealValue) {
                val sin = calcSin(f)
                val cos = calcSin((f + (IrrationalValue.PI / IntValue[2])) as RealValue)

                if (sin !== null && cos !== null && sin !== IntValue[0]) {
                    return cos / sin
                }
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return CotangentExpression(vars, f, !isNegative)
    }

    override fun toString(): String {
        return "cot($f)"
    }
}