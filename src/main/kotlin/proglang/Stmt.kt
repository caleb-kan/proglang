package proglang

interface Stmt {
    var next: Stmt?

    val lastInSequence: Stmt
        get() = traverse(this)

    fun traverse(stmt: Stmt): Stmt {
        return if (stmt.next == null) {
            stmt
        } else {
            traverse(stmt.next!!)
        }
    }

    fun toString(indent: Int): String

    fun clone(): Stmt

    class Assign(val name: String, val expr: IntExpr, private val stmt: Stmt ?= null) : Stmt {
        override var next: Stmt? = stmt

        override fun toString(): String {
            return toString(0)
        }

        override fun toString(indent: Int): String {
            var returnString: String = " ".repeat(indent) + name + " = " + "$expr" + "\n"
            if (next != null) {
                returnString += next!!.toString(indent)
            }
            return returnString
        }

        override fun clone(): Stmt {
            return if (next != null) {
                Assign(name, expr, next!!.clone())
            } else {
                Assign(name, expr)
            }
        }
    }

    class If(val condition: BoolExpr, val thenStmt: Stmt, val elseStmt: Stmt ?= null, private val stmt: Stmt ?= null) : Stmt {
        private val indentation: Int = 4

        override var next: Stmt? = stmt

        override fun toString(): String {
            return toString(0)
        }

        override fun toString(indent: Int): String {
            var returnString: String = " ".repeat(indent) + "if " + "($condition)" + " {" + "\n"
            returnString += thenStmt.toString(indent + indentation)
            returnString += " ".repeat(indent) + "}"
            if (elseStmt != null) {
                returnString += " else " + "{" + "\n"
                returnString += elseStmt.toString(indent + indentation)
                returnString += " ".repeat(indent) + "}" + "\n"
            } else {
                returnString += "\n"
            }
            if (next != null) {
                returnString += next!!.toString(indent)
            }
            return returnString
        }

        override fun clone(): Stmt {
            return if (elseStmt != null && next != null) {
                If(condition, thenStmt.clone(), elseStmt.clone(), next!!.clone())
            } else if (elseStmt == null && next == null) {
                If(condition, thenStmt.clone())
            } else if (elseStmt != null) {
                If(condition, thenStmt.clone(), elseStmt.clone())
            } else {
                If(condition, thenStmt.clone(), stmt = next!!.clone())
            }
        }
    }

    class While(val condition: BoolExpr, val body: Stmt ?= null, private val stmt: Stmt ?= null) : Stmt {
        private val indentation: Int = 4

        override var next: Stmt? = stmt

        override fun toString(): String {
            return toString(0)
        }

        override fun toString(indent: Int): String {
            var returnString: String = " ".repeat(indent) + "while " + "($condition)" + " {" + "\n"
            if (body != null) {
                returnString += body.toString(indent + indentation)
            }
            returnString += " ".repeat(indent) + "}" + "\n"
            if (next != null) {
                returnString += next!!.toString(indent)
            }
            return returnString
        }

        override fun clone(): Stmt {
            return if (body != null && next != null) {
                While(condition, body.clone(), next!!.clone())
            } else if (body == null && next == null) {
                While(condition)
            } else if (body != null) {
                While(condition, body.clone())
            } else {
                While(condition, stmt = next!!.clone())
            }
        }
    }
}

fun Stmt.step(store: MutableMap<String, Int>): Stmt? {
    if (this is Stmt.Assign) {
        store[name] = expr.eval(store)
        return next
    } else if (this is Stmt.If) {
        val cond: Boolean = condition.eval(store)
        if (cond) {
            val returnStmt: Stmt = thenStmt
            returnStmt.lastInSequence.next = next
            return returnStmt
        } else if (elseStmt != null) {
            val returnStmt: Stmt = elseStmt
            returnStmt.lastInSequence.next = next
            return returnStmt
        } else {
            return next
        }
    } else if (this is Stmt.While) {
        val cond: Boolean = condition.eval(store)
        if (cond && body != null) {
            val returnStmt: Stmt = body.clone()
            returnStmt.lastInSequence.next = this
            return returnStmt
        } else if (cond) {
            return this
        } else {
            return next
        }
    } else {
        throw UndefinedBehaviourException("The above should account for all kinds of Stmt.")
    }
}