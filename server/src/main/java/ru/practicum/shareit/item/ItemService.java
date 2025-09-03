package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    public ItemDto addItem(ItemCreateDto item);

    public ItemDto patchItem(ItemPatchDto patchItem);

    public ItemDto getItemById(Long itemId, Long userId);

    public List<ItemDto> getItemsFromUser(Long userId);

    public List<ItemDto> searchItems(String text);

    boolean existsUserById(long userId);

    public CommentDto addComment(CommentCreateDto comment);
}