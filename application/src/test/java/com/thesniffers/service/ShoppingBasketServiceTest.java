package com.thesniffers.service;

import com.thesniffers.dao.model.BasketStatus;
import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.repository.BasketItemRepository;
import com.thesniffers.dao.repository.CustomerRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.ShoppingBasketDto;
import com.thesniffers.mapper.ShoppingBasketMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingBasketServiceTest extends ServiceTestBase{

    @Mock private ShoppingBasketRepository shoppingBasketRepository;
    @Mock private BasketItemRepository basketItemRepository;
    @Mock private ShoppingBasketMapper shoppingBasketMapper;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks private ShoppingBasketService shoppingBasketService;

    private UUID basketId;
    private UUID customerId;
    private ShoppingBasket basket;
    private Customer customer;
    private ShoppingBasketDto basketDto;

    @BeforeEach
    void setUp() {
        basketId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setOwner(TENANT_1_SECRET_TOKEN);

        basket = new ShoppingBasket();
        basket.setId(basketId);
        basket.setCustomer(customer);
        basket.setStatus(BasketStatus.NEW);

        basketDto = new ShoppingBasketDto(basketId, ZonedDateTime.now(), BasketStatus.NEW.name(), ZonedDateTime.now(), customerId);
    }

    @Test
    void getAllBaskets_shouldReturnBaskets() {
        mockCurrentUser();
        when(shoppingBasketRepository.findAllAccessibleBaskets(TENANT_1_SECRET_TOKEN, false))
                .thenReturn(List.of(basket));
        when(shoppingBasketMapper.toDto(basket)).thenReturn(basketDto);

        List<ShoppingBasketDto> baskets = shoppingBasketService.getAllBaskets();

        assertEquals(1, baskets.size());
        assertEquals(BasketStatus.NEW.name(), baskets.getFirst().status());
        verify(shoppingBasketRepository).findAllAccessibleBaskets(anyString(), anyBoolean());
    }

    @Test
    void getBasketById_shouldReturnBasket() {
        mockCurrentUser();
        when(shoppingBasketRepository.findAccessibleBasketById(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(Optional.of(basket));
        when(shoppingBasketMapper.toDto(basket)).thenReturn(basketDto);

        Optional<ShoppingBasketDto> result = shoppingBasketService.getBasketById(basketId);

        assertTrue(result.isPresent());
        assertEquals(BasketStatus.NEW.name(), result.get().status());
    }

    @Test
    void createBasket_shouldCreateBasket_whenAuthorized() {
        mockCurrentUser();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(shoppingBasketMapper.toEntity(basketDto)).thenReturn(basket);
        when(shoppingBasketRepository.save(any())).thenReturn(basket);
        when(shoppingBasketMapper.toDto(any())).thenReturn(basketDto);

        ShoppingBasketDto createdBasket = shoppingBasketService.createBasket(basketDto);

        assertNotNull(createdBasket);
        assertEquals(BasketStatus.NEW.name(), createdBasket.status());
        verify(shoppingBasketRepository).save(any(ShoppingBasket.class));
    }

    @Test
    void createBasket_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        customer.setOwner(OTHER_USER_TOKEN);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class, () -> shoppingBasketService.createBasket(basketDto));
    }

    @Test
    void deleteBasket_shouldDeleteBasket_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canDeleteBasket(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(true);

        shoppingBasketService.deleteBasket(basketId);

        verify(shoppingBasketRepository).deleteById(basketId);
    }

    @Test
    void deleteBasket_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canDeleteBasket(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> shoppingBasketService.deleteBasket(basketId));
    }

    @Test
    void updateStatus_shouldUpdateStatus_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canUpdateBasketStatus(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(true);
        when(shoppingBasketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(shoppingBasketRepository.save(any())).thenReturn(basket);
        when(shoppingBasketMapper.toDto(any())).thenReturn(basketDto);

        Optional<ShoppingBasketDto> updated = shoppingBasketService.updateStatus(basketId, BasketStatus.PAID.name());

        assertTrue(updated.isPresent());
        assertEquals(BasketStatus.NEW.name(), updated.get().status());
        verify(shoppingBasketRepository).save(any());
    }

    @Test
    void updateStatus_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canUpdateBasketStatus(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> shoppingBasketService.updateStatus(basketId, BasketStatus.PAID.name()));
    }

    @Test
    void checkoutBasket_shouldCheckout_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canCheckoutBasket(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(true);
        when(shoppingBasketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(shoppingBasketRepository.save(any())).thenReturn(basket);
        when(shoppingBasketMapper.toDto(any())).thenReturn(basketDto);

        Optional<ShoppingBasketDto> result = shoppingBasketService.checkoutBasket(basketId);

        assertTrue(result.isPresent());
        assertEquals(BasketStatus.NEW.name(), result.get().status());
        verify(shoppingBasketRepository).save(any());
    }

    @Test
    void checkoutBasket_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canCheckoutBasket(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> shoppingBasketService.checkoutBasket(basketId));
    }

    @Test
    void clearBasket_shouldClearItems_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canClearBasket(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(true);
        when(basketItemRepository.deleteAllByShoppingBasketId(basketId)).thenReturn(3);

        shoppingBasketService.clearBasket(basketId);

        verify(basketItemRepository).deleteAllByShoppingBasketId(basketId);
    }

    @Test
    void getTotalItems_shouldReturnCount_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canViewBasketItems(basketId, TENANT_1_SECRET_TOKEN, false))
                .thenReturn(true);
        when(basketItemRepository.findByShoppingBasketId(basketId)).thenReturn(List.of());

        int count = shoppingBasketService.getTotalItems(basketId);

        assertEquals(0, count);
    }
}
