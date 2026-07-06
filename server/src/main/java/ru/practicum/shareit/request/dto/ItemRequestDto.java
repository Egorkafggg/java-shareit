package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long requestorId; // userId
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDateTime created;

    List<ResponseDto> items;
}
