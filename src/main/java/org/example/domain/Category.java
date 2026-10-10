package org.example.domain;

public enum Category {
    DAILY("일상"), QUESTION("질문"), INFORMATION("정보");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
