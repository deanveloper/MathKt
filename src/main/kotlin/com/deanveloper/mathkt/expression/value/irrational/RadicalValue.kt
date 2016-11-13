package com.deanveloper.mathkt.expression.value.irrational

import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.value.rational.IntValue
import java.math.BigDecimal

/**
 * @author Dean
 */
class RadicalValue(
        val root: RealValue,
        val radicand: RealValue,
        baseApprox: BigDecimal,
        plus: RealValue = IntValue[0],
        times: RealValue = IntValue[1],
        power: RealValue = IntValue[1]
) : IrrationalValue(format(root, radicand), baseApprox, plus, times, power) {
    companion object {
        fun format(root: RealValue, radicand: RealValue): String {
            return buildString {
                if (root == IntValue[2]) {
                    append("sqrt(")
                } else {
                    append(root).append("root(")
                }
                append(radicand).append(")")
            }
        }
    }

}
