package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String idParamPath = "/{itemId}";

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody NewCommentDto comment) {
        return itemService.addComment(itemId, userId, comment);
    }

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") Long ownerUserId,
                                 @RequestBody CreateNewItemDto newItem) {
        return itemService.createNewItem(newItem, ownerUserId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsForRenter(@RequestParam(name = "text", defaultValue = "") String renterWishes) {
        return itemService.getItemsForRenter(renterWishes);
    }

    @GetMapping(idParamPath)
    public ExtendedItemDto getItemByOwnerAndId(@RequestHeader("X-Sharer-User-Id") Long ownerUserId, @PathVariable("itemId") Long itemId) {
        return itemService.getItemByOwnerAndId(ownerUserId, itemId);
    }

    @GetMapping
    public List<ExtendedItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long ownerUserId) {
        return itemService.getAllItemsByOwner(ownerUserId);
    }

    @PatchMapping(idParamPath)
    public ItemDto updateExistsItem(@PathVariable("itemId") Long itemId,
                          @RequestBody UpdateExistsItemDto newItem,
                          @RequestHeader("X-Sharer-User-Id") Long ownerUserId) {
        return itemService.updateExistsItem(newItem, ownerUserId, itemId);
    }

    @DeleteMapping(idParamPath)
    public void deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
    }

}
