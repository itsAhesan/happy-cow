<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Agent Dashboard - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        body { font-family: Arial, sans-serif; background-color: #f9fafb; }
        .sidebar { height: 100vh; background-color: #f8f9fa; border-right: 1px solid #ddd; padding-top: 1rem; }
        .sidebar a { display: block; padding: 0.75rem 1rem; margin: 0.2rem 0; color: #333; text-decoration: none; border-radius: 6px; transition: 0.2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #e9ecef; font-weight: bold; }
        .navbar-custom { background-color: #fff; border-bottom: 1px solid #ddd; }
        .navbar-custom .navbar-brand { font-weight: bold; color: #2ea44f; }
        .profile-icon img { width: 35px; height: 35px; border-radius: 50%; object-fit: cover; }
        .card { border-radius: 12px; transition: transform 0.2s ease-in-out; }
        .card:hover { transform: translateY(-5px); }
        .card-header { font-weight: bold; background-color: #f8f9fa; }
        .card-footer { background-color: #ffffff; }
        .pagination .page-item.disabled .page-link { cursor: not-allowed; }
        .search-box input {
            border-radius: 30px;
            padding: 0.6rem 1rem;
            border: 1px solid #ccc;
            transition: 0.2s;
        }
        .search-box input:focus {
            border-color: #2ea44f;
            box-shadow: 0 0 6px rgba(46, 164, 79, 0.4);
        }
        .search-box button {
            border-radius: 30px;
            font-weight: 500;
        }

        /* Button consistency */
        .btn-sm i { margin-right: 4px; }
        .btn-info {
            background-color: #0dcaf0;
            border-color: #0dcaf0;
            color: white;
        }
        .btn-info:hover {
            background-color: #0bb8de;
            border-color: #0bb8de;
        }
    </style>
</head>
<body>

<!-- Navbar -->
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
        <!-- Sidebar -->
        <div class="col-md-2 sidebar">
            <a href="adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="agentDashboard" class="active"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>
            <a href="productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
           <a href="${pageContext.request.contextPath}/payments/history"><i class="fa-solid fa-receipt me-2"></i> Payment History</a>
            <a href="logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- Main Content -->
        <div class="col-md-10 py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-success mb-0">
                    <i class="fa-solid fa-users me-2"></i>Agents Dashboard
                </h2>
                <a href="registerAgent" class="btn btn-success">
                    <i class="fa-solid fa-user-plus me-2"></i> Register New Agent
                </a>
            </div>

            <!-- Search Bar -->
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <form class="row g-2 align-items-center search-box" action="agentDashboard" method="get">
                        <div class="col-sm-10">
                            <input type="text" name="search" class="form-control"
                                   placeholder="Search agents by name, email, or phone..."
                                   value="${fn:escapeXml(param.search)}">
                        </div>
                        <div class="col-sm-2 d-flex">
                            <button class="btn btn-success w-100" type="submit">
                                <i class="fa-solid fa-magnifying-glass me-1"></i> Search
                            </button>
                        </div>
                    </form>
                    <c:if test="${not empty param.search}">
                        <p class="text-muted small mt-2 mb-0">
                            Showing results for "<strong>${param.search}</strong>"
                            <a href="agentDashboard" class="ms-2 text-decoration-none">
                                <i class="fa-solid fa-xmark"></i> Clear
                            </a>
                        </p>
                    </c:if>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <!-- Agent Cards -->
            <div class="row g-4">
                <c:forEach var="agent" items="${agents}">
                    <div class="col-sm-6 col-md-4 col-lg-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header text-primary">
                                <i class="fa-solid fa-user-tie me-1"></i>
                                <a href="agent/${agent.agentId}/product-collections">

                                ${agent.firstName} ${agent.lastName} </a>
                            </div>
                            <div class="card-body">
                                <p><strong>ID:</strong> ${agent.agentId}</p>
                                <p><strong>Email:</strong> ${agent.email}</p>
                                <p><strong>Phone:</strong> ${agent.phoneNumber}</p>
                                <p><strong>Address:</strong> ${agent.address}</p>
                                <p><strong>Milk Type:</strong> ${agent.typesOfMilk}</p>
                            </div>
                            <div class="card-footer d-flex justify-content-between">
                                <a href="editAgent?id=${agent.agentId}" class="btn btn-sm btn-warning">
                                    <i class="fa-solid fa-pen-to-square"></i> Edit
                                </a>
                                <a href="agentBankDetails?id=${agent.agentId}" class="btn btn-sm btn-info">
                                    <i class="fa-solid fa-building-columns"></i> Bank
                                </a>
                                <a href="#" class="btn btn-sm btn-danger"
                                   data-bs-toggle="modal" data-bs-target="#deleteModal"
                                   data-id="${agent.agentId}">
                                   <i class="fa-solid fa-trash"></i> Delete
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${empty agents}">
                <div class="alert alert-info text-center mt-4">
                    <i class="fa-solid fa-circle-info me-2"></i>
                    No agents found. Click <strong>"Register New Agent"</strong> to add one.
                </div>
            </c:if>

            <p class="text-center text-muted mt-3">
                Showing ${(currentPage-1)*pageSize + 1} to
                ${currentPage*pageSize > totalRecords ? totalRecords : currentPage*pageSize}
                of ${totalRecords} agents
            </p>

            <c:if test="${totalPages > 1}">
                <nav aria-label="Agent pagination">
                    <ul class="pagination justify-content-center mt-2">
                        <!-- Pagination logic (unchanged) -->
                        <li class="page-item <c:if test='${currentPage == 1}'>disabled</c:if>'">
                            <a class="page-link" href="agentDashboard?page=1&size=${pageSize}&search=${fn:escapeXml(param.search)}">
                                <i class="fa-solid fa-angle-left"></i><i class="fa-solid fa-angle-left"></i>
                            </a>
                        </li>
                        <li class="page-item <c:if test='${currentPage == 1}'>disabled</c:if>'">
                            <a class="page-link" href="agentDashboard?page=${currentPage - 1}&size=${pageSize}&search=${fn:escapeXml(param.search)}">
                                <i class="fa-solid fa-angle-left"></i> Prev
                            </a>
                        </li>

                        <c:set var="startPage" value="${currentPage - 2}" />
                        <c:set var="endPage" value="${currentPage + 2}" />
                        <c:if test="${startPage < 1}"><c:set var="startPage" value="1"/></c:if>
                        <c:if test="${endPage > totalPages}"><c:set var="endPage" value="${totalPages}"/></c:if>

                        <c:if test="${startPage > 1}">
                            <li class="page-item disabled"><span class="page-link">…</span></li>
                        </c:if>

                        <c:forEach var="i" begin="${startPage}" end="${endPage}">
                            <li class="page-item <c:if test='${i == currentPage}'>active</c:if>'">
                                <a class="page-link" href="agentDashboard?page=${i}&size=${pageSize}&search=${fn:escapeXml(param.search)}">${i}</a>
                            </li>
                        </c:forEach>

                        <c:if test="${endPage < totalPages}">
                            <li class="page-item disabled"><span class="page-link">…</span></li>
                        </c:if>

                        <li class="page-item <c:if test='${currentPage == totalPages}'>disabled</c:if>'">
                            <a class="page-link" href="agentDashboard?page=${currentPage + 1}&size=${pageSize}&search=${fn:escapeXml(param.search)}">
                                Next <i class="fa-solid fa-angle-right"></i>
                            </a>
                        </li>
                        <li class="page-item <c:if test='${currentPage == totalPages}'>disabled</c:if>'">
                            <a class="page-link" href="agentDashboard?page=${totalPages}&size=${pageSize}&search=${fn:escapeXml(param.search)}">
                                <i class="fa-solid fa-angle-right"></i><i class="fa-solid fa-angle-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>

        </div>
    </div>
</div>

<!-- Delete Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content shadow-lg">
      <div class="modal-header bg-danger text-white">
        <h5 class="modal-title"><i class="fa-solid fa-triangle-exclamation me-2"></i>Confirm Deletion</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <p class="mb-0">Are you sure you want to delete this agent? This action cannot be undone.</p>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="fa-solid fa-xmark me-1"></i> Cancel
        </button>
        <a id="confirmDeleteBtn" href="#" class="btn btn-danger">
          <i class="fa-solid fa-trash me-1"></i> Delete
        </a>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener("DOMContentLoaded", function () {
    var deleteModal = document.getElementById("deleteModal");
    var confirmBtn = document.getElementById("confirmDeleteBtn");

    deleteModal.addEventListener("show.bs.modal", function (event) {
        var button = event.relatedTarget;
        var agentId = button.getAttribute("data-id");
        confirmBtn.href = "deleteAgent?id=" + agentId;
    });
});
</script>

</body>
</html>
