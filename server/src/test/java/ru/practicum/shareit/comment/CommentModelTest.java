package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentModelTest {

    @Test
    void whenCreatingComment_ThenAllFieldsAreInitializedCorrectly() {
        Long commentId = 1L;
        String commentText = "Comment 4 test";
        Item item = new Item();
        User author = new User();
        LocalDateTime created = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText(commentText);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        assertEquals(commentId, comment.getId());
        assertEquals(commentText, comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(created, comment.getCreated());
    }

    @Test
    void whenTextIsNull_ThenTextShouldBeNull() {
        Comment comment = new Comment();
        comment.setText(null);
        assertNull(comment.getText());
    }

    @Test
    void whenCreatedIsNull_ThenCreatedShouldBeNull() {
        Comment comment = new Comment();
        comment.setCreated(null);
        assertNull(comment.getCreated());
    }

    @Test
    void whenItemIsNull_ThenItemShouldBeNull() {
        Comment comment = new Comment();
        comment.setItem(null);
        assertNull(comment.getItem());
    }

    @Test
    void whenAuthorIsNull_ThenAuthorShouldBeNull() {
        Comment comment = new Comment();
        comment.setAuthor(null);
        assertNull(comment.getAuthor());
    }

    @Test
    void whenIdIsNull_ThenIdShouldBeNull() {
        Comment comment = new Comment();
        assertNull(comment.getId());
    }

    @Test
    void whenSettingLongText_ThenTextIsSetCorrectly() {
        Comment comment = new Comment();
        String longText = "a".repeat(1000);

        comment.setText(longText);

        assertEquals(longText, comment.getText());
        assertEquals(1000, comment.getText().length());
    }

    @Test
    void testToStringDoesNotIncludeItemAndAuthor() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        String toStringResult = comment.toString();

        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("text=Test comment"));
        assertTrue(toStringResult.contains("created="));
        assertFalse(toStringResult.contains("item="));
        assertFalse(toStringResult.contains("author="));
    }
}
