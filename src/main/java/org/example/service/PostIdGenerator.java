package org.example.service;

import org.springframework.stereotype.Component;

@Component
public class PostIdGenerator {
    private long nextId = 1;

    public synchronized long generate() {
        return nextId++;
    }
}
