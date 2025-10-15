<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Agent Bank Details - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">

    <style>
        body { font-family: Arial, sans-serif; background-color: #f9fafb; }
        .sidebar { height: 100vh; background-color: #f8f9fa; border-right: 1px solid #ddd; padding-top: 1rem; }
        .sidebar a { display: block; padding: 0.75rem 1rem; margin: 0.2rem 0; color: #333; text-decoration: none; border-radius: 6px; transition: 0.2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #e9ecef; font-weight: bold; }
        .navbar-custom { background-color: #fff; border-bottom: 1px solid #ddd; }
        .navbar-custom .navbar-brand { font-weight: bold; color: #2ea44f; }
        .profile-icon img { width: 35px; height: 35px; border-radius: 50%; object-fit: cover; }
        .card { border-radius: 12px; transition: transform 0.2s ease-in-out; }
        .card:hover { transform: translateY(-3px); }
        .card-header { font-weight: bold; background-color: #f8f9fa; }
        .bank-label { font-weight: 600; color: #555; }
        .btn i { margin-right: 5px; }
    </style>
</head>
<body>

<!-- ✅ Navbar -->
<nav class="navbar navbar-expand-lg navbar-custom px-3">
    <a class="navbar-brand" href="adminDashboard">HappyCow Dairy</a>
    <div class="dropdown ms-auto">
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
            <a href="productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="agentDashboard" class="active"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>
            <a href="productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
            <a href="logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- ✅ Main Content -->
        <div class="col-md-10 py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-success mb-0">
                    <i class="fa-solid fa-building-columns me-2"></i> Agent Bank Details
                </h2>
                <a href="agentDashboard" class="btn btn-outline-success">
                    <i class="fa-solid fa-arrow-left"></i> Back to Agents
                </a>
            </div>

            <!-- ✅ Success Message -->
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                            <i class="fa-solid fa-circle-check me-2"></i>
                            ${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

            <div class="card shadow-sm p-4">
                <c:choose>
                    <c:when test="${not empty bank}">
                        <table class="table table-bordered">
                            <tr>
                                <td class="bank-label">Bank Name</td>
                                <td>${bank.bankName}</td>
                            </tr>
                            <tr>
                                <td class="bank-label">Branch Name</td>
                                <td>${bank.branchName}</td>
                            </tr>
                            <tr>
                                <td class="bank-label">Account Holder</td>
                                <td>${bank.accountHolderName}</td>
                            </tr>
                            <tr>
                                <td class="bank-label">Account Number</td>
                                <td>${bank.accountNumber}</td>
                            </tr>
                            <tr>
                                <td class="bank-label">IFSC</td>
                                <td>${bank.ifsc}</td>
                            </tr>
                            <tr>
                                <td class="bank-label">Account Type</td>
                                <td>${bank.accountType}</td>
                            </tr>
                        </table>

                        <button class="btn btn-warning mt-3" data-bs-toggle="modal" data-bs-target="#editBankModal">
                            <i class="fa-solid fa-pen-to-square"></i> Edit Bank Details
                        </button>
                    </c:when>

                    <c:otherwise>
                        <div class="alert alert-info text-center">
                            <i class="fa-solid fa-circle-info me-2"></i>${bankMsg}
                        </div>

                    </c:otherwise>
                </c:choose>
            </div>
        </div>








    </div>
</div>

<!-- ✅ Edit / Add Bank Modal -->
<div class="modal fade" id="editBankModal" tabindex="-1" aria-labelledby="editBankModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="saveBankDetails" method="post">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title" id="editBankModalLabel">
                        <i class="fa-solid fa-pen-to-square me-2"></i> Update Bank Details
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>

                <div class="modal-body">
                    <input type="hidden" name="agentId" value="${bank.agentId}">

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Bank Name</label>
                            <input type="text" name="bankName" class="form-control" value="${bank.bankName}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Branch Name</label>
                            <input type="text" name="branchName" class="form-control" value="${bank.branchName}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Account Holder Name</label>
                            <input type="text" name="accountHolderName" class="form-control" value="${bank.accountHolderName}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Account Number</label>
                            <input type="text" name="accountNumber" class="form-control" value="${bank.accountNumber}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Confirm Account Number</label>
                            <input type="text" name="confirmAccountNumber" class="form-control" value="${bank.accountNumber}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">IFSC Code</label>
                            <input type="text" name="ifsc" class="form-control" value="${bank.ifsc}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Account Type</label>
                            <select name="accountType" class="form-select" required>
                                <option value="">Select</option>
                                <option value="SAVINGS" ${bank.accountType == 'SAVINGS' ? 'selected' : ''}>Savings</option>
                                <option value="CURRENT" ${bank.accountType == 'CURRENT' ? 'selected' : ''}>Current</option>
                                <option value="SALARY" ${bank.accountType == 'SALARY' ? 'selected' : ''}>Salary</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fa-solid fa-xmark"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-success">
                        <i class="fa-solid fa-floppy-disk"></i> Save Changes
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ✅ Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const alertEl = document.querySelector(".alert-success");
    if (alertEl) {
        setTimeout(() => {
            const alert = new bootstrap.Alert(alertEl);
            alert.close();
        }, 4000); // auto-dismiss in 4 seconds
    }
});
</script>


</body>
</html>
