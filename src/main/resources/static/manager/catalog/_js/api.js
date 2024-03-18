//import * as mock from "./mock.js";

//export
const api = {
  async getCatalog(page, pageSize, sorting, filterBy) {
    let items = mockCatalog.items;
    if (filterBy) {
      items = items.filter(
        (i) =>
          i.barcode == filterBy || i.title.match(new RegExp(filterBy, "gi"))
      );
    }
    if (sorting) {
      items = sorted(items, sorting);
    }
    return items.slice(page * pageSize, (page + 1) * pageSize);
  },

  getPagesCount(pageSize, filterBy) {
    return new Promise((resolve) =>
      setTimeout(
        () => resolve(Math.ceil(mockCatalog.items.length / pageSize)),
        1200
      )
    );
  },
};
