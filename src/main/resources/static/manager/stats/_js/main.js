//import api from "./api.js";

document.addEventListener("DOMContentLoaded", () => {
  const table = document.getElementById("table-stats");
  const periodFrom = document.getElementById("input-period_from");
  const periodTo = document.getElementById("input-period_to");
  const loadingSpinner = document.getElementById("block-loading");
  const alert = document.getElementById("block-alert");

  periodFrom.addEventListener("change", loadStats);
  periodTo.addEventListener("change", loadStats);

  loadStats();

  function loadStats() {
    setLoading(loadingSpinner, true);
    setAlert(alert);
    table.hidden = true;

    api
      .getStats(periodFrom.value, periodTo.value)
      .then((result) => {
        setRows(
          table,
          result.map((s) => ({
            year: s.month.getFullYear(),
            month: s.month.toLocaleString("ru-RU", { month: "long" }),
            totalAmount: s.totalAmount,
            totalQuantity: s.totalQuantity,
            totalItemsCount: s.totalItemsCount,
            cancelAmount: s.cancelAmount,
            cancelQuantity: s.cancelQuantity,
          }))
        );

        showTotals(result);
        table.hidden = false;
      })
      .catch((err) => {
        console.error("getStats failed", err);
        setAlert(alert, "Произошла ошибка при загрузке статистики");
      })
      .finally(() => setLoading(loadingSpinner, false));
  }

  function showTotals(result) {
    const totals = getTotals(result);
    const tableFooterTr = table.querySelector("tfoot tr");
    tableFooterTr.children[1].innerText = autoFormat(totals.totalAmount);
    tableFooterTr.children[2].innerText = autoFormat(
      totals.totalQuantity,
      "integer"
    );
    tableFooterTr.children[3].innerText = autoFormat(
      totals.totalItemsCount,
      "integer"
    );
    tableFooterTr.children[4].innerText = autoFormat(totals.cancelAmount);
    tableFooterTr.children[5].innerText = autoFormat(
      totals.cancelQuantity,
      "integer"
    );
  }

  function getTotals(stats) {
    let totals = stats.reduce(
      (s, sum) => ({
        totalAmount: sum.totalAmount + (+s.totalAmount || 0),
        totalQuantity: sum.totalQuantity + (+s.totalQuantity || 0),
        totalItemsCount: sum.totalItemsCount + (+s.totalItemsCount || 0),
        cancelAmount: sum.cancelAmount + (+s.cancelAmount || 0),
        cancelQuantity: sum.cancelQuantity + (+s.cancelQuantity || 0),
      }),
      {
        totalAmount: 0,
        totalQuantity: 0,
        totalItemsCount: 0,
        cancelAmount: 0,
        cancelQuantity: 0,
      }
    );
    return totals;
  }
});
