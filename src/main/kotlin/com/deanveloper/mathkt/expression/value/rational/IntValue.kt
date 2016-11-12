package com.deanveloper.mathkt.expression.value.rational

import com.deanveloper.mathkt.expression.value.rational.RationalValue
import com.deanveloper.mathkt.expression.value.RealValue
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * @author Dean
 */
class IntValue
@Deprecated("Use get() for automatic caching", ReplaceWith("IntValue[value]"))
constructor(val value: BigInteger)
    : RationalValue(value, BigInteger.ONE) {

    override val approx: BigDecimal by lazy { BigDecimal(value) }

    companion object {
        @JvmStatic
        private val cache = WeakHashMap<BigInteger, IntValue>()

        @JvmStatic operator fun get(value: BigInteger): IntValue = cache.getOrPut(value) { IntValue(value) }
        @JvmStatic operator fun get(value: Long) = get(BigInteger.valueOf(value))
        @JvmStatic operator fun get(value: Int) = get(BigInteger.valueOf(value.toLong()))
        @JvmStatic operator fun get(value: Short) = get(BigInteger.valueOf(value.toLong()))
        @JvmStatic operator fun get(value: Byte) = get(BigInteger.valueOf(value.toLong()))

        @JvmStatic fun valueOf(value: BigInteger) = get(value)
        @JvmStatic fun valueOf(value: Long) = get(value)
        @JvmStatic fun valueOf(value: Int) = get(value)
        @JvmStatic fun valueOf(value: Short) = get(value)
        @JvmStatic fun valueOf(value: Byte) = get(value)
    }

    /**
     * Optimized for IntValue
     */
    override fun onPlus(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> Companion[value + o.value]
            is RationalValue ->
                RationalValue(
                        (value * o.bottom + o.top),
                        o.bottom
                )
            else -> throw UnsupportedOperationException("Plus operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    /**
     * Optimized for IntValue
     */
    override fun onTimes(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> Companion[value * o.value]
            is RationalValue ->
                RationalValue(
                        this.value * o.top,
                        o.bottom
                )
            else -> throw UnsupportedOperationException("Times operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun onPow(o: RealValue): RealValue {
        if (this == Companion[0]) {
            if (o != Companion[0]) {
                return Companion[0]
            } else {
                throw ArithmeticException("0.pow(0) is undefined!")
            }
        }
        if (this == Companion[1]) {
            return Companion[1]
        }
        if (o is IntValue) {
            return Companion[this.value.pow(o.value.toInt())]
        } else if (o is RationalValue) {
            val intPart = o.floor()
            val fracPart = (o - intPart) as RationalValue

            TODO("Not implemented yet")
        }

        TODO("Not implemented yet")
    }

    override operator fun unaryMinus() = IntValue(-value)

    override fun floor() = this

    override fun simplify() = this // do nothing, integers are fully simplified

    override fun toString() = value.toString()

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is RealValue) {
            val newOther = other.simplify()

            if (newOther is IntValue) {
                return value == newOther.value
            }
        }

        return false
    }
}