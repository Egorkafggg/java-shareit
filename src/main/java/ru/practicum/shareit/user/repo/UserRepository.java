package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(id));
    }
}
