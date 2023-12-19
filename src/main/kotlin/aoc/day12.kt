package aoc

private val input get() = readLines("12.txt")

fun countArrangements(record: CharArray, groups: IntArray): Long {
    val initial = mapOf(0 to 1L)

    val arrangements = groups.foldIndexed(initial) { groupIdx, arr, currentGroupLen ->
        buildMap {
            val remainingGroups = IntArray(groups.size - groupIdx - 1) { groups[it + groupIdx + 1] }
            arr.forEach { (recordPos, possibilities) ->
                for (groupStart in recordPos ..< record.size - remainingGroups.sum() - remainingGroups.size) {
                    val groupEnd = groupStart + currentGroupLen

                    // group fits inside, and only # or ? inside group
                    if (groupEnd <= record.size && (groupStart..<groupEnd).none { record[it] == '.' }) {

                        val canBeLastGroup = groupIdx == groups.indices.last
                                && (groupEnd..<record.size).none { record[it] == '#' }
                        val hasValidEnd = groupIdx < groups.indices.last
                                && groupEnd < record.size && record[groupEnd] != '#'
                        val hasValidStart = groupIdx == 0
                                || record[groupStart - 1] != '#'
                        if (canBeLastGroup || (hasValidStart && hasValidEnd)) {
                            this[groupEnd + 1] = possibilities + (this[groupEnd + 1] ?: 0)
                        }
                    }

                    // cannot move past the # since all must be accounted for
                    if (record[groupStart] == '#') {
                        break
                    }
                }
            }
        }
    }

    return arrangements.values.sum()
}

fun main() {
    val data = input.map { line ->
        val record = line.substringBefore(' ')
        val groups = line.substringAfter(' ')
            .split(',')
            .map { it.toInt() }
        record to groups
    }

    // part A
    run {
        val result = data.sumOf { (r, g) ->
            countArrangements(r.toCharArray(), g.toIntArray())
        }
        println(result)
    }

    // part B
    run {
        val result = data.sumOf { (r, g) ->
            val rr = "$r?$r?$r?$r?$r"
            val gg = (1..5).flatMap { g.toList() }.toIntArray()
            countArrangements(rr.toCharArray(), gg)
        }
        println(result)
    }
}
