package org.example.web.model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private int correctImpl;
    private final List<CheckResult> checkResults = new ArrayList<>();

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCorrectImpl() {
        return correctImpl;
    }

    public void setCorrectImpl(int correctImpl) {
        this.correctImpl = correctImpl;
    }

    public List<CheckResult> getCheckResults() {
        return checkResults;
    }

    public void addCheckResult(CheckResult checkResult) {
        checkResults.add(checkResult);
    }
}
