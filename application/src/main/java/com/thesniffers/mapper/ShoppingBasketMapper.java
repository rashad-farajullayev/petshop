package com.thesniffers.mapper;

import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dto.ShoppingBasketDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShoppingBasketMapper {
    @Mapping(source = "customer.id", target = "customerId")
    ShoppingBasketDto toDto(ShoppingBasket shoppingBasket);

    @Mapping(source = "customerId", target = "customer.id")
    ShoppingBasket toEntity(ShoppingBasketDto shoppingBasketDto);
}
