package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Дата начала бронирования - обязательный реквизит")
    @Column(name = "start_date")
    LocalDateTime startDate;

    @NotNull(message = "Дата окончания бронирования - обязательный реквизит")
    @Column(name = "end_date")
    LocalDateTime endDate;

    @NotNull(message = "Бронируемая вещь/предмет - обязательный реквизит")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @NotNull(message = "Заказчик бронирования - обязательный реквизит")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "booker_id", nullable = false)
    User booker;

    @NotNull(message = "Статус бронирования должен быть одним из: " +
            "\"WAITING\", \"APPROVED\", \"REJECTED\", \"CANCELED\".")
    @Enumerated(EnumType.STRING)
    @Column(length = 100, nullable = false)
    BookingStatuses status;
}
