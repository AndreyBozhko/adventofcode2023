package aoc

private val input get() = readLines("16.txt")

private typealias Contraption = Array<CharArray>

operator fun Contraption.get(state: State): Char = this[state.x][state.y]

data class State(val x: Int, val y: Int, val dir: Direction) {
    fun advance(): State = copy(x = x + dir.dx, y = y + dir.dy)
    fun reflect(c: Char): State = copy(dir = dir.reflect(c)).advance()
    fun split(c: Char): List<State> = dir.split(c).map { copy(dir = it).advance() }
}

fun Contraption.energize(start: State): Int {
    val cur = mutableListOf(start)
    val next = mutableListOf<State>()
    val seen = mutableSetOf<State>()

    while (cur.isNotEmpty()) {
        for (state in cur) {
            if (state.x !in this.indices || state.y !in this[0].indices) {
                continue
            }

            if (state in seen) {
                continue
            }

            seen += state
            when (val cell = this[state]) {
                '-', '|' -> { next += state.split(cell) }
                '\\', '/' -> { next += state.reflect(cell) }
                else -> { next += state.advance() }
            }
        }

        cur.clear()
        cur += next
        next.clear()
    }

    return seen.distinctBy { it.x to it.y }.size
}

fun main() {
    val contraption: Contraption = input.map { it.toCharArray() }.toTypedArray()

    // part A
    run {
        val result = contraption.energize(State(0, 0, Direction.R))
        println(result)
    }

    // part B
    run {
        val states = sequence {
            for (x in contraption.indices) {
                yield(State(x, 0, Direction.R))
                yield(State(x, contraption[0].size - 1, Direction.L))
            }
            for (y in contraption.indices) {
                yield(State(0, y, Direction.D))
                yield(State(contraption.size - 1, y, Direction.U))
            }
        }

        val result = states.maxOf { contraption.energize(it) }
        println(result)
    }
}
