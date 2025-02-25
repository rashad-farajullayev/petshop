package com.thesniffers.rsql;

import org.springframework.data.jpa.domain.Specification;
import cz.jirutka.rsql.parser.ast.*;

public class CustomRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    private final GenericRsqlSpecBuilder<T> builder;

    public CustomRsqlVisitor() {
        this.builder = new GenericRsqlSpecBuilder<>();
    }

    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public Specification<T> visit(ComparisonNode node, Void params) {
        return builder.createSpecification(node);
    }
}
