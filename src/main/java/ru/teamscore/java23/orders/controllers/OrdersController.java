package ru.teamscore.java23.orders.controllers;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.teamscore.java23.orders.controllers.dto.OrderCreateDto;
import ru.teamscore.java23.orders.model.Catalog;
import ru.teamscore.java23.orders.model.OrdersManager;
import ru.teamscore.java23.orders.model.entities.Barcode;
import ru.teamscore.java23.orders.model.entities.OrderWithItems;
import ru.teamscore.java23.orders.model.exceptions.OrderSetStatusException;

import java.util.Arrays;

@Controller
@RequestMapping("/adminpanel/orders")
@AllArgsConstructor
public class OrdersController {
    @Autowired
    private OrdersManager ordersManager;
    @Autowired
    private Catalog catalog;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public String get(ModelMap model,
                      @RequestParam(required = false) Integer page,
                      @RequestParam(required = false) Integer pageSize
    ) {
        var orders = ordersManager.getOrdersAll();
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        long pagesCount = orders.length / pageSize +
                (orders.length % pageSize > 0 ? 1 : 0);
        if (page == null || page <= 0) {
            page = 1;
        } else if (page > pagesCount) {
            page = (int) pagesCount;
        }
        model.addAttribute("orders",
                Arrays.stream(orders).skip((page - 1) * pageSize).limit(pageSize)
        );
        model.addAttribute("pagesCount", pagesCount);
        model.addAttribute("currentPage", page);
        return "adminpanel/orders/index";
    }

    @GetMapping("create")
    public String getCreate(Model model) {
        var newOrder = new OrderCreateDto();
        model.addAttribute("order", newOrder);
        return "adminpanel/orders/create";
    }

    @PostMapping("create")
    public String postCreate(OrderCreateDto order,
                             BindingResult bindingResult,
                             ModelMap model) {
        fillItems(order, bindingResult);
        ordersManager.addOrder(mapCreateOrder(order));
        model.clear();
        return "redirect:/adminpanel/orders";
    }

    @RequestMapping(value = "create", params = {"addItemSubmit"})
    public String postAddItem(@ModelAttribute("order") OrderCreateDto order,
                              BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "adminpanel/orders/create";
        }
        try {
            new Barcode(order.getNewItem().getBarcode());
            order.addItem();
        } catch (IllegalArgumentException ex) {
            bindingResult.addError(new FieldError("order", "newItem.barcode",
                    "Некорректный штрих-код"));
        }
        fillItems(order, bindingResult);
        return "adminpanel/orders/create";
    }

    @RequestMapping(value = "create", params = {"removeItemSubmit"})
    public String postRemoveItem(@ModelAttribute("order") OrderCreateDto order,
                                 @RequestParam String removeItemSubmit,
                                 BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "adminpanel/orders/create";
        }
        order.removeItem(removeItemSubmit);
        fillItems(order, bindingResult);
        return "adminpanel/orders/create";
    }

    @GetMapping("{action}/{id}")
    public ModelAndView closeOrder(@PathVariable String action,
                                   @PathVariable long id,
                                   @RequestParam(required = false) int page,
                                   RedirectAttributes redirectAttrs,
                                   Model model) {
        var order = ordersManager.getOrder(id);
        if (order.isPresent()) {
            try {
                switch (action) {
                    case "close":
                        order.get().getOrder().close();
                        break;
                    case "cancel":
                        order.get().getOrder().cancel();
                        break;
                }
                ordersManager.updateOrder(order.get());
            } catch (OrderSetStatusException ex) {
                // будет добавлено как объект Model
                redirectAttrs.addFlashAttribute("alert", ex.getMessage());
            }
        }
        // будет добавлено как параметр запроса ?page={page}
        model.addAttribute("page", page);
        return new ModelAndView("redirect:/adminpanel/orders", model.asMap());
    }

    private OrderWithItems mapCreateOrder(OrderCreateDto orderFromForm) {
        OrderWithItems orderWithItems = new OrderWithItems();
        orderWithItems.getOrder().setCustomerName(orderFromForm.getCustomerName());
        for (OrderCreateDto.OrderItemCreateDto item : orderFromForm.getItems()) {
            orderWithItems.addItem(item.getItem(), item.getQuantity());
        }
        return orderWithItems;
    }

    private void fillItems(OrderCreateDto order, BindingResult bindingResult) {
        for (int i = 0; i < order.getItems().size(); i++) {
            var oi = order.getItems().get(i);
            var item = catalog.getItem(oi.getBarcode());
            if (item.isPresent()) {
                oi.setItem(item.get());
            } else {
                bindingResult.addError(new FieldError("order", "items[" + i + "].barcode",
                        "Товар с таким штрих-кодом не существует"));
            }
        }
    }
}
