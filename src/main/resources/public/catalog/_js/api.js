import { api as basicApi } from "../../_js/api.js";

export const api = {
  async getCatalog(page, pageSize, sorting, filterBy) {
    const params = new URLSearchParams();
    params.append("page", page || 0);
    params.append("pageSize", pageSize || 10);
    if (filterBy) {
      params.append("search", filterBy);
    }
    if (sorting.field) {
      params.append("sortingField", sorting.field);
    }
    if (!sorting.asc) {
      params.append("sortingDesc", true);
    }
    return basicApi.get("catalog", params);
  },

  getPagesCount(pageSize, filterBy) {
    const params = new URLSearchParams();
    if (filterBy) {
      params.append("search", filterBy);
    }
    return basicApi
      .get("catalog/count", params)
      .then((result) => Math.ceil(result.count / pageSize));
  },
};
