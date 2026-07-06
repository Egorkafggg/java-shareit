package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequest(item.getRequest());
        return itemDto;
    }

    public static Item mapToItem(CreateNewItemDto newItem, Long ownerUserId) {
        Item item = new Item();
        item.setName(newItem.getName());
        item.setDescription(newItem.getDescription());
        item.setAvailable(newItem.getAvailable());
        if (ownerUserId != null) {
            item.setOwner(ownerUserId);
        } else {
            item.setOwner(newItem.getOwner());
        }
        item.setRequest(newItem.getRequest());
        return item;
    }

    public static Item updateItemFields(Item item4upd, UpdateExistsItemDto newItem) {
        if (newItem.hasName()) {
            item4upd.setName(newItem.getName());
        }

        if (newItem.hasDescription()) {
            item4upd.setDescription(newItem.getDescription());
        }

        if (newItem.hasAvailable()) {
            item4upd.setAvailable(newItem.getAvailable());
        }

        return item4upd;
    }

}
