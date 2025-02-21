package com.thesniffers.service;

import com.thesniffers.dao.model.BasketStatus;
import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.repository.CustomerRepository;
import com.thesniffers.dao.repository.BasketItemRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.ShoppingBasketDto;
import com.thesniffers.exception.CustomerNotFoundException;
import com.thesniffers.mapper.ShoppingBasketMapper;
import com.thesniffers.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class ShoppingBasketService {

    private final ShoppingBasketRepository shoppingBasketRepository;
    private final BasketItemRepository basketItemRepository;
    private final ShoppingBasketMapper shoppingBasketMapper;
    private final CustomerRepository customerRepository;

    public ShoppingBasketService(ShoppingBasketRepository shoppingBasketRepository,
                                 BasketItemRepository basketItemRepository,
                                 ShoppingBasketMapper shoppingBasketMapper, CustomerRepository customerRepository) {
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.basketItemRepository = basketItemRepository;
        this.shoppingBasketMapper = shoppingBasketMapper;
        this.customerRepository = customerRepository;
    }

    public List<ShoppingBasketDto> getAllBaskets() {
        log.info("Fetching all shopping baskets");
        var list = shoppingBasketRepository.findAllAccessibleBaskets(SecurityUtils.getCurrentUserToken(), SecurityUtils.isAdmin())
                .stream()
                .map(shoppingBasketMapper::toDto)
                .toList();
        log.info("Retrieved {} shopping baskets", list.size());
        return list;
    }

    public Optional<ShoppingBasketDto> getBasketById(UUID id) {
        log.info("Fetching shopping basket by ID: {}", id);

        return shoppingBasketRepository.findAccessibleBasketById(id, SecurityUtils.getCurrentUserToken(), SecurityUtils.isAdmin())
                .map(shoppingBasketMapper::toDto);
    }


    public ShoppingBasketDto createBasket(ShoppingBasketDto dto) {
        log.info("Creating shopping basket: {}", dto);

        // Get current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Fetch the customer to ensure ownership validation
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> {
                    log.warn("Customer with ID {} does not exist", dto.customerId());
                    return new CustomerNotFoundException("Customer not found with ID: " + dto.customerId());
                });

        // Validate access: Allow only Admins or the Customer Owner
        if (!isAdmin && !currentUserToken.equals(customer.getOwner())) {
            log.warn("Unauthorized attempt by {} to create basket for customer ID: {}", currentUserToken, dto.customerId());
            throw new AccessDeniedException("You do not have permission to create a basket for this customer.");
        }

        // Convert DTO to Entity and Save
        ShoppingBasket basket = shoppingBasketMapper.toEntity(dto);
        basket.setCustomer(customer);
        basket.setCreated(ZonedDateTime.now());
        var savedBasket = shoppingBasketRepository.save(basket);

        log.info("Successfully created shopping basket: {}", savedBasket);
        return shoppingBasketMapper.toDto(savedBasket);
    }

    public void deleteBasket(UUID id) {
        log.info("Deleting shopping basket: {}", id);
        var currentUserToken = SecurityUtils.getCurrentUserToken();

        // Check if the basket exists AND the user has access to delete it
        if (!shoppingBasketRepository.canDeleteBasket(id, currentUserToken, SecurityUtils.isAdmin())) {
            log.warn("Unauthorized attempt by {} to delete shopping basket ID: {}", currentUserToken, id);
            throw new AccessDeniedException("You do not have permission to delete this shopping basket.");
        }

        shoppingBasketRepository.deleteById(id);
        log.info("Shopping basket with ID {} deleted successfully", id);
    }

    public Optional<ShoppingBasketDto> updateStatus(UUID id, String status) {
        log.info("Updating shopping basket: {} into status: {}", id, status);

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to update this basket
        if (!shoppingBasketRepository.canUpdateBasketStatus(id, currentUserToken, isAdmin)) {
            log.warn("Unauthorized attempt by {} to update status of shopping basket: {}", currentUserToken, id);
            throw new AccessDeniedException("You do not have permission to update the status of this shopping basket.");
        }

        return shoppingBasketRepository.findById(id)
                .map(existingBasket -> {
                    existingBasket.setStatus(BasketStatus.valueOf(status.toUpperCase()));
                    existingBasket.setStatusDate(ZonedDateTime.now());
                    var updatedBasket = shoppingBasketMapper.toDto(shoppingBasketRepository.save(existingBasket));
                    log.info("Successfully updated status of shopping basket: {} into status: {}", id, status);
                    return updatedBasket;
                });
    }


    // Checkout Basket (Mark as PAID)
    public Optional<ShoppingBasketDto> checkoutBasket(UUID id) {
        log.info("Checking out shopping basket: {}", id);

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to check out this basket
        if (!shoppingBasketRepository.canCheckoutBasket(id, currentUserToken, isAdmin)) {
            log.warn("Unauthorized attempt by {} to checkout shopping basket: {}", currentUserToken, id);
            throw new AccessDeniedException("You do not have permission to checkout this shopping basket.");
        }

        return shoppingBasketRepository.findById(id)
                .map(existingBasket -> {
                    existingBasket.setStatus(BasketStatus.PAID);
                    existingBasket.setStatusDate(ZonedDateTime.now());
                    var savedBasketDto = shoppingBasketMapper.toDto(shoppingBasketRepository.save(existingBasket));
                    log.info("Successfully checked out shopping basket: {}", savedBasketDto);
                    return savedBasketDto;
                });
    }

    // Clear a Basket (Remove all items)
    public void clearBasket(UUID id) {
        log.info("Clearing shopping basket: {}", id);

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to clear this basket
        if (!shoppingBasketRepository.canClearBasket(id, currentUserToken, isAdmin)) {
            log.warn("Unauthorized attempt by {} to clear shopping basket: {}", currentUserToken, id);
            throw new AccessDeniedException("You do not have permission to clear this shopping basket.");
        }

        // Remove all items from the basket
        var count = basketItemRepository.deleteAllByShoppingBasketId(id);
        log.info("Successfully cleared shopping basket with ID: {}. Number of deleted items: {}", id, count);
    }


    // Get Total Items in a Basket
    public Integer getTotalItems(UUID shoppingBasketId) {
        log.info("Getting shopping basket items: {}", shoppingBasketId);

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to access this basket's items
        if (!shoppingBasketRepository.canViewBasketItems(shoppingBasketId, currentUserToken, isAdmin)) {
            log.warn("Unauthorized attempt by {} to view items in shopping basket: {}", currentUserToken, shoppingBasketId);
            throw new AccessDeniedException("You do not have permission to view the items in this shopping basket.");
        }

        return basketItemRepository.findByShoppingBasketId(shoppingBasketId).size();
    }

}
