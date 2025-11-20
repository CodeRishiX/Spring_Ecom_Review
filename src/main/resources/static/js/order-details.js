// // order-details.js
//
// function renderOrderDetails(items) {
//     const orderItemsDiv = document.getElementById("order-items");
//     const itemCountSpan = document.getElementById("order-item-count");
//     const orderTotal = document.getElementById("order-total");
//     const orderGrandTotal = document.getElementById("order-grand-total");
//
//     orderItemsDiv.innerHTML = "";
//     itemCountSpan.innerText = items.length;
//
//     let total = 0;
//
//     if (items.length === 0) {
//         orderItemsDiv.innerHTML = `
//             <tr>
//                 <td colspan="5" class="text-center">No items in this order.</td>
//             </tr>
//         `;
//     } else {
//         items.forEach(item => {
//             const itemTotal = item.product.price * item.quantity;
//             total += itemTotal;
//
//             orderItemsDiv.innerHTML += `
//                 <tr>
//                     <td class="product-thumbnail">
//                         <img src="http://localhost:8080/api/product/${item.product.id}/image"
//                              alt="${item.product.name}" class="img-fluid">
//                     </td>
//                     <td class="product-name">
//                         <h2 class="h5 text-black">${item.product.name}</h2>
//                     </td>
//                     <td>$${item.product.price.toFixed(2)}</td>
//                     <td>${item.quantity}</td>
//                     <td>$${itemTotal.toFixed(2)}</td>
//                 </tr>
//             `;
//         });
//     }
//
//     orderTotal.innerText = `$${total.toFixed(2)}`;
//     orderGrandTotal.innerText = `$${total.toFixed(2)}`;
// }
//
// function loadOrderDetails() {
//     fetch("http://localhost:8080/api/orderdetails", { credentials: 'include' })
//         .then(res => {
//             if (!res.ok) throw new Error('Could not fetch order details');
//             return res.json();
//         })
//         .then(items => {
//             renderOrderDetails(items);
//         })
//         .catch(error => {
//             console.error("Error fetching order details:", error);
//             alert("Failed to load order details. Please try again.");
//         });
// }
//
// document.addEventListener("DOMContentLoaded", loadOrderDetails);
