package ru.teamscore.java23.orders.model;

import lombok.RequiredArgsConstructor;
import ru.teamscore.java23.orders.model.entities.Order;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class OrdersManager {
    private final List<Order> orders = new ArrayList<>();
}
