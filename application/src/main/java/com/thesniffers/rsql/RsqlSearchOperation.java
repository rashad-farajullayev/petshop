package com.thesniffers.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.HashMap;
import java.util.Map;

public enum RsqlSearchOperation {
    EQUAL(RSQLOperators.EQUAL),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    LESS_THAN(RSQLOperators.LESS_THAN),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    IN(RSQLOperators.IN),
    NOT_IN(RSQLOperators.NOT_IN);

    private final ComparisonOperator operator;
    private static final Map<ComparisonOperator, RsqlSearchOperation> OPERATOR_MAP = new HashMap<>();

    static {
        for (RsqlSearchOperation op : values()) {
            OPERATOR_MAP.put(op.operator, op);
        }
    }

    RsqlSearchOperation(ComparisonOperator operator) {
        this.operator = operator;
    }

    public static RsqlSearchOperation getSimpleOperator(ComparisonOperator operator) {
        return OPERATOR_MAP.get(operator);
    }
}
