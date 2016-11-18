package com.deanveloper.mathkt.value.rational

import com.deanveloper.mathkt.value.irrational.IrrationalValue
import com.deanveloper.mathkt.value.RealValue
import com.deanveloper.mathkt.value.rational.IntValue
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author Dean
 */
open class RationalValue(val top: BigInteger, val bottom: BigInteger)
    : RealValue((top.signum() === -1) != (bottom.signum() === -1)) {

    override val approx: BigDecimal by lazy {
        BigDecimal(top) / BigDecimal(bottom)
    }

    private val hash = ((top.hashCode() and 0x0000FFFF) shl 32) or (bottom.hashCode() and 0x0000FFFF)

    override fun onPlus(o: RealValue): RealValue {
        return when (o) {
            is IntValue, is IrrationalValue -> o.onPlus(this) // delegate to other onPlus handler
            is RationalValue ->
                RationalValue(
                        top * o.bottom + o.top * bottom,
                        o.bottom * o.top
                ).simplify()
            else -> throw UnsupportedOperationException("Plus operation for RationalValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun onTimes(o: RealValue): RealValue {
        return when (o) {
            is IntValue, is IrrationalValue -> o.onTimes(this) // delegate to other onTimes handler
            is RationalValue ->
                RationalValue(
                        this.top * o.top,
                        this.bottom * o.bottom
                )
            else -> throw UnsupportedOperationException("Times operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun onPow(o: RealValue): RealValue {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun floor(): IntValue {
        return IntValue[top / bottom]
    }

    override fun simplify(): RealValue {
        if (top % bottom == BigInteger.ZERO) {
            return IntValue[top / bottom]
        }

        if (top.signum() === 1 && bottom.signum() !== 1) {
            return RationalValue(-top, -bottom).simplify()
        }

        val gcd = top.gcd(bottom)
        return RationalValue(top / gcd, bottom / gcd)
    }

    override operator fun unaryMinus(): RealValue {
        if (bottom.signum() === -1) {
            return RationalValue(top, -bottom)
        }

        return RationalValue(-top, bottom)
    }

    fun inverse(): RationalValue {
        return RationalValue(bottom, top)
    }

    override fun toString(): String {
        return "($top/$bottom)"
    }

    override fun hashCode(): Int {
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other is RealValue) {
            val simp = this.simplify()
            val otherSimp = other.simplify()

            if (simp is RationalValue && otherSimp is RationalValue) {
                return simp.top == otherSimp.top && simp.bottom == otherSimp.bottom
            } else if (simp is IntValue && otherSimp is IntValue) {
                return simp.value == otherSimp.value
            }
        }

        return false
    }
}