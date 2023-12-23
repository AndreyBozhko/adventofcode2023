@file:Suppress("NOTHING_TO_INLINE")

package aoc

import kotlin.experimental.ExperimentalTypeInference

@JvmName("productOfInt")
inline fun Iterable<Int>.product(): Int {
    var result = 1
    for (element in this) {
        result *= element
    }
    return result
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
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

@JvmName("productOfLong")
inline fun Iterable<Long>.product(): Long {
    var result = 1L
    for (element in this) {
        result *= element
    }
    return result
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("productOfLong")
inline fun <T> Iterable<T>.productOf(selector: (T) -> Long): Long {
    var result = 1L
    for (element in this) {
        result *= selector(element)
    }
    return result
}
