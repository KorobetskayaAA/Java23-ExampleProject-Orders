import { api as basicApi } from "../../_js/api.js";

export const api = {
  async getActiveOrders() {
    return basicApi.get("activeorders");
  },
};
