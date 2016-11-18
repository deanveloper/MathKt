package com.deanveloper.mathkt.value.irrational

import com.deanveloper.mathkt.value.RealValue
import com.deanveloper.mathkt.value.rational.IntValue
import com.deanveloper.mathkt.value.rational.RationalValue
import com.deanveloper.mathkt.pow
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Dean
 */
open class IrrationalValue(
        val value: String,
        val baseApprox: BigDecimal,
        val plus: RealValue = IntValue[0],
        val times: RealValue = IntValue[1],
        val power: RealValue = IntValue[1]
) : RealValue((baseApprox * times.approx + plus.approx).signum() === -1) {
    override val approx: BigDecimal = (baseApprox.pow(power.approx)) * times.approx + plus.approx

    companion object {
        @JvmStatic val E = IrrationalValue("e", BigDecimal("2.7182818284590452353"))
        @JvmStatic val PI = IrrationalValue("pi", BigDecimal("3.1415926535897932384"))
        @JvmStatic val SQRT_2 = IrrationalValue("sqrt(2)", BigDecimal("1.4142135623730950488"))
        @JvmStatic val SQRT_3 = IrrationalValue("sqrt(3)", BigDecimal("1.7320508075688772935"))
    }

    override fun floor(): IntValue {
        return IntValue[approx.setScale(0, RoundingMode.FLOOR).toBigIntegerExact()]
    }

    override fun simplify(): IrrationalValue {
        return IrrationalValue(value, baseApprox, plus.simplify(), times.simplify(), power.simplify())
    }

    override operator fun unaryMinus() = IrrationalValue(value, baseApprox, -plus, -times, power)

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

    override fun onPlus(o: RealValue) = IrrationalValue(value, approx, plus.onPlus(o), times, power)

    override fun onTimes(o: RealValue) = IrrationalValue(value, approx, plus, times.onTimes(o), power)

    override fun onPow(o: RealValue) = IrrationalValue(value, approx, plus, times, power.onTimes(o))
}