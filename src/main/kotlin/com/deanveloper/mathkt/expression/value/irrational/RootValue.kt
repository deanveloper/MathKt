package com.deanveloper.mathkt.expression.value.irrational

import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.expression.value.rational.IntValue
import com.deanveloper.mathkt.expression.value.rational.RationalValue
import java.math.BigDecimal
import java.math.BigInteger

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
) : IrrationalValue(format(root, radicand, plus, times, power), baseApprox, plus, times, power) {

    companion object {
        fun format(root: RealValue, radicand: RealValue, plus: RealValue, times: RealValue, power: RealValue): String {
            return buildString {
                if (root == IntValue[2]) {
                    append("sqrt(")
                } else {
                    append(root).append("root(")
                }
                append(radicand).append(")")
                if(power != IntValue[1]) {
                    append("^").append(power)
                }
                if(times != IntValue[1]) {
                    if (times is RationalValue && times.top == BigInteger.ONE) {
                        append(" / ").append(times.bottom)
                    } else {
                        append(" * ").append(times)
                    }
                }
                if(plus != IntValue[0]) {
                    if (plus.isNegative) {
                        append(" - ").append(-plus)
                    } else {
                        append(" + ").append(plus)
                    }
                }
            }
        }
    }

}
