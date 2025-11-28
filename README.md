# 🛒 SpringCart – Full-Stack E-Commerce Web Application  
### Built with Spring Boot, Razorpay, Google OAuth2, Thymeleaf & MySQL

SpringCart is a fully functional **production-grade e-commerce application** featuring a modern UI, complete shopping flow, admin product management, secure Google OAuth login, Razorpay payment gateway, order history with PDF invoices, and a clean backend architecture.  
The project is deployed live on Render and uses MySQL hosted on Aiven.

---

## 🚀 Live Demo  
🔗 **https://springcart.onrender.com**

---

## 📌 Overview  
SpringCart provides a smooth shopping experience like a real e-commerce platform (Flipkart/Amazon–style) including:

- Product browsing  
- Add to cart  
- Checkout summary  
- Razorpay payment  
- Order tracking  
- Invoice downloads  
- Admin product management  
- Google OAuth authentication  
- Clean UI with responsive design  

---

## 🎯 Key Features

### 👤 **User Features**
- Login using **Google OAuth 2.0**
- View all products with clean product cards  
- Product Details page  
- Add to Cart with live cart count update  
- Checkout page displaying:
  - Product price  
  - Delivery charge  
  - Discounts  
  - Payable total  
- **Razorpay Payment Gateway (Test Mode)**  
- Automatic order creation on successful payment  
- Payment success & failure redirection  
- View all previous orders under **My Orders**  
- **Order Details** page showing:
  - Order ID  
  - Product details  
  - Amount  
  - Timestamps  
  - Payment status  
- **Download Invoice (PDF)** for each order  

---

### 🛠️ **Admin Features**
Admin is detected using the admin email inside **CustomAuthSuccessHandler**.

- Google OAuth2 login → auto redirect to admin panel  
- Add product (with validations)  
- Edit product (form auto-fills current values)  
- Delete product  
- Product image upload:
  - JPG only  
  - Max 5MB  
  - URL or file upload  
- Admin UI automatically hides:
  - Add to cart  
  - Buy buttons  

⚠ **Admin cannot order or add to cart.**

---

## 💳 Payment Integration (Razorpay)

- Integrated Razorpay checkout UI (Test Mode)  
- Backend validates signature & payment  
- **Success Flow**:
  - Save order to DB  
  - Clear cart  
  - Redirect to success page  
- **Failure Flow**:
  - Keep cart unchanged  
  - Redirect to failure page  

---

## 📄 Invoice Generation  
Each successful order generates a **PDF invoice**, containing:

- Order ID  
- Product details  
- Payment status  
- Total amount  
- Order timestamp  

---

## 🔧 Technologies Used

### **Backend**
- Spring Boot 3  
- Spring MVC  
- Spring Security (OAuth2 Login)  
- Spring Data JPA  
- Hibernate  
- Razorpay Java SDK  

### **Frontend**
- HTML5  
- CSS3  
- JavaScript  
- Thymeleaf  
- Bootstrap  

### **Database**
- **MySQL (Hosted on Aiven Cloud)**  

### **Deployment**
- Render (Web Service)

---

## 📁 Project Structure

