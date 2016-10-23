package com.deanveloper.derivativekt.expression.twopartexpression

import com.deanveloper.derivativekt.E
import com.deanveloper.derivativekt.expression.Expression
import com.deanveloper.derivativekt.expression.Value
import com.deanveloper.derivativekt.ln
import java.math.BigDecimal

class LogExpression(
        variables: CharArray,
        val base: Expression,
        f: Expression,
        isNegative: Boolean = false
) : Expression.TwoPartExpression(variables, f, base, isNegative) {
    constructor(
            variable: Char,
            base: Expression,
            f: Expression,
            isNegative: Boolean = false
    ) : this(charArrayOf(variable), base, f, isNegative)

    override fun execute(args: Map<Char, Expression>): LogExpression {
        return LogExpression(vars, f.execute(args), g.execute(args))
    }

    override fun derive(variable: Char): Expression {
        if (base is Value) {
            if (base.value == E) {
                // when f(x) = ln(x), f'(x) is 1/x
                return MultiplicationExpression(vars,
                        DivisionExpression(vars,
                                Value(BigDecimal.ONE),
                                f
                        ),
                        f.derive(variable)
                )
            } else {
                // when f(x) = logBASE(c, x) and c is constant, f'(x) = (1/x) / ln(c)
                return MultiplicationExpression(vars,
                        DivisionExpression(vars,
                                DivisionExpression(vars, Value(BigDecimal.ONE), f),
                                LogExpression(vars, Value(E), base)
                        ),
                        f.derive(variable)
                )
            }
        } else {
            // when h(x) = logBASE(b(x), f(x)), h(x) = ln(f(x)) / ln(b(x)) and use quotient rule to get h'(x)
            return DivisionExpression(vars,
                    LogExpression(vars, Value(E), f),
                    LogExpression(vars, Value(E), base)
            )
        }
    }

    override fun simplify(): Expression {
        val simp = LogExpression(vars, base.simplify(), f.simplify())
        with(simp) {
            if (f is ExponentialExpression) {
                return MultiplicationExpression(vars, f.g, LogExpression(vars, base, f.f)).simplify()
            }
            if (f is Value) {
                if (f.value.compareTo(BigDecimal.ONE) === 0) {
                    return Value(BigDecimal.ZERO)
                }

                if (base is Value) {
                    if (base == f) {
                        return Value(BigDecimal.ONE)
                    } else {
                        return Value(f.value.ln() / base.value.ln())
                    }
                }
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return LogExpression(vars, f, g, !isNegative)
    }

    override fun toString(): String {
        if (base is Value && Math.abs(base.value.compareTo(E)) === 0) {
            return "${if (isNegative) "-" else ""}ln($f)"
        } else {
            return "${if (isNegative) "-" else ""}logBASE($base,$f)"
        }
    }
}