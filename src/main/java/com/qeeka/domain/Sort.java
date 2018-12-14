package com.qeeka.domain;

import com.qeeka.enums.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Neal on 8/9 0009.
 */
public class Sort implements Iterable<Sort.Order> {
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    private final List<Order> orders;

    public Sort(List<Order> orders) {
        if (null == orders || orders.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
        }
        this.orders = orders;
    }

    public Sort(String... properties) {
        this(DEFAULT_DIRECTION, properties);
    }

    public Sort(Direction direction, String... properties) {
        this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
    }

    public Sort(Direction direction, List<String> properties) {

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = new ArrayList<>(properties.size());

        for (String property : properties) {
            this.orders.add(new Order(direction, property));
        }
    }

    @Override
    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }


    public static class Order {
        private final Direction direction;
        private final String property;

        public Order(Direction direction, String property) {
            if (property != null && property.length() > 0) {
                for (int i = 0; i < property.length(); i++) {
                    if (!Character.isWhitespace(property.charAt(i))) {
                        break;
                    }
                }
            } else {
                throw new IllegalArgumentException("Property must not null or empty!");
            }
            this.direction = direction == null ? DEFAULT_DIRECTION : direction;
            this.property = property;
        }

        public Direction getDirection() {
            return direction;
        }

        public String getProperty() {
            return property;
        }
    }
}
