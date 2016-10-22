package com.deanveloper.derivativekt

import java.math.BigDecimal
import java.util.*

/**
 * Represents anything that returns a value.
 *
 * @author Dean
 * @since 1.0
 */
abstract class Expression(val variables: LinkedHashSet<Char>) {

    operator fun invoke(vararg args: Expression): BigDecimal {
        require(args.size === variables.size) { "This expression takes ${variables.size} arguments, not ${args.size}" }

        return execute(variables.zip(args).toMap())
    }

    internal abstract fun execute(args: Map<Char, Expression>): BigDecimal

    abstract class TwoPartExpression(val f: Expression, val g: Expression) : Expression(
            extractVariables(f, g)
    ) {
        companion object {
            fun extractVariables(vararg expressions: Expression): LinkedHashSet<Char> {
                return expressions.filter {
                    it is Variable
                }.map {
                    (it as Variable).variable
                }.toCollection(LinkedHashSet())
            }
        }
    }
}

class MultiplicationExpression(f: Expression, g: Expression) : Expression.TwoPartExpression(f, g) {
    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) * g.execute(args)
    }
    override fun toString() = "($f * $g)"
}

class DivisionExpression(f: Expression, g: Expression) : Expression.TwoPartExpression(f, g) {
    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) / g.execute(args)
    }
    override fun toString() = "($f / $g)"
}

class AdditionExpression(f: Expression, g: Expression) : Expression.TwoPartExpression(f, g) {
    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) + g.execute(args)
    }
    override fun toString() = "($f + $g)"
}

class SubtractionExpression(f: Expression, g: Expression) : Expression.TwoPartExpression(f, g) {
    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args) - g.execute(args)
    }
    override fun toString() = "($f - $g)"
}

class ExponentialExpression(f: Expression, g: Expression) : Expression.TwoPartExpression(f, g) {
    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args).pow(g.execute(args))
    }
    override fun toString() = "($f ^ $g)"
}

class LogrithmicExpression(val base: Expression, f: Expression) : Expression.TwoPartExpression(f, base) {
    override fun execute(args: Map<Char, Expression>): BigDecimal {
        return f.execute(args).ln(50) / base.execute(args).ln(50)
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
) : Expression(linkedSetOf(variable)) {
    override fun execute(args: Map<Char, Expression>) = args[variable]!!.execute(emptyMap())
    override fun toString() = variable.toString()
}

class Value(val value: BigDecimal) : Expression(linkedSetOf()) {
    override fun execute(args: Map<Char, Expression>) = value
    override fun toString() = value.toString()
}