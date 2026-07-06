package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.CreateNewUserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void createItemWhenRequestIdProvided() {
        Long ownerId = createUser("owner@mail.ru", "Owner");
        Long requestorId = createUser("req@mail.ru", "Requestor");

        NewItemRequestDto reqDto = new NewItemRequestDto();
        reqDto.setDescription("Нужна дрель");
        ItemRequestDto savedRequest = itemRequestService.create(requestorId, reqDto);

        CreateNewItemDto itemDto = new CreateNewItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(savedRequest.getId());

        ItemDto result = itemService.createNewItem(itemDto, ownerId);

        assertThat(result.getRequestId()).isEqualTo(savedRequest.getId());
    }

    @Test
    void createItemWhenRequestIdNull() {
        Long ownerId = createUser("owner2@mail.ru", "Owner2");

        CreateNewItemDto itemDto = new CreateNewItemDto();
        itemDto.setName("Молоток");
        itemDto.setDescription("Большой молоток");
        itemDto.setAvailable(true);

        ItemDto result = itemService.createNewItem(itemDto, ownerId);

        assertThat(result.getRequestId()).isNull();
    }

    private Long createUser(String email, String name) {
        CreateNewUserDto dto = new CreateNewUserDto();
        dto.setEmail(email);
        dto.setName(name);
        return userService.createNewUser(dto).getId();
    }
}