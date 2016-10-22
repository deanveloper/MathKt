package com.deanveloper.derivativekt

import java.math.BigDecimal

/**
 * Represents anything that returns a value.
 *
 * @author Dean
 * @since 1.0
 */
abstract class Expression(val variables: CharArray) {

    operator fun invoke(vararg args: Expression): BigDecimal {
        require(args.size === variables.size) { "This expression takes ${variables.size} arguments, not ${args.size}" }

        return execute(variables.zip(args).toMap())
    }

    internal abstract fun execute(args: Map<Char, Expression>): BigDecimal

    abstract fun derive(): Expression

    abstract class TwoPartExpression(variables: CharArray, val f: Expression, val g: Expression) : Expression(variables)
}

class MultiplicationExpression(variables: CharArray, f: Expression, g: Expression) : Expression.TwoPartExpression(variables, f, g) {

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) * g.execute(args)
    }

    override fun derive(): Expression {
        return AdditionExpression(variables,
                MultiplicationExpression(variables, f.derive(), g),
                MultiplicationExpression(variables, f, g.derive())
        )
    }

    override fun toString() = "($f * $g)"
}

class DivisionExpression(variables: CharArray, f: Expression, g: Expression) : Expression.TwoPartExpression(variables, f, g) {
    override fun derive(): Expression {
        return DivisionExpression(variables,
                SubtractionExpression(variables,
                        MultiplicationExpression(variables, g, f.derive()),
                        MultiplicationExpression(variables, f.derive(), g)
                ),
                MultiplicationExpression(variables, g, g) // ez
        )
    }

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) / g.execute(args)
    }

    override fun toString() = "($f / $g)"
}

class AdditionExpression(variables: CharArray, f: Expression, g: Expression) : Expression.TwoPartExpression(variables, f, g) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun derive(): Expression {
        return AdditionExpression(variables, f.derive(), g.derive())
    }

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) + g.execute(args)
    }

    override fun toString() = "($f + $g)"
}

class SubtractionExpression(variables: CharArray, f: Expression, g: Expression) : Expression.TwoPartExpression(variables, f, g) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) - g.execute(args)
    }

    override fun derive(): Expression {
        return SubtractionExpression(variables, f.derive(), g.derive())
    }

    override fun toString() = "($f - $g)"
}

class ExponentialExpression(variables: CharArray, f: Expression, g: Expression) : Expression.TwoPartExpression(variables, f, g) {

    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args).pow(g.execute(args))
    }

    override fun derive(): Expression {
        if (f.variables.isEmpty() && g.variables.isNotEmpty()) { // power function
            // when f(x) = c^x and c is constant, f'(x) = c^x * ln(x)
            return MultiplicationExpression(variables, this, LogrithmicExpression(variables, Value(E), f))
        } else if (f.variables.isNotEmpty() && g.variables.isEmpty()) { // exponential function
            // when f(x) = x^c and c is constant, f'(x) = c * x^(c-1)
            return MultiplicationExpression(variables,
                    g,
                    ExponentialExpression(variables,
                            f,
                            SubtractionExpression(variables,
                                    g,
                                    Value(BigDecimal.ONE)
                            )
                    )
            )
        } else if (f.variables.isNotEmpty() && g.variables.isNotEmpty()) { // weird-ass function
            // when f(x) = g(x) ^ h(x), f'(x) = f(x) * (h(x) / g(x) + h'(x) * ln(g(x))
            return MultiplicationExpression(variables,
                    this,
                    AdditionExpression(variables,
                            DivisionExpression(variables, g, f),
                            MultiplicationExpression(variables,
                                    g.derive(),
                                    LogrithmicExpression(variables, Value(E), g)
                            )
                    )
            )
        } else {
            // when f(x) = c ^ d, where c and d are constants, f'(x) = 0
            return Value(BigDecimal.ZERO)
        }
    }

    override fun toString() = "($f ^ $g)"
}

class LogrithmicExpression(variables: CharArray, val base: Expression, f: Expression) : Expression.TwoPartExpression(variables, f, base) {
    constructor(variable: Char, f: Expression, g: Expression) : this(charArrayOf(variable), f, g)

    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args).ln() / base.execute(args).ln()
    }

    override fun derive(): Expression {
        if (base is Value) {
            if (base.value == E) {
                // when f(x) = ln(x), f'(x) is 1/x
                return DivisionExpression(variables, Value(BigDecimal.ONE), f)
            } else {
                // when f(x) = logBASE(c, x) and c is constant, f'(x) = (1/x) / ln(c)
                return DivisionExpression(variables,
                        DivisionExpression(variables, Value(BigDecimal.ONE), f),
                        LogrithmicExpression(variables, Value(E), base)
                )
            }
        } else {
            // when h(x) = logBASE(b(x), f(x)), h(x) = ln(f(x)) / ln(b(x)) and use quotient rule to get h'(x)
            return DivisionExpression(variables,
                    LogrithmicExpression(variables, Value(E), f),
                    LogrithmicExpression(variables, Value(E), base)
            ).derive()
        }
    }


    override fun toString(): String {
        if (base is Value && Math.abs(base.value.compareTo(E)) === 0) {
            return "ln($f)"
        } else {
            return "logBASE($base,$f)"
        }
    }
}

class Variable(
        val variable: Char
) : Expression(charArrayOf(variable)) {
    override fun execute(args: Map<Char, Expression>) = args[variable]!!.execute(emptyMap())
    override fun derive() = Value(BigDecimal.ONE)
    override fun toString() = variable.toString()
}

class Value(val value: BigDecimal) : Expression(charArrayOf()) {
    override fun execute(args: Map<Char, Expression>) = value
    override fun derive() = Value(BigDecimal.ZERO)
    override fun toString() = value.toString()
}