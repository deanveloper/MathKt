package com.deanveloper.mathkt

import com.deanveloper.mathkt.value.RealValue
import com.deanveloper.mathkt.expression.Variable
import com.deanveloper.mathkt.expression.trigexpression.SineExpression
import com.deanveloper.mathkt.expression.twopartexpression.AdditionExpression
import com.deanveloper.mathkt.expression.twopartexpression.PowerExpression
import com.deanveloper.mathkt.expression.twopartexpression.LogExpression
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import com.deanveloper.mathkt.value.rational.IntValue
import java.math.BigDecimal

/**
 * @author Dean
 */
fun main(vararg args: String) {
    val exp = SineExpression('x',
            LogExpression('x',
                    IrrationalValue.E,
                    PowerExpression('x', Variable['x'], IntValue[3]) + IntValue[0] * Variable['x']
            )
    )

    println(exp)
    println(exp.simplify())
    println(exp.derive('x'))
    println(exp.derive('x').simplify())

    println(exp(IntValue[5]).simplify())
}