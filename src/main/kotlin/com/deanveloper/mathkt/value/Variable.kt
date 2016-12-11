package com.deanveloper.mathkt.value

import java.math.BigDecimal

/**
 * @author Dean
 */
class Variable(val name: String, val isNegative: Boolean = false) : RealValue {
    override val approx: BigDecimal
        get() = throw RuntimeException("Variables have no approximate value.")
    
    
    
    override fun unaryMinus(): RealValue = Variable(name, !isNegative)
    
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
    
    override fun simplify(): RealValue {
        TODO()
    }
}