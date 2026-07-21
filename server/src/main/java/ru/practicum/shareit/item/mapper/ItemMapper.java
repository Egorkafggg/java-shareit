package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.item.Item;


public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static Item toEntity(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static Item updateEntity(Item existingItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            existingItem.setRequestId(itemDto.getRequestId());
        }
        return existingItem;
    }
}