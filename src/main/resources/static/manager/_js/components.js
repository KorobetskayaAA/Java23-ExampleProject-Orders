//import { autoFormat } from "./helpers.js";

//export
function setRows(table, rows) {
  const tableBody = table.querySelector("tbody");
  tableBody.replaceChildren();
  for (row of rows) {
    tableBody.append(createRow(row));
  }
}

function createRow(row) {
  console.log("createRow", row);
  const tr = document.createElement("tr");
  for (field in row) {
    tr.append(createCell(field, row[field]));
  }
  return tr;
}

function createCell(field, value) {
  const td = document.createElement("td");
  if (typeof value === "number") {
    td.classList.add("td-number");
  }
  td.innerText = autoFormat(
    value,
    (field.match(/quantity|count/gi) && "integer") ||
      (field == "year" && "year")
  );
  return td;
}

//export
function setLoading(spinner, isLoading) {
  spinner.hidden = !isLoading;
}

//export
function setAlert(alert, message) {
  if (!message) {
    alert.hidden = true;
    return;
  }
  alert.innerText = message;
  alert.hiddent = false;
}

//export
function showSortBy(sortOptions, sorting) {
  sortOptions.forEach((s) => {
    const si = s.querySelector(".bi");
    si.classList.remove("bi-sort-down-alt");
    si.classList.remove("bi-sort-up-alt");
    if (s.getAttribute("sortBy") == sorting.field) {
      si.classList.add(sorting.asc ? "bi-sort-down-alt" : "bi-sort-up-alt");
    }
  });
}
