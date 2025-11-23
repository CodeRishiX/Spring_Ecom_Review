(function () {
    const API = window.location.origin + "/api";
    const pageSizeDefault = 8;
    const skeletonCount = pageSizeDefault;

    // ========== SKELETON HELPERS ==========
    function renderSkeletonCard() {
        return `
      <div class="col mb-5">
        <div class="card h-100">
          <div class="loading-skeleton" style="height:200px; border-radius:8px;"></div>
          <div class="card-body p-4 text-center">
            <div class="loading-skeleton" style="height:24px;width:70%;margin-bottom:10px;"></div>
            <div class="loading-skeleton" style="height:18px; width:40%;"></div>
          </div>
          <div class="card-footer bg-transparent text-center">
            <div class="loading-skeleton" style="height:38px; width:70%"></div>
          </div>
        </div>
      </div>
    `;
    }

    function showSkeletons(productGrid, count = skeletonCount) {
        productGrid.innerHTML = Array.from({ length: count }).map(renderSkeletonCard).join("");
    }
    function hideSkeletons(productGrid) {
        productGrid.innerHTML = ""; // Optionally clear (or render products after hide)
    }

    // ========== PRODUCT CARD ===============
    function renderProductCard(p) {
        return `
      <div class="col mb-5">
        <div class="card h-100">
          <img class="card-img-top" src="${API}/product/${p.id}/image" alt="${escapeHtml(p.name)}">
          <div class="card-body p-4 text-center">
            <h5 class="fw-bolder">${escapeHtml(p.name)}</h5>
            <div class="fw-bold">$${Number(p.price).toFixed(2)}</div>
          </div>
          <div class="card-footer bg-transparent text-center">
            <a class="btn btn-primary" href="/product/${p.id}">View Details</a>
          </div>
        </div>
      </div>
    `;
    }

    function escapeHtml(s) {
        if (!s) return "";
        return String(s)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    // ========== PRODUCTS + PAGINATION ==========
    let currentPage = 0;
    let pageSize = pageSizeDefault;

    function loadProductsPage(page = 0, size = pageSize) {
        currentPage = page;
        const productGrid = document.getElementById("productGrid");
        const pagination = document.getElementById("pagination");
        const productsTitle = document.getElementById("productsTitle");
        if (!productGrid || !pagination) return;

        // SHOW SKELETONS while loading
        showSkeletons(productGrid, size);

        fetch(`${API}/products?page=${page}&size=${size}`, { credentials: "include" })
            .then(res => {
                if (!res.ok) throw new Error("Failed to fetch products");
                return res.json();
            })
            .then(data => {
                const products = data.content || [];
                const totalPages = Number.isFinite(data.totalPages) ? data.totalPages : 1;

                productGrid.innerHTML = products.length === 0
                    ? '<p class="text-center w-100">No products available.</p>'
                    : products.map(renderProductCard).join("");

                // pagination build
                let html = "";
                html += `<li class="page-item ${page === 0 ? "disabled" : ""}">
                  <a class="page-link" href="#" data-page="${page - 1}">Previous</a>
                 </li>`;

                const maxButtons = 7;
                let start = Math.max(0, page - Math.floor(maxButtons / 2));
                let end = Math.min(totalPages - 1, start + maxButtons - 1);
                start = Math.max(0, end - (maxButtons - 1));

                for (let i = start; i <= end; i++) {
                    html += `<li class="page-item ${i === page ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                   </li>`;
                }

                html += `<li class="page-item ${page + 1 >= totalPages ? "disabled" : ""}">
                  <a class="page-link" href="#" data-page="${page + 1}">Next</a>
                 </li>`;

                pagination.innerHTML = html;

                pagination.querySelectorAll("a.page-link").forEach(a => {
                    a.addEventListener("click", (ev) => {
                        ev.preventDefault();
                        const targetPage = Number(a.getAttribute("data-page"));
                        if (isNaN(targetPage) || targetPage < 0) return;
                        if (targetPage >= totalPages) return;
                        loadProductsPage(targetPage, pageSize);
                        document.getElementById("products")?.scrollIntoView({ behavior: "smooth" });
                    });
                });

                if (productsTitle) productsTitle.textContent = "Our Products";
            })
            .catch(err => {
                console.error("Product fetch error:", err);
                productGrid.innerHTML = `<p class="text-center text-danger w-100">Failed to load products.</p>`;
            });
    }

    // ========== SEARCH ==========
    function attachSearch() {
        const searchForm = document.getElementById("searchForm");
        const searchInput = document.getElementById("searchInput");
        const productGrid = document.getElementById("productGrid");
        const productsTitle = document.getElementById("productsTitle");
        if (!searchForm || !searchInput || !productGrid) return;

        searchForm.addEventListener("submit", (ev) => {
            ev.preventDefault();
            const q = (searchInput.value || "").trim();
            if (!q) {
                // clear search -> go back to paginated list
                loadProductsPage(0, pageSize);
                return;
            }

            showSkeletons(productGrid, skeletonCount);

            fetch(`${API}/products/search?keyword=${encodeURIComponent(q)}`, { credentials: "include" })
                .then(res => {
                    if (!res.ok) throw new Error("Search failed");
                    return res.json();
                })
                .then(products => {
                    productGrid.innerHTML = (products && products.length > 0)
                        ? products.map(renderProductCard).join("")
                        : '<p class="text-center w-100">No products found.</p>';
                    if (productsTitle) productsTitle.textContent = `Search Results`;
                    // hide pagination when showing search results:
                    const pagination = document.getElementById("pagination");
                    if (pagination) pagination.innerHTML = "";
                })
                .catch(err => {
                    console.error("Search error:", err);
                    productGrid.innerHTML = `<p class="text-center text-danger w-100">Search failed.</p>`;
                });
        });
    }

    // ========== NEW ARRIVALS ==========
    function loadNewArrivals() {
        const productGrid = document.getElementById("productGrid");
        if (!productGrid) return;
        showSkeletons(productGrid, skeletonCount);

        fetch(`${API}/New-Arrival?new=true`, { credentials: "include" })
            .then(res => {
                if (!res.ok) throw new Error("Failed to load new arrivals");
                return res.json();
            })
            .then(products => {
                productGrid.innerHTML = (products && products.length > 0)
                    ? products.map(renderProductCard).join("")
                    : '<p class="text-center w-100">No new arrivals.</p>';
                const pagination = document.getElementById("pagination");
                if (pagination) pagination.innerHTML = "";
                const productsTitle = document.getElementById("productsTitle");
                if (productsTitle) productsTitle.textContent = "New Arrivals";
            })
            .catch(err => {
                console.error("New arrivals error:", err);
                productGrid.innerHTML = `<p class="text-center text-danger w-100">Failed to load new arrivals.</p>`;
            });
    }

    // ========== AUTH STATUS + ROLE (navbar) ==========
    function checkAuthAndRole() {
        const signInButton = document.getElementById("signInButton");
        const userProfile = document.getElementById("userProfile");
        const userImage = document.getElementById("userImage");
        const userName = document.getElementById("userName");
        const adminBadge = document.getElementById("adminBadge");
        const userBadge = document.getElementById("userBadge");
        const addProductItems = document.querySelectorAll(".admin-only");

        Promise.all([
            fetch(`${API}/auth/status`, { credentials: "include" }).then(r => r.ok ? r.json() : null).catch(() => null),
            fetch(`${API}/user/role`, { credentials: "include" }).then(r => r.ok ? r.text() : null).catch(() => null)
        ]).then(([status, role]) => {
            const authenticated = !!(status && status.authenticated);
            if (authenticated) {
                signInButton?.classList.add("d-none");
                userProfile?.classList.remove("d-none");
                if (userImage && status.imageUrl) userImage.src = status.imageUrl;
                if (userName) userName.textContent = status.name || "";
            } else {
                signInButton?.classList.remove("d-none");
                userProfile?.classList.add("d-none");
            }

            const isAdmin = role === "ROLE_ADMIN" || role === "ADMIN" || role === "admin";
            addProductItems.forEach(el => {
                if (isAdmin) el.classList.remove("d-none"); else el.classList.add("d-none");
            });

            if (isAdmin) {
                adminBadge?.classList.remove("d-none");
                userBadge?.classList.add("d-none");
            } else {
                adminBadge?.classList.add("d-none");
                if (authenticated) userBadge?.classList.remove("d-none"); else userBadge?.classList.add("d-none");
            }
        }).catch(e => console.error("Auth/role check failed:", e));
    }

    // ========== CART COUNT ==========
    function updateCartCount() {
        const badge = document.getElementById("cart-badge");
        if (!badge) return;
        fetch(`${API}/cart`, { credentials: "include" })
            .then(r => r.ok ? r.json() : [])
            .then(items => { badge.textContent = (items && items.length) ? items.length : "0"; })
            .catch(() => badge.textContent = "0");
    }

    // ========== INIT ==========
    document.addEventListener("DOMContentLoaded", () => {
        attachSearch();
        checkAuthAndRole();
        updateCartCount();

        const path = window.location.pathname.toLowerCase();
        if (path.includes("new-arrivals") || path.includes("new-arrival")) {
            loadNewArrivals();
        } else {
            loadProductsPage(0, pageSize);
        }
    });
    // PROFILE DROPDOWN TOGGLE ON MOBILE
    document.addEventListener("DOMContentLoaded", () => {
        const trigger = document.querySelector(".profile-trigger");
        const dropdown = document.querySelector(".profile-dropdown-menu");

        if (!trigger || !dropdown) return;

        trigger.addEventListener("click", (e) => {
            e.stopPropagation();
            dropdown.style.display =
                dropdown.style.display === "block" ? "none" : "block";
        });

        document.addEventListener("click", () => {
            dropdown.style.display = "none";
        });
    });
})();
