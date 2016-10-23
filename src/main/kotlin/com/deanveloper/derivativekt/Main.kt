package com.deanveloper.derivativekt

import com.deanveloper.derivativekt.expression.AdditionExpression
import com.deanveloper.derivativekt.expression.MultiplicationExpression
import com.deanveloper.derivativekt.expression.Value
import com.deanveloper.derivativekt.expression.Variable
import java.math.BigDecimal

/**
 * @author Dean
 */
fun main(vararg args: String) {
    val exp = AdditionExpression('x',
            MultiplicationExpression('x', Value(BigDecimal.ONE), Variable('x')),
            MultiplicationExpression('x', Value(BigDecimal.ZERO), Variable('x'))
    )

    println(exp)
    println(exp.simplify())
    println(exp.derive())
    println(exp.derive().simplify())
    println(exp.simplify().derive())
}