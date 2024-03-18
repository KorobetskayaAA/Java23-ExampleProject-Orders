//import api from "./api.js";

document.addEventListener("DOMContentLoaded", () => {
  const table = document.getElementById("table-active_order_items");
  const loadingSpinner = document.getElementById("loading");
  const alert = document.getElementById("alert");
  const total = document.getElementById("total");
  const totalValue = document.getElementById("total_value");
  const sortOptions = document.querySelectorAll("[sortBy]");

  let items = [];
  const sorting = {};

  sortOptions.forEach((s) => {
    s.addEventListener("click", (evt) => {
      setSorting(evt.target.attributes["sortBy"].value);
    });
  });

  setSorting("title");
  loadActiveItems();

  function loadActiveItems() {
    setLoading(loadingSpinner, true);
    setAlert(alert);
    table.hidden = true;
    total.hidden = true;

    api
      .getActiveOrders()
      .then((result) => {
        items = result.items.map((i) => ({
          barcode: i.barcode,
          title: i.title,
          price: i.price,
          quantity: i.quantity,
        }));
        showSortedItems();
        showTotalValue(result.totalAmount);
        table.hidden = false;
        total.hidden = false;
      })
      .catch((err) => {
        console.error("getActiveOrderItems failed", err);
        setAlert(alert, "Произошла ошибка при загрузке активных заказов");
      })
      .finally(() => setLoading(loadingSpinner, false));
  }

  function showSortedItems() {
    items = sorted(items, sorting);
    setRows(table, items);
  }

  function setSorting(field) {
    changeSorting(sorting, field);
    showSortBy(sortOptions, sorting);
    showSortedItems();
  }

  function showTotalValue(value) {
    totalValue.innerText = autoFormat(value);
  }
});
