package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.E
import com.deanveloper.mathkt.expression.Expression
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

    override fun execute(args: Map<Char, Expression>): ExponentialExpression {
        return ExponentialExpression(vars, f.execute(args), g.execute(args))
    }

    override fun derive(variable: Char): Expression {
        if (f.vars.isEmpty() && g.vars.isNotEmpty()) { // power function
            // when f(x) = c^x and c is constant, f'(x) = c^x * ln(x) (with chain rule appended)
            return MultiplicationExpression(vars,
                    MultiplicationExpression(vars,
                            this,
                            LogExpression(vars,
                                    RealValue(E),
                                    f
                            )
                    ),
                    g.derive(variable)
            )
        } else if (f.vars.isNotEmpty() && g.vars.isEmpty()) { // exponential function
            // when f(x) = x^c and c is constant, f'(x) = c * x^(c-1) (with chain rule appended)
            return MultiplicationExpression(vars,
                    MultiplicationExpression(vars,
                            g,
                            ExponentialExpression(vars,
                                    f,
                                    SubtractionExpression(vars,
                                            g,
                                            RealValue(BigDecimal.ONE)
                                    )
                            )
                    ),
                    f.derive(variable)
            )
        } else if (f.vars.isNotEmpty() && g.vars.isNotEmpty()) { // weird-ass function
            // when h(x) = f(x) ^ g(x), h'(x) = h(x) * (g(x) / f(x) + g'(x) * ln(g(x))
            return MultiplicationExpression(vars,
                    this,
                    AdditionExpression(vars,
                            DivisionExpression(vars, g, f),
                            MultiplicationExpression(vars,
                                    g.derive(variable),
                                    LogExpression(vars, RealValue(E), f)
                            )
                    )
            )

        } else {
            // when f(x) = c ^ d, where c and d are constants, f'(x) = 0
            return RealValue(BigDecimal.ZERO)
        }
    }

    override fun simplify(): Expression {
        val simp = ExponentialExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is RealValue && g is RealValue) {
                return RealValue(f.value.pow(g.value))
            }
            if (f is ExponentialExpression) {
                return ExponentialExpression(vars, f.f, MultiplicationExpression(vars, f.g, g)).simplify()
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return ExponentialExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f ^ $g)"
}