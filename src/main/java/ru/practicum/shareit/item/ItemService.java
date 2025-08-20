package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.List;

public interface ItemService {
    public ItemDto addItem(ItemCreateDto item);

    public ItemDto patchItem(ItemPatchDto patchItem);

    public ItemDto getItemById(Long itemId);

    public List<ItemDto> getItemsFromUser(Long userId);

    public List<ItemDto> searchItems(String text);

    public boolean existsUserById(Long id);
}