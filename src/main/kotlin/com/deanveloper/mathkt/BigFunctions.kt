package com.deanveloper.mathkt

/*LICENSE FOR ANY FUNCTION LABELLED WITH
"Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java"

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.*/

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

/**
 * The number of significant figures to calculate to.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 */
var defaultScale = 50
    set(value) {
        recalculateConstants()
        field = value
    }

/**
 * Compute x^exponent to a given scale.  Uses the same
 * algorithm as class numbercruncher.mathutils.IntPower.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value x
 * @param exponent the exponent value
 * @param scale the desired scale of the result
 * @return the result value
 */
fun BigDecimal.intPower(exponent: Long, scale: Int = defaultScale): BigDecimal {
    var x = this
    var exp = exponent
    // If the exponent is negative, compute 1/(x^-exponent).
    if (exp < 0) {
        return BigDecimal.valueOf(1).divide(x.intPower(-exp, scale), scale,
                BigDecimal.ROUND_HALF_EVEN)
    }

    var power = BigDecimal.valueOf(1)

    // Loop to compute value^exponent.
    while (exp > 0) {

        // Is the rightmost bit a 1?
        if (exp and 1 == 1L) {
            power = power.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
        }

        // Square x and shift exponent 1 bit to the right.
        x = x.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
        exp = exp shr 1

        Thread.`yield`()
    }

    return power
}

/**
 * Compute the integral root of x to a given scale, x >= 0.
 * Use Newton's algorithm.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value of x
 * @param index the integral root value
 * @param scale the desired scale of the result
 * @return the result value
 */
fun BigDecimal.intRoot(index: Long, scale: Int = defaultScale): BigDecimal {
    var x = this
    // Check that x >= 0.
    if (x.signum() < 0) {
        throw IllegalArgumentException("x < 0")
    }

    val sp1 = scale + 1
    val n = x
    val i = BigDecimal.valueOf(index)
    val im1 = BigDecimal.valueOf(index - 1)
    val tolerance = BigDecimal.valueOf(5).movePointLeft(sp1)
    var xPrev: BigDecimal

    // The initial approximation is x/index.
    x = x.divide(i, scale, BigDecimal.ROUND_HALF_EVEN)

    // Loop until the approximations converge
    // (two successive approximations are equal after rounding).
    do {
        // x^(index-1)
        val xToIm1 = x.intPower(index - 1, sp1)

        // x^index
        val xToI = x.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

        // n + (index-1)*(x^index)
        val numerator = n.add(im1.multiply(xToI)).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

        // (index*(x^(index-1))
        val denominator = i.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

        // x = (n + (index-1)*(x^index)) / (index*(x^(index-1)))
        xPrev = x
        x = numerator.divide(denominator, sp1, BigDecimal.ROUND_DOWN)

        Thread.`yield`()
    } while (x.subtract(xPrev).abs() > tolerance)

    return x
}

/**
 * Compute e^x to a given scale.
 * Break x into its whole and fraction parts and
 * compute (e^(1 + fraction/whole))^whole using Taylor's formula.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value of x
 * @param scale the desired scale of the result
 * @return the result value
 */
fun BigDecimal.exp(scale: Int = defaultScale): BigDecimal {
    // e^0 = 1
    if (signum() == 0) {
        return BigDecimal.valueOf(1)
    } else if (signum() == -1) {
        return BigDecimal.valueOf(1).divide(this.negate().exp(scale), scale,
                BigDecimal.ROUND_HALF_EVEN)
    }// If x is negative, return 1/(e^-x).

    // Compute the whole part of x.
    var xWhole = setScale(0, BigDecimal.ROUND_DOWN)

    // If there isn't a whole part, compute and return e^x.
    if (xWhole.signum() == 0) return this.expTaylor(scale)

    // Compute the fraction part of x.
    val xFraction = this.subtract(xWhole)

    // z = 1 + fraction/whole
    val z = BigDecimal.valueOf(1).add(xFraction.divide(
            xWhole, scale,
            BigDecimal.ROUND_HALF_EVEN))

    // t = e^z
    val t = z.expTaylor(scale)

    val maxLong = BigDecimal.valueOf(java.lang.Long.MAX_VALUE)
    var result = BigDecimal.valueOf(1)

    // Compute and return t^whole using intPower().
    // If whole > Long.MAX_VALUE, then first compute products
    // of e^Long.MAX_VALUE.
    while (xWhole >= maxLong) {
        result = result.multiply(
                t.intPower(java.lang.Long.MAX_VALUE, scale)).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
        xWhole = xWhole.subtract(maxLong)

        Thread.`yield`()
    }
    return result.multiply(t.intPower(xWhole.toLong(), scale)).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
}

/**
 * Compute e^x to a given scale by the Taylor series.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value of x
 * @param scale the desired scale of the result
 * @return the result value
 */
private fun BigDecimal.expTaylor(scale: Int = defaultScale): BigDecimal {
    var factorial = BigDecimal.valueOf(1)
    var xPower = this
    var sumPrev: BigDecimal

    // 1 + x
    var sum = add(BigDecimal.valueOf(1))

    // Loop until the sums converge
    // (two successive sums are equal after rounding).
    var i = 2
    do {
        // x^i
        xPower = xPower.multiply(this).setScale(scale, BigDecimal.ROUND_HALF_EVEN)

        // i!
        factorial = factorial.multiply(BigDecimal.valueOf(i.toLong()))

        // x^i/i!
        val term = xPower.divide(factorial, scale,
                BigDecimal.ROUND_HALF_EVEN)

        // sum = sum + x^i/i!
        sumPrev = sum
        sum = sum.add(term)

        ++i
        Thread.`yield`()
    } while (sum.compareTo(sumPrev) != 0)

    return sum
}

/**
 * Compute the natural logarithm of x to a given scale, x > 0.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 */
fun BigDecimal.ln(scale: Int = defaultScale): BigDecimal {
    // Check that x > 0.
    if (signum() <= 0) {
        throw IllegalArgumentException("x <= 0")
    }

    // The number of digits to the left of the decimal point.
    val magnitude = toString().length - scale() - 1

    if (magnitude < 3) {
        return this.lnNewton(scale)
    } else {

        // x^(1/magnitude)
        val root = intRoot(magnitude.toLong(), scale)

        // ln(x^(1/magnitude))
        val lnRoot = root.lnNewton(scale)

        // magnitude*ln(x^(1/magnitude))
        return BigDecimal.valueOf(magnitude.toLong()).multiply(lnRoot).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
    }// Compute magnitude*ln(x^(1/magnitude)).
}

/**
 * Compute the natural logarithm of x to a given scale, x > 0.
 * Use Newton's algorithm.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 */
private fun BigDecimal.lnNewton(scale: Int = defaultScale): BigDecimal {
    var x = this
    val sp1 = scale + 1
    val n = x
    var term: BigDecimal

    // Convergence tolerance = 5*(10^-(scale+1))
    val tolerance = BigDecimal.valueOf(5).movePointLeft(sp1)

    // Loop until the approximations converge
    // (two successive approximations are within the tolerance).
    do {

        // e^x
        val eToX = x.exp(sp1)

        // (e^x - n)/e^x
        term = eToX.subtract(n).divide(eToX, sp1, BigDecimal.ROUND_DOWN)

        // x - (e^x - n)/e^x
        x = x.subtract(term)
    } while (term > tolerance)

    return x.setScale(scale, BigDecimal.ROUND_HALF_EVEN)
}

/**
 * Compute the arctangent of x to a given scale, |x| < 1
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value of x
 * @param scale the desired scale of the result
 * @return the result value
 */
fun BigDecimal.arctan(scale: Int = defaultScale): BigDecimal {
    // Check that |x| < 1.
    if (this.abs() >= BigDecimal.valueOf(1)) {
        throw IllegalArgumentException("|x| >= 1")
    }

    // If x is negative, return -arctan(-x).
    if (this.signum() == -1) {
        return this.negate().arctan(scale).negate()
    } else {
        return this.arctanTaylor(scale)
    }
}

/**
 * Compute the arctangent of x to a given scale
 * by the Taylor series, |x| < 1
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value of x
 * @param scale the desired scale of the result
 * @return the result value
 */
private fun BigDecimal.arctanTaylor(scale: Int = defaultScale): BigDecimal {
    val sp1 = scale + 1
    var i = 3
    var addFlag = false

    var power = this
    var sum = this
    var term: BigDecimal

    // Convergence tolerance = 5*(10^-(scale+1))
    val tolerance = BigDecimal.valueOf(5).movePointLeft(sp1)

    // Loop until the approximations converge
    // (two successive approximations are within the tolerance).
    do {
        // x^i
        power = power.multiply(this).multiply(this).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

        // (x^i)/i
        term = power.divide(BigDecimal.valueOf(i.toLong()), sp1,
                BigDecimal.ROUND_HALF_EVEN)

        // sum = sum +- (x^i)/i
        sum = if (addFlag)
            sum.add(term)
        else
            sum.subtract(term)

        i += 2
        addFlag = !addFlag

        Thread.`yield`()
    } while (term > tolerance)

    return sum
}

/**
 * Compute the square root of x to a given scale, x >= 0.
 * Use Newton's algorithm.
 *
 * Taken from https://github.com/tareknaj/BigFunctions/blob/master/BigFunctions.java
 *
 * @receiver the value of x
 * @param scale the desired scale of the result
 * @return the result value
 */
fun BigDecimal.sqrt(scale: Int = defaultScale): BigDecimal {
    // Check that x >= 0.
    if (signum() < 0) {
        throw IllegalArgumentException("x < 0")
    }

    // n = x*(10^(2*scale))
    val n = movePointRight(scale shl 1).toBigInteger()

    // The first approximation is the upper half of n.
    val bits = n.bitLength() + 1 shr 1
    var ix = n.shiftRight(bits)
    var ixPrev: BigInteger

    // Loop until the approximations converge
    // (two successive approximations are equal after rounding).
    do {
        ixPrev = ix

        // x = (x + n/x)/2
        ix = ix.add(n.divide(ix)).shiftRight(1)
    } while (ix.compareTo(ixPrev) != 0)

    return BigDecimal(ix, scale)
}

/**
 * Raises one BigDecimal to a power of another BigDecimal
 */
fun BigDecimal.pow(other: BigDecimal, scale: Int = defaultScale): BigDecimal {
    if (other.signum() === 0 || other.scale() <= 0 || other.stripTrailingZeros().scale() <= 0) {
        var toReturn = BigDecimal.ONE

        val max = other.toBigIntegerExact()

        try {
            for (iter in 1..max.longValueExact()) {
                toReturn *= this
            }
        } catch (e: ArithmeticException) { // If the value cannot be stored in a long
            var iter = BigInteger.ZERO

            while (iter < max) {
                toReturn *= this
                iter += BigInteger.ONE
            }
        }

        return toReturn
    }

    val sp1 = scale + 1
    return (this.ln(sp1) * other).exp(sp1).round(MathContext(scale, RoundingMode.HALF_UP))
}

/**
 * Computes the sine of a BigDecimal. It checks a few
 * cached values, then uses the Taylor Series if needed.
 */
fun BigDecimal.sin(scale: Int = defaultScale): BigDecimal {
    return with(this.mod(PI * BigDecimal.valueOf(2))) {
        when {
            compareTo(BigDecimal.ZERO) === 0 -> BigDecimal.ZERO
            compareTo(PI.divide(BigDecimal.valueOf(2))) === 0 -> BigDecimal.ONE
            compareTo(PI) === 0 -> BigDecimal.ZERO
            compareTo(PI.times(BigDecimal("1.5"))) === 0 -> -BigDecimal.ONE
            else -> sinTaylor(scale)
        }
    }
}

/**
 * Computes the sine of a BigDecimal based on the Sine Taylor Series.
 */
private fun BigDecimal.sinTaylor(scale: Int = defaultScale): BigDecimal {
    if (this !in BigDecimal.ZERO..(PI * BigDecimal.valueOf(2))) {
        return this.mod(PI * BigDecimal.valueOf(2)).sinTaylor()
    }

    var last = this
    var current = this
    var n = BigDecimal.valueOf(3)
    var add = false
    // x - x^3/3! + x^5/5! - x^7/7! ...

    do {
        last = current
        if (add) {
            current += current.pow(n).divide(n.factorial(), scale + 1)
        } else {
            current -= current.pow(n).divide(n.factorial(), scale + 1)
        }

        n += BigDecimal.valueOf(2)
        add = !add
    } while (last.compareTo(current) === 0)

    return current
}

/**
 * Computes the cosine of a BigDecimal by calculating sine of this + pi/2.
 * The [sin] checks a few cached values, then uses the Taylor Series if needed.
 */
fun BigDecimal.cos(scale: Int = defaultScale): BigDecimal {
    return (this + (PI / BigDecimal.valueOf(2))).cos(scale)
}

/**
 * Calculates the inverse sine function of a BigDecimal.
 * Checks a few cached values, then uses an iterative method if needed.
 */
fun BigDecimal.asin(scale: Int = defaultScale): BigDecimal {
    return with(this.mod(PI * BigDecimal.valueOf(2))) {
        when {
            compareTo(BigDecimal.ZERO) === 0 -> BigDecimal.ZERO
            compareTo(BigDecimal.ONE) === 0 -> PI.divide(BigDecimal.valueOf(2))
            compareTo(BigDecimal.ZERO) === 0 -> PI
            compareTo(-BigDecimal.ONE) === 0 -> PI.times(BigDecimal("1.5"))
            else -> asinIterate(scale)
        }
    }
}

/**
 * An iterative method to calculate the arcsine of a BigDecimal.
 */
private fun BigDecimal.asinIterate(scale: Int = defaultScale): BigDecimal {
    // 2^n * sin(x/2^n) as n->Infinity
    var toReturn: BigDecimal
    do {
        val n = BigDecimal("1000000")
        val power = BigDecimal.valueOf(2).pow(n)
        val np1 = n + BigDecimal.ONE
        val powerp1 = BigDecimal.valueOf(2).pow(np1)
        toReturn = power.times(this.divide(power, scale))
    } while (toReturn.compareTo(powerp1.times(this.divide(powerp1, scale))) === 0)

    return toReturn
}

/**
 * Calculates the inverse cosine function of a BigDecimal using the asin(-this) + 1
 * [asin] checks a few cached values, then uses an iterative method if needed.
 */
fun BigDecimal.acos(scale: Int = defaultScale): BigDecimal {
    return (-this).asin(scale) + BigDecimal.ONE
}

/**
 * Computes the factorial of a BigDecimal.
 *
 * @throws ArithmeticException if the BigDecimal is not an integer
 */
private fun BigDecimal.factorial(): BigDecimal {
    var toReturn = BigDecimal.ONE
    var i = BigInteger.valueOf(2)

    while(i <= this.toBigIntegerExact()) {
        toReturn *= BigDecimal(i)
        i += BigInteger.ONE
    }

    return toReturn
}
