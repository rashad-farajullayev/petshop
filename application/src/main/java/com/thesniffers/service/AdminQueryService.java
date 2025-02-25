package com.thesniffers.service;

import com.thesniffers.dao.repository.BasketItemGenericRepository;
import com.thesniffers.dao.repository.CustomerGenericRepository;
import com.thesniffers.dao.repository.ShoppingBasketGenericRepository;
import com.thesniffers.rsql.CustomRsqlVisitor;
import com.thesniffers.dao.repository.GenericRepository;
import cz.jirutka.rsql.parser.RSQLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminQueryService {

    private final Map<String, GenericRepository<?>> repositories = new HashMap<>();

    public AdminQueryService(CustomerGenericRepository customerGenericRepository,
                             ShoppingBasketGenericRepository shoppingBasketGenericRepository,
                             BasketItemGenericRepository basketItemGenericRepository) {
        this.repositories.put("Customer", customerGenericRepository);
        this.repositories.put("ShoppingBasket", shoppingBasketGenericRepository);
        this.repositories.put("BasketItem", basketItemGenericRepository);
    }

    public Page<?> queryEntity(String entityName, String filter, int page, int size, String sort) {
        @SuppressWarnings("unchecked")
        GenericRepository<Object> repository = (GenericRepository<Object>) repositories.get(entityName);

        if (repository == null) {
            throw new IllegalArgumentException("No repository found for entity: " + entityName);
        }

        Sort sortCriteria = parseSort(sort);
        PageRequest pageRequest = PageRequest.of(page, size, sortCriteria);

        Specification<Object> spec = Specification.where(null);
        if (filter != null && !filter.isEmpty()) {
            spec = new RSQLParser().parse(filter).accept(new CustomRsqlVisitor<>());
        }

        return repository.findAll(spec, pageRequest);
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }
}
