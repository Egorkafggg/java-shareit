package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.dto.item.BookingInfoDto;
import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.CommentRequestDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toEntity(itemDto, userId);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();

        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of this item");
        }

        Item updatedItem = ItemMapper.updateEntity(item, itemDto);
        updatedItem = itemRepository.save(updatedItem);
        return getItemDtoWithBookingsAndComments(userId, updatedItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();

        return getItemDtoWithBookingsAndComments(userId, item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        return items.stream()
                .map(item -> getItemDtoWithBookingsAndComments(userId, item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }

        LocalDateTime now = LocalDateTime.now();

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, now);

        if (!hasBooked) {
            throw new BadRequestException("User has not booked this item or booking is not completed");
        }

        Comment comment = new Comment();
        comment.setText(requestDto.getText());
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setCreated(now);

        comment = commentRepository.save(comment);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(user.getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    private ItemDto getItemDtoWithBookingsAndComments(Long userId, Item item) {
        ItemDto itemDto = ItemMapper.toDto(item);

        LocalDateTime now = LocalDateTime.now();

        // Получаем комментарии
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedAsc(item.getId());
        List<CommentDto> commentDtos = comments.stream()
                .map(c -> {
                    CommentDto dto = new CommentDto();
                    dto.setId(c.getId());
                    dto.setText(c.getText());
                    dto.setCreated(c.getCreated());

                    userRepository.findById(c.getAuthorId())
                            .ifPresent(author -> dto.setAuthorName(author.getName()));
                    return dto;
                })
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        // Только для владельца показываем бронирования
        if (item.getOwnerId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrderByStartAsc(
                    item.getId(), BookingStatus.APPROVED);

            Booking lastBooking = bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .reduce((first, second) -> second)
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .findFirst()
                    .orElse(null);

            if (lastBooking != null) {
                itemDto.setLastBooking(new BookingInfoDto(
                        lastBooking.getId(),
                        lastBooking.getBookerId()
                ));
            }

            if (nextBooking != null) {
                itemDto.setNextBooking(new BookingInfoDto(
                        nextBooking.getId(),
                        nextBooking.getBookerId()
                ));
            }
        }

        return itemDto;
    }
}