package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class LineCountVisitorSpec : Spek({
	it("counts source lines") {
		val code = """
				fun test() {
					println()
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}

	it("ignores empty lines") {
		val code = """
				fun test() {
					println()

					println()


					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}

	it("ignores line-comments") {
		val code = """
				fun test() {
					// commented println()
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(4)
	}

	it("counts lines ending with line-comment") {
		val code = """
				fun test() {
					println() // comment
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}

	it("counts lines ending with block-comment") {
		val code = """
				fun test() {
					println() /* comment */
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}

	it("counts lines ending with multi-line block-comment") {
		val code = """
				fun test() {
					println() /* multi
					line
					comment
					*/
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}

	it("ignores single line block-comments") {
		val code = """
				fun test() {
					/* commented println() */
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(4)
	}

	it("ignores multi-line line block-comments") {
		val code = """
				fun test() {
					/* commented
                       println() */
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(4)
	}

	it("counts lines starting after block-comment") {
		val code = """
				fun test() {
					/* hello */ println()
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}

	it("counts lines starting after mutli-line block-comment") {
		val code = """
				fun test() {
					/*
					   hello
					   world */ println()
					println()
					println()
				}
			"""
		val subject = LineCountVisitor()

		subject.visitFile(code.compile())

		assertThat(subject.sloc).isEqualTo(5)
	}
})

private fun String.compile() = KtTestCompiler.compileFromContent(this.trimIndent())
