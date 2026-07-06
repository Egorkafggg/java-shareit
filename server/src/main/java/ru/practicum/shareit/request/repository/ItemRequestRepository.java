package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findByRequestorId(Long requestorId);

}
