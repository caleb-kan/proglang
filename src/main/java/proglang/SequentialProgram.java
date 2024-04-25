package proglang;

import java.util.HashMap;
import java.util.Map;

final public class SequentialProgram {
    private Stmt topLevel;

    public SequentialProgram(Stmt topLevel) {
        this.topLevel = topLevel;
    }

    public Map<String, Integer> execute(Map<String, Integer> initialStore) {
        Map<String, Integer> workingStore = new HashMap<String, Integer>(initialStore);
        Stmt currentStmt = topLevel;
        while (currentStmt != null) {
            currentStmt = StmtKt.step(currentStmt, workingStore);
        }
        return workingStore;
    }

    @Override
    public String toString() {
        return topLevel.toString();
    }
}
