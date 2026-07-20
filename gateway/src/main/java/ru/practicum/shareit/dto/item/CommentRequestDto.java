package ru.practicum.shareit.dto.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message = "Comment text cannot be empty")
    private String text;
}