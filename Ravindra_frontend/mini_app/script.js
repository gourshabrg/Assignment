// js

// Default product data (used first time)
let defaultProducts = [
  { id: 1, name: "Laptop", price: 50000, stock: 5, category: "electronics" },
  { id: 2, name: "Shirt", price: 1200, stock: 10, category: "clothing" },
  { id: 3, name: "Book", price: 500, stock: 2, category: "books" },
  { id: 4, name: "Headphones", price: 2000, stock: 0, category: "electronics" },
  { id: 5, name: "Watch", price: 3000, stock: 3, category: "accessories" },
];

// Load from localStorage or default
let products = JSON.parse(localStorage.getItem("products")) || defaultProducts;
