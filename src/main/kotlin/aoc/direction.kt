package aoc

enum class Direction(val dx: Int, val dy: Int) {
    D(1, 0)
    {
        override fun reflect(c: Char) = when (c) {
            '\\' -> R
            else -> L
        }
        override fun split(c: Char) = when (c) {
            '-' -> listOf(L, R)
            else -> listOf(this)
        }
    },

    L(0, -1)
    {
        override fun reflect(c: Char) = when (c) {
            '\\' -> U
            else -> D
        }
        override fun split(c: Char) = when (c) {
            '|' -> listOf(U, D)
            else -> listOf(this)
        }
    },

    U(-1, 0)
    {
        override fun reflect(c: Char) = when (c) {
            '\\' -> L
            else -> R
        }
        override fun split(c: Char) = when (c) {
            '-' -> listOf(L, R)
            else -> listOf(this)
        }
    },

    R(0, 1)
    {
        override fun reflect(c: Char) = when (c) {
            '\\' -> D
            else -> U
        }
        override fun split(c: Char) = when (c) {
            '|' -> listOf(U, D)
            else -> listOf(this)
        }
    };

    abstract fun reflect(c: Char): Direction
    abstract fun split(c: Char): List<Direction>

    fun turnCounterClockwise() = entries[(ordinal + entries.size - 1) % entries.size]
    fun turnClockwise() = entries[(ordinal + 1) % entries.size]
}
