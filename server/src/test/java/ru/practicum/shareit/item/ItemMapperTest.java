package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void mapToItemDto_shouldMapAllFields_withRequestId() {
        Item item = buildItem(1L, "Дрель", "Мощная", true);
        item.setRequestId(5L);

        ItemDto dto = ItemMapper.mapToItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Мощная");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getOwner()).isEqualTo(1L);
        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void mapToItemDto_shouldMapFields_withNullRequestId() {
        Item item = buildItem(2L, "Молоток", "Обычный", false);

        ItemDto dto = ItemMapper.mapToItemDto(item);

        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void mapToItem_shouldCreateItem_withRequestId() {
        CreateNewItemDto newItem = new CreateNewItemDto();
        newItem.setName("Пила");
        newItem.setDescription("Острая");
        newItem.setAvailable(true);
        newItem.setRequestId(10L);

        User owner = new User(1L, "owner@mail.ru", "Owner");
        Item item = ItemMapper.mapToItem(newItem, owner);

        assertThat(item.getName()).isEqualTo("Пила");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwnerUser()).isSameAs(owner);
        assertThat(item.getRequestId()).isEqualTo(10L);
    }

    @Test
    void mapToItem_shouldCreateItem_withoutRequestId() {
        CreateNewItemDto newItem = new CreateNewItemDto();
        newItem.setName("Отвёртка");
        newItem.setDescription("Плоская");
        newItem.setAvailable(true);

        User owner = new User(1L, "owner@mail.ru", "Owner");
        Item item = ItemMapper.mapToItem(newItem, owner);

        assertThat(item.getRequestId()).isNull();
    }

    @Test
    void updateItemFields_shouldUpdateAllFields_whenAllProvided() {
        Item item = buildItem(1L, "Старое", "Старое описание", true);
        UpdateExistsItemDto upd = new UpdateExistsItemDto();
        upd.setName("Новое");
        upd.setDescription("Новое описание");
        upd.setAvailable(false);

        Item result = ItemMapper.updateItemFields(item, upd);

        assertThat(result.getName()).isEqualTo("Новое");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItemFields_shouldNotUpdateName_whenNameBlank() {
        Item item = buildItem(1L, "Оригинал", "Описание", true);
        UpdateExistsItemDto upd = new UpdateExistsItemDto();
        upd.setName("   ");
        upd.setDescription("Новое описание");

        Item result = ItemMapper.updateItemFields(item, upd);

        assertThat(result.getName()).isEqualTo("Оригинал");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
    }

    @Test
    void updateItemFields_shouldNotUpdateDescription_whenDescriptionBlank() {
        Item item = buildItem(1L, "Имя", "Оригинальное описание", true);
        UpdateExistsItemDto upd = new UpdateExistsItemDto();
        upd.setDescription("");
        upd.setAvailable(false);

        Item result = ItemMapper.updateItemFields(item, upd);

        assertThat(result.getDescription()).isEqualTo("Оригинальное описание");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItemFields_shouldNotUpdateAvailable_whenNull() {
        Item item = buildItem(1L, "X", "X", true);
        UpdateExistsItemDto upd = new UpdateExistsItemDto();
        upd.setName("NewName");

        Item result = ItemMapper.updateItemFields(item, upd);

        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void mapToExtendedItemDto_shouldIncludeBookingDates_whenPresent() {
        Item item = buildItem(1L, "Дрель", "Мощная", true);
        item.setRequestId(3L);
        LocalDateTime last = LocalDateTime.now().minusDays(1);
        LocalDateTime next = LocalDateTime.now().plusDays(1);

        ExtendedItemDto dto = ItemMapper.mapToExtendedItemDto(
                item, Collections.emptyList(), Optional.of(last), Optional.of(next));

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking()).isEqualTo(last);
        assertThat(dto.getNextBooking()).isEqualTo(next);
        assertThat(dto.getRequestId()).isEqualTo(3L);
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void mapToExtendedItemDto_shouldHaveNullDates_whenOptionalEmpty() {
        Item item = buildItem(1L, "X", "X", false);

        ExtendedItemDto dto = ItemMapper.mapToExtendedItemDto(
                item, Collections.emptyList(), Optional.empty(), Optional.empty());

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void mapToExtendedItemDto_2arg_shouldMapCorrectly_withComments() {
        Item item = buildItem(1L, "Вещь", "Описание", true);
        item.setRequestId(7L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отлично!");
        comment.setItem(item);
        User author = new User(2L, "author@mail.ru", "Author");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        ExtendedItemDto dto = ItemMapper.mapToExtendedItemDto(item, List.of(comment));

        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Отлично!");
        assertThat(dto.getRequestId()).isEqualTo(7L);
    }

    @Test
    void mapToExtendedItemDto_2arg_shouldNotSetRequestId_whenNull() {
        Item item = buildItem(1L, "Вещь", "Описание", true);

        ExtendedItemDto dto = ItemMapper.mapToExtendedItemDto(item, Collections.emptyList());

        assertThat(dto.getRequestId()).isNull();
    }

    private Item buildItem(Long id, String name, String desc, boolean available) {
        User owner = new User(1L, "owner@mail.ru", "Owner");
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(desc);
        item.setAvailable(available);
        item.setOwnerUser(owner);
        return item;
    }
}
