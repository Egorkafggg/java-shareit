package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.comment.dto.UpdateCommentDto;


public class CommentDtoTest {

    // тесты на класс CommentDto

    @Test
    void commentDto_WhenSettingAllFields_ThenGettersReturnCorrectValues() {
        CommentDto commentDto = new CommentDto();
        Long id = 1L;
        String text = "Test comment text";
        Long itemId = 10L;
        String authorName = "User Name";
        LocalDateTime created = LocalDateTime.now();

        commentDto.setId(id);
        commentDto.setText(text);
        commentDto.setItemId(itemId);
        commentDto.setAuthorName(authorName);
        commentDto.setCreated(created);

        assertEquals(id, commentDto.getId());
        assertEquals(text, commentDto.getText());
        assertEquals(itemId, commentDto.getItemId());
        assertEquals(authorName, commentDto.getAuthorName());
        assertEquals(created, commentDto.getCreated());
    }

    @Test
    void commentDto_WhenFieldsAreNull_ThenGettersReturnNull() {
        CommentDto commentDto = new CommentDto();

        assertNull(commentDto.getId());
        assertNull(commentDto.getText());
        assertNull(commentDto.getItemId());
        assertNull(commentDto.getAuthorName());
        assertNull(commentDto.getCreated());
    }

    // тесты на класс NewCommentDto
    @Test
    void newCommentDto_WhenSettingAllFields_ThenGettersReturnCorrectValues() {
        NewCommentDto dto = new NewCommentDto();
        String text = "Test new comment";
        Long itemId = 10L;
        Long authorId = 5L;

        dto.setText(text);
        dto.setItemId(itemId);
        dto.setAuthorId(authorId);

        assertEquals(text, dto.getText());
        assertEquals(itemId, dto.getItemId());
        assertEquals(authorId, dto.getAuthorId());
    }

    @Test
    void newCommentDto_WhenFieldsAreNull_ThenGettersReturnNull() {
        NewCommentDto dto = new NewCommentDto();

        // проверяем, что все геттеры возвращают null для нового объекта
        assertNull(dto.getText());
        assertNull(dto.getItemId());
        assertNull(dto.getAuthorId());
    }

    // тесты на класс UpdateCommentDto
    @Test
    void updCommentDto_WhenSettingAllFields_ThenGettersReturnCorrectValues() {
        UpdateCommentDto dto = new UpdateCommentDto();
        Long id = 1L;
        String text = "updated comment text";

        dto.setId(id);
        dto.setText(text);

        assertEquals(id, dto.getId());
        assertEquals(text, dto.getText());
    }

    @Test
    void updCommentDto_EqualsAndHashCode_BasedOnIdOnly() {
        // два объекта с одинаковым id, но разным text
        UpdateCommentDto dto1 = new UpdateCommentDto();
        dto1.setId(1L);
        dto1.setText("1st comment");

        UpdateCommentDto dto2 = new UpdateCommentDto();
        dto2.setId(1L);
        dto2.setText("2nd comment");

        // equals должен возвращать true, так как сравнивается только по id
        assertTrue(dto1.equals(dto2));
        assertTrue(dto2.equals(dto1));
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // объекты с разными id не должны быть равны
        UpdateCommentDto dto3 = new UpdateCommentDto();
        dto3.setId(2L);
        dto3.setText("Yet another comment");

        assertFalse(dto1.equals(dto3));
        assertFalse(dto3.equals(dto1));
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }


}
