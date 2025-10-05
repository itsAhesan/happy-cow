<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-lg py-3 fixed-top" style="min-height:90px; z-index: 1030;">
    <div class="container">
        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="index.jsp">
            <img src="<c:url value='/images/happy-cow-logo.png'/>" alt="HappyCow logo" width="70" height="70"
                 class="rounded-circle border border-3 border-success shadow-lg me-3" style="object-fit:cover;" loading="lazy">
            <span class="fs-2 fw-bold"
                  style="background: linear-gradient(90deg, #43e97b, #38f9d7); -webkit-background-clip: text; -webkit-text-fill-color: transparent;
                  font-family: 'Montserrat', 'Segoe UI', Arial, sans-serif; letter-spacing: 2px; text-shadow: 2px 2px 8px #fff, 0 2px 8px #ccc;">
                HappyCow
            </span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarNav" aria-controls="navbarNav"
                aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto gap-2">
                <li class="nav-item"><a class="nav-link fs-5 fw-bold px-3 py-2 rounded-pill text-success" href="index.jsp">Home</a></li>
                <li class="nav-item"><a class="nav-link fs-5 fw-bold px-3 py-2 rounded-pill text-success" href="customerLogin.jsp">Customer Login</a></li>
                <li class="nav-item"><a class="nav-link fs-5 fw-bold px-3 py-2 rounded-pill text-success" href="agentLogin.jsp">Agent Login</a></li>
                <li class="nav-item"><a class="nav-link active fs-5 fw-bold px-3 py-2 rounded-pill text-success" href="adminLogin">Admin Login</a></li>
            </ul>
        </div>
    </div>
</nav>
