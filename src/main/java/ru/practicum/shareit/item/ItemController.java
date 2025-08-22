package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestBody @Valid ItemCreateDto item, @RequestHeader(xSharerUserId) long userId) {
        item.setOwner(userId);
        return service.addItem(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestBody @Valid ItemPatchDto patchItem,
                         @PathVariable Long itemId, @RequestHeader(xSharerUserId) long userId) {
        patchItem.setId(itemId);
        patchItem.setOwner(userId);
        return service.patchItem(patchItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(xSharerUserId) long userId) {
        if (service.existsUserById(userId)) {
            return service.getItemById(itemId);
        } else {
            throw new ForbiddenOperationException("Недостаточно прав для выполнения операции");
        }
    }

    @GetMapping
    public List<ItemDto> getItemsFromUser(@RequestHeader(xSharerUserId) long userId) {
        return service.getItemsFromUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return service.searchItems(text);
    }
}