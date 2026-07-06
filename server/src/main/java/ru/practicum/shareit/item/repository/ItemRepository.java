package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it "  +
            "from Item as it " +
            "where it.ownerUser.id = :userId ")
    List<Item> findAllByUserId(@Param("userId")Long ownerId);

    @Query("select it " +
            "from Item as it " +
            "join it.ownerUser as u " +
            "where it.available = true " +
            "and (lower(it.name) like lower(concat('%', :text, '%')) " +
            "or lower(it.description) like lower(concat('%', :text, '%'))) ")
    List<Item> getItemsForRenter(@Param("text")String renterWishes);

    Collection<Item> findByRequestId(Long requestId);

    Collection<Item> findByRequestIdIn(List<Long> requestIds);

}
