package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.E
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.value.rational.IntValue
import com.deanveloper.mathkt.value.irrational.IrrationalValue
import com.deanveloper.mathkt.value.RealValue
import com.deanveloper.mathkt.pow
import java.math.BigDecimal

open class PowerExpression(
        variables: CharArray,
        val base: Expression,
        val power: Expression,
        isNegative: Boolean = false
) : Expression.TwoPartExpression(variables, base, power, isNegative) {
    constructor(
            variable: Char,
            base: Expression,
            power: Expression,
            isNegative: Boolean = false
    ) : this(charArrayOf(variable), base, power, isNegative)

    override fun insertValues(args: Map<Char, Expression>): PowerExpression {
        return PowerExpression(vars, f.insertValues(args), g.insertValues(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        if (!f.vars.contains(variable) && g.vars.contains(variable)) { // power function

            // when f(x) = c^x and c is constant, f'(x) = c^x * ln(x) (with chain rule appended)
            return this * (f logOfBase IrrationalValue.E) * g.derive(variable)

        } else if (f.vars.contains(variable) && !g.vars.contains(variable)) { // exponential function

            // when f(x) = x^c and c is constant, f'(x) = c * x^(c-1) (with chain rule appended)
            return g * (f pow (g - IntValue[1])) * f.derive(variable)

        } else if (f.vars.contains(variable) && g.vars.contains(variable)) { // weird-ass function
            // when h(x) = f(x) ^ g(x), h'(x) = h(x) * (g(x) / f(x) + g'(x) * ln(g(x))
            return this * ((g / f) + (g.derive(variable) * f logOfBase IrrationalValue.E))

        } else {
            // when f(x) = c ^ d, where c and d are constants, f'(x) = 0
            return IntValue[0]
        }
    }

    override fun simplify(): Expression {
        val simp = f.simplify() pow g.simplify()
        with(simp) {
            if (f is RealValue && g is RealValue) {
                return f.pow(g)
            }
            if (f is PowerExpression) {
                return (f.f pow (f.g * g)).simplify()
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return PowerExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f ^ $g)"
}