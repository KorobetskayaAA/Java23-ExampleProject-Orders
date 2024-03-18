package ru.teamscore.java23.orders.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.teamscore.java23.orders.controllers.dto.OrdersStatisticsDto;
import ru.teamscore.java23.orders.model.OrdersManager;
import ru.teamscore.java23.orders.model.statistics.OrdersStatistics;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private final OrdersManager ordersManager;
    @Autowired
    private final ModelMapper modelMapper;

    @GetMapping
    public List<OrdersStatisticsDto> getStats(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ordersManager
                .getInfo()
                .getStatistics(from, to)
                .stream()
                .map(s -> new OrdersStatisticsDto(s))
                //.map(s -> convertDtoStats(s))
                .toList();
    }

    private OrdersStatisticsDto convertDtoStats(OrdersStatistics stats) {
        modelMapper.typeMap(OrdersStatistics.class, OrdersStatisticsDto.class);
        return modelMapper.map(stats, OrdersStatisticsDto.class);
    }
}
