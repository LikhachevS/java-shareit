package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        List<Item> answerItems = itemRepository.findAllByRequestId(itemRequest.getId());
        List<ItemDto> itemDtos = answerItems.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(itemDtos);

        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto, LocalDateTime now) {
        User requester = userRepository.findById(itemRequestCreateDto.getRequesterId()).get();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreateDto.getDescription());
        itemRequest.setCreated(now);
        itemRequest.setRequester(requester);

        return itemRequest;
    }
}