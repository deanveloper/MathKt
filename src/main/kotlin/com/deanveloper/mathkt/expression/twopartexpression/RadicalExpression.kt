package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.value.rational.IntValue

/**
 * @author Dean
 */
class RadicalExpression(
        variables: CharArray,
        f: Expression,
        val root: Expression,
        isNegative: Boolean = false
) : PowerExpression(variables, f, IntValue[1] / root, isNegative) {

    override fun insertValues(args: Map<Char, Expression>): RadicalExpression {
        return RadicalExpression(vars, f.insertValues(args), root.insertValues(args), isNegative)
    }

    override fun unaryMinus(): RadicalExpression {
        return RadicalExpression(vars, f, root, isNegative)
    }
}