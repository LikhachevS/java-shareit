package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto) {
        if (!userRepository.existsById(itemCreateDto.getOwner())) {
            throw new NotFoundException("Пользователь с id " + itemCreateDto.getOwner() + " не найден.");
        }

        Item newItem = itemMapper.toItem(itemCreateDto);

        if (itemCreateDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id " + itemCreateDto.getRequestId() + " не найден."));

            newItem.setRequest(itemRequest);
        }

        return itemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto patchItem(ItemPatchDto patchItem) {
        Item existingItem = itemRepository.findById(patchItem.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + patchItem.getId() + " не найдена."));

        if (!existingItem.getOwner().getId().equals(patchItem.getOwner())) {
            throw new ForbiddenOperationException("Недостаточно прав для выполнения операции");
        }

        if (patchItem.getName() != null) {
            existingItem.setName(patchItem.getName());
        }
        if (patchItem.getDescription() != null) {
            existingItem.setDescription(patchItem.getDescription());
        }
        if (patchItem.getAvailable() != null) {
            existingItem.setAvailable(patchItem.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена."));

        ItemDto itemDto = itemMapper.toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime lastBooking = bookingRepository.findLastBookingDate(itemId, LocalDateTime.now())
                    .orElse(null);
            itemDto.setLastBooking(lastBooking);

            LocalDateTime nextBooking = bookingRepository.findNextBookingDate(itemId, LocalDateTime.now())
                    .orElse(null);
            itemDto.setNextBooking(nextBooking);
        }

        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsFromUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toItemDto(item);

                    LocalDateTime lastBooking = bookingRepository.findLastBookingDate(item.getId(), LocalDateTime.now())
                            .orElse(null);
                    itemDto.setLastBooking(lastBooking);

                    LocalDateTime nextBooking = bookingRepository.findNextBookingDate(item.getId(), LocalDateTime.now())
                            .orElse(null);
                    itemDto.setNextBooking(nextBooking);

                    List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());
                    List<CommentDto> commentDtos = comments.stream()
                            .map(commentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    itemDto.setComments(commentDtos);

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsUserById(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public CommentDto addComment(CommentCreateDto commentCreateDto) {
        LocalDateTime now = LocalDateTime.now();

        if (!userRepository.existsById(commentCreateDto.getAuthorId())) {
            throw new NotFoundException("Пользователь с id " + commentCreateDto.getAuthorId() + " не найден");
        }

        if (!itemRepository.existsById(commentCreateDto.getItemId())) {
            throw new NotFoundException("Вещь с id " + commentCreateDto.getItemId() + " не найдена");
        }

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(commentCreateDto.getAuthorId(),
                commentCreateDto.getItemId(), now)) {
            throw new ValidationException("Пользователь не арендовал эту вещь и не может оставлять комментарии");
        }

        return commentMapper.toCommentDto(commentRepository.save(commentMapper.toComment(commentCreateDto, now)));
    }
}