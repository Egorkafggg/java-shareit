package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String idParamPath = "/{id}";

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") Long ownerUserId,
                          @Valid @RequestBody CreateNewItemDto newItem) {
        return itemService.createNewItem(newItem, ownerUserId);
    }

    @GetMapping(idParamPath)
    public ItemDto getItemById(@PathVariable("id") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsForRent(@RequestParam(name = "text", defaultValue = "") String renterWishes) {
        return itemService.getItemsForRent(renterWishes);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerUserId) {
        return itemService.getAllItemsByOwner(ownerUserId);
    }

    @PatchMapping(idParamPath)
    public ItemDto updateExistsItem(@PathVariable("id") Long itemId,
                          @Valid @RequestBody UpdateExistsItemDto newItem,
                          @RequestHeader("X-Sharer-User-Id") Long ownerUserId) {
        return itemService.updateExistsItem(newItem, ownerUserId, itemId);
    }

    @DeleteMapping(idParamPath)
    public boolean deleteItem(@PathVariable("id") Long itemId) {
        return itemService.deleteItem(itemId);
    }

}
