package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item insert(Item newItem);

    Item update(Item newItem);

    boolean delete(Long itemId);

    boolean deleteByOwner(Long ownerUserId);

    Item selectOne(Long itemId);

    List<Item> selectAll();

    List<Item> selectAllItemsByOwner(Long ownerUserId);

    List<Item> getItemsForRent(String renterWishes);

}
