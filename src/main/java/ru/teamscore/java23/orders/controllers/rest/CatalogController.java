package ru.teamscore.java23.orders.controllers.rest;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.teamscore.java23.orders.controllers.dto.CatalogCountDto;
import ru.teamscore.java23.orders.controllers.dto.CatalogDto;
import ru.teamscore.java23.orders.controllers.dto.CatalogItemDto;
import ru.teamscore.java23.orders.model.Catalog;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;

@RestController
@AllArgsConstructor
@RequestMapping("/api/catalog")
public class CatalogController {
    @Autowired
    private final Catalog catalog;
    @Autowired
    private final ModelMapper modelMapper;

    @GetMapping
    public CatalogDto getCatalog(@RequestParam int page,
                                 @RequestParam int pageSize,
                                 @RequestParam(required = false) String sortingField,
                                 @RequestParam(required = false) Boolean sortingDesc,
                                 @RequestParam(required = false) String search) {
        var items = catalog.getSorted(
                sortingField != null ? CatalogSortOption.valueOf(sortingField.toUpperCase()) : null,
                sortingDesc != null ? sortingDesc : false,
                search,
                page,
                pageSize
        );
        return new CatalogDto(items.stream().map(this::mapCatalogItem).toList());
    }

    @GetMapping("count")
    public CatalogCountDto getCount(@RequestParam(required = false) String search) {
        return new CatalogCountDto(search != null ? catalog.getCount(search) : catalog.getItemsCount());
    }

    private CatalogItemDto mapCatalogItem(Item item) {
        modelMapper.typeMap(Item.class, CatalogItemDto.class);
        return modelMapper.map(item, CatalogItemDto.class);
    }
}
