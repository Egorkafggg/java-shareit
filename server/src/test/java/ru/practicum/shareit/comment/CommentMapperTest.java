package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.comment.dto.UpdateCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentMapperTest {

    @Test
    void mapToCommentDto_ShouldMapAllFieldsCorrectly() {
        User author = mock(User.class);
        Item item = mock(Item.class);
        Comment comment = mock(Comment.class);

        when(comment.getId()).thenReturn(1L);
        when(comment.getText()).thenReturn("Comment text");
        when(comment.getItem()).thenReturn(item);
        when(item.getId()).thenReturn(10L);
        when(comment.getAuthor()).thenReturn(author);
        when(author.getName()).thenReturn("User Name");
        when(comment.getCreated()).thenReturn(LocalDateTime.of(2026, 5, 16, 11, 11));

        CommentDto result = CommentMapper.mapToCommentDto(comment);

        assertEquals(1L, result.getId());
        assertEquals("Comment text", result.getText());
        assertEquals(10L, result.getItemId());
        assertEquals("User Name", result.getAuthorName());
        assertEquals(LocalDateTime.of(2026, 5, 16, 11, 11), result.getCreated());
    }

    @Test
    void mapToComment_ShouldCreateCommentWithCorrectFields() {
        User author = new User();
        Item item = new Item();
        NewCommentDto request = new NewCommentDto();
        request.setText("New comment text");

        Comment result = CommentMapper.mapToComment(author, item, request);

        assertEquals("New comment text", result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated()); // устанавливается как LocalDateTime.now()
    }

    @Test
    void updateComment_ShouldUpdateTextField() {
        Comment comment = new Comment();
        comment.setText("previous comment text");

        UpdateCommentDto request = new UpdateCommentDto();
        request.setId(1L);
        request.setText("updated comment text");

        Comment result = CommentMapper.updateComment(comment, request);

        assertEquals("updated comment text", result.getText());
        assertSame(comment, result); // должен вернуть тот же объект
    }

}
