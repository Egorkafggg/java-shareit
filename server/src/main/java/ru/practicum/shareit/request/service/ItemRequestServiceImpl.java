package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private ItemRequest findById(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос c ID %d не найден", itemRequestId)));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Владелец вещи c ID %d не найден", userId)));
    }

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, NewItemRequestDto newItemRequestDto) {
        log.debug("Создаем запись о запросе");

        User findUser = findUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(newItemRequestDto, findUser, LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto findItemRequest(Long itemRequestId) {
        //return ItemRequestMapper.mapToItemRequestDto(findById(itemRequestId));

        log.debug("Поиск данных о запросе id = {}", itemRequestId);
        ItemRequest itemRequest = findById(itemRequestId);

        Collection<Item> items = itemRepository.findByRequestId(itemRequestId);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    @Override
    public Collection<ItemRequestDto> findAll() {
        log.debug("Получаем записи о всех запросах");
        return itemRequestRepository.findAll()
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestDto update(Long requestId, Long userId, UpdateItemRequestDto updateItemRequestDto) {
        log.debug("Обновляем данные запроса");

        User findUser = findUserById(userId);

        ItemRequest updatedItem = ItemRequestMapper.updateItemFields(findById(requestId), updateItemRequestDto/*, findUser*/);
        updatedItem = itemRequestRepository.save(updatedItem);

        return ItemRequestMapper.mapToItemRequestDto(updatedItem);
    }

    @Override
    @Transactional
    public void delete(Long itemRequestId) {
        ItemRequest itemRequest = findById(itemRequestId);
        log.debug("Удаляем данные запроса с ID {}", itemRequest.getId());
        itemRequestRepository.delete(itemRequest);
    }

    @Override
    public Collection<ItemRequestDto> findAllOfAnotherRequestors(Long requestorId) {
        log.debug("Получаем записи о всех запросах для пользователя с ID {}", requestorId);
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> findAllByRequestorId(Long requestorId) {
        log.debug("Получаем записи о всех запросах пользователя с ID {}", requestorId);

        User findUser = findUserById(requestorId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(requestorId);

        return fillRequestsData(requests)
                .stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    private List<ItemRequestDto> fillRequestsData(List<ItemRequest> requests) {

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<Item>> requestItems = itemRepository
                .findByRequestIdIn(requestIds)
                .stream()
                .collect(groupingBy(Item::getRequestId, toList()));

        List<ItemRequestDto> requestsList = new ArrayList<>();
        for (ItemRequest request : requests) {

            requestsList.add(ItemRequestMapper.mapToItemRequestDto(request,
                    requestItems.getOrDefault(request.getId(), Collections.emptyList()))
            );
        }

        return requestsList;
    }



}

