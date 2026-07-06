package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OtherOwnerItemEditingException;
import ru.practicum.shareit.exception.WrongBookingStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void create_shouldReturn201_andBookingDto() throws Exception {
        BookingDto resp = buildBookingDto(1L, BookingStatuses.WAITING);
        when(bookingService.create(eq(1L), any())).thenReturn(resp);

        NewBookingDto req = new NewBookingDto();
        req.setItemId(10L);
        req.setStart(LocalDateTime.now().plusHours(1));
        req.setEnd(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void create_shouldReturn404_whenItemNotFound() throws Exception {
        when(bookingService.create(any(), any()))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":99,\"start\":\"2030-01-01T10:00:00\",\"end\":\"2030-01-02T10:00:00\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findBooking_shouldReturn200_andDto() throws Exception {
        BookingDto resp = buildBookingDto(5L, BookingStatuses.APPROVED);
        when(bookingService.findBooking(5L, 1L)).thenReturn(resp);

        mockMvc.perform(get("/bookings/5")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void findBooking_shouldReturn404_whenNotFound() throws Exception {
        when(bookingService.findBooking(eq(99L), any()))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/bookings/99")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByUser_shouldReturn200_withDefaultState() throws Exception {
        when(bookingService.findAllBookingsByUser(1L, "ALL"))
                .thenReturn(List.of(buildBookingDto(1L, BookingStatuses.WAITING)));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllByUser_shouldReturn200_withCustomState() throws Exception {
        when(bookingService.findAllBookingsByUser(1L, "PAST")).thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .param("state", "PAST")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findAllByOwner_shouldReturn200_andList() throws Exception {
        when(bookingService.findAllBookingsByOwnerItems(2L, "ALL"))
                .thenReturn(List.of(buildBookingDto(1L, BookingStatuses.APPROVED)));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void approveBooking_shouldReturn200_whenApproved() throws Exception {
        BookingDto resp = buildBookingDto(1L, BookingStatuses.APPROVED);
        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(resp);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveBooking_shouldReturn403_whenNotOwner() throws Exception {
        when(bookingService.approveBooking(any(), any(), any()))
                .thenThrow(new OtherOwnerItemEditingException("Not owner"));

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID, 2L)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void approveBooking_shouldReturn400_whenAlreadyBooked() throws Exception {
        when(bookingService.approveBooking(any(), any(), any()))
                .thenThrow(new WrongBookingStatusException("Already booked"));

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200_andUpdatedDto() throws Exception {
        BookingDto resp = buildBookingDto(1L, BookingStatuses.APPROVED);
        when(bookingService.update(any())).thenReturn(resp);

        UpdateBookingDto req = new UpdateBookingDto();
        req.setId(1L);
        req.setStatus(BookingStatuses.APPROVED);

        mockMvc.perform(put("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_shouldReturn200_whenBookingDeleted() throws Exception {
        doNothing().when(bookingService).delete(1L);

        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new NotFoundException("Not found")).when(bookingService).delete(99L);

        mockMvc.perform(delete("/bookings/99"))
                .andExpect(status().isNotFound());
    }

    private BookingDto buildBookingDto(Long id, BookingStatuses status) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setStatus(status);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusDays(1));
        ItemDto item = new ItemDto();
        item.setId(10L);
        item.setName("Дрель");
        dto.setItem(item);
        UserDto booker = new UserDto(2L, "booker@mail.ru", "Booker");
        dto.setBooker(booker);
        return dto;
    }
}
