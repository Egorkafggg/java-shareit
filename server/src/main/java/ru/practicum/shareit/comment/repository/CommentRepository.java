package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    @Query("select c " +
            "from Comment as c " +
            "where c.item.id in (:ids) " +
            "order by c.created DESC")
    List<Comment> findByItemIn(@Param("ids")List<Long> ids);
}