package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserService userService;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        userService.getUserById(ownerId);
        return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long id) {
        return mapper.toItemDto(itemStorage.getItemById(id));
    }

    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        userService.getUserById(ownerId);

        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
    }

    public void delete(Long itemId, Long ownerId) {
        userService.getUserById(ownerId);

        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        itemStorage.delete(itemId);
    }

    public void deleteItemsByOwner(Long ownerId) {
        itemStorage.deleteItemsByOwner(ownerId);
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(searchText).stream()
                .filter(Item::getAvailable)
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }
}
