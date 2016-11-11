package com.deanveloper.mathkt.expression.value

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Dean
 */
class IrrationalValue(
        val value: String,
        val approx: BigDecimal,
        val plus: RealValue,
        val times: RealValue,
        isNegative: Boolean
) : RealValue(isNegative) {
    override fun floor(): IntValue {
        return approx.setScale(0, RoundingMode.FLOOR).toBigIntegerExact().toValue
    }

    override fun simplify() = this

    override operator fun unaryMinus() = IrrationalValue(value, -approx, -plus, times, !isNegative)

    override fun toString() = value

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is IrrationalValue) {
            return value == other.value && isNegative == other.isNegative
        }

        return false
    }

    override fun onPlus(o: RealValue) = IrrationalValue(value, approx, plus.onPlus(o), times, isNegative)

    override fun onMinus(o: RealValue) = IrrationalValue(value, approx, plus.onMinus(o), times, isNegative)

    override fun onTimes(o: RealValue) = IrrationalValue(value, approx, plus, times.onTimes(o), isNegative)

    override fun onDiv(o: RealValue) = IrrationalValue(value, approx, plus, times.onDiv(o), isNegative)
}