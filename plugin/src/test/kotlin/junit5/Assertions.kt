package junit5

import org.junit.jupiter.api.Assertions

class AssertAllClosure {

    var message: String? = null

    operator fun String.unaryPlus() {
        message = this
    }

    val assertions = mutableListOf<() -> Unit>()

    fun assert(init: Assert.() -> Unit) {
        val assert = Assert()
        assert.init()
        assertions.add(assert.assertion)
    }
}

class Assert {

    var message: String? = null

    operator fun String.unaryPlus() {
        message = this
    }


    var assertion: () -> Unit = { }

    infix fun Any?.equalsTo(any: Any?) {
        assertion = {
            Assertions.assertEquals(any, this@equalsTo, message)
        }
    }
}


fun assertAll(init: AssertAllClosure.() -> Unit) {
    val assert = AssertAllClosure()
    assert.init()

    org.junit.jupiter.api.assertAll(assert.message, *assert.assertions.toTypedArray())
}

fun assert(init: Assert.() -> Unit) {
    val assert = Assert()
    assert.init()
    assert.assertion()
}
