<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .sidebar {
            height: 100vh;
            background-color: #f8f9fa;
            border-right: 1px solid #ddd;
            padding-top: 1rem;
        }
        .sidebar a {
            display: block;
            padding: 0.75rem 1rem;
            margin: 0.2rem 0;
            color: #333;
            text-decoration: none;
            border-radius: 6px;
            transition: 0.2s;
        }
        .sidebar a:hover, .sidebar a.active {
            background-color: #e9ecef;
            font-weight: bold;
        }
        .main-content {
            padding: 2rem;
        }
        .navbar-custom {
            background-color: #fff;
            border-bottom: 1px solid #ddd;
        }
        .navbar-custom .navbar-brand {
            font-weight: bold;
            color: #2ea44f;
        }
        .search-box {
            max-width: 400px;
            width: 100%;
        }
        .profile-icon img {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            object-fit: cover;
        }
    </style>
</head>
<body>


<!-- ✅ Navbar -->
<nav class="navbar navbar-expand-lg navbar-custom px-3">
    <a class="navbar-brand" href="#">HappyCow Dairy</a>

    <!-- Center Search -->
    <div class="mx-auto search-box">
        <input type="text" class="form-control form-control-sm" placeholder="Search...">
    </div>

    <!-- ✅ Profile Dropdown -->
    <div class="dropdown">
        <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle"
           id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
            <c:choose>
                <c:when test="${not empty loggedInAdmin.profilePicture}">
                    <img src="data:${loggedInAdmin.profilePictureContentType};base64,${loggedInAdmin.profilePictureBase64}"
                         alt="Profile" class="rounded-circle" width="35" height="35">
                </c:when>
                <c:otherwise>
                    <img src="images/default-profile.png" alt="Profile" class="rounded-circle" width="35" height="35">
                </c:otherwise>
            </c:choose>
        </a>
        <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="profileDropdown">
            <li class="dropdown-header text-center">
                <strong>${loggedInAdmin.adminName}</strong><br>
                <small class="text-muted">${loggedInAdmin.emailId}</small>
            </li>
            <li><hr class="dropdown-divider"></li>

            <li><a class="dropdown-item" href="adminProfile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
            <li><a class="dropdown-item" href="#"><i class="fa-solid fa-gear me-2"></i>Settings</a></li>
            <li><a class="dropdown-item text-danger" href="logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
        </ul>
    </div>
</nav>


<div class="container-fluid">
    <div class="row">
        <!-- ✅ Sidebar -->
        <div class="col-md-2 sidebar">
            <a href="#" class="active"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>

            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>

            <a href="agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a> <!-- ✅ New Agents tab -->
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>

            <a href="productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
            <a href="logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- ✅ Main Content -->
        <div class="col-md-10 main-content">
            <h2 class="fw-bold mb-4">Welcome, <c:out value="${loggedInAdmin.adminName}"/></h2>
            <p class="text-muted">Here’s an overview of your dairy operations.</p>

            <!-- Example Cards -->
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card shadow-sm rounded-4 border-0">
                        <div class="card-body">
                            <h5 class="card-title">Total Products</h5>
                            <p class="display-6 fw-bold text-success">120</p>
                            <p class="text-muted small">Dairy products currently listed</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm rounded-4 border-0">
                        <div class="card-body">
                            <h5 class="card-title">Pending Orders</h5>
                            <p class="display-6 fw-bold text-primary">45</p>
                            <p class="text-muted small">Orders waiting for processing</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm rounded-4 border-0">
                        <div class="card-body">
                            <h5 class="card-title">Customers</h5>
                            <p class="display-6 fw-bold text-warning">350</p>
                            <p class="text-muted small">Registered customers</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ✅ Agents Section (Placeholder for DB Data) -->
            <div id="agentsSection" class="mt-5">
                <h3 class="fw-bold">Agents</h3>
                <p class="text-muted">List of agents will be displayed here.</p>

                <!-- Sample Static Table (Replace later with DB data) -->
                <table class="table table-bordered table-hover mt-3">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Region</th>
                        <th>Contact</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>1</td>
                        <td>Ravi Kumar</td>
                        <td>Bangalore</td>
                        <td>+91 9876543210</td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>Anita Sharma</td>
                        <td>Mysore</td>
                        <td>+91 9123456780</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
