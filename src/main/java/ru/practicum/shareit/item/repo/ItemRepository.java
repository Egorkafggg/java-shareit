package ru.practicum.shareit.item.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return items.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public boolean existsById(Long id) {
        return items.containsKey(id);
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable())
                .filter(item ->
                        item.getName().toLowerCase().contains(lowerCaseText) ||
                                item.getDescription().toLowerCase().contains(lowerCaseText)
                )
                .collect(Collectors.toList());
    }
}
