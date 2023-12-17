package aoc

private val input = readLines("14.txt")

private typealias Platform = Array<CharArray>

fun Platform.rotateClockwise(): Platform = Array(this[0].size) { x ->
    CharArray(this.size) { y -> this[this[0].size - 1 - y][x] }
}

fun Platform.slide() = forEach { arr ->
    var left = arr.indexOfFirst { it != '#' }
    if (left == -1) return@forEach

    for (right in left .. arr.size) {
        if (right == arr.size || arr[right] == '#') {
            arr.sort(fromIndex = left, toIndex = right)
            left = right + 1
        }
    }
}

fun Platform.state(): String = joinToString("") { it.concatToString() }

fun CharArray.calculateLoad(): Int = foldIndexed(0) { idx, acc, c ->
    acc + if (c == 'O') idx+1 else 0
}

fun main() {
    val platform: Platform = input.map { it.toCharArray() }.toTypedArray()

    // part A
    run {
        val p = platform.rotateClockwise().apply { slide() }

        val result = p.sumOf { it.calculateLoad() }
        println(result)
    }

    // part B
    run {
        val numCycles = 1000000000

        var tmp = platform
        val seenMap = mutableMapOf<String, Int>()
        seenMap[platform.state()] = 0

        val seenList = mutableListOf(platform)

        for (i in 1..numCycles) {
            tmp = tmp.rotateClockwise().apply { slide() }
            tmp = tmp.rotateClockwise().apply { slide() }
            tmp = tmp.rotateClockwise().apply { slide() }
            tmp = tmp.rotateClockwise().apply { slide() }

            val state = tmp.state()
            if (state in seenMap) {
                break
            }
            seenMap[state] = i
            seenList += tmp
        }

        val idx2 = seenMap.size
        val idx1 = seenMap[tmp.state()]!!
        val idx = (numCycles - idx1) % (idx2 - idx1) + idx1

        val p = seenList[idx].rotateClockwise()
        val result = p.sumOf { it.calculateLoad() }
        println(result)
    }
}
