package com.example.storage;

import java.util.Optional;

public interface ContentStorage {

    void put(String key, byte[] content);

    Optional<byte[]> getByKey(String key);
}
