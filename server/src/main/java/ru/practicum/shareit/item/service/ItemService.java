package ru.practicum.shareit.item.service;



import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.CommentRequestDto;
import ru.practicum.shareit.dto.item.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentRequestDto requestDto);
}
