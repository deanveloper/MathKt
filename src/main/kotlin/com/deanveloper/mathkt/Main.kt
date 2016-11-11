package com.deanveloper.mathkt

import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.Variable
import com.deanveloper.mathkt.expression.trigexpression.SineExpression
import com.deanveloper.mathkt.expression.twopartexpression.AdditionExpression
import com.deanveloper.mathkt.expression.twopartexpression.ExponentialExpression
import com.deanveloper.mathkt.expression.twopartexpression.LogExpression
import com.deanveloper.mathkt.expression.twopartexpression.MultiplicationExpression
import java.math.BigDecimal

/**
 * @author Dean
 */
fun main(vararg args: String) {
    val exp = SineExpression('x',
            LogExpression('x',
                    RealValue(E),
                    AdditionExpression('x',
                            ExponentialExpression('x', Variable('x'), RealValue(BigDecimal.valueOf(3))),
                            MultiplicationExpression('x', RealValue(BigDecimal.ZERO), Variable('x'))
                    )
            )
    )

    println(exp)
    println(exp.simplify())
    println(exp.derive('x'))
    println(exp.derive('x').simplify())

    println(exp(RealValue(BigDecimal.valueOf(5))).simplify())
}