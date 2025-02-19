package com.thesniffers.mapper;

import com.thesniffers.dao.model.Item;
import com.thesniffers.dto.BasketItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BasketItemMapper {

    @Mapping(source = "shoppingBasket.id", target = "shoppingBasketId")
    BasketItemDto toDto(Item item);

    @Mapping(source = "shoppingBasketId", target = "shoppingBasket.id")
    Item toEntity(BasketItemDto dto);
}
