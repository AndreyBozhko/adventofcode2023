@file:Suppress("NOTHING_TO_INLINE")

package aoc

@JvmName("productOfInt")
inline fun Iterable<Int>.product(): Int {
    var result = 1
    for (element in this) {
        result *= element
    }
    return result
}

@JvmName("productOfInt")
inline fun <T> Iterable<T>.productOf(selector: (T) -> Int): Int {
    var result = 1
    for (element in this) {
        result *= selector(element)
    }
    return result
}

@JvmName("productOfLong")
inline fun <T> Array<T>.productOf(selector: (T) -> Long): Long {
    var result = 1L
    for (element in this) {
        result *= selector(element)
    }
    return result
}
