package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.internal.LineCountVisitor
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.allChildren

/**
 * Methods should have one responsibility. Long methods can indicate that a method handles too many cases at once.
 * Prefer smaller methods with clear names that describe their functionality clearly.
 *
 * Extract parts of the functionality of long methods into separate, smaller methods.
 *
 * @configuration threshold - maximum lines in a method (default: 20)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class LongMethod(config: Config = Config.empty,
				 threshold: Int = DEFAULT_ACCEPTED_METHOD_LENGTH) : ThresholdRule(config, threshold) {

	override val issue = Issue("LongMethod",
			Severity.Maintainability,
			"One method should have one responsibility. Long methods tend to handle many things at once. " +
					"Prefer smaller methods to make them easier to understand.",
			Debt.TWENTY_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		val body: KtBlockExpression? = function.bodyExpression.asBlockExpression()
		body?.let {
			val size = body.statements.size
			println("found $size statements in function ${function.name}")
//			println("found ${countNewLines(body)} newlines in function ${function.name}")
			val visitor = LineCountVisitor()
			visitor.visitNamedFunction(function)
			println("found ${visitor.newLineCount} newlines in function ${function.name}")
			println("===")
			if (size >= threshold) report(
					ThresholdedCodeSmell(issue,
							Entity.from(function),
							Metric("SIZE", size, threshold),
							"The function ${function.nameAsSafeName} is too long. The maximum length is " +
									"$threshold."))
		}
		super.visitNamedFunction(function)
	}

	private fun countNewLines(body: PsiElement): Int {
		val allChildren = if (body.parent is KtNamedFunction) {
			val firstNonWhiteSpace = body.children.firstOrNull()
			body.allChildren
				.dropWhile { it != firstNonWhiteSpace }
				.takeWhile {
					// TODO test this:
					// fun f() {
					//   if (s) { } } // <-- same line
					// and this
					// fun f() { println() }
					// and this
					// fun f() { println()
					//    println()
					// }
					// and this
					// fun f() { println() }
					// and this
					// fun f() { for (i in (0..10)) { println(i) } }
					// and this
					// fun f() = for (i in (0..10)) { println(i) }
					// and this
					// fun f() = println(i)
					!(isNewLine(it) && it.nextSibling.node.elementType == KtTokens.RBRACE)
				}
		} else body.allChildren
		return allChildren.sumBy { child ->
			when {
				!child.allChildren.isEmpty -> countNewLines(child)
				isNewLine(child) -> 1
				else -> 0
			}
		}
	}

	private fun isNewLine(element: PsiElement): Boolean {
		return element is PsiWhiteSpace && element.textContains('\n')
	}

	companion object {
		const val DEFAULT_ACCEPTED_METHOD_LENGTH = 20
	}
}
