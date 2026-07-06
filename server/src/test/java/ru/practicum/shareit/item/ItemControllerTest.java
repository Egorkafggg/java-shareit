package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OtherOwnerItemEditingException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CreateNewItemDto;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateExistsItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createNewItem_shouldReturn200_andItemDto() throws Exception {
        ItemDto resp = itemDto(1L, "Дрель", "Мощная", true);
        when(itemService.createNewItem(any(), eq(1L))).thenReturn(resp);

        CreateNewItemDto req = new CreateNewItemDto();
        req.setName("Дрель");
        req.setDescription("Мощная");
        req.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void createNewItem_shouldReturn404_whenUserNotFound() throws Exception {
        when(itemService.createNewItem(any(), any()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"description\":\"X\",\"available\":true}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExistsItem_shouldReturn200_andUpdatedDto() throws Exception {
        ItemDto resp = itemDto(1L, "Updated", "Новое описание", false);
        when(itemService.updateExistsItem(any(), eq(1L), eq(1L))).thenReturn(resp);

        UpdateExistsItemDto req = new UpdateExistsItemDto();
        req.setName("Updated");

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void updateExistsItem_shouldReturn403_whenNotOwner() throws Exception {
        when(itemService.updateExistsItem(any(), any(), any()))
                .thenThrow(new OtherOwnerItemEditingException("Not owner"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getItemByOwnerAndId_shouldReturn200_andExtendedDto() throws Exception {
        ExtendedItemDto resp = extendedItemDto(1L);
        when(itemService.getItemByOwnerAndId(1L, 1L)).thenReturn(resp);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemByOwnerAndId_shouldReturn404_whenNotFound() throws Exception {
        when(itemService.getItemByOwnerAndId(any(), eq(99L)))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/items/99")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturn200_andList() throws Exception {
        when(itemService.getAllItemsByOwner(1L))
                .thenReturn(List.of(extendedItemDto(1L), extendedItemDto(2L)));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findAll_shouldReturn200_andEmptyList_whenOwnerHasNoItems() throws Exception {
        when(itemService.getAllItemsByOwner(99L)).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void search_shouldReturn200_andMatchingItems() throws Exception {
        when(itemService.getItemsForRenter("дрель"))
                .thenReturn(List.of(itemDto(1L, "Дрель", "Мощная", true)));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void search_shouldReturn200_andEmptyList_whenTextEmpty() throws Exception {
        when(itemService.getItemsForRenter("")).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deleteItem_shouldReturn200() throws Exception {
        doNothing().when(itemService).deleteItem(1L);

        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new NotFoundException("Not found")).when(itemService).deleteItem(99L);

        mockMvc.perform(delete("/items/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_shouldReturn200_andCommentDto() throws Exception {
        CommentDto resp = new CommentDto();
        resp.setId(1L);
        resp.setText("Отличная дрель!");
        resp.setAuthorName("Booker");
        resp.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(1L), eq(2L), any())).thenReturn(resp);

        NewCommentDto req = new NewCommentDto();
        req.setText("Отличная дрель!");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная дрель!"))
                .andExpect(jsonPath("$.authorName").value("Booker"));
    }

    @Test
    void addComment_shouldReturn400_whenUserNeverBookedItem() throws Exception {
        when(itemService.addComment(any(), any(), any()))
                .thenThrow(new ValidationException("Пользователь не может добавить комментарий"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Test\"}"))
                .andExpect(status().isBadRequest());
    }

    private ItemDto itemDto(Long id, String name, String desc, boolean available) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(desc);
        dto.setAvailable(available);
        return dto;
    }

    private ExtendedItemDto extendedItemDto(Long id) {
        ExtendedItemDto dto = new ExtendedItemDto();
        dto.setId(id);
        dto.setName("Item " + id);
        dto.setDescription("Description " + id);
        dto.setAvailable(true);
        dto.setComments(List.of());
        return dto;
    }
}
