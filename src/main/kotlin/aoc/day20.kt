package aoc

private val input = readLines("20.txt")

enum class Pulse {
    HIGH, LOW;

    fun invert() = if (this == LOW) HIGH else LOW
}

data class Signal(val src: String?, val rcv: String, val pulse: Pulse)

class Interrupted : Exception()

class ModuleCommunication(private val modules: Map<String, Module>) {
    private val workQueue = ArrayDeque<Signal>()
    private val sent = IntArray(2)

    val stats: Long
        get() = sent[0].toLong() * sent[1].toLong()

    fun process(signal: Signal) {
        workQueue += signal
    }

    fun start(waitFor: String? = null) {
        while (workQueue.isNotEmpty()) {
            val (src, rcv, p) = workQueue.removeFirst()
            if (rcv == waitFor && p == Pulse.LOW) {
                throw Interrupted()
            }

            val srcM = src?.let { modules[it]!! }
            val rcvM = modules[rcv]!!
            sent[p.ordinal] += 1
            rcvM.process(srcM, p, this)
        }
    }

}

sealed class Module(private val tp: String, val name: String) {
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
                    modules[n] ?: Output(n).also { modules[n] = it } }
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

    override fun pulseToSend(upstream: Module?, pulse: Pulse): Pulse {
        memory[upstream!!.name] = pulse
        return if (memory.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
    }
}

class FlipFlop(name: String) : Module("%", name) {
    private var memory = Pulse.LOW
    override fun pulseToSend(upstream: Module?, pulse: Pulse) = when (pulse) {
        Pulse.HIGH -> null
        Pulse.LOW -> {
            memory = memory.invert()
            memory
        }
    }
}


fun main() {
    val modules = Module.parse(input)

    // part A
    run {
        val comm = ModuleCommunication(modules)
        for (i in 1..1000) {
            comm.process(Signal(null, "broadcaster", Pulse.LOW))
            comm.start()
        }
        println(comm.stats)
    }

    // part B
    run {
        // TODO FIXME
        val comm = ModuleCommunication(modules)
        var i = 0
        try {
            while (true) {
                i += 1
                comm.process(Signal(null, "broadcaster", Pulse.LOW))
                comm.start(waitFor = "rx")
            }
        } catch (e: Interrupted) {
            println(i)
        }
    }
}
