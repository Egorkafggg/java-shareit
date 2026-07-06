package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwnerUser().getId());
        /*if (item.getItemRequest() != null) {
            itemDto.setRequest(item.getItemRequest().getId());
        }*/
        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId());
        }
        return itemDto;
    }

    public static Item mapToItem(CreateNewItemDto newItem, User ownerUser) {
        Item item = new Item();
        item.setName(newItem.getName());
        item.setDescription(newItem.getDescription());
        item.setAvailable(newItem.getAvailable());
        /*if (ownerUserId != null) {
            item.setOwnerUser(ownerUserId);
        } else {
            item.setOwnerUser(newItem.getOwnerUser());
        }
        item.setItemRequest(newItem.getRequest());*/
        item.setOwnerUser(ownerUser);
        if (newItem.hasRequestId()) {
            item.setRequestId(newItem.getRequestId());
        }
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


    public static ExtendedItemDto mapToExtendedItemDto(Item item,
                                                       List<Comment> comments,
                                                       Optional<LocalDateTime> lastBooking,
                                                       Optional<LocalDateTime> nextBooking) {
        ExtendedItemDto dto = new ExtendedItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerUser().getId());
        lastBooking.ifPresent(dto::setLastBooking);
        nextBooking.ifPresent(dto::setNextBooking);
        dto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());
        if (item.getRequestId() != null) {
            dto.setRequestId(item.getRequestId());
        }

        return dto;
    }

    public static ExtendedItemDto mapToExtendedItemDto(Item item, List<Comment> comments) {
        ExtendedItemDto dto = new ExtendedItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerUser().getId());
        dto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());
        if (item.getRequestId() != null) {
            dto.setRequestId(item.getRequestId());
        }

        return dto;
    }





}
