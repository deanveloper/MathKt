package com.deanveloper.derivativekt.expression

import com.deanveloper.derivativekt.E
import com.deanveloper.derivativekt.ln
import com.deanveloper.derivativekt.pow
import java.math.BigDecimal

/**
 * Represents anything that returns a value.
 *
 * @author Dean
 * @since 1.0
 */
abstract class Expression(val vars: CharArray, val isNegative: Boolean = false) {

    operator fun invoke(vararg args: Expression): BigDecimal {
        require(args.size === vars.size) { "This expression takes ${vars.size} arguments, not ${args.size}" }

        return execute(vars.zip(args).toMap())
    }

    internal abstract fun execute(args: Map<Char, Expression>): BigDecimal

    abstract fun derive(): Expression

    abstract class TwoPartExpression(
            variables: CharArray,
            val f: Expression,
            val g: Expression,
            negative: Boolean = false)
    : Expression(variables, negative)

    abstract fun simplify(): Expression

    abstract operator fun unaryMinus(): Expression
}

class MultiplicationExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) * g.execute(args)
    }

    override fun derive(): Expression {
        // (f * g)' == f * g' + f' * g
        return AdditionExpression(vars,
                MultiplicationExpression(vars, f.derive(), g),
                MultiplicationExpression(vars, f, g.derive())
        )
    }

    override fun simplify(): Expression {
        val simp = MultiplicationExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is Value && g is Value) {
                return Value(f.value * g.value)
            }
            if (f is Value) {
                if (f.value === BigDecimal.ONE) { // 1 * g == g
                    return g
                } else if (f.value === BigDecimal.ZERO) { // 0 * g == 0
                    return f // value 0
                }
            }
            if (g is Value) {
                if (g.value === BigDecimal.ONE) { // f * 1 == f
                    return f
                } else if (g.value === BigDecimal.ZERO) { // f * 0 == 0
                    return g // value 0
                }
            }
            if (f is DivisionExpression) {
                return DivisionExpression(vars, MultiplicationExpression(vars, g, f.f).simplify(), f.g)
            }
            if (g is DivisionExpression) {
                return DivisionExpression(vars, MultiplicationExpression(vars, f, g.f).simplify(), g.g)
            }

            //TODO: x^c * x^d == x^(c+d)
        }
        return this
    }

    override operator fun unaryMinus(): Expression {
        return MultiplicationExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f * $g)"
}

class DivisionExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) / g.execute(args)
    }

    override fun derive(): Expression {
        // (h / l)' == (l * h' - h - l') / (l*l)
        return DivisionExpression(vars,
                SubtractionExpression(vars,
                        MultiplicationExpression(vars, g, f.derive()),
                        MultiplicationExpression(vars, f, g.derive())
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

class AdditionExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) + g.execute(args)
    }

    override fun derive(): Expression {
        return AdditionExpression(vars, f.derive(), g.derive())
    }

    override fun simplify(): Expression {
        val simp = AdditionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is Value && g is Value) {
                return Value(f.value + g.value)
            }

            if (f is Value) {
                if (f.isNegative) {
                    return SubtractionExpression(vars, g, f)
                }
                if (f.value.signum() === 0) {
                    return g
                }
            }

            if (g is Value) {
                if (g.isNegative) {
                    return SubtractionExpression(vars, f, g)
                }
                if (g.value.signum() === 0) {
                    return f
                }
            }

            //TODO: cx + dx = (c+d)x (only perform if c+d makes a nice number)
        }

        return this
    }

    override fun unaryMinus(): Expression {
        return AdditionExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f + $g)"
}

class SubtractionExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) - g.execute(args)
    }

    override fun derive(): Expression {
        return SubtractionExpression(vars, f.derive(), g.derive())
    }

    override fun simplify(): Expression {
        val simp = SubtractionExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (g.isNegative) {
                return AdditionExpression(vars, f, -g).simplify()
            }
            if (f is Value && g is Value) {
                return Value(f.value - g.value)
            }
            if (f is Value) {
                if (f.value.signum() === 0) {
                    return -g
                }
            }

            if (g is Value) {
                if (g.value.signum() < 0) {
                    return AdditionExpression(vars, f, g)
                }
                if (g.value.signum() === 0) {
                    return f
                }
            }

            return this
        }
    }

    override fun unaryMinus(): Expression {
        return SubtractionExpression(vars, f, g, !isNegative)
    }

    override fun toString() = "${if (isNegative) "-" else ""}($f - $g)"
}

class ExponentialExpression(variables: CharArray, f: Expression, g: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, g, negative) {

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args).pow(g.execute(args))
    }

    override fun derive(): Expression {
        if (f.vars.isEmpty() && g.vars.isNotEmpty()) { // power function
            // when f(x) = c^x and c is constant, f'(x) = c^x * ln(x)
            return MultiplicationExpression(vars, this, LogExpression(vars, Value(E), f))
        } else if (f.vars.isNotEmpty() && g.vars.isEmpty()) { // exponential function
            // when f(x) = x^c and c is constant, f'(x) = c * x^(c-1)
            return MultiplicationExpression(vars,
                    g,
                    ExponentialExpression(vars,
                            f,
                            SubtractionExpression(vars,
                                    g,
                                    Value(BigDecimal.ONE)
                            )
                    )
            )
        } else if (f.vars.isNotEmpty() && g.vars.isNotEmpty()) { // weird-ass function
            // when h(x) = f(x) ^ g(x), h'(x) = h(x) * (g(x) / f(x) + g'(x) * ln(g(x))
            return MultiplicationExpression(vars,
                    this,
                    AdditionExpression(vars,
                            DivisionExpression(vars, g, f),
                            MultiplicationExpression(vars,
                                    g.derive(),
                                    LogExpression(vars, Value(E), f)
                            )
                    )
            )
        } else {
            // when f(x) = c ^ d, where c and d are constants, f'(x) = 0
            return Value(BigDecimal.ZERO)
        }
    }

    override fun simplify(): Expression {
        val simp = ExponentialExpression(vars, f.simplify(), g.simplify())
        with(simp) {
            if (f is Value && g is Value) {
                return Value(f.value.pow(g.value))
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

class LogExpression(variables: CharArray, val base: Expression, f: Expression, negative: Boolean = false) : Expression.TwoPartExpression(variables, f, base, negative) {

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args).ln() / base.execute(args).ln()
    }

    override fun derive(): Expression {
        if (base is Value) {
            if (base.value == E) {
                // when f(x) = ln(x), f'(x) is 1/x
                return DivisionExpression(vars, Value(BigDecimal.ONE), f)
            } else {
                // when f(x) = logBASE(c, x) and c is constant, f'(x) = (1/x) / ln(c)
                return DivisionExpression(vars,
                        DivisionExpression(vars, Value(BigDecimal.ONE), f),
                        LogExpression(vars, Value(E), base)
                )
            }
        } else {
            // when h(x) = logBASE(b(x), f(x)), h(x) = ln(f(x)) / ln(b(x)) and use quotient rule to get h'(x)
            return DivisionExpression(vars,
                    LogExpression(vars, Value(E), f),
                    LogExpression(vars, Value(E), base)
            ).derive()
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

class Variable(val variable: Char, negative: Boolean = false) : Expression(charArrayOf(variable), negative) {
    override fun execute(args: Map<Char, Expression>) = args[variable]!!.execute(emptyMap())
    override fun derive() = Value(BigDecimal.ONE)
    override fun simplify() = this // do nothing
    override fun toString() = "${if (isNegative) "-" else ""}$variable"
    override fun equals(other: Any?): Boolean {
        if (other is Variable) {
            if (other.variable === variable && other.isNegative === isNegative) {
                return true
            }
        }

        return false
    }

    override fun unaryMinus(): Expression {
        return Variable(variable, !isNegative)
    }

    override fun hashCode(): Int {
        return variable.hashCode()
    }
}

class Value(val value: BigDecimal) : Expression(charArrayOf(), value.signum() === -1) {
    override fun execute(args: Map<Char, Expression>) = value
    override fun derive() = Value(BigDecimal.ZERO)
    override fun simplify(): Value = this // do nothing
    override fun toString() = value.toPlainString()!!
    override fun equals(other: Any?): Boolean {
        if (other is Value) {
            return value.compareTo(other.value) === 0
        }

        return false
    }

    override fun unaryMinus(): Expression {
        return Value(-value)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}