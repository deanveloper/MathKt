package com.deanveloper.mathkt.value.rational

import com.deanveloper.mathkt.value.RealValue
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author Dean
 */
class DecimalValue(val exact: BigDecimal) : RationalValue(convertToFrac(exact).first, convertToFrac(exact).second) {
    
    companion object {
        @JvmStatic fun convertToFrac(exact: BigDecimal): Pair<BigInteger, BigInteger> {
            val mult = Math.max(0, exact.stripTrailingZeros().scale())
            return exact.movePointRight(mult).toBigInteger() to BigInteger.TEN.pow(mult)
        }
    }

    override fun plus(o: RealValue): RealValue {
        TODO()
    }

    override fun minus(o: RealValue): RealValue {
        TODO()
    }

    override fun times(o: RealValue): RealValue {
        TODO()
    }

    override fun div(o: RealValue): RealValue {
        TODO()
    }

    override fun pow(o: RealValue): RealValue {
        TODO()
    }
    
    override fun root(o: RealValue): RealValue {
        TODO()
    }
}
