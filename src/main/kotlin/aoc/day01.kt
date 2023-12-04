package aoc

private val input get() = readLines("01.txt")

private val mapping = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9",
)

private val regexFwd = mapping.keys
    .union(mapping.values)
    .joinToString("|")
    .toRegex()
private val regexRev = mapping.keys
    .union(mapping.values)
    .joinToString("|")
    .reversed()
    .toRegex()

fun main() {
    // part A
    run {
        val result = input
            .sumOf { line ->
                val c1 = line.first { it.isDigit() }
                val c2 = line.last { it.isDigit() }

                c1.digitToInt() * 10 + c2.digitToInt()
            }

        println(result)
    }

    // part B
    run {
        val result = input
            .sumOf { line ->
                val m1 = regexFwd.find(line)!!.value
                val c1 = mapping[m1] ?: m1

                val m2 = regexRev.find(line.reversed())!!.value.reversed()
                val c2 = mapping[m2] ?: m2

                c1.toInt() * 10 + c2.toInt()
            }

        println(result)
    }
}
