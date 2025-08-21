package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item patchItem(Item newItem);

    List<Item> getItemsFromUser(Long userId);

    Optional<Item> getItemById(Long itemId);

    List<Item> searchItems(String text);
}