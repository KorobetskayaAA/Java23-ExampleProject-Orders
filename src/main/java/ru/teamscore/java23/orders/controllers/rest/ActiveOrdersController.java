package ru.teamscore.java23.orders.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.teamscore.java23.orders.controllers.dto.ActiveOrderItemDto;
import ru.teamscore.java23.orders.controllers.dto.ActiveOrderItemsDto;
import ru.teamscore.java23.orders.model.OrdersManager;
import ru.teamscore.java23.orders.model.entities.OrderItem;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activeorders")
public class ActiveOrdersController {
    @Autowired
    private final OrdersManager ordersManager;
    @Autowired
    private final ModelMapper modelMapper;

    @GetMapping
    public ActiveOrderItemsDto getActiveOrders() {
        return new ActiveOrderItemsDto(
                ordersManager
                        .getInfo().getProcessingOrderItems()
                        .stream()
                        .map(this::convertDtoOrderItems)
                        .toList(),
                ordersManager.getInfo().getProcessingTotalAmount()
        );
    }

    private ActiveOrderItemDto convertDtoOrderItems(OrderItem item) {
        // LOOSE - мягкое связывание для вложенной сущности Item
        // (проверяет совпадение по имени поля)
        // При этом price есть и в OrderItem, и в Item - будет взят из "верхней" сущности OrderItem
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.typeMap(OrderItem.class, ActiveOrderItemDto.class);
        return modelMapper.map(item, ActiveOrderItemDto.class);
    }
}
