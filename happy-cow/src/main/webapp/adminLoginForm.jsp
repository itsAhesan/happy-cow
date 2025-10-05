<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
  <%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Login - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- ✅ Include Navbar -->
<jsp:include page="navbar.jsp"/>



<!-- ✅ Admin Login Form -->
<section id="login-section" class="py-5 bg-light" style="min-height: 80vh; display: flex; align-items: center;">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-md-6 col-lg-5">
                <div class="card shadow-lg border-0 rounded-4">
                    <div class="card-body p-5">
                       <h2 class="text-center mb-4 fw-bold text-success">
                           <i class="fa-solid fa-user-shield me-2"></i>Admin Login
                       </h2>

                       <!-- ✅ Show logout success message -->
                       <c:if test="${not empty logoutMessage}">
                           <div class="alert alert-success text-center rounded-3">
                               ${logoutMessage}
                           </div>
                       </c:if>

                       <!-- ✅ Show error message if login fails -->
                       <c:if test="${not empty errorMessage}">
                           <div class="alert alert-danger text-center rounded-3">
                               ${errorMessage}
                           </div>
                       </c:if>



                        <form action="adminLoginProcess" method="post">
                            <div class="mb-3">
                                <label for="email" class="form-label fw-bold">Email Address</label>
                                <input type="email" class="form-control form-control-lg rounded-3" id="email" name="email" placeholder="Enter email" required>
                            </div>
                            <div class="mb-4">
                                <label for="password" class="form-label fw-bold">Password</label>
                                <input type="password" class="form-control form-control-lg rounded-3" id="password" name="password" placeholder="Enter password" required>
                            </div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-success btn-lg rounded-3 shadow">
                                    <i class="fa-solid fa-right-to-bracket me-2"></i>Login
                                </button>
                            </div>
                        </form>

                        <div class="text-center mt-4">
                            <a href="forgetPassword" class="text-decoration-none">Forgot Password?</a>
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
