package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OtherOwnerItemEditingException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createNewItemWhenOwnerMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CreateNewItemDto dto = new CreateNewItemDto();
        dto.setName("X");
        dto.setDescription("X");
        dto.setAvailable(true);

        assertThatThrownBy(() -> itemService.createNewItem(dto, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createNewItemWithoutRequestId() {
        User owner = buildUser(1L);
        Item saved = buildItem(10L, owner);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(saved);

        CreateNewItemDto dto = new CreateNewItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        ItemDto result = itemService.createNewItem(dto, 1L);
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void createNewItemWithRequestId() {
        User owner = buildUser(1L);
        Item saved = buildItem(10L, owner);
        saved.setRequestId(5L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(saved);

        CreateNewItemDto dto = new CreateNewItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);
        dto.setRequestId(5L);

        ItemDto result = itemService.createNewItem(dto, 1L);
        assertThat(result.getRequestId()).isEqualTo(5L);
    }

    @Test
    void updateExistsItemWhenItemMissing() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                itemService.updateExistsItem(new UpdateExistsItemDto(), 1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateExistsItemWhenNotOwner() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                itemService.updateExistsItem(new UpdateExistsItemDto(), 99L, 5L))
                .isInstanceOf(OtherOwnerItemEditingException.class);
    }

    @Test
    void updateExistsItemWhenAllProvided() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        UpdateExistsItemDto dto = new UpdateExistsItemDto();
        dto.setName("Новое имя");
        dto.setDescription("Новое описание");
        dto.setAvailable(false);

        ItemDto result = itemService.updateExistsItem(dto, 1L, 5L);
        assertThat(result).isNotNull();
        verify(itemRepository).save(any());
    }

    @Test
    void updateExistsItemWhenAllBlankOrNull() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        UpdateExistsItemDto dto = new UpdateExistsItemDto();
        dto.setName("  ");
        dto.setDescription("");

        itemService.updateExistsItem(dto, 1L, 5L);
        verify(itemRepository).save(item);
    }

    @Test
    void deleteItemWhenItemMissing() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteItem(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteItemShouldCallRepositoryDelete() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        itemService.deleteItem(5L);
        verify(itemRepository).delete(item);
    }

    @Test
    void getItemByOwnerAndIdForOwner() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(5L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingEndByItemId(5L)).thenReturn(List.of());
        when(bookingRepository.findNextBookingStartByItemId(5L)).thenReturn(List.of());

        ExtendedItemDto result = itemService.getItemByOwnerAndId(1L, 5L);

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void getItemByOwnerAndIdForNonOwner() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(5L)).thenReturn(Collections.emptyList());

        ExtendedItemDto result = itemService.getItemByOwnerAndId(2L, 5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void getItemByOwnerAndIdWhenPresent() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        LocalDateTime lastEnd = LocalDateTime.now().minusDays(1);
        LocalDateTime nextStart = LocalDateTime.now().plusDays(1);

        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(5L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingEndByItemId(5L)).thenReturn(List.of(lastEnd));
        when(bookingRepository.findNextBookingStartByItemId(5L)).thenReturn(List.of(nextStart));

        ExtendedItemDto result = itemService.getItemByOwnerAndId(1L, 5L);

        assertThat(result.getLastBooking()).isEqualTo(lastEnd);
        assertThat(result.getNextBooking()).isEqualTo(nextStart);
    }

    @Test
    void getItemsForRenterWhenTextBlank() {
        List<ItemDto> result = itemService.getItemsForRenter("");

        assertThat(result).isEmpty();
    }

    @Test
    void getItemsForRenterWhenTextProvided() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.getItemsForRenter("дрель")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getItemsForRenter("Дрель");

        assertThat(result).hasSize(1);
    }

    @Test
    void addCommentWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                itemService.addComment(1L, 99L, buildCommentDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentWhenItemMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                itemService.addComment(99L, 1L, buildCommentDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentWhenUserNeverBooked() {
        User user = buildUser(1L);
        Item item = buildItem(5L, buildUser(2L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(any(), any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                itemService.addComment(5L, 1L, buildCommentDto()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void addCommentWhenUserHasFinishedBooking() {
        User user = buildUser(1L);
        Item item = buildItem(5L, buildUser(2L));

        Comment saved = new Comment();
        saved.setId(1L);
        saved.setText("Отлично!");
        saved.setItem(item);
        saved.setAuthor(user);
        saved.setCreated(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(any(), any(), any()))
                .thenReturn(true);
        when(commentRepository.save(any())).thenReturn(saved);

        CommentDto result = itemService.addComment(5L, 1L, buildCommentDto());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Отлично!");
        verify(commentRepository).save(any());
    }

    @Test
    void getAllItemsByOwnerWhenOwnerHasNoItems() {
        when(itemRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());

        List<ExtendedItemDto> result = itemService.getAllItemsByOwner(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllItemsByOwnerWithEmptyBookingMaps() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);
        when(itemRepository.findAllByUserId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndEndBefore(anyList())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemInAndStartAfter(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemIn(anyList())).thenReturn(Collections.emptyList());

        List<ExtendedItemDto> result = itemService.getAllItemsByOwner(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5L);
    }

    @Test
    void getAllItemsByOwnerWithBookingDates() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, owner);

        Booking pastBooking = new Booking();
        pastBooking.setId(1L);
        pastBooking.setItem(item);
        pastBooking.setBooker(buildUser(2L));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(1));
        pastBooking.setStartDate(LocalDateTime.now().minusDays(2));
        pastBooking.setStatus(BookingStatuses.APPROVED);

        Booking futureBooking = new Booking();
        futureBooking.setId(2L);
        futureBooking.setItem(item);
        futureBooking.setBooker(buildUser(2L));
        futureBooking.setStartDate(LocalDateTime.now().plusDays(1));
        futureBooking.setEndDate(LocalDateTime.now().plusDays(2));
        futureBooking.setStatus(BookingStatuses.APPROVED);

        when(itemRepository.findAllByUserId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndEndBefore(anyList())).thenReturn(List.of(pastBooking));
        when(bookingRepository.findByItemInAndStartAfter(anyList())).thenReturn(List.of(futureBooking));
        when(commentRepository.findByItemIn(anyList())).thenReturn(Collections.emptyList());

        List<ExtendedItemDto> result = itemService.getAllItemsByOwner(1L);

        assertThat(result).hasSize(1);
    }

    private User buildUser(Long id) {
        return new User(id, "user" + id + "@mail.ru", "User" + id);
    }

    private Item buildItem(Long id, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item" + id);
        item.setDescription("Desc" + id);
        item.setAvailable(true);
        item.setOwnerUser(owner);
        return item;
    }

    private NewCommentDto buildCommentDto() {
        NewCommentDto dto = new NewCommentDto();
        dto.setText("Отлично!");
        return dto;
    }
}
