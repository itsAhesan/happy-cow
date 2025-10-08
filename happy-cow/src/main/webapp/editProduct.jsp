<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Product - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- ✅ Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- ✅ FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9fafb;
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
        .card {
            border-radius: 12px;
        }
        .invalid-feedback {
            display: block;
        }
    </style>
</head>
<body>

<!-- ✅ Navbar -->
<nav class="navbar navbar-expand-lg navbar-custom px-3">
    <a class="navbar-brand" href="adminDashboard">HappyCow Dairy</a>

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
            <a href="adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="#" class="active"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>
            <a href="productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
                        <a href="productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>

            <a href="logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- ✅ Main Content -->
        <div class="col-md-10 py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-success mb-0">
                    <i class="fa-solid fa-pen-to-square me-2"></i> Edit Product
                </h2>
                <a href="productDashboard" class="btn btn-secondary">
                    <i class="fa-solid fa-arrow-left me-2"></i> Back to Dashboard
                </a>
            </div>

            <!-- ✅ Edit Product Form -->
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <form id="editProductForm" action="${pageContext.request.contextPath}/updateProduct" method="post">
                        <input type="hidden" name="productId" value="${product.productId}"/>

                        <div class="mb-3">
                            <label class="form-label">Product Name</label>
                            <input type="text" name="productName" class="form-control" value="${product.productName}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Price (₹)</label>
                            <input type="number" step="0.01" name="productPrice" class="form-control" value="${product.productPrice}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Type</label>
                            <div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="productType" id="buyOption" value="Buy"
                                           <c:if test="${product.productType == 'Buy'}">checked</c:if>>
                                    <label class="form-check-label" for="buyOption">
                                        <i class="fa-solid fa-cart-arrow-down me-1 text-success"></i> Buy
                                    </label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="productType" id="sellOption" value="Sell"
                                           <c:if test="${product.productType == 'Sell'}">checked</c:if>>
                                    <label class="form-check-label" for="sellOption">
                                        <i class="fa-solid fa-store me-1 text-primary"></i> Sell
                                    </label>
                                </div>
                            </div>
                        </div>





                        <div class="text-end">
                            <button type="submit" class="btn btn-success">
                                <i class="fa-solid fa-floppy-disk me-2"></i> Save Changes
                            </button>
                        </div>
                    </form>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- ✅ jQuery + Validation Plugin -->
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.5/dist/jquery.validate.min.js"></script>

<script>
$(function () {
    $("#editProductForm").validate({
        rules: {
            productName: {
                required: true,
                minlength: 2,
                maxlength: 100
            },
            productPrice: {
                required: true,
                number: true,
                min: 1
            }
        },
        messages: {
            productName: {
                required: "Product name is required",
                minlength: "Min 2 characters required",
                maxlength: "Max 100 characters allowed"
            },
            productPrice: {
                required: "Product price is required",
                number: "Enter a valid number",
                min: "Price must be greater than 0"
            }
        },
        errorElement: "div",
        errorClass: "invalid-feedback",
        highlight: function (element) {
            $(element).addClass("is-invalid").removeClass("is-valid");
        },
        unhighlight: function (element) {
            $(element).removeClass("is-invalid").addClass("is-valid");
        },
        errorPlacement: function (error, element) {
            error.insertAfter(element);
        }
    });
});
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
