// Full-order-details.js
document.addEventListener("DOMContentLoaded", () => {
    initLotties();
    loadOrderDetails();
    attachDownload();
});

let lottieSuccess, lottieFail;

function initLotties() {
    // success lottie
    lottieSuccess = lottie.loadAnimation({
        container: document.getElementById('lottie-success'),
        renderer: 'svg',
        loop: false,
        autoplay: false,
        path: 'https://assets1.lottiefiles.com/packages/lf20_jbrw3hcz.json' // success animation
    });

    // fail lottie
    lottieFail = lottie.loadAnimation({
        container: document.getElementById('lottie-fail'),
        renderer: 'svg',
        loop: false,
        autoplay: false,
        path: 'https://assets8.lottiefiles.com/packages/lf20_jzq0d8yk.json' // error animation
    });
}

async function loadOrderDetails() {
    const id = window.location.pathname.split("/").pop();
    const infoBox = document.getElementById("order-info");
    const itemsBox = document.getElementById("order-items");
    const totalEl = document.getElementById("order-total");

    // skeleton
    infoBox.innerHTML = `<div class="row"><div class="col-6 skeleton" style="height:18px"></div></div>`;
    itemsBox.innerHTML = `<div class="order-item"><div class="skeleton skeleton-img"></div><div style="flex:1"><div class="skeleton skeleton-line"></div><div class="skeleton skeleton-line" style="width:60%"></div></div></div>`;

    try {
        const res = await fetch(`/api/orders/${id}`);
        if (!res.ok) throw new Error('Failed to fetch order');
        const data = await res.json();

        // clear lottie visibility
        document.getElementById('lottie-success').classList.add('d-none');
        document.getElementById('lottie-fail').classList.add('d-none');

        // status badge class
        const statusCls = data.status === 'PAID' ? 'status-paid' : data.status === 'FAILED' ? 'status-failed' : 'status-pending';

        // Info
        infoBox.innerHTML = `
      <div class="row">
        <div class="col-md-6"><div class="label">Order ID</div><div class="value">#${data.id}</div></div>
        <div class="col-md-6"><div class="label">Payment ID</div><div class="value">${data.paymentId || '-'}</div></div>
        <div class="col-md-6 mt-2"><div class="label">Razorpay Order</div><div class="value">${data.razorpayOrderId || '-'}</div></div>
        <div class="col-md-6 mt-2"><div class="label">Status</div><div class="value"><span class="status-badge ${statusCls}">${data.status}</span></div></div>
        <div class="col-md-6 mt-2"><div class="label">Date</div><div class="value">${data.createdAt}</div></div>
      </div>
    `;

        // Items
        itemsBox.innerHTML = '';
        (data.items || []).forEach(it => {
            const img = it.imageUrl || `/api/product/${it.product?.id || ''}/image`;
            const name = it.name || (it.product && it.product.name) || 'Product';
            itemsBox.innerHTML += `
        <div class="order-item">
          <img src="${img}" class="order-item-img" onerror="this.src='https://placehold.co/200x200?text=Image'">
          <div class="order-item-meta">
            <div class="order-item-name">${escapeHtml(name)}</div>
            <div class="order-item-price">₹${it.price}</div>
            <div class="order-item-qty">Quantity: ${it.quantity}</div>
          </div>
        </div>
      `;
        });

        totalEl.textContent = `₹${data.totalAmount}`;

        // show Lottie on status
        if (data.status === 'PAID') {
            const box = document.getElementById('lottie-success');
            box.classList.remove('d-none');
            lottieSuccess.goToAndPlay(0,true);
        } else if (data.status === 'FAILED') {
            const box = document.getElementById('lottie-fail');
            box.classList.remove('d-none');
            lottieFail.goToAndPlay(0,true);
        }

        // store current order id for download
        window.CURRENT_ORDER_ID = data.id;

    } catch (err) {
        infoBox.innerHTML = `<div class="text-danger">Could not load order — ${err.message}</div>`;
        itemsBox.innerHTML = '';
        console.error(err);
    }
}

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function (m) { return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'})[m]; });
}

function attachDownload() {
    const btn = document.getElementById('btn-download-invoice');
    btn.addEventListener('click', async () => {
        const id = window.CURRENT_ORDER_ID || window.location.pathname.split("/").pop();
        if (!id) return alert('No order selected');

        btn.classList.add('disabled');
        btn.innerHTML = 'Preparing...';

        try {
            const res = await fetch(`/api/orders/${id}/invoice`, { method: 'GET' });
            if (!res.ok) throw new Error('Invoice generation failed');
            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `invoice-order-${id}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            alert('Failed to download invoice: ' + err.message);
            console.error(err);
        } finally {
            btn.classList.remove('disabled');
            btn.innerHTML = '<i class="bi bi-download me-1"></i> Download Invoice';
        }
    });
}
