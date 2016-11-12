package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.E
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.IntValue
import com.deanveloper.mathkt.expression.value.IrrationalValue
import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.pow
import java.math.BigDecimal

class ExponentialExpression(
        variables: CharArray,
        f: Expression,
        g: Expression,
        isNegative: Boolean = false
) : Expression.TwoPartExpression(variables, f, g, isNegative) {
    constructor(
            variable: Char,
            f: Expression,
            g: Expression,
            isNegative: Boolean = false
    ) : this(charArrayOf(variable), f, g, isNegative)

    override fun insertValues(args: Map<Char, Expression>): ExponentialExpression {
        return ExponentialExpression(vars, f.insertValues(args), g.insertValues(args))
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
            if (f is ExponentialExpression) {
                return (f.f pow (f.g * g)).simplify()
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return ExponentialExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f ^ $g)"
}