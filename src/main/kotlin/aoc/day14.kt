package aoc

private val input = readLines("14.txt")

fun calculateLoad(platform: List<String>, column: Int): Int {
    var i1 = platform.indexOfFirst { it[column] != '#' }
    if (i1 == -1) {
        return 0
    }

    var total = 0
    var rocks = 0
    for (i2 in i1 .. platform.size) {
        if (i2 == platform.size || platform[i2][column] == '#') {
            val range = (platform.size - i1 - rocks + 1 .. platform.size - i1)
            total += range.sum()
            i1 = i2 + 1
            rocks = 0
        } else if (platform[i2][column] == 'O') {
            rocks += 1
        }
    }
    return total
}

fun main() {
    // part A
    run {

    }

    // part B
    run {
        val result = input[0].indices.sumOf {
            calculateLoad(input, column = it)
        }
        println(result)
    }
}
