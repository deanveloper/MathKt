package com.deanveloper.mathkt.expression.value

import java.math.BigInteger

/**
 * @author Dean
 */
open class RationalValue(val top: IntValue, val bottom: IntValue) : RealValue(top.isNegative != bottom.isNegative) {

    private val hash = ((top.hashCode() and 0x0000FFFF) shl 32) or (bottom.hashCode() and 0x0000FFFF)

    override fun onPlus(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> o.onPlus(this) // let IntValue's plus operation handle it
            is RationalValue ->
                RationalValue(
                        (top.value * o.bottom.value + o.top.value * bottom.value).toValue,
                        (o.bottom * o.top) as IntValue
                )
            else -> throw UnsupportedOperationException("Plus operation for RationalValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun onMinus(o: RealValue) = onPlus(-o)

    override fun onTimes(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> o.onTimes(this) // let IntValue's times operation handle it
            is RationalValue ->
                RationalValue(
                        (this.top * o.top) as IntValue,
                        (this.bottom * o.bottom) as IntValue
                )
            else -> throw UnsupportedOperationException("Times operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun onDiv(o: RealValue): RealValue {
        return when (o) {
            is RationalValue -> this.onTimes(o.inverse()) // Also works for IntValue
            else -> throw UnsupportedOperationException("Times operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun floor(): IntValue {
        return (top.value / bottom.value).toValue
    }

    override fun simplify(): RealValue {
        if (top.value % bottom.value == BigInteger.ZERO) {
            return IntValue[top.value / bottom.value]
        }

        if (top.isNegative && bottom.isNegative) {
            return RationalValue(-top, -bottom).simplify()
        }

        return this
    }

    override operator fun unaryMinus(): RealValue {
        if (bottom.isNegative) {
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