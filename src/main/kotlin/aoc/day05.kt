package aoc

import aoc.CustomRule.Companion.toCustomRule
import kotlin.math.*

private val input get() = readLines("05.txt")

private data class CustomRule(
    val rangeFrom: LongRange,
    val rangeTo: LongRange
) {
    private val shift = rangeTo.first - rangeFrom.first

    init {
        require(rangeFrom.last - rangeFrom.first == rangeTo.last - rangeTo.first)
    }

    operator fun get(value: Long) = value + shift

    operator fun contains(value: Long) = value in rangeFrom

    fun mapRange(inputRange: LongRange): Triple<LongRange?, LongRange?, LongRange?> {
        val r1 = if (inputRange.first < rangeFrom.first) {
            inputRange.first .. min(inputRange.last, rangeFrom.first - 1)
        } else {
            null
        }
        val r2 = if (inputRange.last >= rangeFrom.first && inputRange.first <= rangeFrom.last) {
            get(max(inputRange.first, rangeFrom.first)) .. get(min(inputRange.last, rangeFrom.last))
        } else {
            null
        }
        val r3 = if (inputRange.last > rangeFrom.last) {
            max(rangeFrom.last + 1, inputRange.first) .. inputRange.last
        } else {
            null
        }
        return Triple(r1, r2, r3)
    }

    companion object {
        fun String.toCustomRule(): CustomRule {
            val values = split(' ')
                .map { it.toLong() }
                .also { require(it.size == 3) }

            return CustomRule(
                rangeFrom = values[1] ..< values[1] + values[2],
                rangeTo = values[0] ..< values[0] + values[2]
            )
        }
    }
}

private data class Mapping(val conversion: String, val customRules: List<CustomRule>) {

    operator fun get(value: Long): Long {
        val rule = customRules
            .firstOrNull { value in it }
            ?: return value
        return rule[value]
    }

    fun mapRange(inputRange: LongRange): List<LongRange> = buildList {
        var cur = inputRange
        for (rule in customRules) {
            val (left, middle, right) = rule.mapRange(cur)
            left?.let { add(it) }
            middle?.let { add(it) }
            right?.let { cur = it } ?: return@buildList
        }
        add(cur)
    }

    companion object {
        fun parse(input: List<String>) =
            Mapping(
                conversion = input[0].removeSuffix(" map:"),
                customRules = input.drop(1)
                    .map { it.toCustomRule() }
                    .sortedBy { it.rangeFrom.first }
            )
    }
}

fun main() {
    val seeds = input[0]
        .substring(7)
        .split(' ')
        .map { it.toLong() }

    val newlineIndices = input
        .toMutableList()
        .apply {
            this += ""
        }
        .withIndex()
        .filter { it.value.isEmpty() }
        .map { it.index }
        .zipWithNext()

    val mappings = newlineIndices
        .map { Mapping.parse(input.subList(it.first + 1, it.second)) }

    // part A
    run {
        val result = seeds.minOf { seed ->
            mappings.fold(seed) { loc, mapping -> mapping[loc] }
        }
        println(result)
    }

    // part B
    run {
        val seedRanges = seeds
            .chunked(2) { it[0] ..< it[0] + it[1] }
            .sortedBy { it.first }

        val locationRanges = mappings.fold(seedRanges) { range, mapping ->
            range.flatMap(mapping::mapRange)
        }

        val result = locationRanges.minOf { it.first }
        println(result)
    }
}
