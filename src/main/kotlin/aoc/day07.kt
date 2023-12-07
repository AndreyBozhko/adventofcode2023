package aoc

import aoc.CamelCardHandType.Companion.identifyType

private val input get() = readLines("07.txt")

@JvmInline
value class Bid(val value: Long)

@JvmInline
value class CamelCardHandType(val strength: String) {
    companion object {
        fun String.identifyType() = CamelCardHandType(
            toCharArray()
                .groupBy { it }
                .map { it.value.size }
                .sortedDescending()
                .joinToString(""))
    }
}

data class CamelCardHand(
    val value: String,
    val type: CamelCardHandType = value.identifyType()
): Comparable<CamelCardHand> {

    fun maximizeType(): CamelCardHand {
        val possibilities = value.toCharArray().map {
            value.replace('?', it).identifyType()
        }
        val best = possibilities.maxBy { it.strength }
        return copy(type = best)
    }

    override operator fun compareTo(other: CamelCardHand): Int {
        val diff = type.strength.compareTo(other.type.strength)
        if (diff != 0) {
            return diff
        }

        val common = value.commonPrefixWith(other.value)
        if (value == common) {
            return 0
        }
        return CARD_VALUES.indexOf(value[common.length]) - CARD_VALUES.indexOf(other.value[common.length])
    }

    companion object {
        private const val CARD_VALUES = "?23456789TJQKA"
    }
}

fun main() {
    // part A
    run {
        val handsAndBids = input.map {
            val parts = it.split(' ')
            Bid(parts[1].toLong()) to CamelCardHand(parts[0])
        }

        val result = handsAndBids
            .sortedBy { it.second }
            .withIndex()
            .sumOf { (it.index + 1) * it.value.first.value }
        println(result)
    }

    // part B
    run {
        val handsAndBids = input.map {
            val parts = it.split(' ')
            val hand = parts[0].replace('J', '?')
            Bid(parts[1].toLong()) to CamelCardHand(hand).maximizeType()
        }

        val result = handsAndBids
            .sortedBy { it.second }
            .withIndex()
            .sumOf { (it.index + 1) * it.value.first.value }
        println(result)
    }
}
