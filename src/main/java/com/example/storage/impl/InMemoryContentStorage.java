package com.example.storage.impl;

import com.example.storage.ContentStorage;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryContentStorage implements ContentStorage {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public void put(@NonNull String key, @NonNull byte[] content) {
        storage.put(key, content);
    }

    @Override
    public Optional<byte[]> getByKey(String key) {
        return Optional.ofNullable(storage.get(key));
    }
}
