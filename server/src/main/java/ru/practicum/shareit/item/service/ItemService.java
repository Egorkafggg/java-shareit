package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createNewItem(CreateNewItemDto newItem, Long ownerUserId);

    ItemDto updateExistsItem(UpdateExistsItemDto newItem, Long ownerUserId, Long itemId);

    void deleteItem(Long itemId);

    ExtendedItemDto getItemByOwnerAndId(Long ownerUserId, Long itemId);

    List<ExtendedItemDto> getAllItemsByOwner(Long ownerUserId);

    List<ItemDto> getItemsForRenter(String renterWishes);

    CommentDto addComment(Long itemId, Long userId, NewCommentDto comment);

}
