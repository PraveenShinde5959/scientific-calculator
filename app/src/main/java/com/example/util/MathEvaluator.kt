package com.example.util

import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cbrt
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

object MathEvaluator {

    fun evaluate(expression: String, isRadian: Boolean = false): EvaluationResult {
        if (expression.isBlank()) return EvaluationResult.Success("", "")

        try {
            val sanitized = prepareExpression(expression)
            val tokens = tokenize(sanitized)
            val ast = Parser(tokens).parse()
            val resultValue = evaluateAst(ast, isRadian)

            if (resultValue.isNaN()) {
                return EvaluationResult.Error("Undefined Result")
            }
            if (resultValue.isInfinite()) {
                return EvaluationResult.Error("Cannot divide by 0")
            }

            val formatted = formatNumber(resultValue)
            return EvaluationResult.Success(sanitized, formatted)
        } catch (e: ArithmeticException) {
            return EvaluationResult.Error(e.message ?: "Arithmetic Error")
        } catch (e: IllegalArgumentException) {
            return EvaluationResult.Error(e.message ?: "Invalid Input")
        } catch (e: Exception) {
            return EvaluationResult.Error("Syntax Error")
        }
    }

    private fun prepareExpression(raw: String): String {
        return raw
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "PI")
            .replace("MOD", "%")
            .replace("mod", "%")
            .replace("e", "E_CONST")
            .replace("E_CONSTx", "ex")
    }

    private fun formatNumber(value: Double): String {
        if (value == 0.0 || value == -0.0) return "0"
        
        if (abs(value - round(value)) < 1e-10) {
            return String.format("%.0f", value)
        }
        
        if (abs(value) >= 1e12 || (abs(value) <= 1e-6 && abs(value) > 0.0)) {
            return String.format("%.6e", value)
                .replace("e+", "e")
                .replace(".000000", "")
        }

        return String.format("%.10f", value)
            .trimEnd('0')
            .trimEnd('.')
    }

    private fun factorial(n: Double): Double {
        if (n < 0 || n != floor(n)) throw IllegalArgumentException("Factorial needs non-negative integer")
        if (n > 170) throw ArithmeticException("Overflow")
        var res = 1.0
        for (i in 2..n.toInt()) {
            res *= i
        }
        return res
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++
                c in "0123456789." -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i] in "0123456789." || expr[i] == 'E')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isLetter() || expr[i] == '_')) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString()
                    tokens.add(word)
                }
                c in "+-*/^%!()" -> {
                    tokens.add(c.toString())
                    i++
                }
                c == '√' -> {
                    tokens.add("sqrt")
                    i++
                }
                else -> {
                    i++
                }
            }
        }

        val result = mutableListOf<String>()
        for (idx in tokens.indices) {
            val curr = tokens[idx]
            result.add(curr)
            if (idx < tokens.size - 1) {
                val next = tokens[idx + 1]
                val currIsVal = isNumber(curr) || curr == "PI" || curr == "E_CONST" || curr == ")" || curr == "!"
                val nextIsValOrFunc = isNumber(next) || next == "PI" || next == "E_CONST" || next == "(" || isFunction(next)
                if (currIsVal && nextIsValOrFunc) {
                    result.add("*")
                }
            }
        }
        return result
    }

    private fun isNumber(token: String): Boolean = token.toDoubleOrNull() != null

    private fun isFunction(token: String): Boolean {
        return token in listOf(
            "sin", "cos", "tan", "asin", "acos", "atan",
            "sinh", "cosh", "tanh", "log", "ln", "sqrt",
            "cbrt", "fact", "abs", "floor", "ceil", "round", "rand"
        )
    }

    private sealed class Node {
        data class Num(val value: Double) : Node()
        data class Constant(val name: String) : Node()
        data class Unary(val op: String, val operand: Node) : Node()
        data class Binary(val op: String, val left: Node, val right: Node) : Node()
        data class FuncCall(val name: String, val arg: Node) : Node()
    }

    private class Parser(private val tokens: List<String>) {
        private var pos = 0

        private fun peek(): String? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): String = tokens[pos++]

        fun parse(): Node = parseExpressionAtPrecedence(0)

        private fun parsePrimary(): Node {
            val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")
            
            if (token == "-" || token == "+") {
                consume()
                val operand = parsePrimary()
                return Node.Unary(token, operand)
            }

            if (token == "(") {
                consume()
                val exprNode = parseExpressionAtPrecedence(0)
                if (peek() == ")") consume()
                return exprNode
            }

            if (isFunction(token)) {
                val func = consume()
                var hasParen = false
                if (peek() == "(") {
                    consume()
                    hasParen = true
                }
                val arg = parseExpressionAtPrecedence(0)
                if (hasParen && peek() == ")") consume()
                return Node.FuncCall(func, arg)
            }

            if (token == "PI" || token == "E_CONST") {
                consume()
                return Node.Constant(token)
            }

            if (token == "rand") {
                consume()
                if (peek() == "(") {
                    consume()
                    if (peek() == ")") consume()
                }
                return Node.FuncCall("rand", Node.Num(0.0))
            }

            if (isNumber(token)) {
                consume()
                return Node.Num(token.toDouble())
            }

            throw IllegalArgumentException("Unexpected token: $token")
        }

        private fun parsePostfix(): Node {
            var node = parsePrimary()
            while (peek() == "!") {
                val op = consume()
                node = Node.Unary(op, node)
            }
            return node
        }

        private fun getPrecedence(op: String): Int {
            return when (op) {
                "+", "-" -> 1
                "*", "/", "%" -> 2
                "^" -> 3
                else -> 0
            }
        }

        private fun parseExpressionAtPrecedence(minPrec: Int): Node {
            var left = parsePostfix()

            while (true) {
                val op = peek() ?: break
                if (op !in listOf("+", "-", "*", "/", "%", "^")) break
                val prec = getPrecedence(op)
                if (prec < minPrec) break

                consume()
                val nextMinPrec = if (op == "^") prec else prec + 1
                val right = parseExpressionAtPrecedence(nextMinPrec)
                left = Node.Binary(op, left, right)
            }

            return left
        }
    }

    private fun evaluateAst(node: Node, isRadian: Boolean): Double {
        return when (node) {
            is Node.Num -> node.value
            is Node.Constant -> when (node.name) {
                "PI" -> PI
                "E_CONST" -> E
                else -> 0.0
            }
            is Node.Unary -> {
                val valOp = evaluateAst(node.operand, isRadian)
                when (node.op) {
                    "-" -> -valOp
                    "+" -> valOp
                    "!" -> factorial(valOp)
                    else -> valOp
                }
            }
            is Node.Binary -> {
                val l = evaluateAst(node.left, isRadian)
                val r = evaluateAst(node.right, isRadian)
                when (node.op) {
                    "+" -> l + r
                    "-" -> l - r
                    "*" -> l * r
                    "/" -> {
                        if (r == 0.0) throw ArithmeticException("Cannot divide by 0")
                        l / r
                    }
                    "%" -> {
                        if (r == 0.0) throw ArithmeticException("Cannot divide by 0")
                        l % r
                    }
                    "^" -> l.pow(r)
                    else -> 0.0
                }
            }
            is Node.FuncCall -> {
                val arg = evaluateAst(node.arg, isRadian)
                val radArg = if (isRadian) arg else Math.toRadians(arg)
                when (node.name) {
                    "sin" -> sin(radArg)
                    "cos" -> cos(radArg)
                    "tan" -> tan(radArg)
                    "asin" -> {
                        if (arg < -1.0 || arg > 1.0) throw IllegalArgumentException("Domain Error")
                        val resRad = asin(arg)
                        if (isRadian) resRad else Math.toDegrees(resRad)
                    }
                    "acos" -> {
                        if (arg < -1.0 || arg > 1.0) throw IllegalArgumentException("Domain Error")
                        val resRad = acos(arg)
                        if (isRadian) resRad else Math.toDegrees(resRad)
                    }
                    "atan" -> {
                        val resRad = atan(arg)
                        if (isRadian) resRad else Math.toDegrees(resRad)
                    }
                    "sinh" -> sinh(arg)
                    "cosh" -> cosh(arg)
                    "tanh" -> tanh(arg)
                    "log" -> {
                        if (arg <= 0) throw IllegalArgumentException("Domain Error")
                        log10(arg)
                    }
                    "ln" -> {
                        if (arg <= 0) throw IllegalArgumentException("Domain Error")
                        ln(arg)
                    }
                    "sqrt" -> {
                        if (arg < 0) throw IllegalArgumentException("Domain Error")
                        sqrt(arg)
                    }
                    "cbrt" -> cbrt(arg)
                    "fact" -> factorial(arg)
                    "abs" -> abs(arg)
                    "floor" -> floor(arg)
                    "ceil" -> ceil(arg)
                    "round" -> round(arg).toDouble()
                    "rand" -> Math.random()
                    else -> 0.0
                }
            }
        }
    }
}

sealed class EvaluationResult {
    data class Success(val expression: String, val result: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}
