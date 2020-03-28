package com.github.hejcz.cartographers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ScrollingThroughRuinsTest {
    @Test
    internal fun `ruin are first in round`() {
        val game = GameImplementation(deck = listOf(Ruins06, Fends15, Orchard13), monstersDeck = emptyList(),
            shuffler = NO_SHUFFLER)
        Assertions.assertFalse(
            game.join("julian")
                .join("tom")
                .leave("julian")
                .join("julian")
                .recentEvents()
                .getValue("julian")
                .any { it.type == EventType.NEW_CARD && (it as NewCardEvent).card == "06" }
        )
    }

    companion object {
        private val NO_SHUFFLER: (List<Card>) -> List<Card> = { deck -> deck }
    }
}