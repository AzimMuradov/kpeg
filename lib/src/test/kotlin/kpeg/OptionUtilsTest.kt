package kpeg

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.kotest.assertions.arrow.option.beNone
import io.kotest.assertions.arrow.option.shouldBeSome
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class OptionUtilsTest {

    @Nested
    inner class Get {

        private lateinit var option: Option<Unit>


        @Test
        fun `Some(value) - get value`() {
            option = Some(Unit)

            val actualValue = option.get()

            actualValue shouldBe Unit
        }

        @Test
        fun `None - throw exception`() {
            option = None

            val e = shouldThrow<IllegalStateException> { option.get() }

            e.message shouldBe "Option is empty"
        }
    }

    @Nested
    inner class AlsoIfSome {

        private lateinit var option: Option<Unit>


        @Test
        fun `Some(value) - call block & get this`() {
            option = Some(Unit)

            var msg = ""
            val res = option.alsoIfSome { msg = "MSG" }

            res shouldBeSome Unit
            msg shouldBe "MSG"
        }

        @Test
        fun `None - do nothing & get this`() {
            option = None

            var msg = ""
            val res = option.alsoIfSome { msg = "MSG" }

            res should beNone()
            msg shouldBe ""
        }
    }
}