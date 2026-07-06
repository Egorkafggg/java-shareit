package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findAllByItemId_ShouldReturnCommentsForGivenItem() {
        // Given: создаём тестовые данные
        User user = new User();
        user.setName("User Name");
        user.setEmail("mail@server.domain");
        entityManager.persist(user);

        Item item = new Item();
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        entityManager.persist(item);

        Comment comment1 = new Comment();
        comment1.setText("1st comment");
        comment1.setItem(item);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now().minusDays(1));
        entityManager.persist(comment1);

        Comment comment2 = new Comment();
        comment2.setText("2nd comment");
        comment2.setItem(item);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());
        entityManager.persist(comment2);

        // Сохраняем в БД
        entityManager.flush();

        List<Comment> result = commentRepository.findAllByItemId(item.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(comment1));
        assertTrue(result.contains(comment2));
    }

    @Test
    void findByItemIn_ShouldReturnCommentsForMultipleItems() {
        // создаём два предмета и комментарии к ним
        User user = new User();
        user.setName("User Name");
        user.setEmail("mail@server.domain");
        entityManager.persist(user);

        Item item1 = new Item();
        item1.setName("Test item 1 ");
        item1.setDescription("Test item 1 description");
        item1.setAvailable(true);
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Test item 2 ");
        item2.setDescription("Test item 2 description");
        item2.setAvailable(true);
        entityManager.persist(item2);

        Comment comment1 = new Comment();
        comment1.setText("comment for item1");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now().minusHours(2));
        entityManager.persist(comment1);

        Comment comment2 = new Comment();
        comment2.setText("comment for item2");
        comment2.setItem(item2);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now().minusHours(1));
        entityManager.persist(comment2);

        entityManager.flush();

        // ищем комментарии для обоих предметов
        List<Long> itemIds = List.of(item1.getId(), item2.getId());
        List<Comment> result = commentRepository.findByItemIn(itemIds);

        // проверяем, что вернулись оба комментария, отсортированные по created DESC
        assertEquals(2, result.size());
        assertEquals(comment2.getText(), result.get(0).getText()); // comment2 новее
        assertEquals(comment1.getText(), result.get(1).getText());
    }
}
