package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OtherOwnerItemEditingException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            CommentRepository commentRepository

    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;

    }

    private Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет брониования id = %d не найден", itemId)));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Собственник предмета бронирования id = %d не найден", userId)));
    }

    @Override
    @Transactional
    public ItemDto createNewItem(CreateNewItemDto newItem, Long ownerUserId) {
        log.debug("Добавление нового предмета бронирования");
        User findUser = findUserById(ownerUserId);
        Item item = ItemMapper.mapToItem(newItem, findUser);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateExistsItem(UpdateExistsItemDto newItem, Long ownerUserId, Long itemId) {
        log.debug("Обновление предмета бронирования id = {}", itemId);
        Item item = findById(itemId);

        if (!item.getOwnerUser().getId().equals(ownerUserId)) {
            throw new OtherOwnerItemEditingException("Изменять предмет бронирования может только его владелец");
        }

        Item updatedItem = itemRepository.save(ItemMapper.updateItemFields(item, newItem));
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        Item item = findById(itemId);
        log.debug("Удаление предмета бронирования {}", item.toString());
        itemRepository.delete(item);
    }

    public ExtendedItemDto getItemByOwnerAndId(Long ownerUserId, Long itemId) {
        log.debug("Поиск сведений о предмете бронирования id = {}, принадлежащего владельцу user_id = {}", itemId, ownerUserId);
        Item item = findById(itemId);

        if (item.getOwnerUser().getId().equals(ownerUserId)) {
            return ItemMapper.mapToExtendedItemDto(findById(itemId),
                    commentRepository.findAllByItemId(itemId),
                    getLastBookingEndDate(itemId),
                    getNextBookingStartDate(itemId));
        }

        return ItemMapper.mapToExtendedItemDto(findById(itemId), commentRepository.findAllByItemId(itemId));
    }

    public List<ItemDto> getItemsForRenter(String renterWishes) {
        log.debug("Поиск предметов бронирования по ключевым словам \"{}\"", renterWishes);
        if (renterWishes.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.getItemsForRenter(renterWishes.toLowerCase()).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, NewCommentDto request) {
        log.debug("Добавление комментария к предмету бронирования");

        User findUser = findUserById(userId);
        Item findItem = findById(itemId);

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException(String.format("Пользователь %s не может добавить комментарий, " +
                    "так как не пользовался предметом %s", findUser.getName(), findItem.getName()));
        }

        Comment comment = CommentMapper.mapToComment(findUser, findItem, request);
        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }


    private List<ExtendedItemDto> fillItemData(List<Item> userItems) {
        List<Long> itemIds = userItems.stream().map(Item::getId).toList();

        Map<Item, LocalDateTime> lastItemBookingEndDate = bookingRepository
                .findByItemInAndEndBefore(itemIds)
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getEndDate));

        Map<Item, LocalDateTime> nextItemBookingStartDate = bookingRepository
                .findByItemInAndStartAfter(itemIds)
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getStartDate));

        Map<Item, List<Comment>> itemsWithComments = commentRepository
                .findByItemIn(itemIds)
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<ExtendedItemDto> itemsList = new ArrayList<>();
        for (Item item : userItems) {
            Optional<LocalDateTime> lastEndDate;
            if (!lastItemBookingEndDate.isEmpty()) {
                lastEndDate = Optional.of(lastItemBookingEndDate.get(item));
            } else {
                lastEndDate = Optional.empty();
            }

            Optional<LocalDateTime> nextStartDate;
            if (!nextItemBookingStartDate.isEmpty()) {
                nextStartDate = Optional.of(nextItemBookingStartDate.get(item));
            } else {
                nextStartDate = Optional.empty();
            }

            itemsList.add(ItemMapper.mapToExtendedItemDto(item,
                            itemsWithComments.getOrDefault(item, Collections.emptyList()),
                            lastEndDate,
                            nextStartDate
                    )
            );
        }

        return itemsList;
    }

    @Override
    public List<ExtendedItemDto> getAllItemsByOwner(Long ownerUserId) {
        log.debug("Получение записей обо всех предметах бронирования по указанному владельцу id = {}", ownerUserId);

        List<Item> userItems = itemRepository.findAllByUserId(ownerUserId);

        if (!userItems.isEmpty()) {
            return fillItemData(userItems);
        }

        return Collections.emptyList();
    }

    private Optional<LocalDateTime> getLastBookingEndDate(Long itemId) {
        return bookingRepository.findLastBookingEndByItemId(itemId)
                .stream()
                .max(Comparator.naturalOrder());
    }

    private Optional<LocalDateTime> getNextBookingStartDate(Long itemId) {
        return bookingRepository.findNextBookingStartByItemId(itemId)
                .stream()
                .min(Comparator.naturalOrder());
    }

}
