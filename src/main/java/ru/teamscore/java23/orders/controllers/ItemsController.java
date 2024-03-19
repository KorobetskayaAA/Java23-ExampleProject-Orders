package ru.teamscore.java23.orders.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.teamscore.java23.orders.controllers.dto.CatalogItemDto;
import ru.teamscore.java23.orders.model.Catalog;
import ru.teamscore.java23.orders.model.entities.Item;
import ru.teamscore.java23.orders.model.enums.CatalogSortOption;
import ru.teamscore.java23.orders.model.enums.ItemStatus;

@Controller
@RequestMapping("/adminpanel/items")
@AllArgsConstructor
public class ItemsController {
    @Autowired
    private Catalog catalog;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public String get(Model model,
                      @RequestParam(required = false) String sorting,
                      @RequestParam(required = false) String search,
                      @RequestParam(required = false) Integer page,
                      @RequestParam(required = false) Integer pageSize
    ) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        long itemsCount = search != null ? catalog.getCount(search) : catalog.getItemsCount();
        long pagesCount = itemsCount / pageSize +
                (itemsCount % pageSize > 0 ? 1 : 0);
        if (page == null || page <= 0) {
            page = 1;
        } else if (page > pagesCount) {
            page = (int) pagesCount;
        }
        CatalogSortOption sortOption = (sorting == null)
                ? CatalogSortOption.TITLE
                : CatalogSortOption.valueOf(sorting
                .toUpperCase()
                .replace("ASC", "")
                .replace("DESC", "")
        );
        boolean desc = sorting != null && sorting.toLowerCase().endsWith("desc");

        var items = catalog.getSorted(sortOption, desc, search, page - 1, pageSize);

        model.addAttribute("items", items);
        model.addAttribute("pagesCount", pagesCount);
        model.addAttribute("currentPage", page);
        return "/adminpanel/items/index";
    }

    @GetMapping("create")
    public String getCreateForm(Model model) {
        model.addAttribute("item", new Item());
        return "/adminpanel/items/edit";
    }

    @GetMapping("edit")
    public String getCreateForm(@RequestParam String barcode, Model model) {
        var item = catalog.getItem(barcode);
        model.addAttribute("item", item.get());
        return "/adminpanel/items/edit";
    }

    @PostMapping("create")
    public String postCreate(@ModelAttribute("item") @Valid Item item,
                             BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "/adminpanel/items/edit";
        }
        catalog.addItem(item);
        return "redirect:/adminpanel/items";
    }

    @PostMapping("edit")
    public String postEdit(@ModelAttribute("item") @Valid Item item,
                           BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "/adminpanel/items/edit";
        }
        catalog.updateItem(item);
        return "redirect:/adminpanel/items";
    }

    private CatalogItemDto mapCatalogItem(Item item) {
        modelMapper.typeMap(Item.class, CatalogItemDto.class);
        return modelMapper.map(item, CatalogItemDto.class);
    }

    private Item mapCatalogItem(CatalogItemDto item) {
        Converter<String, ItemStatus> itemStatusConverter = new AbstractConverter<String, ItemStatus>() {
            protected ItemStatus convert(String source) {
                return source == null ? null : ItemStatus.valueOf(source);
            }
        };

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        modelMapper.addConverter(itemStatusConverter);
        modelMapper.typeMap(CatalogItemDto.class, Item.class);
        var result = modelMapper.map(item, Item.class);
        return result;
    }
}
