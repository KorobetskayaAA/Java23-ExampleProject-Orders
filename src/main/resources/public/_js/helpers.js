export function autoFormat(value, format) {
  if (value === undefined || value === null) {
    return "";
  }
  switch (format || typeof value) {
    case "string":
      return value;
    case "year":
      return value.toLocaleString("ru-RU", {
        maximumFractionDigits: 0,
        useGrouping: false,
      });
    case "integer":
      return value.toLocaleString("ru-RU", {
        maximumFractionDigits: 0,
        useGrouping: true,
      });
    case "number":
    case "currency":
      return value.toLocaleString("ru-RU", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
        useGrouping: true,
      });
    case "boolean":
      return value ? "Да" : "Нет";
    default:
      if (value instanceof Date) {
        return value.toLocaleString();
      }
      return value?.toString();
  }
}

export function sorted(arr, sorting) {
  const arrCopy = [...arr];
  arrCopy.sort(
    (i1, i2) =>
      ((i1[sorting.field] > i2[sorting.field]) -
        (i1[sorting.field] < i2[sorting.field])) *
      (sorting.asc || -1)
  );
  return arrCopy;
}

export function generateRange(count) {
  return Array.from(Array(count).keys());
}

export function changeSorting(sorting, field) {
  if (field == sorting.field) {
    sorting.asc = !sorting.asc;
  } else {
    sorting.field = field;
    sorting.asc = true;
  }
  return sorting;
}

export function getISODate(date) {
  return date.toISOString().substring(0, 10);
}
