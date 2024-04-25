package proglang

sealed interface IntExpr {
    class Add(val lhs: IntExpr, val rhs: IntExpr) : IntExpr {
        override fun toString(): String = "$lhs + $rhs"
    }

    class Literal(val value: Int) : IntExpr {
        override fun toString(): String = "$value"
    }

    class Var(val name: String) : IntExpr {
        override fun toString(): String = name
    }

    class Mul(val lhs: IntExpr, val rhs: IntExpr) : IntExpr {
        override fun toString(): String = "$lhs * $rhs"
    }

    class Sub(val lhs: IntExpr, val rhs: IntExpr) : IntExpr {
        override fun toString(): String = "$lhs - $rhs"
    }

    class Div(val lhs: IntExpr, val rhs: IntExpr) : IntExpr {
        override fun toString(): String = "$lhs / $rhs"
    }

    class Fact(val expr: IntExpr) : IntExpr {
        override fun toString(): String = "$expr!"
    }

    class Paren(val expr: IntExpr) : IntExpr {
        override fun toString(): String = "($expr)"
    }
}

fun IntExpr.eval(store: Map<String, Int>): Int = when (this) {
    is IntExpr.Add -> lhs.eval(store) + rhs.eval(store)
    is IntExpr.Literal -> value
    is IntExpr.Var ->
        if (store[name] == null) {
            throw UndefinedBehaviourException("Variable not in store")
        } else {
            store[name]!!
        }
    is IntExpr.Mul -> lhs.eval(store) * rhs.eval(store)
    is IntExpr.Sub -> lhs.eval(store) - rhs.eval(store)
    is IntExpr.Div ->
        if (rhs.eval(store) == 0) {
            throw UndefinedBehaviourException("Divide by zero error")
        } else {
            lhs.eval(store) / rhs.eval(store)
        }
    is IntExpr.Fact ->
        if (expr.eval(store) < 0) {
            throw UndefinedBehaviourException("Cannot operate factorial on negative")
        } else {
            var sum: Int = 1
            for (num in 1..(expr.eval(store))) {
                sum *= num
            }
            sum
        }
    is IntExpr.Paren -> expr.eval(store)
}
