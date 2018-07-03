package junit5

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertAll


class TypeAssertion<T> {

    var message: String? = null

    operator fun String.unaryMinus() {
        message = this
    }
}

interface AssertionFunctions {

    fun isTrue(assertion: TypeAssertion<Boolean>.() -> Boolean)

    fun isFalse(assertion: TypeAssertion<Boolean>.() -> Boolean)

    infix fun Any?.expectedFrom(any: Any?)

    infix fun Any?.notExpectedFrom(any: Any?)
}

class AssertMany : AssertionFunctions {

    private var message: String? = null

    operator fun String.unaryPlus() {
        message = this
    }

    override fun isTrue(assertion: TypeAssertion<Boolean>.() -> Boolean) {
        val type = TypeAssertion<Boolean>()
        val evaluation = type.assertion()
        assertTrue(evaluation, type.message ?: message)
        message = null
    }

    override fun isFalse(assertion: TypeAssertion<Boolean>.() -> Boolean) {
        val type = TypeAssertion<Boolean>()
        val evaluation = type.assertion()
        assertFalse(evaluation, type.message ?: message)
        message = null
    }

    override infix fun Any?.expectedFrom(any: Any?) {
        assertEquals(this, any, message)
        message = null
    }

    override infix fun Any?.notExpectedFrom(any: Any?) {
        assertNotEquals(this, any, message)
        message = null
    }
}

class AssertAll : AssertionFunctions {

    private var message: String? = null
    internal var allMessage: String? = null

    internal var assertions = mutableListOf<() -> Unit>()

    operator fun String.unaryPlus() {
        message = this
    }

    operator fun String.unaryMinus() {
        allMessage = this
    }

    override fun isTrue(assertion: TypeAssertion<Boolean>.() -> Boolean) {
        val comment = message

        assertions.add {
            val type = TypeAssertion<Boolean>()
            val evaluation = type.assertion()
            assertTrue(evaluation, type.message ?: comment)
        }

        message = null
    }

    override fun isFalse(assertion: TypeAssertion<Boolean>.() -> Boolean) {
        val comment = message

        assertions.add {
            val type = TypeAssertion<Boolean>()
            val evaluation = type.assertion()
            assertFalse(evaluation, type.message ?: comment)
        }

        message = null
    }

    override infix fun Any?.expectedFrom(any: Any?) {
        val comment = message

        assertions.add {
            assertEquals(this, any, comment)
        }

        message = null
    }

    override infix fun Any?.notExpectedFrom(any: Any?) {
        val comment = message

        assertions.add {
            assertNotEquals(this, any, comment)
        }

        message = null
    }
}

fun assertMany(init: AssertMany.() -> Unit) {
    val target = AssertMany()
    target.init()
}

fun assertAll(init: AssertAll.() -> Unit) {
    val assert = AssertAll()
    assert.init()
    assertAll(assert.allMessage, *assert.assertions.toTypedArray())
}