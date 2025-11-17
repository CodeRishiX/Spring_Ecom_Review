// ============================================
// NEW ARRIVALS PAGE - Fetch ONLY new products
// ============================================
console.log("✅ new-arrivals.js LOADED");
document.addEventListener('DOMContentLoaded', () => {
    const productGrid = document.getElementById('newArrivalGrid');
    const productsTitle = document.getElementById('productsTitle');

    // Render helper
    function renderProducts(products) {
        productGrid.innerHTML = '';

        if (!products || products.length === 0) {
            productGrid.innerHTML = '<p class="text-center">No new arrivals at the moment.</p>';
            productsTitle.textContent = 'New Arrivals';
            return;
        }

        products.forEach(p => {
            const col = document.createElement('div');
            col.className = 'col mb-5';
            const price = (typeof p.price === 'number') ? p.price.toFixed(2) : '0.00';
            col.innerHTML = `
                <div class="card h-100">
                    <img class="card-img-top"
                         src="${window.location.origin}/api/product/${encodeURIComponent(p.id)}/image"
                         alt="${(p.name || 'Product')}"
                         onerror="this.src='https://dummyimage.com/450x300/dee2e6/6c757d.jpg'">
                    <div class="card-body p-4">
                        <div class="text-center">
                            <h5 class="fw-bolder">${p.name || ''}</h5>
                            $${price}
                        </div>
                    </div>
                    <div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
                        <div class="text-center">
                            <a class="btn btn-primary mt-auto" href="product-details.html?id=${encodeURIComponent(p.id)}">View Details</a>
                        </div>
                    </div>
                </div>`;
            productGrid.appendChild(col);
        });

        productsTitle.textContent = 'New Arrivals';
    }

    // Fetch new arrivals from backend (ask the server for new=true)
    fetch(window.location.origin + '/api/New-Arrival?new=true')
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { throw new Error(`HTTP ${res.status}: ${text}`); });
            }
            return res.json();
        })
        .then(products => {
            // backend should return only new products for ?new=true
            renderProducts(products);
        })
        .catch(e => {
            console.error('Fetch error:', e.message);
            productGrid.innerHTML = `<p class="text-center text-danger">Failed to load products: ${e.message}</p>`;
        });
});

// NOTE: Login notification and auth check are handled by app.js (universal)
