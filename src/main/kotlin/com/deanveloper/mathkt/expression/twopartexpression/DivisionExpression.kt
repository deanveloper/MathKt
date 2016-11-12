package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.IntValue
import com.deanveloper.mathkt.expression.value.RationalValue
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
        return DivisionExpression(vars, f.insertValues(args), g.insertValues(args))
    }

    override fun derive(variable: Char): Expression {
        // (h / l)' == (l * h' - h - l') / (l*l)
        return DivisionExpression(vars,
                SubtractionExpression(vars,
                        MultiplicationExpression(vars, g, f.derive(variable)),
                        MultiplicationExpression(vars, f, g.derive(variable))
                ),
                ExponentialExpression(g.vars, g, IntValue[2])
        )
    }

    override fun simplify(): Expression {
        val simp = DivisionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (g is IntValue) {
                if (g == IntValue[1]) {
                    return f
                }

                if (f is IntValue) {
                    return RationalValue(f, g).simplify()
                }
            }

            if (f is DivisionExpression) {
                // (f/g)/h == f/(g*h)
                return DivisionExpression(vars, f.f, MultiplicationExpression(vars, f.g, g)).simplify()
            }

            // same as above but for values
            if (f is RationalValue) {
                return DivisionExpression(vars, f.top, MultiplicationExpression(vars, f.bottom, g)).simplify()
            }

            // f/(g/h) == (f*h)/g
            if (g is DivisionExpression) {
                return DivisionExpression(vars, MultiplicationExpression(vars, f, g.g), g.f).simplify()
            }

            // same as above but for values
            if (g is RationalValue) {
                return DivisionExpression(vars, MultiplicationExpression(vars, f, g.bottom), g.top).simplify()
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

                    val value = RationalValue(valuePart, g).simplify()

                    // (6x)/3 == 2x
                    if (value is IntValue) {
                        return (value * nonValuePart).simplify()
                    }

                    // (6x)/4 == (3x) / 2
                    if (value is RationalValue) {
                        return ((value.top * f.g) / value.bottom).simplify()
                    }

                    // (3x) / 6 == (x / 2)
                    val inverse = (g.onDiv(valuePart)).simplify()

                    if(inverse is IntValue) {
                        return DivisionExpression(vars, nonValuePart, inverse).simplify() // put value in denominator
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