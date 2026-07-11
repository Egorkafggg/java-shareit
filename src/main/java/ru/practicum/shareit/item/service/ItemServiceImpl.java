package ru.practicum.shareit.item.service;

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

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        checkUserExists(userId);
        Item item = ItemMapper.toEntity(itemDto, userId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        checkUserExists(userId);

        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }

        if (!existingItem.getOwnerId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of this item");
        }

        Item updatedItem = ItemMapper.updateEntity(existingItem, itemDto);
        itemRepository.update(updatedItem);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        checkUserExists(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }
}
