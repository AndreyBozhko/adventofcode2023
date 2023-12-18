package aoc

private val input get() = readLines("12.txt")

fun countArrangements(
    record: CharArray,
    groups: IntArray,
    recordIdx: Int = 0,
    currentGroup: Int = 0,
    requiredCharsMatched: Int = 0
): Long {
    if (currentGroup == groups.size) {
        return if (requiredCharsMatched == record.count { it == '#' }) 1 else 0
    }

    val rest = (currentGroup ..< groups.size).sumOf { groups[it] + 1 } - 1

    val currentGroupSize = groups[currentGroup]

    var total = 0L
    for (i in recordIdx .. record.size-rest) {
        val currentGroupRange = i ..< i+currentGroupSize
        if (currentGroupRange.any { record[it] == '.' }) continue

        val beforeGroup = currentGroupRange.first - 1
        if (beforeGroup >= 0 && record[beforeGroup] == '#') continue

        val afterGroup = currentGroupRange.last + 1
        if (afterGroup < record.size && record[afterGroup] == '#') continue

//        if (currentGroup == 0 && (0..i-2).any { record[it] == '#' }) continue

        val matched = currentGroupRange.count { record[it] == '#' }

        total += countArrangements(
            record,
            groups,
            currentGroupRange.last + 2,
            currentGroup + 1,
            requiredCharsMatched + matched
        )
    }
    return total
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
//        var i = 0
//        val result = data.sumOf { (r, g) ->
//            i += 1
//            val rr = "$r?$r?$r?$r?$r"
//            val gg = (1..5).flatMap { g.toList() }.toIntArray()
//            countArrangements(rr.toCharArray(), gg)
//        }
//        println(result)
    }
}
