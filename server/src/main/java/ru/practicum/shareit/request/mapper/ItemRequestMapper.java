package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(Collections.emptyList());
        return dto;
    }

    public static ItemRequest mapToItemRequest(NewItemRequestDto newItemRequest, User findUser, LocalDateTime createdDateTime) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(newItemRequest.getDescription());
        itemRequest.setRequestor(findUser);
        itemRequest.setCreated(createdDateTime);
        return itemRequest;
    }

    public static ItemRequest updateItemFields(ItemRequest itemRequest, UpdateItemRequestDto updateItemRequest/*, User findUser*/) {
        itemRequest.setDescription(updateItemRequest.getDescription());
        return itemRequest;
    }

    private static ResponseDto mapToResponseDto(Item item) {
        ResponseDto dto = new ResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwnerUser().getId());

        return dto;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, Collection<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(items.stream().map(ItemRequestMapper::mapToResponseDto).toList());

        return dto;
    }

}

