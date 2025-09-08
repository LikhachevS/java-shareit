package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class IntegrationTest {

    private final ItemServiceImpl itemService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestServiceImpl itemRequestService;
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    public void testGetItemsFromUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        List<ItemDto> items = itemService.getItemsFromUser(user.getId());

        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getName()).isEqualTo("Test Item");
    }

    @Test
    public void integrationTest_addComment_SuccessfulCommentAdded() {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivanov@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Электрочайник");
        item.setDescription("Красный электрочайник мощностью 2 кВт");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(1)); // Закончено раньше текущего момента
        booking.setEnd(LocalDateTime.now().minusMinutes(30));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличный чайник!");
        dto.setAuthorId(user.getId());
        dto.setItemId(item.getId());

        CommentDto addedComment = itemService.addComment(dto);

        assertThat(addedComment.getText()).isEqualTo("Отличный чайник!");
        assertThat(addedComment.getAuthorName()).isEqualTo("Иван Иванов");
    }

    @Test
    public void integrationTest_addItem_NewItemSuccessfullyAdded() {
        User user = new User();
        user.setName("Иван Петров");
        user.setEmail("petrov@example.com");
        userRepository.save(user);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Новогодняя гирлянда");
        itemCreateDto.setDescription("Яркая новогодняя гирлянда длиной 5 метров");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setOwner(user.getId());

        itemCreateDto.setRequestId(null);

        ItemDto addedItem = itemService.addItem(itemCreateDto);

        assertThat(addedItem.getName()).isEqualTo("Новогодняя гирлянда");
        assertThat(addedItem.getDescription()).isEqualTo("Яркая новогодняя гирлянда длиной 5 метров");
        assertThat(addedItem.isAvailable()).isTrue();

        Optional<Item> savedItemOptional = itemRepository.findById(addedItem.getId());
        assertTrue(savedItemOptional.isPresent());
        Item savedItem = savedItemOptional.get();
        assertThat(savedItem.getName()).isEqualTo("Новогодняя гирлянда");
        assertThat(savedItem.getDescription()).isEqualTo("Яркая новогодняя гирлянда длиной 5 метров");
        assertThat(savedItem.getAvailable()).isTrue();
        assertThat(savedItem.getOwner().getId()).isEqualTo(user.getId()); // В базе мы можем достать владельца
    }

    @Test
    public void integrationTest_patchItem_ItemSuccessfullyUpdated() {
        User user = new User();
        user.setName("Василий Сидоров");
        user.setEmail("sidorov@gmail.com");
        userRepository.save(user);

        Item originalItem = new Item();
        originalItem.setName("Старая гитара");
        originalItem.setDescription("Старенькая акустическая гитара");
        originalItem.setAvailable(false);
        originalItem.setOwner(user);
        itemRepository.save(originalItem);

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setId(originalItem.getId());
        patchDto.setOwner(user.getId());
        patchDto.setName("Новачок-гитара");
        patchDto.setDescription("Идеальна для начинающих музыкантов");
        patchDto.setAvailable(true);

        ItemDto updatedItem = itemService.patchItem(patchDto);

        assertThat(updatedItem.getName()).isEqualTo("Новачок-гитара");
        assertThat(updatedItem.getDescription()).isEqualTo("Идеальна для начинающих музыкантов");
        assertThat(updatedItem.isAvailable()).isTrue();

        Optional<Item> savedItemOptional = itemRepository.findById(updatedItem.getId());
        assertTrue(savedItemOptional.isPresent());
        Item savedItem = savedItemOptional.get();
        assertThat(savedItem.getName()).isEqualTo("Новачок-гитара");
        assertThat(savedItem.getDescription()).isEqualTo("Идеальна для начинающих музыкантов");
        assertThat(savedItem.getAvailable()).isTrue();
    }

    @Test
    public void integrationTest_getItemById_ReturnsCorrectItemDetails() {
        User user = new User();
        user.setName("Алексей Смирнов");
        user.setEmail("smirnov@gmail.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Профессиональная гитара");
        item.setDescription("Высшего качества струнный инструмент");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusMonths(1));
        pastBooking.setEnd(LocalDateTime.now().minusWeeks(2));
        pastBooking.setItem(item);
        pastBooking.setBooker(user);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusWeeks(2));
        futureBooking.setEnd(LocalDateTime.now().plusMonths(1));
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        Comment comment = new Comment();
        comment.setText("Отличная гитара, звучит прекрасно!");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        ItemDto retrievedItem = itemService.getItemById(item.getId(), user.getId());

        assertThat(retrievedItem.getName()).isEqualTo("Профессиональная гитара");
        assertThat(retrievedItem.getDescription()).isEqualTo("Высшего качества струнный инструмент");
        assertThat(retrievedItem.isAvailable()).isTrue();
        assertThat(retrievedItem.getLastBooking()).isNotNull();
        assertThat(retrievedItem.getNextBooking()).isNotNull();
        assertThat(retrievedItem.getComments().size()).isEqualTo(1);
        assertThat(retrievedItem.getComments().get(0).getText()).isEqualTo("Отличная гитара, звучит прекрасно!");
    }

    @Test
    public void integrationTest_searchItems_ReturnsMatchingItemsOnly() {
        User user = new User();
        user.setName("Александр Попов");
        user.setEmail("popov@example.com");
        userRepository.save(user);

        Item guitar = new Item();
        guitar.setName("Черная гитара");
        guitar.setDescription("Музыкальный инструмент высокого качества");
        guitar.setAvailable(true);
        guitar.setOwner(user);
        itemRepository.save(guitar);

        Item jacket = new Item();
        jacket.setName("Красная куртка");
        jacket.setDescription("Теплая осенняя одежда");
        jacket.setAvailable(false);
        jacket.setOwner(user);
        itemRepository.save(jacket);

        List<ItemDto> searchResults = itemService.searchItems("гитара");

        assertThat(searchResults.size()).isEqualTo(1); // Должна вернуться только одна вещь
        assertThat(searchResults.get(0).getName()).isEqualTo("Черная гитара");
        assertThat(searchResults.get(0).getDescription()).isEqualTo("Музыкальный инструмент высокого качества");
        assertThat(searchResults.get(0).isAvailable()).isTrue();
    }

    @Test
    public void testGetBookingsForOwnedItems() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<BookingDto> bookings = bookingService.getBookingsForOwnedItems(BookingController.State.ALL, user.getId());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item.getId());
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(user.getId());
    }

    @Test
    public void integrationTest_createBooking_SuccessfullyCreatesBooking() {
        User user = new User();
        user.setName("Андрей Кузнецов");
        user.setEmail("kuznetsov@example.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Микрофон");
        item.setDescription("Высококачественный микрофон для караоке");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setBookerId(user.getId());
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookedItem = bookingService.createBooking(bookingCreateDto);

        assertThat(bookedItem.getBooker().getId()).isEqualTo(user.getId());
        assertThat(bookedItem.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookedItem.getStart()).isAfter(LocalDateTime.now());
        assertThat(bookedItem.getEnd()).isAfter(LocalDateTime.now());

        Optional<Booking> savedBookingOpt = bookingRepository.findById(bookedItem.getId());
        assertTrue(savedBookingOpt.isPresent());
        Booking savedBooking = savedBookingOpt.get();
        assertThat(savedBooking.getBooker().getId()).isEqualTo(user.getId());
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    public void integrationTest_approveBooking_SucceedsWhenApprovedByOwner() {
        User owner = new User();
        owner.setName("Сергей Петухов");
        owner.setEmail("petuhov@example.com");
        userRepository.save(owner);

        User client = new User();
        client.setName("Дмитрий Александров");
        client.setEmail("alexandrov@example.com");
        userRepository.save(client);

        Item item = new Item();
        item.setName("Камера Sony Alpha");
        item.setDescription("Фотокамера среднего ценового сегмента");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(client);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDto updatedBooking = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED); // Успешное подтверждение

        Optional<Booking> savedBookingOpt = bookingRepository.findById(booking.getId());
        assertTrue(savedBookingOpt.isPresent());
        Booking savedBooking = savedBookingOpt.get();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void integrationTest_getBooking_AccessibleByOwnerAndBooker() {
        User owner = new User();
        owner.setName("Виктор Ильин");
        owner.setEmail("ilyin@example.com");
        userRepository.save(owner);

        User client = new User();
        client.setName("Антон Савченко");
        client.setEmail("savchenko@example.com");
        userRepository.save(client);

        Item item = new Item();
        item.setName("Экшн-камера GoPro Hero");
        item.setDescription("Профессиональная экшн-камера для активного отдыха");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(client);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        BookingDto bookingInfoOwner = bookingService.getBooking(booking.getId(), owner.getId());
        assertThat(bookingInfoOwner.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingInfoOwner.getBooker().getId()).isEqualTo(client.getId());

        BookingDto bookingInfoBooker = bookingService.getBooking(booking.getId(), client.getId());
        assertThat(bookingInfoBooker.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingInfoBooker.getBooker().getId()).isEqualTo(client.getId());
    }

    @Test
    public void integrationTest_getBookingsForCurrentUser_ReturnsCurrentBookings() {
        User owner = new User();
        owner.setName("Владимир Михайлов");
        owner.setEmail("mikhailov@example.com");
        userRepository.save(owner);

        User client = new User();
        client.setName("Кирилл Гуров");
        client.setEmail("gurov@example.com");
        userRepository.save(client);

        Item item = new Item();
        item.setName("Планшет iPad Pro");
        item.setDescription("Профессиональный планшет с большой диагональю экрана");
        item.setAvailable(true);
        item.setOwner(owner); // !!! ВАЖНО: присваиваем владельца вещи !!!
        itemRepository.save(item);

        Booking currentBooking = new Booking();
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusDays(3));
        currentBooking.setItem(item);
        currentBooking.setBooker(client);
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusWeeks(2));
        futureBooking.setEnd(LocalDateTime.now().plusWeeks(3));
        futureBooking.setItem(item);
        futureBooking.setBooker(client);
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        List<BookingDto> currentBookings = bookingService.getBookingsForCurrentUser(BookingController.State.CURRENT, client.getId());

        assertThat(currentBookings).hasSize(1); // Должно быть только текущее бронирование
        assertThat(currentBookings.get(0).getItem().getId()).isEqualTo(item.getId());
        assertThat(currentBookings.get(0).getBooker().getId()).isEqualTo(client.getId());
    }

    @Test
    public void integrationTest_createRequest_CorrectlyCreatesRequest() {
        // 1. Подготовка тестовых данных
        User user = new User();
        user.setName("Ольга Васильевна");
        user.setEmail("olga.vasilieva@example.com");
        userRepository.save(user);

        // Заполняем DTO для создания запроса
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setRequesterId(user.getId());
        createDto.setDescription("Необходим комплект посуды для кухни");

        // 2. Вызов метода создания запроса
        ItemRequestDto createdRequest = itemRequestService.createRequest(createDto);

        // 3. Проверка результата
        assertThat(createdRequest.getDescription()).isEqualTo("Необходим комплект посуды для кухни");

        // Дополнительно проверим, что объект появился в базе данных
        Optional<ItemRequest> savedRequestOpt = itemRequestRepository.findById(createdRequest.getId());
        assertTrue(savedRequestOpt.isPresent());
        ItemRequest savedRequest = savedRequestOpt.get();
        assertThat(savedRequest.getDescription()).isEqualTo("Необходим комплект посуды для кухни");
        assertThat(savedRequest.getRequester().getId()).isEqualTo(user.getId());
    }

    @Test
    public void testGetOwnRequests() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> requests = itemRequestService.getOwnRequests(user.getId());

        assertThat(requests).isNotEmpty();
        assertThat(requests.get(0).getDescription()).isEqualTo("Test Request");
        assertThat(requests.get(0).getCreated()).isNotNull();
        assertThat(requests.get(0).getItems()).isEmpty();
    }

    @Test
    public void integrationTest_getOtherRequests_RetrievesForeignRequests() {
        User user = new User();
        user.setName("Михаил Петрович");
        user.setEmail("michael.petrovich@example.com");
        userRepository.save(user);

        User foreignUser = new User();
        foreignUser.setName("Сергей Сергеевич");
        foreignUser.setEmail("sergey.sergeevich@example.com");
        userRepository.save(foreignUser);

        ItemRequest foreignRequest = new ItemRequest();
        foreignRequest.setDescription("Покупаю детскую коляску");
        foreignRequest.setRequester(foreignUser);
        foreignRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(foreignRequest);

        List<ItemRequestDto> foreignRequests = itemRequestService.getOtherRequests(user.getId());

        assertThat(foreignRequests).hasSize(1);
        assertThat(foreignRequests.get(0).getDescription()).isEqualTo("Покупаю детскую коляску");
    }

    @Test
    public void integrationTest_getRequestById_RetrievesSpecificRequest() {
        User user = new User();
        user.setName("Анна Николаевна");
        user.setEmail("anna.nikolaeva@example.com");
        userRepository.save(user);

        // Создаем запрос
        ItemRequest request = new ItemRequest();
        request.setDescription("Куплю набор инструментов");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request);

        ItemRequestDto specificRequest = itemRequestService.getRequestById(request.getId(), user.getId());

        assertThat(specificRequest.getDescription()).isEqualTo("Куплю набор инструментов");
    }

    @Test
    public void integrationTest_addUser_UserSuccessfullyAdded() {
        // 1. Подготовка тестовых данных
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Артем Васильев");
        userCreateDto.setEmail("artem.vasilev@example.com");

        // 2. Вызов метода добавления пользователя
        UserDto addedUser = userService.addUser(userCreateDto);

        // 3. Проверка результата
        assertThat(addedUser.getName()).isEqualTo("Артем Васильев");
        assertThat(addedUser.getEmail()).isEqualTo("artem.vasilev@example.com");

        // Дополнительно проверим, что пользователь попал в базу данных
        Optional<User> savedUserOpt = userRepository.findById(addedUser.getId());
        assertTrue(savedUserOpt.isPresent());
        User savedUser = savedUserOpt.get();
        assertThat(savedUser.getName()).isEqualTo("Артем Васильев");
        assertThat(savedUser.getEmail()).isEqualTo("artem.vasilev@example.com");
    }

    @Test
    public void integrationTest_patchUser_UserSuccessfullyUpdated() {
        // 1. Подготовка тестовых данных
        User user = new User();
        user.setName("Никита Борисов");
        user.setEmail("nikita.borisov@example.com");
        userRepository.save(user);

        // Готовим патч
        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setId(user.getId());
        patchDto.setName("Никита Игоревич Борисов");
        patchDto.setEmail("igor.borisov@example.com");

        // 2. Вызов метода редактирования пользователя
        UserDto patchedUser = userService.patchUser(patchDto);

        // 3. Проверка результата
        assertThat(patchedUser.getName()).isEqualTo("Никита Игоревич Борисов");
        assertThat(patchedUser.getEmail()).isEqualTo("igor.borisov@example.com");

        // Проверим, что изменения внесены в базу данных
        Optional<User> savedUserOpt = userRepository.findById(patchedUser.getId());
        assertTrue(savedUserOpt.isPresent());
        User savedUser = savedUserOpt.get();
        assertThat(savedUser.getName()).isEqualTo("Никита Игоревич Борисов");
        assertThat(savedUser.getEmail()).isEqualTo("igor.borisov@example.com");
    }

    @Test
    public void integrationTest_getAllUsers_ListOfUsersRetrieved() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("User 1");
        assertThat(users.get(1).getName()).isEqualTo("User 2");
    }

    @Test
    public void integrationTest_getUserById_UserRetrieved() {
        // 1. Подготовка тестовых данных
        User user = new User();
        user.setName("Арсений Алексеевич");
        user.setEmail("arseny.alekseevich@example.com");
        userRepository.save(user);

        // 2. Вызов метода получения пользователя по идентификатору
        UserDto retrievedUser = userService.getUserById(user.getId());

        // 3. Проверка результата
        assertThat(retrievedUser.getName()).isEqualTo("Арсений Алексеевич");
        assertThat(retrievedUser.getEmail()).isEqualTo("arseny.alekseevich@example.com");
    }

    @Test
    public void integrationTest_deleteUserById_UserDeleted() {
        // 1. Подготовка тестовых данных
        User user = new User();
        user.setName("Роман Викторович");
        user.setEmail("roman.victorovich@example.com");
        userRepository.save(user);

        // 2. Вызов метода удаления пользователя
        userService.deleteUserById(user.getId());

        // 3. Проверка результата
        Optional<User> deletedUserOpt = userRepository.findById(user.getId());
        assertFalse(deletedUserOpt.isPresent());
    }
}