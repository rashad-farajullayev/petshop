package com.thesniffers.service;

import com.thesniffers.dao.model.Item;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.repository.BasketItemRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.BasketItemDto;
import com.thesniffers.mapper.BasketItemMapper;
import com.thesniffers.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketItemServiceTest extends ServiceTestBase{


    @Mock private BasketItemRepository basketItemRepository;
    @Mock private ShoppingBasketRepository shoppingBasketRepository;
    @Mock private BasketItemMapper basketItemMapper;

    @InjectMocks private BasketItemService basketItemService;
    private MockedStatic<SecurityUtils> securityMock;

    private UUID basketId;
    private UUID basketItemId;
    private BasketItemDto basketItemDto;
    private Item item;
    private ShoppingBasket basket;
    private Customer customer;

    @BeforeEach
    void setUp() {
        basketId = UUID.randomUUID();
        basketItemId = UUID.randomUUID();

        customer = new Customer();
        customer.setOwner(TENANT_1_SECRET_TOKEN);

        basket = new ShoppingBasket();
        basket.setId(basketId);
        basket.setCustomer(customer);

        item = new Item();
        item.setId(basketItemId);
        item.setShoppingBasket(basket);

        basketItemDto = new BasketItemDto(basketItemId, BASKET_ITEM_NAME, 2, basketId);
    }

    @Test
    void getItemsByBasketId_shouldReturnItems_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canViewBasketItems(basketId, "tenant1-secret-token-abcdef", false)).thenReturn(true);
        when(basketItemRepository.findByShoppingBasketId(basketId)).thenReturn(List.of(item));
        when(basketItemMapper.toDto(item)).thenReturn(basketItemDto);

        List<BasketItemDto> items = basketItemService.getItemsByBasketId(basketId);

        assertEquals(1, items.size());
        assertEquals(BASKET_ITEM_NAME, items.get(0).description());
        verify(basketItemRepository).findByShoppingBasketId(basketId);
    }

    @Test
    void getItemsByBasketId_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.canViewBasketItems(basketId, TENANT_1_SECRET_TOKEN, false)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> basketItemService.getItemsByBasketId(basketId));
    }

    @Test
    void getBasketItemById_shouldReturnItem_whenAuthorized() {
        mockCurrentUser();
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(item));
        when(basketItemMapper.toDto(item)).thenReturn(basketItemDto);

        Optional<BasketItemDto> foundItem = basketItemService.getBasketItemById(basketItemId);

        assertTrue(foundItem.isPresent());
        assertEquals(BASKET_ITEM_NAME, foundItem.get().description());
    }

    @Test
    void getBasketItemById_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        item.getShoppingBasket().getCustomer().setOwner("other-user-token");

        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> basketItemService.getBasketItemById(basketItemId));
    }

    @Test
    void createBasketItem_shouldCreateItem_whenAuthorized() {
        mockCurrentUser();
        when(shoppingBasketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(basketItemMapper.toEntity(basketItemDto)).thenReturn(item);
        when(basketItemRepository.save(item)).thenReturn(item);
        when(basketItemMapper.toDto(item)).thenReturn(basketItemDto);

        BasketItemDto createdItem = basketItemService.createBasketItem(basketItemDto);

        assertNotNull(createdItem);
        assertEquals(BASKET_ITEM_NAME, createdItem.description());
        verify(basketItemRepository).save(any(Item.class));
    }

    @Test
    void createBasketItem_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        basket.getCustomer().setOwner(OTHER_USER_TOKEN);

        when(shoppingBasketRepository.findById(basketId)).thenReturn(Optional.of(basket));

        assertThrows(AccessDeniedException.class, () -> basketItemService.createBasketItem(basketItemDto));
    }

    @Test
    void updateBasketItem_shouldUpdateItem_whenAuthorized() {
        mockCurrentUser();
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(item));
        when(shoppingBasketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(basketItemRepository.save(any(Item.class))).thenReturn(item);
        when(basketItemMapper.toDto(item)).thenReturn(basketItemDto);

        BasketItemDto updatedItem = basketItemService.updateBasketItem(basketItemId, basketItemDto);

        assertNotNull(updatedItem);
        assertEquals(BASKET_ITEM_NAME, updatedItem.description());
        verify(basketItemRepository).save(item);
    }

    @Test
    void updateBasketItem_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        basket.getCustomer().setOwner(OTHER_USER_TOKEN);

        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(item));
        when(shoppingBasketRepository.findById(basketId)).thenReturn(Optional.of(basket));

        assertThrows(AccessDeniedException.class, () -> basketItemService.updateBasketItem(basketItemId, basketItemDto));
    }

    @Test
    void deleteBasketItem_shouldDeleteItem_whenAuthorized() {
        mockCurrentUser();
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(item));

        basketItemService.deleteBasketItem(basketItemId);

        verify(basketItemRepository).deleteById(basketItemId);
    }

    @Test
    void deleteBasketItem_shouldThrowAccessDenied_whenUnauthorized() {
        mockCurrentUser();
        basket.getCustomer().setOwner(OTHER_USER_TOKEN);

        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> basketItemService.deleteBasketItem(basketItemId));
    }
}
