package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;
    // УДАЛИТЕ: private final CheckConsistencyService checker;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Получен GET-запрос к эндпоинту: '/items/{}' на получение вещи", itemId);
        return itemService.getItemById(itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("Получен POST-запрос к эндпоинту: '/items' на добавление вещи владельцем с ID={}", ownerId);

        // УДАЛИТЕ проверку: if (!checker.isExistUser(ownerId))
        // Проверка существования пользователя теперь в сервисе

        return itemService.create(itemDto, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение всех вещей владельца с ID={}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/items/{}' на обновление вещи", itemId);

        // УДАЛИТЕ проверку: if (!checker.isExistUser(ownerId))

        return itemService.update(itemDto, ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId,
                       @RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/items/{}' на удаление вещи", itemId);
        itemService.delete(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam @NotBlank String text) {
        log.info("Получен GET-запрос к эндпоинту: '/items/search' на поиск вещи с текстом='{}'", text);
        return itemService.getItemsBySearchQuery(text);
    }
}
