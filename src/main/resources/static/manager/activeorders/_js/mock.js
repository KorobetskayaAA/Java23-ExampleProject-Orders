const mockItems = [
  {
    barcode: "4601234500004",
    title: "Блендер ",
    price: 1848.14,
    quantity: 1,
  },
  {
    barcode: "4601234500004",
    title: "Блендер ",
    price: 3504.5,
    quantity: 2,
  },
  {
    barcode: "4601234500006",
    title: "Кофеварка ",
    price: 2299.84,
    quantity: 1,
  },
  {
    barcode: "4601234500007",
    title: "Утюг ",
    price: 1320.98,
    quantity: 32,
  },
  {
    barcode: "4601234500007",
    title: "Утюг ",
    price: 2359.22,
    quantity: 11,
  },
  {
    barcode: "4601234500008",
    title: "Фен ",
    price: 1424.93,
    quantity: 1,
  },
  {
    barcode: "4601234500009",
    title: "Пылесос ",
    price: 5355.67,
    quantity: 19,
  },
  {
    barcode: "4601234500011",
    title: "Холодильник ",
    price: 19164.8,
    quantity: 1,
  },
  {
    barcode: "4601234500012",
    title: "Телевизор ",
    price: 18276.37,
    quantity: 1,
  },
  {
    barcode: "4601234500014",
    title: "Планшет ",
    price: 5498.21,
    quantity: 5,
  },
  {
    barcode: "4601234500014",
    title: "Планшет ",
    price: 7971.74,
    quantity: 121,
  },
  {
    barcode: "4601234500014",
    title: "Планшет ",
    price: 9358.68,
    quantity: 14,
  },
];

//export
const mockActiveOrders = {
  items: mockItems,
  totalAmount: mockItems.reduce((sum, oi) => sum + oi.price * oi.quantity, 0),
};
