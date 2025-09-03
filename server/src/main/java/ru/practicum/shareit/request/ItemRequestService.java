package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestCreateDto requestDto);

    List<ItemRequestDto> getOwnRequests(long userId);

    List<ItemRequestDto> getOtherRequests(long userId);

    ItemRequestDto getRequestById(long requestId, long userId);
}
