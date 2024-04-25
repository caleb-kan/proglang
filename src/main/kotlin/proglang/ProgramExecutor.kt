package proglang

import java.lang.Thread.sleep
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class ProgramExecutor(private val threadBody: Stmt, private val lock: Lock, private val pauseValue: Long, private val store: MutableMap<String, Int>) : Runnable {
    override fun run() {
        var currentStmt: Stmt? = threadBody
        while (currentStmt != null) {
            sleep(pauseValue)
            lock.withLock {
                currentStmt = currentStmt!!.step(store)
            }
        }
    }
}