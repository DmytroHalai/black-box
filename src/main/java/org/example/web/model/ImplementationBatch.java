package org.example.web.model;

import java.util.ArrayList;
import java.util.List;

public class ImplementationBatch {
    private List<String> implementations;
    private int correctImplementation;

    public ImplementationBatch() {
    }

    public List<String> getImplementations() {
        return implementations;
    }

    public void setImplementations(List<String> implementations) {
        this.implementations = new ArrayList<>(implementations);
    }

    public int getCorrectImplementation() {
        return correctImplementation;
    }

    public void setCorrectImplementation(int correctImplementation) {
        this.correctImplementation = correctImplementation;
    }
}
