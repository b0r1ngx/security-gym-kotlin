package org.jetbrains.amper

import org.springframework.expression.EvaluationContext
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.expression.common.TemplateParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.SimpleEvaluationContext
import org.springframework.expression.spel.support.StandardEvaluationContext

class SpEL {
    // The given code processing of SpEL expressions with the ability to create files in the system (RCE).
    fun processString(string: String): String {
        val context: EvaluationContext = StandardEvaluationContext()
        val parser: ExpressionParser = SpelExpressionParser()
        val expression: Expression = parser.parseExpression(string)
        val result = expression.getValue(context, TemplateParserContext())
        return result.toString()
    }

    // 1. Replace StandardEvaluationContext with SimpleEvaluationContext with limited capabilities.
    // 2. Using forReadOnlyDataBinding() for additional restriction of rights.
    // 3. Moving TemplateParserContext to parseExpression method for correct template handling.
    fun fixedProcessString(string: String): String {
        val context: EvaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().build()
        val parser: ExpressionParser = SpelExpressionParser()
        val expression: Expression = parser.parseExpression(string, TemplateParserContext())
        val result = expression.getValue(context)
        return result.toString()
    }
}
