import { setAlert, setLoading, showSortBy } from "../../_js/components.js";
import { autoFormat, changeSorting, generateRange } from "../../_js/helpers.js";
import { api } from "./api.js";

document.addEventListener("DOMContentLoaded", () => {
  const catalogContainer = document.getElementById("catalog-container");
  const pagination = document.getElementById("catalog-pagination");
  const search = document.getElementById("input-search");
  const sortOptions = document.querySelectorAll("[sortBy]");
  const loadingSpinner = document.getElementById("loading");
  const alert = document.getElementById("alert");

  let filterBy;
  let sorting = {};
  let currentPage = 0;
  let pageSize = 10;

  search.addEventListener("change", (evt) => {
    setFilter(evt.target.value);
  });
  sortOptions.forEach((s) => {
    s.addEventListener("click", (evt) => {
      setSorting(evt.target.attributes["sortBy"].value);
    });
  });

  setSorting("title");
  loadCatalog();

  function loadCatalog() {
    setLoading(loadingSpinner, true);
    setAlert(alert);
    catalogContainer.replaceChildren();
    pagination.replaceChildren();

    Promise.all([
      api.getCatalog(currentPage, pageSize, sorting, filterBy),
      api.getPagesCount(pageSize, filterBy),
    ])
      .then(([catalog, pagesCount]) => {
        setCatalog(catalogContainer, catalog.items);
        setPagesCount(pagesCount);
      })
      .catch((err) => {
        console.error("getCatalog failed", err);
        setAlert(alert, "Произошла ошибка при загрузке каталога");
      })
      .finally(() => setLoading(loadingSpinner, false));
  }

  function setSorting(field) {
    changeSorting(sorting, field);
    showSortBy(sortOptions, sorting);
    loadCatalog();
  }

  function setFilter(filter) {
    filterBy = filter;
    loadCatalog();
  }

  function setCatalog(catalogContainer, items) {
    for (let i of items) {
      catalogContainer.append(createCard(i));
    }

    function createCard(item) {
      const col = document.createElement("div");
      col.className = "col";
      const card = document.createElement("div");
      card.className = "card product-card";
      const cardHeader = document.createElement("div");
      cardHeader.className =
        "card-header align-content-center d-flex justify-content-between";
      const cardBody = document.createElement("div");
      cardBody.className = "card-body d-flex gap-2";
      const barcode = document.createElement("div");
      barcode.className = "bi-upc";
      barcode.innerText = item.barcode;

      const title = document.createElement("span");
      title.className = "flex-grow-1";
      title.innerText = item.title;

      const price = document.createElement("span");
      price.innerText = autoFormat(item.price);

      cardHeader.append(barcode);
      cardBody.append(title);
      cardBody.append(price);
      card.append(cardHeader);
      card.append(cardBody);
      col.append(card);
      return col;
    }
  }

  function setPagesCount(count) {
    pagination.replaceChildren();
    if (count <= 0) {
      return;
    }
    pagination.append(createPaginationLi(0, "&laquo;"));
    generateRange(count).forEach((p) => {
      pagination.append(createPaginationLi(p, p + 1, p == currentPage));
    });
    pagination.append(createPaginationLi(count - 1, "&raquo;"));

    function createPaginationLi(page, text, active) {
      const li = document.createElement("li");
      const a = document.createElement("a");
      li.append(a);
      li.classList.add("page-item");
      a.classList.add("page-link");
      if (active) {
        li.classList.add("active");
      } else {
        a.href = "#";
        a.addEventListener("click", () => {
          currentPage = page;
          loadCatalog();
        });
      }
      a.innerHTML = text;
      return li;
    }
  }
});
