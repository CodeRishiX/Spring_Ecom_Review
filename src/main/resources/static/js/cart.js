// ===============================
// CART.JS — FULLY FIXED + WRAPPED
// ===============================

(() => {

    // Prevent conflicts with app.js
    window.CART_PAGE = true;

    const API_BASE = window.location.origin + "/api";

    document.addEventListener("DOMContentLoaded", loadCart);

    function showSnackbar(msg, error = false) {
        alert(msg);
    }

    async function loadCart() {
        const cartItemsDiv = document.getElementById("cart-items");
        const emptyState = document.getElementById("cart-empty");
        const checkoutWrapper = document.getElementById("checkout-box-wrapper");
        const totalDisplay = document.getElementById("grand-total");

        cartItemsDiv.innerHTML = "";

        try {
            const res = await fetch(`${API_BASE}/cart`, { credentials: 'include' });
            if (!res.ok) throw new Error("Could not fetch cart");

            const items = await res.json();

            if (!items || items.length === 0) {
                emptyState.classList.remove("d-none");
                checkoutWrapper.classList.add("d-none");
                updateCartBadge(0);
                return;
            }

            emptyState.classList.add("d-none");
            checkoutWrapper.classList.remove("d-none");

            let total = 0;

            items.forEach(item => {
                const subtotal = item.product.price * item.quantity;
                total += subtotal;

                const col = document.createElement("div");
                col.className = "cart-card mb-3";

                col.innerHTML = `
                    <img src="${API_BASE}/product/${item.product.id}/image"
                         class="cart-item-img"
                         alt="${item.product.name}"
                         onerror="this.src='https://dummyimage.com/450x300/dee2e6/6c757d.jpg'">

                    <div class="flex-grow-1">
                        <h5 class="fw-semibold mb-1">${item.product.name}</h5>

                        <div class="qty-box">
                            <button class="qty-minus" data-id="${item.product.id}" data-qty="${item.quantity}">−</button>
                            <input readonly value="${item.quantity}">
                            <button class="qty-plus" data-id="${item.product.id}" data-qty="${item.quantity}">+</button>
                        </div>
                    </div>

                    <div class="fw-bold fs-5 text-primary">$${subtotal.toFixed(2)}</div>

                    <button class="btn btn-sm btn-outline-danger remove-btn"
                            data-id="${item.product.id}">
                        Remove
                    </button>
                `;

                cartItemsDiv.appendChild(col);
            });

            totalDisplay.textContent = `$${total.toFixed(2)}`;
            updateCartBadge(items.length);

            attachButtonActions();

        } catch (err) {
            console.error(err);
            cartItemsDiv.innerHTML = `<p class="text-center text-danger">Failed to load cart.</p>`;
        }
    }

    // Attach event listeners AFTER rendering
    function attachButtonActions() {

        document.querySelectorAll(".qty-minus").forEach(btn => {
            btn.addEventListener("click", () => {
                const id = btn.getAttribute("data-id");
                const qty = parseInt(btn.getAttribute("data-qty"));
                updateQty(id, qty - 1);
            });
        });

        document.querySelectorAll(".qty-plus").forEach(btn => {
            btn.addEventListener("click", () => {
                const id = btn.getAttribute("data-id");
                const qty = parseInt(btn.getAttribute("data-qty"));
                updateQty(id, qty + 1);
            });
        });

        document.querySelectorAll(".remove-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const id = btn.getAttribute("data-id");
                removeItem(id);
            });
        });
    }

    async function updateQty(productId, newQty) {
        if (newQty < 1) return;

        await fetch(`${API_BASE}/cart/update?productId=${productId}&quantity=${newQty}`, {
            method: "PUT",
            credentials: "include"
        });

        loadCart();
    }

    async function removeItem(productId) {
        if (!confirm("Remove this item?")) return;

        await fetch(`${API_BASE}/cart/remove/${productId}`, {
            method: "DELETE",
            credentials: "include"
        });

        loadCart();
    }

    function updateCartBadge(count) {
        const badge = document.getElementById("cart-badge");
        if (badge) badge.textContent = count;
    }

})();
v