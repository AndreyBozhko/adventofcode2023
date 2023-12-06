package aoc

private val input get() = readLines("04.txt")

fun main() {
    val matchArray = input.map { line ->
        val sep1 = line.indexOf(':')
        val sep2 = line.indexOf('|')

        val win = line.substring(sep1 + 1, sep2 - 1)
            .trim()
            .split(whitespaceRegex)
            .toSet()
        val have = line.substring(sep2 + 1)
            .trim()
            .split(whitespaceRegex)
            .toSet()

        win.intersect(have).size
    }.toIntArray()

    // part A
    run {
        val result = matchArray.sumOf { sz ->
            when (sz) {
                0 -> 0
                else -> 1 shl (sz - 1)
            }
        }
        println(result)
    }

    // part B
    run {
        val counts = IntArray(matchArray.size) { 1 }
        for (idx in counts.indices) {
            for (j in 1..matchArray[idx]) {
                counts[idx + j] += counts[idx]
            }
        }

        val result = counts.sum()
        println(result)
    }
}
