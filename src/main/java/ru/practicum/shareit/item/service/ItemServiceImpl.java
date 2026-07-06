package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.OtherOwnerItemEditingException;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(@Qualifier("ItemStorageInMemory") ItemStorage itemStorage,
                           @Qualifier("UserStorageInMemory") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto createNewItem(CreateNewItemDto newItem, Long ownerUserId) {
        log.debug("Добавление нового предмета бронирования");
        User user = userStorage.selectOne(ownerUserId);
        Item item = ItemMapper.mapToItem(newItem, ownerUserId);
        item = itemStorage.insert(item);
        return ItemMapper.mapToItemDto(item);
    }

    public ItemDto updateExistsItem(UpdateExistsItemDto newItem, Long ownerUserId, Long itemId) {
        log.debug("Обновление предмета бронирования id = {}", itemId);
        Item item = itemStorage.selectOne(itemId);

        if (!item.getOwner().equals(ownerUserId)) {
            throw new OtherOwnerItemEditingException("Изменять предмет бронирования может только его владелец");
        }

        Item updatedItem = itemStorage.update(ItemMapper.updateItemFields(item, newItem));
        return ItemMapper.mapToItemDto(updatedItem);
    }

    public boolean deleteItem(Long itemId) {
        Item item = itemStorage.selectOne(itemId);
        log.debug("Удаление предмета бронирования {}", item.toString());
        return itemStorage.delete(itemId);
    }

    public boolean deleteItemsByOwner(Long ownerUserId) {
        log.debug("Удаление всех предметов бронирования по владельцу id = {}", ownerUserId);
        User user = userStorage.selectOne(ownerUserId);
        return itemStorage.deleteByOwner(ownerUserId);
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.mapToItemDto(itemStorage.selectOne(itemId));
    }

    public List<ItemDto> getAllItems() {
        log.debug("Получение всех записей обо всех предметах бронирования всех владельцев");
        return itemStorage.selectAll().stream().map(ItemMapper::mapToItemDto).toList();
    }

    public List<ItemDto> getAllItemsByOwner(Long ownerUserId) {
        log.debug("Получение записей обо всех предметах бронирования по указанному владельцу");
        return itemStorage.selectAllItemsByOwner(ownerUserId).stream().map(ItemMapper::mapToItemDto).toList();
    }

    public List<ItemDto> getItemsForRent(String renterWishes) {
        log.debug("Поиск предметов бронирования по ключевым словам \"{}\"", renterWishes);
        if (renterWishes.isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.getItemsForRent(renterWishes.toLowerCase()).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();


    }

}
