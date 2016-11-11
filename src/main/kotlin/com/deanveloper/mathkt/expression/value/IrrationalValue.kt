package com.deanveloper.mathkt.expression.value

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Dean
 */
class IrrationalValue(
        val value: String,
        val approx: BigDecimal,
        val plus: RealValue = IntValue[0],
        val times: RealValue = IntValue[0],
        isNegative: Boolean = approx.signum() === -1
) : RealValue(isNegative) {

    companion object {
        @JvmStatic val E = IrrationalValue("e", BigDecimal("2.7182818284590452353"))
        @JvmStatic val PI = IrrationalValue("pi", BigDecimal("3.1415926535897932384"))
        @JvmStatic val SQRT_2 = IrrationalValue("sqrt(2)", BigDecimal("1.4142135623730950488"))
        @JvmStatic val SQRT_3 = IrrationalValue("sqrt(3)", BigDecimal("1.7320508075688772935"))
    }

    override fun floor(): IntValue {
        return approx.setScale(0, RoundingMode.FLOOR).toBigIntegerExact().toValue
    }

    override fun simplify(): IrrationalValue {
        if (times.isNegative) {
            IrrationalValue(value, approx, plus.simplify(), -times.simplify(), !isNegative)
        }
        return IrrationalValue(value, approx, plus.simplify(), times.simplify(), isNegative)
    }

    override operator fun unaryMinus() = IrrationalValue(value, -approx, -plus, times, !isNegative)

    override fun toString(): String {
        return buildString {
            append('(')
            append(when (times) {
                is IntValue -> append("$times $value")
                is RationalValue -> append("${times.top} $value / ${times.bottom}")
                else -> append("$times * $value")
            })
            if (!plus.isNegative) {
                append(" + ")
                append(plus)
            } else {
                append(" - ")
                append(-plus)
            }
        }
    }

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is IrrationalValue) {
            return value == other.value && plus == other.plus && times == other.times && isNegative == other.isNegative
        }

        return false
    }

    override fun onPlus(o: RealValue) = IrrationalValue(value, approx, plus.onPlus(o), times, isNegative)

    override fun onMinus(o: RealValue) = IrrationalValue(value, approx, plus.onMinus(o), times, isNegative)

    override fun onTimes(o: RealValue) = IrrationalValue(value, approx, plus, times.onTimes(o), isNegative)

    override fun onDiv(o: RealValue) = IrrationalValue(value, approx, plus, times.onDiv(o), isNegative)

    override fun onPow(o: RealValue): RealValue {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}