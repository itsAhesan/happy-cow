<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Forgot Password - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- ✅ Include Navbar -->
<jsp:include page="navbar.jsp"/>

<!-- ✅ Forgot Password Section -->
<section id="forgot-section" class="py-5 bg-light" style="min-height: 80vh; display: flex; align-items: center;">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-md-6 col-lg-5">
                <div class="card shadow-lg border-0 rounded-4">
                    <div class="card-body p-5">
                        <h2 class="text-center mb-4 fw-bold text-primary">
                            <i class="fa-solid fa-key me-2"></i>Forgot Password
                        </h2>

                        <!-- ✅ Info Message -->
                        <div class="alert alert-warning text-center rounded-3">
                            Please enter your registered email address.<br>
                            We will send you an <b>OTP</b> to verify your identity and reset your password.
                        </div>

                        <!-- ✅ Show success / error message -->
                        <c:if test="${not empty message}">
                            <div class="alert alert-info text-center rounded-3">
                                ${message}
                            </div>
                        </c:if>

                        <!-- ✅ Send OTP Form -->
                        <form action="sendOtp" method="post">
                            <div class="mb-3">
                                <label for="email" class="form-label fw-bold">Email Address</label>
                                <input type="email" class="form-control form-control-lg rounded-3" id="email" name="email" placeholder="Enter your registered email" required>
                            </div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary btn-lg rounded-3 shadow">
                                    <i class="fa-solid fa-paper-plane me-2"></i>Send OTP
                                </button>
                            </div>
                        </form>

                        <div class="text-center mt-4">
                            <a href="adminLoginForm.jsp" class="text-decoration-none">
                                <i class="fa-solid fa-arrow-left me-1"></i>Back to Login
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- ✅ Include Footer -->
<jsp:include page="footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
