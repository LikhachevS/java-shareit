package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public Item toItem(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(itemCreateDto.getOwner());
        return item;
    }

    public Item toItem(ItemPatchDto itemPatchDto) {
        Item item = new Item();
        item.setId(itemPatchDto.getId());
        item.setName(itemPatchDto.getName());
        item.setDescription(itemPatchDto.getDescription());
        item.setAvailable(itemPatchDto.getAvailable());
        item.setOwner(itemPatchDto.getOwner());
        return item;
    }
}