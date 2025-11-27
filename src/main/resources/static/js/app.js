(function () {
    // --- CONFIGURATION ---
    const API = window.location.origin + "/api";
    const pageSizeDefault = 8;
    const skeletonCount = pageSizeDefault;

    // --- GLOBAL HELPERS (Exposed for inline HTML onclick events) ---
    window.addToCart = function(productId) {
        fetch(`${API}/cart/add?productId=${productId}`, {
            method: 'POST',
            credentials: 'include'
        })
            .then(res => {
                if (res.ok) {
                    updateCartCount();
                    console.log("Item added to cart");
                } else if (res.status === 401) {
                    window.location.href = "/oauth2/authorization/google";
                } else {
                    alert("Could not add item to cart.");
                }
            })
            .catch(err => console.error("Error adding to cart:", err));
    };

    // ========== SKELETON LOADING UI ==========
    function renderSkeletonCard() {
        return `
        <div class="col">
            <div class="product-card h-100" aria-hidden="true">
                <div class="product-image-wrapper" style="background-color: #f0f0f0; animation: pulse 1.5s infinite;"></div>
                <div class="product-info">
                    <div style="height: 15px; width: 50%; background: #e0e0e0; margin: 0 auto 10px; border-radius: 4px;"></div>
                    <div style="height: 20px; width: 80%; background: #e0e0e0; margin: 0 auto 10px; border-radius: 4px;"></div>
                    <div style="height: 20px; width: 40%; background: #e0e0e0; margin: 0 auto; border-radius: 4px;"></div>
                </div>
            </div>
        </div>`;
    }

    function showSkeletons(container, count = skeletonCount) {
        container.innerHTML = Array.from({ length: count }).map(renderSkeletonCard).join("");
    }

    // ========== PRODUCT CARD RENDERING ==========
    function renderProductCard(p) {
        const id = p.id;
        const name = p.name || p.title || 'Product Name';
        const price = p.price ? Number(p.price).toFixed(2) : '0.00';
        const category = p.category || 'Shop';

        const imageUrl = `${API}/product/${id}/image`;
        const fallbackUrl = `https://placehold.co/600x600?text=${encodeURIComponent(name)}`;

        let badgeHtml = '';
        if (p.badge === 'sale' || p.discount > 0) {
            badgeHtml = `<span class="badge-custom bg-sale">Sale</span>`;
        } else if (p.badge === 'new' || p.isNew) {
            badgeHtml = `<span class="badge-custom bg-new">New</span>`;
        }

        return `
        <div class="product-card">
            <div class="product-image-wrapper">
                ${badgeHtml}
                <img src="${imageUrl}" 
                     alt="${name}" 
                     class="product-img"
                     loading="lazy"
                     onerror="this.onerror=null; this.src='${fallbackUrl}';">

                <div class="product-actions">
                    <a href="/product/${id}" class="action-btn" title="View Details">
                        <i class="fa-regular fa-eye"></i>
                    </a>
                </div>
            </div>
            <div class="product-info">
                <div class="product-category">${category}</div>
                <h3 class="product-title">${name}</h3>
                <div class="product-price">$${price}</div>
            </div>
        </div>
        `;
    }

    // ========== PAGINATION LOGIC ==========
    let currentPage = 0;
    let pageSize = pageSizeDefault;

    function loadProductsPage(page = 0, size = pageSize) {
        currentPage = page;
        const productGrid = document.getElementById("productGrid");
        const pagination = document.getElementById("pagination");
        const productsTitle = document.getElementById("productsTitle");

        if (!productGrid || !pagination) return;

        showSkeletons(productGrid, size);

        fetch(`${API}/products?page=${page}&size=${size}`, { credentials: "include" })
            .then(res => {
                if (!res.ok) throw new Error("Failed to fetch products");
                return res.json();
            })
            .then(data => {
                const products = data.content || [];
                const totalPages = Number.isFinite(data.totalPages) ? data.totalPages : 1;

                if (products.length === 0) {
                    productGrid.innerHTML = '<div class="col-12 text-center"><p class="text-muted">No products available.</p></div>';
                } else {
                    productGrid.innerHTML = products.map(renderProductCard).join("");
                }

                let html = "";
                html += `<li class="page-item ${page === 0 ? "disabled" : ""}">
                  <a class="page-link" href="#" data-page="${page - 1}"><i class="fa-solid fa-chevron-left"></i></a>
                 </li>`;

                const maxButtons = 5;
                let start = Math.max(0, page - Math.floor(maxButtons / 2));
                let end = Math.min(totalPages - 1, start + maxButtons - 1);
                start = Math.max(0, end - (maxButtons - 1));

                for (let i = start; i <= end; i++) {
                    html += `<li class="page-item ${i === page ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                   </li>`;
                }

                html += `<li class="page-item ${page + 1 >= totalPages ? "disabled" : ""}">
                  <a class="page-link" href="#" data-page="${page + 1}"><i class="fa-solid fa-chevron-right"></i></a>
                 </li>`;

                pagination.innerHTML = html;

                pagination.querySelectorAll("a.page-link").forEach(a => {
                    a.addEventListener("click", (ev) => {
                        ev.preventDefault();
                        const targetPage = Number(a.getAttribute("data-page"));
                        if (isNaN(targetPage) || targetPage < 0 || targetPage >= totalPages) return;

                        loadProductsPage(targetPage, pageSize);
                        document.getElementById("products")?.scrollIntoView({ behavior: "smooth" });
                    });
                });

                if (productsTitle) productsTitle.textContent = "Our Exclusive Products";
            })
            .catch(err => {
                console.error("Product fetch error:", err);
                productGrid.innerHTML = `<p class="text-center text-danger w-100">Failed to load products.</p>`;
            });
    }

    // ========== SEARCH FUNCTIONALITY ==========
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
                    if (products && products.length > 0) {
                        productGrid.innerHTML = products.map(renderProductCard).join("");
                    } else {
                        productGrid.innerHTML = '<div class="col-12 text-center"><p>No products found matching your search.</p></div>';
                    }

                    if (productsTitle) productsTitle.textContent = `Search Results for "${q}"`;
                    const pagination = document.getElementById("pagination");
                    if (pagination) pagination.innerHTML = "";
                })
                .catch(err => {
                    console.error("Search error:", err);
                    productGrid.innerHTML = `<p class="text-center text-danger w-100">Search error occurred.</p>`;
                });
        });
    }

    // ========== NEW ARRIVALS LOADER ==========
    function loadNewArrivals() {
        const productGrid = document.getElementById("productGrid");
        const productsTitle = document.getElementById("productsTitle");
        const pagination = document.getElementById("pagination");

        if (!productGrid) return;

        showSkeletons(productGrid, skeletonCount);

        fetch(`${API}/New-Arrival?new=true`, { credentials: "include" })
            .then(res => {
                if (!res.ok) throw new Error("Failed to load new arrivals");
                return res.json();
            })
            .then(products => {
                if (products && products.length > 0) {
                    productGrid.innerHTML = products.map(renderProductCard).join("");
                } else {
                    productGrid.innerHTML = '<p class="text-center w-100">No new arrivals yet.</p>';
                }

                if (pagination) pagination.innerHTML = "";
                if (productsTitle) productsTitle.textContent = "New Arrivals";
            })
            .catch(err => {
                console.error("New arrivals error:", err);
                productGrid.innerHTML = `<p class="text-center text-danger w-100">Could not load new arrivals.</p>`;
            });
    }

    // ========== NAVBAR: AUTH & ROLE CHECK ==========
    function checkAuthAndRole() {
        const signInButton = document.getElementById("signInButton");
        const userProfile = document.getElementById("userProfile");
        const userImage = document.getElementById("userImage");
        const userName = document.getElementById("userName");
        const adminItems = document.querySelectorAll(".admin-only");

        Promise.all([
            fetch(`${API}/auth/status`, { credentials: "include" }).then(r => r.ok ? r.json() : null).catch(() => null),
            fetch(`${API}/user/role`, { credentials: "include" }).then(r => r.ok ? r.text() : null).catch(() => null)
        ]).then(([status, roleStr]) => {
            const isAuthenticated = !!(status && status.authenticated);
            if (isAuthenticated) {
                signInButton?.classList.add("d-none");
                userProfile?.classList.remove("d-none");
                if (userImage && status.imageUrl) userImage.src = status.imageUrl;
                if (userName) userName.textContent = status.name || "User";
            } else {
                signInButton?.classList.remove("d-none");
                userProfile?.classList.add("d-none");
            }
            const role = roleStr ? roleStr.replace(/['"]+/g, '') : "";
            const isAdmin = (role === "ROLE_ADMIN" || role === "ADMIN");
            adminItems.forEach(el => {
                if (isAdmin) el.classList.remove("d-none");
                else el.classList.add("d-none");
            });
        }).catch(e => console.error("Auth check failed:", e));
    }

    // ========== CART BADGE UPDATE ==========
    function updateCartCount() {
        const badge = document.getElementById("cart-badge");
        if (!badge) return;
        fetch(`${API}/cart`, { credentials: "include" })
            .then(r => r.ok ? r.json() : [])
            .then(items => {
                badge.textContent = (Array.isArray(items)) ? items.length : "0";
            })
            .catch(() => badge.textContent = "0");
    }

    // ========== PROFILE DROPDOWN LOGIC ==========
    function setupProfileDropdown() {
        const trigger = document.querySelector(".profile-trigger");
        const dropdown = document.querySelector(".profile-dropdown-menu");

        if (trigger && dropdown) {
            // Toggle dropdown on trigger click
            trigger.addEventListener("click", function(e) {
                e.stopPropagation();
                dropdown.style.display = (dropdown.style.display === "block") ? "none" : "block";
            });
            // Hide dropdown if user clicks outside
            document.addEventListener("click", function() {
                dropdown.style.display = "none";
            });
            // Prevent clicks inside dropdown from closing it immediately
            dropdown.addEventListener("click", function(e) {
                e.stopPropagation();
            });
        }
    }

    // ========== INITIALIZATION ==========
    document.addEventListener("DOMContentLoaded", () => {
        attachSearch();
        checkAuthAndRole();
        updateCartCount();
        setupProfileDropdown();

        const path = window.location.pathname.toLowerCase();
        if (path.includes("new-arrivals") || path.includes("new-arrival")) {
            loadNewArrivals();
        } else {
            loadProductsPage(0, pageSize);
        }
    });
})();
