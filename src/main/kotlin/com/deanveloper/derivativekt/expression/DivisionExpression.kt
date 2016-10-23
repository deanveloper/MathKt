package com.deanveloper.derivativekt.expression

import java.math.BigDecimal

class DivisionExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): DivisionExpression {
        return DivisionExpression(vars, f.execute(args), g.execute(args))
    }

    override fun derive(variable: Char): Expression {
        // (h / l)' == (l * h' - h - l') / (l*l)
        return DivisionExpression(vars,
                SubtractionExpression(vars,
                        MultiplicationExpression(vars, g, f.derive(variable)),
                        MultiplicationExpression(vars, f, g.derive(variable))
                ),
                ExponentialExpression(g.vars, g, Value(BigDecimal.valueOf(2)))
        )
    }

    override fun simplify(): Expression {
        val simp = DivisionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (g is Value) {
                if (g.value === BigDecimal.ONE) {
                    return f
                }

                if (f is Value) {
                    if (g.value.compareTo(f.value) === 0) {
                        return Value(BigDecimal.ONE)
                    }

                    if (f.value.mod(g.value).compareTo(BigDecimal.ZERO) === 0) {
                        return Value(f.value / g.value)
                    }
                }
            }

            if (f is DivisionExpression) {
                return DivisionExpression(vars, f.f, MultiplicationExpression(vars, f.g, g))
            }

            if (f is MultiplicationExpression) {
                if (f.f is Value && g is Value) {
                    val value = f.f.value / g.value

                    if (value.mod(BigDecimal.ONE).compareTo(BigDecimal.ZERO) === 0) {
                        return MultiplicationExpression(vars, Value(value), f.g).simplify()
                    }

                    val inverse = g.value / f.f.value

                    if (inverse.mod(BigDecimal.ONE).compareTo(BigDecimal.ZERO) === 0) {
                        return DivisionExpression(vars, f.g, Value(value)).simplify() // put value in denominator
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