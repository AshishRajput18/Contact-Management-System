# 📒 Smart Contact Manager - Java Spring Boot Web Application

## 🚀 Description

**Smart Contact Manager** is a secure and interactive web-based application developed using **Spring Boot**, **Thymeleaf**, **Hibernate (JPA)**, and **MySQL**, designed to help users **manage their personal and professional contacts online**. The application allows each registered user to store, update, search, and delete their own contact list in an intuitive dashboard interface.

Additionally, the app integrates with **Razorpay** for secure donation/payment functionality with **SweetAlert2** for interactive UI alerts.

---

## 🌟 Features

### 🧑‍💻 Core Functionalities

* 🔐 **User Authentication** (Registration & Login)
* 🧾 **Session-based Authorization**
* 👥 **User-specific Contact Management**

  * Add, Edit, Delete Contacts
  * Upload Profile Photos
  * Mark contacts as Favorite or Blocked
* 🔍 **Search & Filter Contacts**
* 🖼️ **Image Upload (Multipart)**
* 📊 **Dashboard with Sidebar Navigation**

### 💳 Razorpay Payment Gateway

* One-click donations
* UPI, Card, Wallet, Netbanking support
* SweetAlert2 integration for success/error messages
* Razorpay Order creation with backend verification

---

## 🧠 Tech Stack

| Layer          | Technology                               |
| -------------- | ---------------------------------------- |
| Backend        | Java, Spring Boot, Spring MVC, Hibernate |
| Frontend       | HTML5, CSS3, Bootstrap,Thymleaf, jQuery  |
| Database       | MySQL                                    |
| Authentication | Spring Security                          |
| Payments       | Razorpay Payment Gateway                 |
| Alerts         | SweetAlert2                              |
| Build Tool     | Maven                                    |
| Server         | Apache Tomcat (Embedded)                 |

---

## 📁 Project Structure

```bash
SmartContactManager/
├── src/main/java/
│   ├── com.smart/
│   │   ├── controller/
│   │   ├── dao/
│   │   ├── entities/
│   │   └── SmartContactManagerApplication.java
├── src/main/resources/
│   ├── templates/
│   ├── static/
│   │   ├── js/
│   │   ├── css/
│   ├── application.properties
├── pom.xml
└── README.md
```

---

## 🔐 Razorpay Integration Flow

1. User enters amount and clicks "Pay"
2. AJAX request sends the amount to backend (`/user/create_order`)
3. Server creates Razorpay order using RazorpayClient (Java SDK)
4. Order ID is sent to frontend
5. Razorpay Checkout popup opens with UPI/Card/Wallet options
6. On success/failure, SweetAlert2 shows status

---

## 🗪 Test Credentials (Razorpay Test Mode)

* **Key ID:** `rzp_test_SFcb5wLLTTOiib`
* **Key Secret:** `PjfxyjRWNEYpakEKNoheziWc`
* Use any UPI ID like `success@razorpay` or test card details from [Razorpay Docs](https://razorpay.com/docs/payments/payment-gateway/test-card-upi-details/)

---

## ⚙️ Setup Instructions

### 🔧 Prerequisites

* Java 17+
* Maven 3.x
* MySQL 8+
* Internet connection (for CDN libraries)

### 🛠️ Steps to Run

```bash
# Clone the project
git clone https://github.com/yourusername/SmartContactManager.git
cd SmartContactManager

# Configure MySQL in application.properties

# Build and run the project
mvn spring-boot:run
```

Then open [http://localhost:8080](http://localhost:8080) in your browser.

---

## 📌 Future Enhancements

* ✅ Email Notification on Payment Success
* ✅ Razorpay Webhook Integration
* ✅ CSV/Excel Export of Contacts
* ✅ Admin Panel with User Management
* ✅ Pagination and Search with AJAX
* ✅ Mobile Responsive UI with Tailwind

## 📜 License

This project is licensed under the [MIT License](LICENSE).

## 📬 Contact

> Developed with 💖 by **Ashish Rajput**


## 🔗 Razorpay Docs for Reference

* [Razorpay Developer Guide](https://razorpay.com/docs/)
* [Checkout Integration (Standard)](https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/)
* [Payment Flow & Webhooks](https://razorpay.com/docs/payments/server-integration/java/)
