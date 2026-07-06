package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId1;
    private Long userId2;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setEmail("a@a.ru");
        user1.setName("Alice");
        userId1 = userRepository.save(user1).getId();

        User user2 = new User();
        user2.setEmail("b@b.ru");
        user2.setName("Bob");
        userId2 = userRepository.save(user2).getId();
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc_shouldReturnSortedOthersRequests() {
        saveRequest(userId1, "Первый", LocalDateTime.now().minusDays(2));
        saveRequest(userId1, "Второй", LocalDateTime.now().minusDays(1));
        saveRequest(userId2, "Чужой", LocalDateTime.now());

        List<ItemRequest> result =
                itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId1).stream().toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Чужой");
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDesc_shouldReturnOthersRequests() {
        saveRequest(userId1, "Мой", LocalDateTime.now().minusDays(1));
        saveRequest(userId2, "Чужой", LocalDateTime.now());

        List<ItemRequest> result =
                itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId1).stream().toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Чужой");
    }

    private void saveRequest(Long userId, String description, LocalDateTime created) {
        User user = userRepository.findById(userId).orElseThrow();
        ItemRequest req = new ItemRequest();
        req.setDescription(description);
        req.setRequestor(user);
        req.setCreated(created);
        itemRequestRepository.save(req);
    }
}