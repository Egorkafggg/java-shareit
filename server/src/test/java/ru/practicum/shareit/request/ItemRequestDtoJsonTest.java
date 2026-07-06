package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void serialize_shouldContainAllFields() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setRequestorId(2L);
        dto.setCreated(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        dto.setItems(List.of(new ResponseDto(10L, "Дрель", 3L)));

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id", 1);
        assertThat(result).hasJsonPathStringValue("$.description", "Нужна дрель");
        assertThat(result).hasJsonPathNumberValue("$.requestorId", 2);
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).hasJsonPathArrayValue("$.items");
        assertThat(result).hasJsonPathNumberValue("$.items[0].id", 10);
        assertThat(result).hasJsonPathStringValue("$.items[0].name", "Дрель");
        assertThat(result).hasJsonPathNumberValue("$.items[0].ownerId", 3);
    }

    @Test
    void deserialize_shouldMapCorrectly() throws Exception {
        String content = "{"
                + "\"id\": 1,"
                + "\"description\": \"Нужна дрель\","
                + "\"requestorId\": 2,"
                + "\"created\": \"2024-01-15T10:30:00\","
                + "\"items\": [{\"id\": 10, \"name\": \"Дрель\", \"ownerId\": 3}]"
                + "}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
    }
}