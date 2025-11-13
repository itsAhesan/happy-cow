<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Agent Dashboard | HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">

    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root { --brand:#28a745; --ink:#0f172a; }
        body { background:#f7f9fc; font-family:"Poppins",sans-serif; padding-top: var(--nav-offset, 110px); }
        .hero { background:linear-gradient(135deg, rgba(40,167,69,.10), rgba(20,140,100,.10)); border-radius:20px; }
        .avatar {
            width:86px; height:86px; border-radius:50%;
            display:flex; align-items:center; justify-content:center;
            font-weight:700; font-size:1.75rem; color:#fff;
            background:linear-gradient(135deg, var(--brand), #16a085);
            box-shadow:0 10px 22px rgba(40,167,69,.25);
            overflow:hidden; position:relative;
        }
        .avatar img { width:100%; height:100%; object-fit:cover; display:block; }
        .card { border:0; border-radius:16px; box-shadow:0 8px 22px rgba(0,0,0,.05); }
        .stat .value { font-size:1.6rem; font-weight:700; }
        .stat .label { color:#6c757d; font-size:.9rem; }
        .list-timeline .list-group-item { border:0; border-left:3px solid #e9ecef; }
        .pill { border:1px solid #e2e8f0; background:#f8fafc; padding:.35rem .65rem; border-radius:999px; font-size:.85rem; }
        .kbd { padding:.15rem .4rem; border:1px solid #cbd5e1; border-bottom-width:2px; border-radius:.35rem; background:#f8fafc; font-family:ui-monospace,Menlo,Consolas,monospace; font-size:.85rem; }
        .shadow-soft { box-shadow:0 6px 18px rgba(0,0,0,.08); }
        .muted { color:#64748b; }
        .stat {
            background: white;
            border-radius: 20px !important;
            padding: 20px;
            transition: 0.2s ease;
        }
        .stat:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }
        .stat .value {
            font-size: 1.7rem;
            font-weight: 700;
            color: #0f172a;
        }
        .stat .label {
            font-size: 0.9rem;
            color: #64748b;
            margin-top: 6px;
        }

    </style>
</head>

<body data-ctx="${pageContext.request.contextPath}">
<jsp:include page="agentNavbar.jsp"/>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- Prefer request 'agent'; else fall back to session 'loggedInAgent' -->
<c:choose>
    <c:when test="${not empty requestScope.agent}">
        <c:set var="agent" value="${requestScope.agent}" />
    </c:when>
    <c:otherwise>
        <c:set var="agent" value="${sessionScope.loggedInAgent}" />
    </c:otherwise>
</c:choose>

<!-- Safe display values -->
<c:set var="firstName" value="${empty agent.firstName ? 'Agent' : agent.firstName}" />
<c:set var="fullName" value="${firstName}" />
<c:if test="${not empty agent.lastName}">
    <c:set var="fullName" value="${fullName} ${agent.lastName}" />
</c:if>
<c:set var="initialFirst" value="${fn:toUpperCase(fn:substring(firstName,0,1))}" />
<c:set var="initialLast"  value="${empty agent.lastName ? '' : fn:toUpperCase(fn:substring(agent.lastName,0,1))}" />
<c:set var="initials"     value="${initialFirst}${initialLast}" />

<section class="container">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item"><a href="${ctx}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${ctx}/agentLogin">Login</a></li>
            <li class="breadcrumb-item active" aria-current="page">Success</li>
        </ol>
    </nav>



    <!-- Main content -->
    <div class="row g-4">
        <!-- Profile -->
        <div class="col-lg-5">
            <div class="card h-100">
                <div class="card-body">
                    <h2 class="h5 mb-4">
                        <i class="fa-solid fa-id-card-clip me-2 text-success"></i>Your Profile
                    </h2>

                    <div class="d-flex align-items-center gap-3 mb-3">
                        <!-- Card Avatar: avoid /photo/null -->
                        <div class="avatar">
                            <c:choose>
                                <c:when test="${not empty agent.agentId}">
                                    <img id="cardPhoto"
                                         src="${ctx}/agent/profile/photo/${agent.agentId}"
                                         alt="Profile photo"
                                         onerror="this.classList.add('d-none'); document.getElementById('cardInitials').classList.remove('d-none');" />
                                    <div id="cardInitials" class="d-none">${initials}</div>
                                </c:when>
                                <c:otherwise>
                                    <div>${initials}</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div>
                            <div class="fw-bold fs-5">${fullName}</div>
                            <div class="text-muted"><c:out value="${empty agent.email ? '—' : agent.email}"/></div>
                            <div class="text-muted"><c:out value="${empty agent.phoneNumber ? '' : agent.phoneNumber}"/></div>
                        </div>
                    </div>

                    <div class="row g-3">
                        <div class="col-12">
                            <div class="p-3 rounded border bg-white h-100">
                                <div class="text-muted small mb-1">Address</div>
                                <div class="fw-semibold">
                                    <c:out value="${empty agent.address ? 'Not provided' : agent.address}"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-12">
                            <div class="p-3 rounded border bg-white h-100">
                                <div class="text-muted small mb-1">Milk Type(s)</div>
                                <div class="fw-semibold">
                                    <c:out value="${empty agent.typesOfMilk ? '—' : agent.typesOfMilk}"/>
                                </div>
                            </div>
                        </div>
                    </div>

                    <hr class="my-4">
                    <div class="d-grid gap-2">
                        <a href="${ctx}/agent/profile" class="btn btn-success">
                            <i class="fa-solid fa-user-gear me-2"></i>View Full Profile
                        </a>
                        <div class="d-grid d-md-flex gap-2">
                            <a href="${ctx}/agent/profile/edit" class="btn btn-outline-success">
                                <i class="fa-solid fa-pen-to-square me-2"></i>Edit Profile
                            </a>
                            <a href="#" class="btn btn-outline-success">
                                <i class="fa-solid fa-shield-halved me-2"></i>Security Settings
                            </a>
                            <a href="#" class="btn btn-outline-success">
                                <i class="fa-solid fa-key me-2"></i>Change Password
                            </a>
                        </div>
                    </div>


                </div>
            </div>
        </div>

        <!-- Right column: quick stats, actions, recent activity placeholders -->
        <div class="col-lg-7">
            <!-- Quick stats -->
         <!-- Quick Stats -->
         <div class="row g-4 mb-4">

             <!-- Milk Collected Today -->
             <div class="col-6 col-md-3">
                 <div class="card stat text-center shadow-soft p-3" style="border-radius: 20px;">
                     <div class="card-body">
                         <div class="value">
                             <c:out value="${todayCollectionLiters}"/> L
                         </div>
                         <div class="label">Milk Collected Today</div>
                     </div>
                 </div>
             </div>

             <!-- Earnings Today -->
             <div class="col-6 col-md-3">
                 <div class="card stat text-center shadow-soft p-3" style="border-radius: 20px;">
                     <div class="card-body">
                         <div class="value">
                             ₹<fmt:formatNumber value="${todayEarnings}" type="number"
                                                minFractionDigits="0" maxFractionDigits="2" />
                         </div>
                         <div class="label">Earnings Today</div>
                     </div>
                 </div>
             </div>

             <!-- Unsettled Amount -->
             <div class="col-6 col-md-3">
                 <div class="card stat text-center shadow-soft p-3" style="border-radius: 20px;">
                     <div class="card-body">
                         <div class="value">
                             ₹<fmt:formatNumber value="${unsettledAmount}" type="number"
                                                minFractionDigits="0" maxFractionDigits="2" />
                         </div>
                         <div class="label">Unsettled Amount</div>
                     </div>
                 </div>
             </div>

             <!-- Payments Received (This Month) -->
             <div class="col-6 col-md-3">
                 <div class="card stat text-center shadow-soft p-3" style="border-radius: 20px;">
                     <div class="card-body">
                         <div class="value">
                             ₹<fmt:formatNumber value="${monthlySettledPayments}" type="number"
                                                minFractionDigits="0" maxFractionDigits="2" />
                         </div>
                         <div class="label">Payments Received (Mo)</div>
                     </div>
                 </div>
             </div>

         </div>




            <!-- Quick actions -->
            <div class="card mb-4">
                <div class="card-body">
                    <h2 class="h6 mb-3 text-uppercase text-muted">Quick Actions</h2>
                    <div class="d-flex flex-wrap gap-2">
                        <a href="${ctx}/agent/profile/orders" class="btn btn-outline-success">
                            <i class="fa-solid fa-list-check me-2"></i>View Orders
                        </a>
                        <a href="${ctx}/agent/profile/earnings" class="btn btn-outline-success">
                            <i class="fa-solid fa-wallet me-2"></i>Earnings
                        </a>
                        <a href="#" class="btn btn-outline-success">
                            <i class="fa-solid fa-route me-2"></i>Delivery Routes
                        </a>
                        <a href="#" class="btn btn-outline-success">
                            <i class="fa-solid fa-headset me-2"></i>Support
                        </a>
                    </div>
                </div>
            </div>

            <!-- Recent activity -->
            <div class="card">
                <div class="card-body">
                    <h2 class="h5 mb-3">
                        <i class="fa-solid fa-clock-rotate-left me-2 text-success"></i>Recent Activity
                    </h2>

                    <c:choose>
                        <c:when test="${not empty requestScope.recentActivities}">
                            <ul class="list-group list-group-flush list-timeline">
                                <c:forEach var="item" items="${requestScope.recentActivities}">
                                    <li class="list-group-item d-flex justify-content-between align-items-start">
                                        <div class="me-3">
                                            <div class="fw-semibold"><c:out value="${item.title}"/></div>
                                            <div class="text-muted small"><c:out value="${item.description}"/></div>
                                        </div>
                                        <span class="badge text-bg-light"><c:out value="${item.time}"/></span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-4">
                                <i class="fa-regular fa-bell-slash fa-2x mb-3"></i>
                                <div>No recent activity yet.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="alert alert-warning mt-4 mb-0">
                <i class="fa-solid fa-triangle-exclamation me-2"></i>
                For your security, never share your OTP.

            </div>
        </div>
    </div>

    <!-- CTA row -->
    <div class="mt-5 text-center">
        <a href="${ctx}/dashboard" class="btn btn-success btn-lg shadow-soft">
            <i class="fa-solid fa-gauge-high me-2"></i>Go to Main Dashboard
        </a>
        <a href="#" class="btn btn-outline-success btn-lg ms-2">
            <i class="fa-solid fa-cart-flatbed me-2"></i>Manage Orders
        </a>
    </div>
</section>

<jsp:include page="agentFooter.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
// Adjust body top padding to match current fixed navbar height (+20px breathing room)
(function () {
    function adjustNavOffset() {
        var nav = document.querySelector('.navbar.fixed-top');
        if (!nav) return;
        var h = nav.offsetHeight || 90;
        document.body.style.setProperty('--nav-offset', (h + 20) + 'px');
    }
    window.addEventListener('load', adjustNavOffset);
    window.addEventListener('resize', adjustNavOffset);

    // Avatar fallback if image fails or loads zero-sized
    ['heroPhoto','cardPhoto'].forEach(function(id){
        var img = document.getElementById(id);
        if(!img) return;
        img.addEventListener('load', function(){
            if (img.naturalWidth === 0) img.dispatchEvent(new Event('error'));
        });
    });
})();
</script>
</body>
</html>
