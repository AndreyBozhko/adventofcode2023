package aoc

private val input = readLines("20.txt")

enum class Pulse {
    LOW, HIGH;

    fun invert() = if (this == LOW) HIGH else LOW
}

data class Signal(val src: String?, val rcv: String, val pulse: Pulse) {
    constructor(src: Module?, rcv: Module, pulse: Pulse) : this(src?.name, rcv.name, pulse)
}

class ModuleCommunication(private val modules: Map<String, Module>) {
    private val workQueue = ArrayDeque<Signal>()

    init {
        modules.forEach { (_, m) -> m.reset() }
    }

    fun process(signal: Signal) {
        workQueue += signal
    }

    fun runUntilComplete(observer: ((Signal) -> Unit)? = null) {
        while (workQueue.isNotEmpty()) {
            val signal = workQueue.removeFirst()
            observer?.invoke(signal)

            val (src, rcv, p) = signal
            val srcM = src?.let { modules[it]!! }
            val rcvM = modules[rcv]!!
            rcvM.process(srcM, p, this)
        }
    }

}

sealed class Module(private val tp: String, val name: String) {
    open fun reset() {}

    internal val dest: MutableList<Module> = mutableListOf()

    fun addDest(modules: Iterable<Module>) = dest.addAll(modules)

    fun process(upstream: Module?, pulse: Pulse, comm: ModuleCommunication) {
        val p = pulseToSend(upstream, pulse) ?: return
        dest.forEach {
            comm.process(Signal(this.name, it.name, p))
        }
    }

    abstract fun pulseToSend(upstream: Module?, pulse: Pulse): Pulse?

    override fun toString() = "$tp$name -> ${dest.map { it.name }}"

    companion object {
        fun parse(input: List<String>): Map<String, Module> = buildMap {
            val tmp = input.associateTo(mutableMapOf()) { line ->
                val parts = line.split(" -> ")
                val dest = parts[1].split(", ")
                parts[0] to dest
            }

            val modules = tmp.map { (nm, _) ->
                val mod = when {
                    nm == "broadcaster" -> Broadcaster()
                    nm[0] == '&' -> Conjunction(nm.substring(1))
                    nm[0] == '%' -> FlipFlop(nm.substring(1))
                    else -> Output(nm)
                }
                nm.trim('%', '&') to mod
            }.toMap().toMutableMap()

            tmp.forEach { (nm, d) ->
                val dest = d.map { n ->
                    modules[n] ?: Output(n).also { modules[n] = it }
                }
                val n = nm.trim('%', '&')
                modules[n]!!.addDest(dest)
            }

            modules.values.forEach { m ->
                m.dest.forEach {
                    if (it is Conjunction) it.registerInput(m)
                }
            }

            return modules
        }
    }


    class Broadcaster : Module("", "broadcaster") {
        override fun pulseToSend(upstream: Module?, pulse: Pulse) = pulse
    }

    class Output(name: String) : Module("", name) {
        override fun pulseToSend(upstream: Module?, pulse: Pulse) = null
    }

    class Conjunction(name: String) : Module("&", name) {
        private val memory: MutableMap<String, Pulse> = mutableMapOf()

        fun registerInput(module: Module) {
            memory[module.name] = Pulse.LOW
        }

        override fun reset() {
            memory.replaceAll { _, _ -> Pulse.LOW }
        }

        override fun pulseToSend(upstream: Module?, pulse: Pulse): Pulse {
            memory[upstream!!.name] = pulse
            return if (memory.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
        }
    }

    class FlipFlop(name: String) : Module("%", name) {
        private var memory = Pulse.LOW

        override fun reset() {
            memory = Pulse.LOW
        }

        override fun pulseToSend(upstream: Module?, pulse: Pulse) = when (pulse) {
            Pulse.HIGH -> null
            Pulse.LOW -> {
                memory = memory.invert()
                memory
            }
        }
    }
}

fun main() {
    val modules = Module.parse(input)
    val buttonPress = Signal(null, "broadcaster", Pulse.LOW)

    // part A
    run {
        val stats = LongArray(2)

        val comm = ModuleCommunication(modules)
        for (i in 1..1000) {
            comm.process(buttonPress)
            comm.runUntilComplete {
                stats[it.pulse.ordinal] += 1L
            }
        }

        val result = stats[0] * stats[1]
        println(result)
    }

    // part B
    run {
        val target = modules["rx"]!!

        val upstream1 = modules.values.single { target in it.dest }
        require(upstream1 is Module.Conjunction)

        val upstream2 = modules.values.filter { upstream1 in it.dest }

        val result = upstream2.productOf { u2 ->
            val signalToIntercept = Signal(u2, upstream1, Pulse.HIGH)
            val comm = ModuleCommunication(modules)
            for (i in 1..Int.MAX_VALUE) {
                try {
                    comm.process(buttonPress)
                    comm.runUntilComplete {
                        if (it == signalToIntercept) throw Exception()
                    }
                } catch (e: Exception) {
                    return@productOf i.toLong()
                }
            }
            0L
        }

        println(result)
    }
}
