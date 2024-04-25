package proglang

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConcurrentProgram(private val threadBodyList: List<Stmt>, private val pauseValueList: List<Long>) {
    init {
        if (threadBodyList.size != pauseValueList.size) {
            throw IllegalArgumentException()
        }
    }

    private val lock: Lock = ReentrantLock()

    fun execute(initialStore: Map<String, Int>): Map<String, Int> {
        val workingStore: MutableMap<String, Int> = initialStore.toMutableMap()
        val threadList: List<Thread> = threadBodyList.indices.map { i -> Thread(ProgramExecutor(threadBodyList[i], lock, pauseValueList[i], workingStore)) }
        threadList.forEach { thread -> thread.start() }
        try {
            threadList.forEach { thread -> thread.join() }
        } catch (e: UndefinedBehaviourException) {
            throw UndefinedBehaviourException("")
        }
        return workingStore.toMap()
    }
}