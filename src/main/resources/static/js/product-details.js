// ============================
// CONFIG
// ============================
const API_BASE = window.location.origin + "/api";

// Get product id from ?id=7 or from /product/12 path
function getProductId() {
    const params = new URLSearchParams(window.location.search);
    let id = params.get("id");
    if (!id) {
        const parts = window.location.pathname.split("/");
        id = parts[parts.length - 1];
    }
    return id;
}

// Toast Notification
function showSnackbar(msg, isError = false) {
    const s = document.createElement("div");
    s.className = `alert alert-${isError ? "danger" : "success"} position-fixed`;
    s.style.cssText = "bottom:20px;right:20px;z-index:9999;";
    s.textContent = msg;
    document.body.appendChild(s);
    setTimeout(() => s.remove(), 3000);
}

// ============================
// LOAD PRODUCT DETAILS with Skeleton
// ============================
document.addEventListener("DOMContentLoaded", async () => {
    const productId = getProductId();
    const spinner = document.getElementById("loading-spinner");
    const skeleton = document.getElementById("product-skeleton");
    const content = document.getElementById("product-content");

    // Initial state: Show skeleton, hide everything else
    spinner.classList.add("d-none");
    skeleton.classList.remove("d-none");
    content.classList.add("d-none");

    if (!productId) {
        skeleton.classList.add("d-none");
        spinner.classList.add("d-none");
        content.classList.add("d-none");
        showSnackbar("❌ No product ID found", true);
        return;
    }

    try {
        // Fetch product details
        const res = await fetch(`${API_BASE}/productdetails/${productId}`, { credentials: "include" });
        if (!res.ok) throw new Error("Failed to load product");
        const p = await res.json();

        // Populate product detail fields
        document.getElementById("productName").textContent = p.name;
        document.getElementById("productPrice").textContent = `$${p.price.toFixed(2)}`;
        document.getElementById("oldPrice").textContent = `$${(p.price * 1.2).toFixed(2)}`;
        document.getElementById("productDescription").textContent = p.description || "No description available";
        document.getElementById("sku").textContent = `PROD-${p.id}`;
        document.getElementById("brandName").textContent = p.brand || "N/A";
        document.getElementById("breadcrumb-name").textContent = p.name;

        const img = document.getElementById("productImage");
        img.src = `${API_BASE}/product/${p.id}/image`;
        img.onerror = () => img.src = "https://dummyimage.com/600x600/dee2e6/6c757d.jpg";

        document.getElementById("updateButton").onclick = () => {
            window.location.href = `/update-product?id=${p.id}`;
        };

        // Hide skeleton and show loaded content
        skeleton.classList.add("d-none");
        content.classList.remove("d-none");
    } catch (err) {
        skeleton.classList.add("d-none");
        spinner.classList.add("d-none");
        content.classList.add("d-none");
        showSnackbar("❌ " + err.message, true);
    }
});

// ============================
// ADD TO CART
// ============================
document.getElementById("addToCartBtn")?.addEventListener("click", async () => {
    try {
        const auth = await fetch(`${API_BASE}/auth/status`, { credentials: "include" }).then(r => r.json());
        if (!auth.authenticated) return (window.location.href = "/oauth2/authorization/google");
        const productId = getProductId();
        const qty = parseInt(document.getElementById("inputQuantity").value || "1");
        const res = await fetch(`${API_BASE}/cart/add?productId=${productId}&quantity=${qty}`, {
            method: "POST",
            credentials: "include"
        });
        if (!res.ok) throw new Error(await res.text());
        const item = await res.json();
        showSnackbar(`🛒 Added ${item.product.name} × ${item.quantity} to cart`);
        updateCartBadge();
    } catch (err) {
        console.error(err);
        showSnackbar("❌ " + err.message, true);
    }
});

// Update Cart Badge
async function updateCartBadge() {
    try {
        const items = await fetch(`${API_BASE}/cart`, { credentials: "include" }).then(r => r.json());
        document.getElementById("cart-badge").textContent = items.length;
    } catch (_) {}
}

// ============================
// DELETE PRODUCT (ADMIN)
// ============================
document.getElementById("deleteButton")?.addEventListener("click", async () => {
    if (!confirm("Delete this product?")) return;
    const productId = getProductId();
    try {
        const res = await fetch(`${API_BASE}/product/${productId}`, {
            method: "DELETE",
            credentials: "include"
        });
        if (!res.ok) throw new Error("Delete failed");
        showSnackbar("✅ Product deleted");
        setTimeout(() => (window.location.href = "/"), 1200);
    } catch (err) {
        showSnackbar("❌ " + err.message, true);
    }
});

// ============================
// QUANTITY CONTROL (+ / -)
// ============================
document.addEventListener("DOMContentLoaded", () => {
    const qtyInput = document.getElementById("inputQuantity");
    const btnPlus = document.getElementById("increaseQty");
    const btnMinus = document.getElementById("decreaseQty");
    if (!qtyInput || !btnPlus || !btnMinus) return;
    btnPlus.addEventListener("click", () => {
        let value = parseInt(qtyInput.value, 10) || 1;
        if (value < 100) qtyInput.value = value + 1;
    });
    btnMinus.addEventListener("click", () => {
        let value = parseInt(qtyInput.value, 10) || 1;
        if (value > 1) qtyInput.value = value - 1;
    });
});

// ============================
// CHECK ADMIN ROLE
// ============================
(async () => {
    try {
        const role = await fetch(`${API_BASE}/user/role`, { credentials: "include", headers: { Accept: "text/plain" } }).then(r => r.text());
        if (role === "ROLE_ADMIN") {
            document.getElementById("adminControls").classList.remove("d-none");
            document.getElementById("addToCartBtn").style.display = "none";
            document.getElementById("inputQuantity").style.display = "none";
        }
    } catch (_) {}
})();
