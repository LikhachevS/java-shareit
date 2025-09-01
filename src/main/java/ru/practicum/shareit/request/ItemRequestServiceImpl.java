package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    public ItemRequestDto createRequest(ItemRequestCreateDto itemRequestCreateDto) {
        LocalDateTime now = LocalDateTime.now();

        if (!userRepository.existsById(itemRequestCreateDto.getRequesterId())) {
            throw new NotFoundException("Пользователь с id " + itemRequestCreateDto.getRequesterId() + " не найден");
        }

        return requestMapper.toItemRequestDto(itemRequestRepository.save(requestMapper.toItemRequest(itemRequestCreateDto, now)));
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherRequests(long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ForbiddenOperationException("Недостаточно прав для выполнения операции");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ForbiddenOperationException("Недостаточно прав для выполнения операции");
        }

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        return requestMapper.toItemRequestDto(request);
    }
}
