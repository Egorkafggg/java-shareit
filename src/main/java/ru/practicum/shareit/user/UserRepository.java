package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ Добавлена пустая строка перед методом
    Optional<User> findByEmail(String email);

    // ✅ Добавлена пустая строка перед методом
    boolean existsByEmail(String email);

    // ✅ Добавлена пустая строка перед методом
    boolean existsByEmailAndIdNot(String email, Long id);
}