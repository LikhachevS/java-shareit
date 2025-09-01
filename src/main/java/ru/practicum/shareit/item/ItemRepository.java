package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE CONCAT('%', LOWER(:text), '%') OR LOWER(i.description) " +
            "LIKE CONCAT('%', LOWER(:text), '%')) AND i.available = true")
    List<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(@Param("text") String text);

    List<Item> findAllByRequestId(Long id);
}