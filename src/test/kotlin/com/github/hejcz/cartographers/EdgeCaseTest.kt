package com.github.hejcz.cartographers

import org.junit.jupiter.api.Test

class EdgeCaseTest {
    /**
     * Jeżeli gracz nie może na swojej mapie narysować żadnego z dostępnych kształtów, zamiast tego rysuje kwadrat
     * o wymiarach 1x1 w dowolnym miejscu mapy i wypełnia go wybranym przez siebie rodzajem terenu
     */
    @Test
    fun case1() {
    }

    /**
     * Jeżeli gracz nie będzie mógł narysować wskazanego na karcie Odkryć kształtu, tak by przykrył on obszar
     * Ruin -LUB- nie ma już na mapie niewypełnionych obszarów Ruin, gracz musi narysować kwadrat o wymiarach 1x1
     * w dowolnym miejscu mapy i wypełnić go dowolnym rodzajem terenu.
     */
    @Test
    fun case2() {
    }

    /**
     * Jeżeli gracz nie będzie mógł narysować przedstawionego na karcie Zasadzki kształtu
     * (nie zmieści się on na mapie sąsiada), zamiast tego będzie musiał narysować w dowolnym miejscu na mapie
     * sąsiada kwadrat o wymiarach 1x1 i wypełnić go rodzajem terenu o symbolu Potwora.
     */
    @Test
    fun case3() {
    }

    /**
     * Uwaga:Jeżeli karta Zasadzki zostanie odkryta od razu po karcie Ruin, karta Zasadzki jest rozgrywana na
     * normalnych przedstawionych powyżej zasadach. Natomiast efekt karty Ruin będzie miał zastosowanie dopiero do
     * następnej karty Odkryć (odkrytej po karcie Zasadzki).
     */
    @Test
    fun case4() {
    }
}