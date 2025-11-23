console.log("Razorpay JS Loaded");

const userId = window.APP_USER_ID;

document.getElementById("btn-proceed").addEventListener("click", async () => {
    if (!userId) {
        alert("User not logged in!");
        return;
    }

    try {
        // -------------------------------
        // 1️⃣ Create Order (Backend)
        // -------------------------------
        const res = await fetch("/api/payment/create-order", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId })
        });

        const data = await res.json();
        console.log("Order Created:", data);

        if (!data.key || !data.razorpayOrderId || !data.internalOrderId) {
            alert("Payment setup error. Please try again.");
            return;
        }

        const orderId = data.internalOrderId;

        // -------------------------------
        // 2️⃣ Razorpay Checkout Options
        // -------------------------------
        const options = {
            key: data.key,
            amount: data.amount,
            currency: data.currency,
            name: "SpringCart",
            description: "Order Payment",
            order_id: data.razorpayOrderId,

            prefill: {
                name: data.name,
                email: data.email
            },

            theme: { color: "#2f855a" },

            // -------------------------------------
            // ⭐ 3️⃣ SUCCESS PAYMENT HANDLER
            // -------------------------------------
            handler: async (response) => {
                console.log("Payment Success:", response);

                const verifyResp = await fetch("/api/payment/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        razorpay_payment_id: response.razorpay_payment_id,
                        razorpay_order_id: response.razorpay_order_id,
                        razorpay_signature: response.razorpay_signature,
                        internalOrderId: orderId
                    })
                });

                const verifyData = await verifyResp.json();
                console.log("Verify Response:", verifyData);

                if (verifyData.status === "success") {
                    window.location.href = `/payment-success?orderId=${orderId}`;
                } else {
                    await markPaymentFailed(orderId);
                }
            },

            // -------------------------------------
            // ⭐ USER CLOSED PAYMENT POPUP
            // -------------------------------------
            modal: {
                ondismiss: async () => {
                    console.log("Payment popup closed");
                    await markPaymentFailed(orderId);
                }
            }
        };

        // Razorpay instance
        const razorpay = new Razorpay(options);

        // -------------------------------------
        // ⭐ PAYMENT FAILED FROM RAZORPAY
        // -------------------------------------
        razorpay.on("payment.failed", async (response) => {
            console.log("Payment Failed:", response.error);
            await markPaymentFailed(orderId);
        });

        // open checkout
        razorpay.open();

    } catch (err) {
        console.error("Payment Error:", err);
        alert("Something went wrong. Please try again.");
    }
});

// ------------------------------------------------------
// 🔥 Reusable function → Marks Failed in Backend
// ------------------------------------------------------
async function markPaymentFailed(orderId) {
    await fetch(`/api/payment/failed/${orderId}`, { method: "POST" });
    window.location.href = "/payment-failed";
}
