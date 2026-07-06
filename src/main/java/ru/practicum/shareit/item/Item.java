package ru.practicum.shareit.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 // уникальный идентификатор вещи

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false)
    private String name;             // краткое название

    private String description;      // развёрнутое описание

    @NotNull(message = "Available status must be specified")
    @Column(nullable = false)
    private Boolean available;       // статус о том, доступна или нет вещь для аренды

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;            // владелец вещи

    @Column(name = "request_id")
    private Long requestId;          // если вещь была создана по запросу другого пользователя,
    // то в этом поле хранится ссылка на соответствующий запрос
}