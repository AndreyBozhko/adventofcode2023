package aoc

private val input = readLines("19.txt")

private typealias Part = IntArray

private operator fun Part.get(c: Char): Int = when (c) {
    'x' -> this[0]
    'm' -> this[1]
    'a' -> this[2]
    else -> this[3]
}

private fun IntRange.sizeAsLong() = (last - first + 1).toLong()

data class Rule(
    val category: Char,
    val operation: Char,
    val threshold: Int,
    val target: String
) {
    operator fun invoke(part: Part): String? {
        return if (operation == '>' && part[category] > threshold) {
            target
        } else if (operation == '<' && part[category] < threshold) {
            target
        } else {
            null
        }
    }

    override fun toString() = "$category$operation$threshold:$target"

    companion object {
        fun parse(input: String) = Rule(
            category = input[0],
            operation = input[1],
            threshold = input.substring(2, input.indexOf(':')).toInt(),
            target = input.substring(input.indexOf(':') + 1)
        )
    }
}

data class Workflow(val name: String, val rules: List<Rule>, val default: String) {
    operator fun invoke(part: Part): String {
        return rules.firstNotNullOfOrNull { it(part) } ?: default
    }
}

class Executor(branches: List<Workflow>) {
    private val workflowMap = branches.associateBy { it.name }

    fun process(part: Part): Boolean {
        var cur = "in"
        while (cur != "A" && cur != "R") {
            val wf = workflowMap[cur]!!
            cur = wf(part)
        }
        return cur == "A"
    }
}


fun main() {
    val idx = input.indexOfFirst { it.isBlank() }
    val workflows = input.take(idx)
        .map { line ->
            val i = line.indexOf('{')
            val name = line.substring(0, i)
            val splits = line.substring(i+1, line.length-1).split(',')
            val rules = splits.dropLast(1).map(Rule::parse)
            Workflow(name, rules, splits.last())
        }

    val parts = input.drop(idx + 1)
        .map { line ->
            val splits = line.trim('{', '}')
                .split(',')
            IntArray(splits.size) { splits[it].substring(2).toInt() }
        }

    val executor = Executor(workflows)

    // part A
    run {
        val result = parts
            .filter { executor.process(it) }
            .sumOf { it.sum() }
        println(result)
    }

    // part B
    run {
//        val boundaries = "xmas".associateWith { c ->
//            workflows.flatMap { w ->
//                w.rules.filter { it.category == c }.map { it.threshold }.toMutableList()
//            }.toMutableList()
//        }
//        boundaries.forEach { (_, ml) ->
//            ml += 1
//            ml += 4000
//            ml.sort()
//        }
//
//        fun Iterable<Int>.generateRanges(): Sequence<IntRange> = sequence {
//            var prev: Int? = null
//            for (element in this@generateRanges) {
//                prev?.let {
//                    yield(it+1..<element)
//                }
//                yield(element..element)
//                prev = element
//            }
//        }
//
//        var total = 0L
//        println(boundaries['x']!!.size)
//        for (dX in boundaries['x']!!.generateRanges()) {
//            println(dX)
//            for (dM in boundaries['m']!!.generateRanges()) {
//                for (dA in boundaries['a']!!.generateRanges()) {
//                    for (dS in boundaries['s']!!.generateRanges()) {
//                        val part = intArrayOf(dX.first, dM.first, dA.first, dS.first)
//                        if (executor.process(part)) {
//                            total += dX.sizeAsLong() * dM.sizeAsLong() * dA.sizeAsLong() * dS.sizeAsLong()
//                        }
//                    }
//                }
//            }
//        }
//        println(total)
    }
}
