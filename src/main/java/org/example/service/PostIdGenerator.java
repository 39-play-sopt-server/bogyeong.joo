package org.example.service;

public class PostIdGenerator {
    private long nextId = 1;

    public long generate() {
        return nextId++;
    }
}
