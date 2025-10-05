<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Verify OTP - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- ✅ Include Navbar -->
<jsp:include page="navbar.jsp"/>

<!-- ✅ OTP Verification Section -->
<section id="verify-otp-section" class="py-5 bg-light" style="min-height: 80vh; display: flex; align-items: center;">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-md-6 col-lg-5">
                <div class="card shadow-lg border-0 rounded-4">
                    <div class="card-body p-5">
                        <h2 class="text-center mb-4 fw-bold text-success">
                            <i class="fa-solid fa-shield-halved me-2"></i>Verify OTP
                        </h2>

                        <!-- ✅ Info Message -->
                        <div class="alert alert-info text-center rounded-3">
                            We’ve sent a <b>6-digit OTP</b> to your registered email.<br>
                            Please enter it below to reset your password.
                        </div>

                        <!-- ✅ Show success / error message -->
                        <c:if test="${not empty message}">
                            <div class="alert alert-danger text-center rounded-3">
                                ${message}
                            </div>
                        </c:if>

                        <!-- ✅ Verify OTP Form -->
                        <form action="verifyOtp" method="post">
                            <!-- Email hidden field -->
                            <input type="hidden" name="email" value="${email}" />

                            <!-- OTP Input -->
                            <div class="mb-3">
                                <label for="otp" class="form-label fw-bold">Enter OTP</label>
                                <input type="text" class="form-control form-control-lg rounded-3" id="otp" name="otp" placeholder="Enter 6-digit OTP" maxlength="6" required>
                            </div>

                            <!-- New Password -->
                            <div class="mb-3">
                                <label for="newPassword" class="form-label fw-bold">New Password</label>
                                <input type="password" class="form-control form-control-lg rounded-3" id="newPassword" name="newPassword" placeholder="Enter new password" required>
                            </div>

                            <!-- Confirm Password -->
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label fw-bold">Confirm Password</label>
                                <input type="password" class="form-control form-control-lg rounded-3" id="confirmPassword" name="confirmPassword" placeholder="Confirm new password" required>
                            </div>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-success btn-lg rounded-3 shadow">
                                    <i class="fa-solid fa-check me-2"></i>Verify & Reset Password
                                </button>
                            </div>
                        </form>

                        <div class="text-center mt-4">
                            <a href="forgetPassword.jsp" class="text-decoration-none">
                                <i class="fa-solid fa-arrow-left me-1"></i>Back to Forgot Password
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
