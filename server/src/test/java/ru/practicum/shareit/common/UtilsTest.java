package ru.practicum.shareit.common;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    /* я таки конечно извиняюсь, но для такого просто случая нафантазировать и накодить
    тесты оказалось намного проще, чем для нескольких слоёв бизнес-логики основного
    приложения. Пусть будут!
     */

    @Test
    void getNewId_WithEmptySet_ShouldReturnOne() {
        Set<Long> idSourceSet = Set.of();
        Long newId = Utils.getNewId(idSourceSet);
        assertThat(newId).isEqualTo(1L);
    }

    @Test
    void getNewId_WithSingleElement_ShouldReturnNextId() {
        Set<Long> idSourceSet = Set.of(5L);
        Long newId = Utils.getNewId(idSourceSet);
        assertThat(newId).isEqualTo(6L);
    }

    @Test
    void getNewId_WithMultipleElements_ShouldReturnMaxPlusOne() {
        Set<Long> idSourceSet = Set.of(1L, 3L, 7L, 2L);
        Long newId = Utils.getNewId(idSourceSet);
        assertThat(newId).isEqualTo(8L);
    }

    @Test
    void getNewId_WithNegativeIds_ShouldReturnCorrectNextId() {
        Set<Long> idSourceSet = Set.of(-5L, -1L, 0L, 3L);
        Long newId = Utils.getNewId(idSourceSet);
        assertThat(newId).isEqualTo(4L);
    }

    @Test
    void getNewId_WithMaxLongValue_ShouldReturnMinLongValue() {
        Set<Long> idSourceSet = Set.of(Long.MAX_VALUE);
        Long newId = Utils.getNewId(idSourceSet);
        assertThat(newId).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void getNewId_WithConsecutiveIds_ShouldReturnNextSequentialId() {
        Set<Long> idSourceSet = Set.of(100L, 101L, 102L, 103L);
        Long newId = Utils.getNewId(idSourceSet);
        assertThat(newId).isEqualTo(104L);
    }
}
