//first request-to server to create the order
console.log("This is a Script File");

// Sidebar toggle function
const toggleSidebar = () => {
    // Check if sidebar is visible
    if ($(".sidebar").is(":visible")) {
        // Hide sidebar
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        // Show sidebar
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

// Start payment
const paymentStart = () => {
    let amount = $("#payment_field").val();

    if (amount === "" || amount == null) {
        alert("Amount is required!");
        return;
    }

    // AJAX request to create order
    $.ajax({
        url: '/user/create_order',
        data: JSON.stringify({ amount: amount, info: 'order_request' }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function (response) {
            console.log("✅ Order created:", response);

            var options = {
                key: "rzp_test_SFcb5wLLTTOiib",
                amount: response.amount * 100,
                currency: "INR",
                name: "Smart Contact Manager",
                description: "Donation",
                image: "/img/contacts.png",
                order_id: response.id, // Razorpay Order ID from backend

                handler: function (rzpResponse) {
                    // Send data to backend to update status
                    updatePaymentOnServer(
                        rzpResponse.razorpay_payment_id,
                        rzpResponse.razorpay_order_id,
                        "paid"
                    );

                    console.log("Payment ID:", rzpResponse.razorpay_payment_id);
                    console.log("Order ID:", rzpResponse.razorpay_order_id);
                    console.log("Signature:", rzpResponse.razorpay_signature);
                },

                prefill: {
                    name: "Smart User",
                    email: "smart@contact.com",
                    contact: ""
                },

                notes: {
                    address: "Smart Contact Office"
                },

                theme: {
                    color: "#3399cc"
                },

                // ✅ Optional: restrict to UPI only
                method: {
                    upi: true,
                    card: true,
                    netbanking: true,
                    wallet: true
                }
            };

            let rzp = new Razorpay(options);
            rzp.on('payment.failed', function (response) {
                alert("❌ Payment Failed: " + response.error.description);
                console.log("Error Details:", response.error);
            });

            rzp.open();
        },
        error: function (error) {
            console.log("❌ Error in creating order:", error);
            alert("Something went wrong");
        }
    });
};

// Corrected function definition (without semicolon and with real logic)
function updatePaymentOnServer(payment_id, order_id, status) {
    $.ajax({
        url: '/user/update_order',
        data: JSON.stringify({
            payment_id: payment_id,
            order_id: order_id,
            status: status
        }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function (response) {
            alert("✅ Payment successfully saved to server!");
        },
        error: function (error) {
            alert("❌ Payment successful, but server update failed!");
            console.log(error);
        }
    });
}
