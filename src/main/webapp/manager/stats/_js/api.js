//import * as mock from "./mock.js";

//export
const api = {
  async getStats(fromDate, toDate) {
    if (!(fromDate instanceof Date)) {
      fromDate = new Date(fromDate);
    }
    if (!(toDate instanceof Date)) {
      toDate = new Date(toDate);
    }
    return mockStats.order_statistics
      .map((s) => ({
        month: new Date(s.month),
        totalAmount: s.total_amount,
        totalQuantity: s.total_quantity,
        totalItemsCount: s.total_items_count,
        cancelAmount: s.cancel_amount,
        cancelQuantity: s.cancel_quantity,
      }))
      .filter((s) => s.month >= fromDate && s.month <= toDate);
  },
};
