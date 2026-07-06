package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.Utils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("ItemStorageInMemory")
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    public Item insert(Item newItem) {
        newItem.setId(Utils.getNewId(items.keySet()));
        log.trace("Создание/сохранение предмета бронирования {}", newItem.toString());
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    public Item update(Item newItem) {
        log.trace("Обновление предмета бронирования {}", newItem.toString());
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    public boolean delete(Long itemId) {
        log.trace("Удаление предмета бронирования id={}", itemId);
        items.remove(itemId);
        return Optional.ofNullable(items.get(itemId)).isPresent();
    }

    public boolean deleteByOwner(Long ownerUserId) {
        log.trace("Удаление всех предметов бронирования владельца id={}", ownerUserId);
        items.entrySet().removeIf(item ->
                (item.getValue().getOwner() != null)
                && (item.getValue().getOwner().equals(ownerUserId))
        );
        Long cnt = items.values().stream().filter(item ->
                        (item.getOwner() != null) && (item.getOwner().equals(ownerUserId))
                        ).count();
        return (cnt == 0);
    }

    public Item selectOne(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> new NotFoundException(String.format("Предмет бронирования id = %d не найден", itemId)));
    }

    public List<Item> selectAll() {
        return items.values().stream().toList();
    }

    public List<Item> selectAllItemsByOwner(Long ownerUserId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(ownerUserId))
                .toList();
    }

    public boolean itemHasSearchCondition(Item item, String searchCondition) {
        if (item.getAvailable()
            && (item.getName().toLowerCase().contains(searchCondition)
                || item.getDescription().toLowerCase().contains(searchCondition))) {
            return true;
        }
        return false;
    }

    public List<Item> getItemsForRent(String renterWishes) {
        return items.values()
                .stream()
                .filter(item -> itemHasSearchCondition(item, renterWishes))
                .toList();
    }

}
