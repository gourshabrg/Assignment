// js

// default product data (used first time)
let defaultProducts = [
  { id: 1, name: "Laptop", price: 50000, stock: 5, category: "electronics" },
  { id: 2, name: "Shirt", price: 1200, stock: 10, category: "clothing" },
  { id: 3, name: "Book", price: 500, stock: 2, category: "books" },
  { id: 4, name: "Headphones", price: 2000, stock: 0, category: "electronics" },
  { id: 5, name: "Watch", price: 3000, stock: 3, category: "accessories" },
];

// load from localStorage or default
let products = JSON.parse(localStorage.getItem("products")) || defaultProducts;

// save to localStorage
function saveData() {
  localStorage.setItem("products", JSON.stringify(products));
}

// pagination
let currentPage = 1;
let itemsPerPage = 4; // number of item display

// edit tracking
let editId = null;

// render products its takes the array of product object
function renderProducts(data) {
  let container = document.getElementById("products"); // get the html container
  container.innerHTML = "";
  // pagination main logic
  let start = (currentPage - 1) * itemsPerPage;
  let paginated = data.slice(start, start + itemsPerPage);

  if (paginated.length === 0) {
    container.innerHTML = "<p>No products found</p>";
    return;
  }

  // for every product object we can create the html
  paginated.forEach((p) => {
    let div = document.createElement("div"); // creating the div
    div.className = "card"; // give the call name of div

    // inside this div add all the details of product
    div.innerHTML = ` 
      <h3>${p.name}</h3>
      <p>Category: ${p.category}</p>
      <p>Price: ₹${p.price}</p>
      <p>Stock: ${p.stock}</p>
      <button onclick="editProduct(${p.id})">Edit</button>
      <button onclick="deleteProduct(${p.id})">Delete</button>
    `;
    // adding the product into the div
    container.appendChild(div);
  });

  // this show the page no and its product on that page or change the inner html according the page no. logic
  document.getElementById("pageInfo").innerText =
    `Page ${currentPage} of ${Math.ceil(data.length / itemsPerPage)}`;
}

// this function take the id of product
// apply the filter method on products array its return the array of those object that id is not equal to given id
function deleteProduct(id) {
  products = products.filter((p) => p.id !== id);
  saveData();
  update();
}

// edit the product
// its take the id of that product to edit
// and put the value into the form then we can edit it
function editProduct(id) {
  let p = products.find((x) => x.id === id);

  document.getElementById("name").value = p.name;
  document.getElementById("price").value = p.price;
  document.getElementById("stock").value = p.stock;
  document.getElementById("categoryInput").value = p.category;

  editId = id;
}

// add / update logic
function addProduct() {
  // get the form value that written by user
  let name = document.getElementById("name").value;
  let price = +document.getElementById("price").value;
  let stock = +document.getElementById("stock").value;
  let category = document.getElementById("categoryInput").value;

  // check the data is valid or not
  if (!name || price <= 0 || stock < 0 || !category) {
    alert("Fill all fields correctly");
    return;
  }

  // if id is present the edit the that product object
  if (editId !== null) {
    let p = products.find((x) => x.id === editId);
    p.name = name;
    p.price = price;
    p.stock = stock;
    p.category = category;
    editId = null;
  } else {
    // if product object is not present then we create
    products.push({
      id: Date.now(), // use id ans date its unique
      name,
      price,
      stock,
      category,
    });
  }

  saveData(); // call the function to save the local storage
  update(); // this function update the ui part

  // empty the form
  document.getElementById("name").value = "";
  document.getElementById("price").value = "";
  document.getElementById("stock").value = "";
  document.getElementById("categoryInput").value = "";
}
