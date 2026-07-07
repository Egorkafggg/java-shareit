package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Item name cannot be empty")
    private String name;

    @NotBlank(message = "Item description cannot be empty")
    private String description;

    @NotBlank(message = "Item availability cannot be empty")
    private Boolean available;
}
