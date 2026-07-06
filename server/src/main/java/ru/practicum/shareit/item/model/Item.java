package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Наименование предмета бронирования не может быть пустым")
    @Column(name = "name", nullable = false, length = 100)
    String name;

    @NotBlank(message = "Описание предмета бронирования не должно быть пустым")
    @Column(name = "description", nullable = false)
    String description;

    @NotNull(message = "Нужно указать статус предмета бронирования: он занят или свободен")
    @Column(name = "available", nullable = false)
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "owner_id")
    User ownerUser;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "request_id")
    ItemRequest itemRequest;*/
    @JoinColumn(name = "request_id")
    Long requestId;
}
