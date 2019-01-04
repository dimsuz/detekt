package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtNamedFunction

class LineCountVisitor : DetektVisitor() {
	var newLineCount: Int = 0
	val sloc get() = newLineCount + 1

	override fun visitNamedFunction(function: KtNamedFunction) {
		super.visitNamedFunction(function)
	}

	override fun visitWhiteSpace(space: PsiWhiteSpace?) {
		super.visitWhiteSpace(space)
		if (space?.isNewLine() == true) newLineCount++
	}
}

private fun PsiElement.isNewLine(): Boolean {
	return this is PsiWhiteSpace && this.textContains('\n')
}
