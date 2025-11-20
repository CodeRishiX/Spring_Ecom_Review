console.log("Razorpay JS Loaded");

// Fetch userId injected from HTML
const userId = window.APP_USER_ID;

document.getElementById("btn-proceed").addEventListener("click", async function () {

    if (!userId) {
        alert("User not logged in!");
        return;
    }

    let response = await fetch("/api/payment/create-order", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: userId })
    });

    let data = await response.json();
    console.log("Razorpay Order Created:", data);

    // 🔥 Important Validation
    if (!data.key || !data.razorpayOrderId) {
        alert("Error: Razorpay configuration missing!");
        console.error("Missing key or orderId:", data);
        return;
    }

    let options = {
        key: data.key,  // ✔ will work now
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


        handler: async function (response) {
            let verifyRes = await fetch("/api/payment/verify", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    razorpay_payment_id: response.razorpay_payment_id,
                    razorpay_order_id: response.razorpay_order_id,
                    razorpay_signature: response.razorpay_signature,
                    internalOrderId: data.internalOrderId
                })
            });
            const razorpay = new Razorpay(options);

            razorpay.on('payment.failed', function (response) {
                console.log("Payment Failed:", response.error);
                window.location.href = "/payment-failed";
            });

            razorpay.open();


            let verifyData = await verifyRes.json();

            if (verifyData.status === "success") {
                window.location.href = "/payment-success?orderId=" + verifyData.orderId;
            } else {
                alert("Payment verification failed!");
            }
        }
    };


    const razorpay = new Razorpay(options);
    razorpay.open();
});

