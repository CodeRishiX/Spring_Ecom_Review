// cart.js

function showSnackbar(msg, error = false) {
    // Replace alert with your preferred snackbar UI later
    alert(msg);
}

function renderCart(items) {
    const cartItemsDiv = document.getElementById("cart-items");
    const countSpan = document.getElementById("cart-count");
    const totalPrice = document.getElementById("total-price");
    const grandTotal = document.getElementById("grand-total");

    cartItemsDiv.innerHTML = ""; // Clear previous table rows
    countSpan.innerText = items.length;

    let total = 0;

    if (items.length === 0) {
        cartItemsDiv.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">Your cart is empty.</td>
            </tr>
        `;
    } else {
        items.forEach(item => {
            const itemTotal = item.product.price * item.quantity;
            total += itemTotal;

            cartItemsDiv.innerHTML += `
                <tr>
                    <td class="product-thumbnail">
                        <img src="http://localhost:8080/api/product/${item.product.id}/image"
                             alt="${item.product.name}" class="img-fluid">
                    </td>
                    <td class="product-name">
                        <h2 class="h5 text-black">${item.product.name}</h2>
                    </td>
                    <td>$${item.product.price.toFixed(2)}</td>
                    <td>${item.quantity}</td>
                    <td>$${itemTotal.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-black btn-sm" onclick="removeItem(${item.product.id})">X</button>
                    </td>
                </tr>
            `;
        });
    }

    totalPrice.innerText = `$${total.toFixed(2)}`;
    grandTotal.innerText = `$${total.toFixed(2)}`;
}

function loadCart() {
    fetch("http://localhost:8080/api/cart", { credentials: 'include' })
        .then(res => {
            if (!res.ok) throw new Error('Could not fetch cart');
            return res.json();
        })
        .then(items => {
            renderCart(items);
        })
        .catch(error => {
            console.error("Error fetching cart:", error);
            showSnackbar("Failed to load cart items. Please try again.", true);
        });
}

function removeItem(productId) {
    if (!confirm('Are you sure you want to remove this item from your cart?')) {
        return;
    }
    fetch(`http://localhost:8080/api/cart/remove/${productId}`, {
        method: "DELETE",
        credentials: "include",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok)
                return response.text().then(text => { throw new Error(text || 'Failed to remove item from cart'); });
            return response.text();
        })
        .then(() => {
            showSnackbar('✅ Item removed successfully');
            setTimeout(() => loadCart(), 500); // Reload cart quickly after deletion
        })
        .catch(error => {
            console.error("Error:", error);
            showSnackbar(`❌ ${error.message}`, true);
        });
}

// Load the cart after DOM is ready
document.addEventListener("DOMContentLoaded", loadCart);
