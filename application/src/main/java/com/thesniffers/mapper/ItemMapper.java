package com.thesniffers.mapper;

import com.thesniffers.dao.model.Item;
import com.thesniffers.dto.ItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "shoppingBasket.id", target = "shoppingBasketId")
    ItemDto toDto(Item item);

    @Mapping(source = "shoppingBasketId", target = "shoppingBasket.id")
    Item toEntity(ItemDto itemDto);
}
