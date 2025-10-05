<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Password Reset Successful - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { padding-top: 80px; } /* keep spacing for navbar */
    </style>
</head>
<body>

<!-- ✅ Navbar -->
<jsp:include page="navbar.jsp"/>

<section class="py-5 bg-light" style="min-height: calc(100vh - 160px); display: flex; align-items: center;">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-md-6 col-lg-5">
                <div class="card shadow-lg border-0 rounded-4 text-center">
                    <div class="card-body p-5">
                        <h2 class="text-success fw-bold mb-3">
                            <i class="fa-solid fa-circle-check me-2"></i>
                            Password Reset Successful
                        </h2>
                        <p class="mb-4">
                            Your password has been updated successfully. You can now log in with your new password.
                        </p>

                        <a href="<c:url value='/adminLogin'/>"
                           class="btn btn-success btn-lg rounded-3 shadow">
                           <i class="fa-solid fa-right-to-bracket me-2"></i> Go to Login
                        </a>

                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- ✅ Footer -->
<jsp:include page="footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
