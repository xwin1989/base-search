package com.qeeka.domain;

import com.qeeka.enums.Direction;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Neal on 2019/08/09.
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

    // Creates a new {@link Sort} for the given properties.
    public static Sort by(String... properties) {
        Assert.notEmpty(properties, "Properties must not be null!");
        return new Sort(properties);
    }

    public static Sort by(Direction direction, String... properties) {
        Assert.notNull(direction, "Direction must not be null!");
        Assert.notEmpty(properties, "Properties must not be null!");

        return new Sort(direction, properties);
    }

    public static Sort by(List<Order> orders) {
        Assert.notEmpty(orders, "Orders must not be null!");
        return new Sort(orders);
    }

    public static Sort by(Order... orders) {
        Assert.notNull(orders, "Orders must not be null!");
        return new Sort(Arrays.asList(orders));
    }

    /**
     * combine other sort.
     */
    public Sort and(Sort sort) {
        Assert.notNull(sort, "Sort must not be null!");
        this.orders.addAll(sort.orders);
        return this;
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
