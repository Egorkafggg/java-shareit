package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {
    private ItemStorage itemStorage;
    private ItemMapper mapper;
    private UserService userService;

    @Autowired
    public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.mapper = itemMapper;
    }

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        // Проверка существования пользователя
        UserDto user = userService.getUserById(ownerId); // Выбросит исключение, если не найден
        return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
    }

    public List<ItemDto> getItemsByOwner(Long ownderId) {
        return itemStorage.getItemsByOwner(ownderId).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public ItemDto getItemById(Long id) {
        return mapper.toItemDto(itemStorage.getItemById(id));
    }

    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
    }

    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.delete(itemId));
    }

    public void deleteItemsByOwner(Long ownderId) {
        itemStorage.deleteItemsByOwner(ownderId);
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(searchText).stream()
                .filter(Item::getAvailable) // ✅ Только доступные
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }
}
