package aoc

private val input = readLines("19.txt")

private typealias Part = IntArray
private typealias PartRange = Array<IntRange>

private operator fun Part.get(c: Char): Int = get("xmas".indexOf(c))
private operator fun PartRange.get(c: Char): IntRange = get("xmas".indexOf(c))
private operator fun PartRange.set(c: Char, value: IntRange) = set("xmas".indexOf(c), value)

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

    operator fun invoke(range: PartRange): Pair<PartRange?, PartRange?> {
        val categoryRange = range[category]
        return if (operation == '>' && categoryRange.first > threshold) {
            range to null
        } else if (operation == '<' && categoryRange.last < threshold) {
            range to null
        } else {
            val satisfied = range.clone()
            val passthrough = range.clone()
            if (operation == '>') {
                satisfied[category] = threshold+1..satisfied[category].last
                passthrough[category] = passthrough[category].first..threshold
            } else {
                satisfied[category] = satisfied[category].first..<threshold
                passthrough[category] = threshold..passthrough[category].last
            }
            satisfied to passthrough
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

    operator fun invoke(range: PartRange) = mutableListOf<Pair<String, PartRange>>().apply {
        var prev = range
        for (rule in rules) {
            val (satisfied, passthrough) = rule(prev)
            satisfied?.let { this += rule.target to it }
            passthrough?.let { prev = it } ?: return@apply
        }
        this += default to prev
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

    fun combinations(range: PartRange): Long {
        var total = 0L
        val queue = ArrayDeque<Pair<String, PartRange>>()
        queue += "in" to range

        while (queue.isNotEmpty()) {
            val (cur, rng) = queue.removeFirst()
            when (cur) {
                "A" -> {
                    total += rng.productOf { it.sizeAsLong() }
                }
                "R" -> {
                    // do nothing
                }
                else -> {
                    val wf = workflowMap[cur]!!
                    val splits = wf(rng)
                    queue += splits
                }
            }
        }
        return total
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
        val ranges = Array(4) { 1..4000 }
        val result = executor.combinations(ranges)
        println(result)
    }
}
