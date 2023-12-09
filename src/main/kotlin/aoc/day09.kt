package aoc

private val input get() = readLines("09.txt")

fun main() {
    val histories = input.map { line ->
        line.split(' ').map { it.toInt() }
    }

    // part A
    run {
        val result = histories.sumOf { h ->
            (2..h.size)
                .runningFold(h) { lst, _ -> lst.zipWithNext { a, b -> b - a } }
                .sumOf { it.last() }
        }
        println(result)
    }

    // part B
    run {
        val result = histories.sumOf { h ->
            (2..h.size)
                .runningFold(h) { lst, _ -> lst.zipWithNext { a, b -> b - a } }
                .withIndex()
                .sumOf { (idx, lst) -> lst.first() * (1 - 2 * (idx and 1)) }
        }
        println(result)
    }
}
