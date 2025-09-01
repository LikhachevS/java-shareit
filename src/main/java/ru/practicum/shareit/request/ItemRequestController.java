package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestServiceImpl service;
    private static final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody @Valid ItemRequestCreateDto requestDto,
                                        @RequestHeader(xSharerUserId) long userId) {
        requestDto.setRequesterId(userId);
        return service.createRequest(requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(xSharerUserId) long userId) {
        return service.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherRequests(@RequestHeader(xSharerUserId) long userId) {
        return service.getOtherRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(xSharerUserId) long userId) {
        return service.getRequestById(requestId, userId);
    }
}
