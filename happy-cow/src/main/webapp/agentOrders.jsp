<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Agent Orders | HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">

    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root { --brand:#28a745; --ink:#0f172a; }
        body { background:#f7f9fc; font-family:"Poppins",sans-serif; padding-top: var(--nav-offset, 110px); }
        .card { border:0; border-radius:16px; box-shadow:0 8px 22px rgba(0,0,0,.05); }
        .table thead { background:#28a745; color:#fff; }
        .avatar {
            width:90px; height:90px; border-radius:50%;
            background:linear-gradient(135deg, var(--brand), #16a085);
            color:#fff; display:flex; align-items:center; justify-content:center;
            font-weight:600; font-size:1.5rem; overflow:hidden;
        }
        .avatar img {
            width:100%; height:100%; object-fit:cover; border-radius:50%;
        }
        .pill { border:1px solid #e2e8f0; background:#f8fafc; padding:.35rem .65rem; border-radius:999px; font-size:.85rem; }
        .shadow-soft { box-shadow:0 6px 18px rgba(0,0,0,.08); }
    </style>
</head>

<body data-ctx="${pageContext.request.contextPath}">
<jsp:include page="agentNavbar.jsp"/>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- Prefer request 'agent'; else fallback to session -->
<c:set var="agent" value="${empty requestScope.agent ? sessionScope.loggedInAgent : requestScope.agent}" />
<c:set var="firstName" value="${empty agent.firstName ? 'Agent' : agent.firstName}" />
<c:set var="lastName"  value="${empty agent.lastName ? '' : agent.lastName}" />
<c:set var="initials"  value="${fn:toUpperCase(fn:substring(firstName,0,1))}${empty lastName ? '' : fn:toUpperCase(fn:substring(lastName,0,1))}" />

<section class="container py-4">
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item"><a href="${ctx}/dashboard">Dashboard</a></li>
            <li class="breadcrumb-item active" aria-current="page">Orders</li>
        </ol>
    </nav>

    <h1 class="h3 mb-4 text-success"><i class="fa-solid fa-cart-flatbed me-2"></i>Your Orders</h1>

    <!-- Agent Overview Card -->
    <c:if test="${not empty detailsList}">
        <c:set var="first" value="${detailsList[0]}"/>
        <div class="card mb-4">
            <div class="card-body d-flex align-items-center flex-wrap gap-4">

                <!-- Avatar or Profile Image -->
                <div class="avatar shadow-soft">
                    <c:choose>
                        <c:when test="${not empty agent.agentId}">
                            <img src="${ctx}/agent/profile/photo/${agent.agentId}"
                                 alt="Profile"
                                 onerror="this.style.display='none'; document.getElementById('agentInitials').style.display='flex';">
                        </c:when>
                    </c:choose>
                    <div id="agentInitials"
                         style="display:none; width:100%; height:100%; align-items:center; justify-content:center; color:white; font-size:1.5rem; font-weight:600;">
                        ${initials}
                    </div>
                </div>

                <div>
                    <h5 class="mb-1 text-dark">${first.agentName}</h5>
                    <div class="text-muted small"><i class="fa-solid fa-envelope me-1"></i>${first.agentEmail}</div>
                    <div class="text-muted small"><i class="fa-solid fa-phone me-1"></i>${first.agentPhone}</div>
                    <div class="text-muted small"><i class="fa-solid fa-location-dot me-1"></i>${first.agentAddress}</div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Orders Details Table -->
    <div class="card">
        <div class="card-body">
            <h5 class="card-title mb-4"><i class="fa-solid fa-boxes-stacked me-2 text-success"></i>Milk Collection Details</h5>

            <c:choose>
                <c:when test="${not empty detailsList}">
                    <div class="table-responsive">
                        <table class="table table-bordered align-middle text-center">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Type of Milk</th>
                                    <th>Price (₹)</th>
                                    <th>Quantity (L)</th>
                                    <th>Total Amount (₹)</th>
                                    <th>Collected Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="details" items="${detailsList}">
                                    <tr>
                                        <td>${details.productCollectionId}</td>
                                        <td>${details.typeOfMilk}</td>
                                        <td>${details.price}</td>
                                        <td>${details.quantity}</td>
                                        <td class="fw-bold text-success">${details.totalAmount}</td>
                                        <td>${details.collectedAt}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning text-center mb-0">
                        <i class="fa-solid fa-circle-info me-2"></i>No order details found.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Actions -->
    <div class="text-center mt-5">
        <a href="${ctx}/dashboard" class="btn btn-outline-success btn-lg me-2">
            <i class="fa-solid fa-arrow-left me-2"></i>Back to Dashboard
        </a>
        <a href="${ctx}/agent/profile" class="btn btn-success btn-lg">
            <i class="fa-solid fa-user me-2"></i>View Profile
        </a>
    </div>
</section>

<jsp:include page="agentFooter.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function () {
    function adjustNavOffset() {
        var nav = document.querySelector('.navbar.fixed-top');
        if (!nav) return;
        var h = nav.offsetHeight || 90;
        document.body.style.setProperty('--nav-offset', (h + 20) + 'px');
    }
    window.addEventListener('load', adjustNavOffset);
    window.addEventListener('resize', adjustNavOffset);
})();
</script>
</body>
</html>
