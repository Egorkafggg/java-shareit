package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createNewItem(CreateNewItemDto newItem, Long ownerUserId);

    ItemDto updateExistsItem(UpdateExistsItemDto newItem, Long ownerUserId, Long itemId);

    boolean deleteItem(Long itemId);

    boolean deleteItemsByOwner(Long ownerUserId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems();

    List<ItemDto> getAllItemsByOwner(Long ownerUserId);

    List<ItemDto> getItemsForRent(String renterWishes);

}
