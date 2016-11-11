package com.deanveloper.mathkt.expression.twopartexpression

import com.deanveloper.mathkt.E
import com.deanveloper.mathkt.expression.Expression
import com.deanveloper.mathkt.expression.value.RealValue
import com.deanveloper.mathkt.ln
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
        return LogExpression(vars, base.execute(args), f.execute(args))
    }

    override fun derive(variable: Char): Expression {
        if (base is RealValue) {
            if (base.value == E) {
                // when f(x) = ln(x), f'(x) is 1/x
                return MultiplicationExpression(vars,
                        DivisionExpression(vars,
                                RealValue(BigDecimal.ONE),
                                f
                        ),
                        f.derive(variable)
                )
            } else {
                // when f(x) = logBASE(c, x) and c is constant, f'(x) = (1/x) / ln(c)
                return MultiplicationExpression(vars,
                        DivisionExpression(vars,
                                DivisionExpression(vars, RealValue(BigDecimal.ONE), f),
                                LogExpression(vars, RealValue(E), base)
                        ),
                        f.derive(variable)
                )
            }
        } else {
            // when h(x) = logBASE(b(x), f(x)), h(x) = ln(f(x)) / ln(b(x)) and use quotient rule to get h'(x)
            return DivisionExpression(vars,
                    LogExpression(vars, RealValue(E), f),
                    LogExpression(vars, RealValue(E), base)
            )
        }
    }

    override fun simplify(): Expression {
        val simp = LogExpression(vars, base.simplify(), f.simplify())
        with(simp) {
            if (f is ExponentialExpression) {
                return MultiplicationExpression(vars, f.g, LogExpression(vars, base, f.f)).simplify()
            }
            if (f is RealValue) {
                if (f.value.compareTo(BigDecimal.ONE) === 0) {
                    return RealValue(BigDecimal.ZERO)
                }

                if (base is RealValue) {
                    if (base == f) {
                        return RealValue(BigDecimal.ONE)
                    } else if (base == RealValue(E)) {
                        return RealValue(f.value.ln())
                    } else {
                        return RealValue(f.value.ln() / base.value.ln())
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
        if (base is RealValue && Math.abs(base.value.compareTo(E)) === 0) {
            return "${if (isNegative) "-" else ""}ln($f)"
        } else {
            return "${if (isNegative) "-" else ""}logBASE($base,$f)"
        }
    }
}