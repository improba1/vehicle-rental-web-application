# 🚗 Vehicle Rental Platform

A backend of a vehicle rental service built with **Spring Boot**. The platform features role-based access, payment processing, and real-time vehicle tracking.

## ✨ Features

### 🔐 Authentication & Authorization
- **JWT-based authentication** with Spring Security
- **Role-based access control** for Clients and Administrators
- Secure password encryption

### 🚦 Core Functionality
- **Vehicle catalog** with filtering and search capabilities
- **User dashboard** for managing bookings and personal data
- **Admin panel** for vehicle and user management

### 💳 Payment Integration
- **Stripe API integration** for secure payment processing
- **Webhook handling** for payment status updates

### 🗺️ Real-time Features
- **Interactive map** with vehicle locations (Leaflet + OpenStreetMap)
- **Spring Scheduler** for simulating real-time vehicle movement

### 📊 Database Management
- **Multiple database support** via Hibernate ORM and JDBC
- **Data persistence** with PostgreSQL

## 🛠️ Tech Stack

### Backend
- **Java** • **Spring Boot** • **Spring Security**
- **Hibernate** • **JDBC** • **JWT** • **Stripe API**

### Database
- **PostgreSQL** 

### Tools
- **Maven** • **Git** • **Postman**

## 📦 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | User registration |
| `POST` | `/api/auth/login` | User authentication |
| `GET` | `/api/vehicles/available` | Get available vehicles |
| `POST` | `/api/rentals/rent` | Rent vehicle |
| `POST` | `/api/rentals/return` | Return vehicle |
| `DELETE` | `/api/admin/user/delete/{id}` | Delete user |
| `POST` | `/api/admin/user/grant/{id}` | Grant user with new role |
| `POST` | `/api/admin/user/revoke/{id}` | Revoke role from user |
| `POST` | `/api/admin/add/{role}` | Add new role |
| `POST` | `/api/rentals/return` | Return vehicle |

