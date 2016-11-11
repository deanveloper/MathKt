package com.deanveloper.mathkt.expression.value

import java.math.BigInteger

/**
 * @author Dean
 */
class IntValue
@Deprecated("Use get() for automatic caching", ReplaceWith("IntValue[value]"))
constructor(val value: BigInteger) :
        RationalValue(value.toValue, BigInteger.ONE.toValue) {

    companion object {
        @JvmStatic
        private val cache = mutableMapOf<BigInteger, IntValue>()

        init {
            for (i in 0L..10L) {
                cache[BigInteger.valueOf(i)] = IntValue(BigInteger.valueOf(i))
            }
        }

        @JvmStatic operator fun get(value: BigInteger) = cache.getOrElse(value) { IntValue(value) }
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
            is IntValue -> IntValue[value + o.value]
            is RationalValue ->
                RationalValue(
                        (value * o.bottom.value + o.top.value).toValue,
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
            is IntValue -> IntValue[value * o.value]
            is RationalValue ->
                RationalValue(
                        (this * o.top) as IntValue,
                        o.bottom
                )
            else -> throw UnsupportedOperationException("Times operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    /**
     * Optimized for IntValue
     */
    override fun onDiv(o: RealValue): RealValue {
        return when (o) {
            is IntValue -> RationalValue(this, o)
            is RationalValue -> this.onTimes(o.inverse())
            else -> throw UnsupportedOperationException("Divide operation for IntValue is not implemented yet " +
                    "for ${o.javaClass.simpleName}")
        }
    }

    override fun onPow(o: RealValue): RealValue {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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