package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.rational.IntValue
import com.deanveloper.mathkt.expression.value.rational.RationalValue
import com.deanveloper.mathkt.expression.value.RealValue
import java.math.BigDecimal

class DivisionExpression(
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

    override fun insertValues(args: Map<Char, Expression>): DivisionExpression {
        return DivisionExpression(vars, f.insertValues(args), g.insertValues(args), isNegative)
    }

    override fun derive(variable: Char): Expression {
        // (h / l)' == (l * h' - h - l') / (l*l)
        return (g * f.derive(variable) - f * g.derive(variable)) / (g pow IntValue[2])
    }

    override fun simplify(): Expression {
        val simp = f.simplify() / g.simplify()
        with(simp) {
            if (g is IntValue) {
                if (g == IntValue[1]) {
                    return f
                }

                if (f is IntValue) {
                    return RationalValue(f.value, g.value).simplify()
                }
            }

            if (f is DivisionExpression) {
                // (f/g)/h == f/(g*h)
                return (f.f / (f.g * g)).simplify()
            }

            // same as above but for values
            if (f is RationalValue) {
                return (IntValue[f.top] / (IntValue[f.bottom] * g)).simplify()
            }

            // f/(g/h) == (f*h)/g
            if (g is DivisionExpression) {
                return DivisionExpression(vars, MultiplicationExpression(vars, f, g.g), g.f).simplify()
            }

            // same as above but for values
            if (g is RationalValue) {
                return ((f * IntValue[g.bottom]) / IntValue[g.top]).simplify()
            }

            if (f is MultiplicationExpression) {
                // gets the value part and non-value part of the expression
                val (valuePart, nonValuePart) =
                        if (f.f is IntValue) {
                            f.f to f.g
                        } else if (f.g is IntValue) {
                            f.g to f.f
                        } else {
                            null to null
                        }
                // (6x)/3 == 2x || (3x)/6 == x/2 etc
                if (valuePart != null && nonValuePart != null && g is IntValue) {

                    val value = RationalValue(valuePart.value, g.value).simplify()

                    // (6x)/3 == 2x
                    if (value is IntValue) {
                        return (value * nonValuePart).simplify()
                    }

                    // (6x)/4 == (3x) / 2
                    if (value is RationalValue) {
                        return ((IntValue[value.top] * f.g) / IntValue[value.bottom]).simplify()
                    }

                    // (3x) / 6 == (x / 2)
                    val inverse = (g / valuePart).simplify()

                    if(inverse is IntValue) {
                        return (nonValuePart / inverse).simplify() // put value in denominator
                    }
                }
            }

            //TODO: x^c / x^d == x^(c-d)

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return DivisionExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f / $g)"
}