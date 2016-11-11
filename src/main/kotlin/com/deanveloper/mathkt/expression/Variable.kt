package com.deanveloper.mathkt.expression

import com.deanveloper.mathkt.expression.value.IntValue

class Variable
@Deprecated("Use get() for automatic caching", ReplaceWith("Variable[variable]"))
constructor(val variable: Char, negative: Boolean = false) : Expression(charArrayOf(variable), negative) {

    companion object {
        @JvmStatic
        private val cache = mutableMapOf<Pair<Char, Boolean>, Variable>()

        @[JvmOverloads JvmStatic] operator fun get(value: Char, negative: Boolean = false): Variable {
            return cache.getOrPut(value to negative) { Variable(value, negative) }
        }
        @[JvmOverloads JvmStatic] fun valueOf(value: Char, negative: Boolean = false) = get(value, negative)
    }

    override fun execute(args: Map<Char, Expression>) = args[variable]?.execute(args) ?: this
    override fun derive(variable: Char): Expression {
        if (variable === this.variable) {
            return IntValue[1]
        } else {
            throw IllegalArgumentException("Cannot take derivative of $this with respect to $variable, " +
                    "define $this")
        }
    }

    override fun simplify() = this // do nothing
    override fun toString() = "${if (isNegative) "-" else ""}$variable"
    override fun hashCode() = variable.hashCode()
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
}