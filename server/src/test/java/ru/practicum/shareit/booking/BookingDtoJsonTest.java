package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_shouldContainAllFields() throws Exception {
        BookingDto dto = buildDto();

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id", 1);
        assertThat(result).hasJsonPathStringValue("$.status", "WAITING");
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).hasJsonPathNumberValue("$.item.id", 10);
        assertThat(result).hasJsonPathStringValue("$.item.name", "Дрель");
        assertThat(result).hasJsonPathNumberValue("$.booker.id", 2);
        assertThat(result).hasJsonPathStringValue("$.booker.email", "booker@mail.ru");
    }

    @Test
    void deserialize_shouldMapAllFields() throws Exception {
        String content = "{"
                + "\"start\":\"2030-01-01T10:00:00\","
                + "\"end\":\"2030-01-02T10:00:00\","
                + "\"status\":\"WAITING\","
                + "\"item\":{\"name\":\"Дрель\",\"description\":\"Мощная\",\"available\":true},"
                + "\"booker\":{\"email\":\"booker@mail.ru\",\"name\":\"Booker\"}"
                + "}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getStatus()).isEqualTo(BookingStatuses.WAITING);
        assertThat(dto.getItem()).isNotNull();
        assertThat(dto.getBooker()).isNotNull();
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2030, 1, 1, 10, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2030, 1, 2, 10, 0, 0));
    }

    @Test
    void serialize_idShouldBeReadOnly() throws Exception {
        BookingDto dto = buildDto();
        var result = json.write(dto);
        assertThat(result).hasJsonPathNumberValue("$.id");
    }

    private BookingDto buildDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatuses.WAITING);
        dto.setStart(LocalDateTime.of(2030, 1, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 2, 10, 0, 0));

        ItemDto item = new ItemDto();
        item.setId(10L);
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        dto.setItem(item);

        UserDto booker = new UserDto(2L, "booker@mail.ru", "Booker");
        dto.setBooker(booker);

        return dto;
    }
}

