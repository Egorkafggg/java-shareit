package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, NewItemRequestDto newItemRequestDto);

    ItemRequestDto findItemRequest(Long itemRequestId);

    Collection<ItemRequestDto> findAll();

    ItemRequestDto update(Long requestId, Long user, UpdateItemRequestDto updateItemRequestDto);

    void delete(Long itemRequestId);

    Collection<ItemRequestDto> findAllOfAnotherRequestors(Long requestorId);

    Collection<ItemRequestDto> findAllByRequestorId(Long requestorId);

}

