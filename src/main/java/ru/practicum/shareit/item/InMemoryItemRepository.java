package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private Long nextId;
    private final HashMap<Long, List<Item>> itemsByOwner;

    public InMemoryItemRepository() {
        itemsByOwner = new HashMap<>();
        nextId = 1L;
    }

    @Override
    public Item save(Item item) {
        item.setId(nextId);
        nextId++;
        if (itemsByOwner.containsKey(item.getOwner())) {
            itemsByOwner.get(item.getOwner()).add(item);
        } else {
            ArrayList<Item> newList = new ArrayList<>();
            newList.add(item);
            itemsByOwner.put(item.getOwner(), newList);
        }
        return itemsByOwner.get(item.getOwner()).getLast();
    }

    @Override
    public Item patchItem(Item patchItem) {
        Item existingItem = getItemById(patchItem.getId()).get();
        if (patchItem.getName() != null) {
            existingItem.setName(patchItem.getName());
        }
        if (patchItem.getDescription() != null) {
            existingItem.setDescription(patchItem.getDescription());
        }
        if (patchItem.getAvailable() != null) {
            existingItem.setAvailable(patchItem.getAvailable());
        }
        return existingItem;
    }

    @Override
    public List<Item> getItemsFromUser(Long userId) {
        return itemsByOwner.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        for (List<Item> items : itemsByOwner.values()) {
            for (Item item : items) {
                if (item.getId().equals(itemId)) {
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> newItems = new ArrayList<>();
        for (List<Item> items : itemsByOwner.values()) {
            for (Item item : items) {
                if (item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                    newItems.add(item);
                }
            }
        }
        return newItems;
    }
}