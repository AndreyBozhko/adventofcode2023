package aoc

data class Point2D(val x: Int, val y: Int) {
    companion object {
        infix fun Int.at(other: Int) = Point2D(this, other)
    }
}
