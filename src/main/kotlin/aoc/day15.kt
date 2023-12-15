package aoc

private val input = readLines("15.txt")[0].split(',')

fun String.asciiHashCode(): Int = toCharArray().fold(0) { acc, ch ->
    ((acc + ch.code) * 17) and 0xFF
}

class AsciiHashMap {
    private data class Entry(val key: String, val value: Int)

    private val table = Array(256) { mutableListOf<Entry>() }

    val focusingPower: Int
        get() = table.withIndex().sumOf { (idx1, bucket) ->
            (idx1 + 1) * bucket.withIndex().sumOf { (idx2, entry) -> (idx2 + 1) * entry.value }
        }

    operator fun set(key: String, value: Int) {
        val bucket = table[key.asciiHashCode()]
        val idx = bucket.indexOfFirst { it.key == key }
        if (idx == -1) {
            bucket += Entry(key, value)
        } else {
            bucket[idx] = Entry(key, value)
        }
    }

    operator fun minusAssign(key: String) {
        val bucket = table[key.asciiHashCode()]
        bucket.removeAll { it.key == key }
    }
}

fun main() {
    // part A
    run {
        val result = input.sumOf { it.asciiHashCode() }
        println(result)
    }

    // part B
    run {
        val m = AsciiHashMap()

        input.forEach { inst ->
            if (inst.last() == '-') {
                val label = inst.substringBefore('-')
                m -= label
            } else {
                val label = inst.substringBefore('=')
                val focus = inst.substringAfter('=').toInt()
                m[label] = focus
            }
        }

        println(m.focusingPower)
    }
}
