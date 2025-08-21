package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addItem(ItemCreateDto item) {
        if (existsUserById(item.getOwner())) {
            return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(item)));
        } else {
            throw new NotFoundException("Пользователь с id " + item.getOwner() + " не найден.");
        }
    }

    @Override
    public ItemDto patchItem(ItemPatchDto patchItem) {
        Optional<Item> optionalItem = itemRepository.getItemById(patchItem.getId());

        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь с id " + patchItem.getId() + " не найдена.");
        }

        Item existingItem = optionalItem.get();

        if (!existingItem.getOwner().equals(patchItem.getOwner())) {
            throw new ForbiddenOperationException("Недостаточно прав для выполнения операции");
        }

        return itemMapper.toItemDto(itemRepository.patchItem(itemMapper.toItem(patchItem)));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена.")));
    }

    @Override
    public List<ItemDto> getItemsFromUser(Long userId) {
        if (existsUserById(userId)) {
            List<Item> items = itemRepository.getItemsFromUser(userId);
            return items.stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.searchItems(text);
        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsUserById(Long id) {
        return userRepository.getUserById(id).isPresent();
    }
}