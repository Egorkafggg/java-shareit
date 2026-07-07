package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validateUserExists(userId);
        validateItemFields(itemDto);

        Item item = itemMapper.toEntity(itemDto, userId);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        validateUserExists(userId);

        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }

        if (!existingItem.getOwnerId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of this item");
        }

        Item updatedItem = itemMapper.updateEntity(existingItem, itemDto);
        itemRepository.update(updatedItem);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        validateUserExists(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.search(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    private void validateItemFields(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Item availability must be specified");
        }
    }
}
