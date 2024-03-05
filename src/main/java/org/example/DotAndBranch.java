package org.example;

public class DotAndBranch {
    private final Dot dot;
    private final Branch branch;

    public DotAndBranch(Dot dot, Branch branch) {
        this.dot = dot;
        this.branch = branch;
    }

    public Dot getDot() {
        return dot;
    }

    public Branch getBranch() {
        return branch;
    }
}
