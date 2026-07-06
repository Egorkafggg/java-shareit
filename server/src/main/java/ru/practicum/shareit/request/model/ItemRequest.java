package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
@EqualsAndHashCode(of = {"id"})
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Текст запроса предмета бронирования не должен быть пустым")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requestor_id")
    @NotNull(message = "Нужно указать пользователя, оставившего заявку на бронь")
    User requestor;

    @Column(name = "created")
    //@NotNull(message = "Дата/время создания заявки на бронь не должна быть пустой")
    LocalDateTime created;
}