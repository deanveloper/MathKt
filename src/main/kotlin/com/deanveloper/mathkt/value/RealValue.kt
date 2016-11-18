package com.deanveloper.mathkt.value

import com.deanveloper.mathkt.value.rational.DecimalValue
import com.deanveloper.mathkt.value.rational.IntValue
import com.deanveloper.mathkt.value.rational.RationalValue
import java.math.BigDecimal
import java.math.BigInteger

interface RealValue {
    companion object {
        fun from(number: String): RealValue {
            try {
                val bigInt = BigInteger(number)
                return IntValue[bigInt]
            } catch (e: NumberFormatException) {
                val bigDec = BigDecimal(number)
                return DecimalValue(bigDec)
            }
        }
    }

    val approx: BigDecimal

    operator fun unaryMinus(): RealValue

    operator fun plus(o: RealValue): RealValue

    operator fun minus(o: RealValue): RealValue

    operator fun times(o: RealValue): RealValue

    operator fun div(o: RealValue): RealValue

    fun pow(o: RealValue): RealValue

    fun root(o: RealValue): RealValue
}