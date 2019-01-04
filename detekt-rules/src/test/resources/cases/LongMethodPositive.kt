package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class LongMethodPositive {

	// reports 1 - too many statements
	fun longMethod() {
		println()
		println()
		println()

		// reports 1 - too many statements
		fun nestedLongMethod() {
			println()
			println()
			println()
		}

	}

	fun longMethodSingleStatement() {
		if ("one" === "three") {
			println("weird")
		} else {
			println("sane")
		}
	}
}
