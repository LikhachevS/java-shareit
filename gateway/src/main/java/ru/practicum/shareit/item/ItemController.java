package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemCreateDto item, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Adding item: {}", item);
        return itemClient.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestBody @Valid ItemPatchDto patchItem,
                                            @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Patching item with id {}: {}", itemId, patchItem);
        return itemClient.patchItem(itemId, patchItem, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting item with id: {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting items from user with id: {}", userId);
        return itemClient.getItemsFromUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Searching items with text: {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentCreateDto comment,
                                             @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Adding comment to item with id {}: {}", itemId, comment);
        return itemClient.addComment(itemId, comment, userId);
    }
}