package com.deanveloper.mathkt.value.rational

import com.deanveloper.mathkt.value.RealValue
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
    override fun plus(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> IntValue[value + o.value]
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
    override fun times(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> IntValue[value * o.value]
            is RationalValue ->
                RationalValue(
                        this.value * o.top,
                        o.bottom
                )
            else -> throw UnsupportedOperationException("Times operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun pow(o: RealValue): RealValue {
        if (this == IntValue[0]) {
            if (o != IntValue[0]) {
                return IntValue[1]
            } else {
                throw ArithmeticException("0.pow(0) is undefined!")
            }
        }
        if (this == IntValue[1]) {
            return this
        }
        if (o == IntValue[0]) {
            return IntValue[1]
        }
        if (o == IntValue[1]) {
            return this
        }
        if (o is IntValue) {
            return IntValue[this.value.pow(o.value.toInt())]
        } else if (o is RationalValue) {
            val newO = o.simplify()
            if (newO is IntValue) {
                return pow(newO)
            } else if (newO is RationalValue) {
                return pow(IntValue[newO.top]).root(IntValue[newO.bottom])
            }
        }

        TODO("Not implemented yet")
    }

    override fun root(o: RealValue): RealValue {
        if (this == IntValue[0]) {
            return IntValue[0]
        }
        if (this == IntValue[1]) {
            return IntValue[1]
        }
        if (o is IntValue) {
            return RootValue(o, this)
        } else if (o is RationalValue) {
            val newO = o.simplify()
            if (newO is IntValue) {
                return pow(newO)
            } else if (newO is RationalValue) {
                return pow(IntValue[newO.top]).root(IntValue[newO.bottom])
            }
        }

        TODO("Not implemented yet")
    }

    override operator fun unaryMinus() = IntValue(-value)

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