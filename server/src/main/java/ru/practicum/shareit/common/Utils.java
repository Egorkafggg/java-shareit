package ru.practicum.shareit.common;

import java.util.Set;

public class Utils {
    public static Long getNewId(Set<Long> idSourceSet) {
        long lastId = idSourceSet
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++lastId;
    }
}
