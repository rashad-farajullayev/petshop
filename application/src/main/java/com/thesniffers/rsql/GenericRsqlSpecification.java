package com.thesniffers.rsql;

import com.thesniffers.dao.model.Customer;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GenericRsqlSpecification<T> implements Specification<T> {

    private final String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    public GenericRsqlSpecification(String property, ComparisonOperator operator, List<String> arguments) {
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Object> args = castArguments(root);
        Object argument = args.get(0);

        Expression<Comparable> path = root.get(property);

        if (argument instanceof ZonedDateTime) {
            // Convert ZonedDateTime to LocalDateTime to ensure comparison works
            Expression<LocalDateTime> localDateTimePath = root.get(property).as(LocalDateTime.class);
            LocalDateTime dateTimeArg = ((ZonedDateTime) argument).toLocalDateTime();

            switch (RsqlSearchOperation.getSimpleOperator(operator)) {
                case EQUAL:
                    return builder.equal(localDateTimePath, dateTimeArg);
                case NOT_EQUAL:
                    return builder.notEqual(localDateTimePath, dateTimeArg);
                case GREATER_THAN:
                    return builder.greaterThan(localDateTimePath, dateTimeArg);
                case GREATER_THAN_OR_EQUAL:
                    return builder.greaterThanOrEqualTo(localDateTimePath, dateTimeArg);
                case LESS_THAN:
                    return builder.lessThan(localDateTimePath, dateTimeArg);
                case LESS_THAN_OR_EQUAL:
                    return builder.lessThanOrEqualTo(localDateTimePath, dateTimeArg);
            }
        } else {
            // Default behavior for other types
            switch (RsqlSearchOperation.getSimpleOperator(operator)) {
                case EQUAL:
                    return builder.equal(path, argument);
                case NOT_EQUAL:
                    return builder.notEqual(path, argument);
                case GREATER_THAN:
                    if (property.equals("basketCount")) {  // ✅ Handle collection count using subquery
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<Customer> subRoot = subquery.from(Customer.class);
                        subquery.select(builder.count(subRoot.join("shoppingBaskets"))).where(builder.equal(subRoot, root));
                        return builder.greaterThan(subquery, (Long) argument);
                    }
                    return builder.greaterThan(root.get(property), argument.toString());
                case GREATER_THAN_OR_EQUAL:
                    return builder.greaterThanOrEqualTo(path, (Comparable) argument);
                case LESS_THAN:
                    return builder.lessThan(path, (Comparable) argument);
                case LESS_THAN_OR_EQUAL:
                    return builder.lessThanOrEqualTo(path, (Comparable) argument);
            }
        }

        return null;
    }


    private List<Object> castArguments(final Root<T> root) {
        Class<?> type = root.get(property).getJavaType();

        return arguments.stream().map(arg -> {
            if (type.equals(Integer.class)) {
                return Integer.parseInt(arg);
            } else if (type.equals(Long.class)) {
                return Long.parseLong(arg);
            } else if (type.equals(Double.class)) {
                return Double.parseDouble(arg);
            } else if (type.equals(Boolean.class)) {
                return Boolean.parseBoolean(arg);
            } else if (type.equals(ZonedDateTime.class)) {
                return ZonedDateTime.parse(arg);
            } else {
                return arg;
            }
        }).collect(Collectors.toList());
    }
}

