package aoc

val whitespaceRegex = "\\s+".toRegex()

data class Point2D(val x: Int, val y: Int) {
    companion object {
        infix fun Int.at(other: Int) = Point2D(this, other)
    }
}

data class Point3D(val x: Long, val y: Long, val z: Long)
