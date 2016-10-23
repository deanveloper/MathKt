package com.deanveloper.derivativekt

import com.deanveloper.derivativekt.expression.*
import java.math.BigDecimal

/**
 * @author Dean
 */
fun main(vararg args: String) {
    val exp = LogExpression('x',
            Value(E),
            AdditionExpression('x',
                    ExponentialExpression('x', Variable('x'), Value(BigDecimal.valueOf(3))),
                    MultiplicationExpression('x', Value(BigDecimal.ZERO), Variable('x'))
            )
    )

    println(exp)
    println(exp.simplify())
    println(exp.derive())
    println(exp.derive().simplify())
}