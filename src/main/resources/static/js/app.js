// ----------------------------------------------
// PRODUCT LIST + SEARCH
// ----------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    const productGrid = document.getElementById('productGrid');
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');
    const productsTitle = document.getElementById('productsTitle');

    function fetchProducts(url) {
        fetch(url)
            .then(res => res.json())
            .then(products => {
                productGrid.innerHTML = '';
                if (products.length === 0) {
                    productGrid.innerHTML = '<p class="text-center">No products found.</p>';
                    productsTitle.textContent = 'Search Results';
                    return;
                }
                products.forEach(p => {
                    productGrid.innerHTML += `
                    <div class="col mb-5">
                        <div class="card h-100">
                            <img class="card-img-top" src="http://localhost:8080/api/product/${p.id}/image" alt="${p.name}">
                            <div class="card-body p-4 text-center">
                                <h5 class="fw-bolder">${p.name}</h5>
                                $${p.price.toFixed(2)}
                            </div>
                            <div class="card-footer bg-transparent">
                                <div class="text-center">
                                    <a class="btn btn-primary" href="/product/${p.id}">View Details</a>
                                </div>
                            </div>
                        </div>
                    </div>`;
                });
            })
            .catch(e => console.error('Fetch error:', e));
    }

    if (productGrid && !window.location.pathname.includes("New-Arrivals")) {
        // Load ALL products only when NOT on New Arrivals page
        fetchProducts('http://localhost:8080/api/products');

        searchForm?.addEventListener('submit', (e) => {
            e.preventDefault();
            const query = searchInput.value.trim();
            fetchProducts(query ? `http://localhost:8080/api/products/search?keyword=${query}` : 'http://localhost:8080/api/products');
        });
    }

});

// ----------------------------------------------
// LOGIN STATUS + ROLE CHECK (UPDATED)
// ----------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    const signInButton = document.getElementById('signInButton');
    const userProfile = document.getElementById('userProfile');
    const userImage = document.getElementById('userImage');
    const userName = document.getElementById('userName');
    const adminBadge = document.getElementById('adminBadge');
    const userBadge = document.getElementById('userBadge');
    const addProductItems = document.querySelectorAll('.admin-only');

    // Fetch both status and role, then update UI once
    Promise.all([
        fetch('/api/auth/status', { credentials: 'include' }).then(res => res.ok ? res.json() : null).catch(() => null),
        fetch('/api/user/role', { credentials: 'include' }).then(res => res.ok ? res.text() : null).catch(() => null)
    ]).then(([status, role]) => {
        const authenticated = !!(status && status.authenticated);

        if (authenticated) {
            signInButton?.classList.add('d-none');
            userProfile?.classList.remove('d-none');
            if (userImage && status.imageUrl) userImage.src = status.imageUrl;
            if (userName) userName.textContent = status.name || '';
        } else {
            signInButton?.classList.remove('d-none');
            userProfile?.classList.add('d-none');
        }

        const isAdmin = role === 'ROLE_ADMIN' || role === 'ADMIN' || role === 'admin';

        // Toggle admin-only elements (e.g., Add Product link)
        addProductItems.forEach(el => {
            if (isAdmin) {
                el.classList.remove('d-none');
                el.style.display = ''; // let CSS determine layout
            } else {
                el.classList.add('d-none');
                el.style.display = 'none';
            }
        });

        // Badges: show only one based on role; admin (orange) or user (green)
        if (isAdmin) {
            adminBadge?.classList.remove('d-none');
            userBadge?.classList.add('d-none');
        } else {
            adminBadge?.classList.add('d-none');
            // show user badge only when authenticated (hide for anonymous)
            if (authenticated) userBadge?.classList.remove('d-none'); else userBadge?.classList.add('d-none');
        }
    }).catch(e => {
        console.error('Auth/Role check failed:', e);
    });
});

// ----------------------------------------------
// CART COUNT
// ----------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    fetch('http://localhost:8080/api/cart', { credentials: 'include' })
        .then(res => res.json())
        .then(items => {
            const badge = document.getElementById('cart-badge');
            badge && (badge.textContent = items.length);
        });
});
