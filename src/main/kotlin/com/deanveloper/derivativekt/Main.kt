package com.deanveloper.derivativekt

import java.math.BigDecimal

/**
 * @author Dean
 */
fun main(vararg args: String) {
    println(
            ExponentialExpression(charArrayOf('x'),
                    AdditionExpression(
                            charArrayOf('x'),
                            Value(BigDecimal.valueOf(5)),
                            Variable('x')
                    ),
                    Variable('x')
            ).invoke(Value(BigDecimal.valueOf(5)))
    )
}