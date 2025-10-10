<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Profile | HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">

    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root { --brand:#28a745; --ink:#0f172a; }
        body { background:#f7f9fc; font-family:"Poppins",sans-serif; padding-top:var(--nav-offset, 110px); }
        .hero { background:linear-gradient(135deg, rgba(40,167,69,.10), rgba(20,140,100,.10)); border-radius:20px; }
        .avatar-xl {
            width:128px; height:128px; border-radius:50%;
            display:flex; align-items:center; justify-content:center;
            font-weight:700; font-size:2.25rem; color:#fff;
            background:linear-gradient(135deg, var(--brand), #16a085);
            box-shadow:0 12px 26px rgba(40,167,69,.25);
            overflow:hidden; position:relative;
        }
        .avatar-xl img { width:100%; height:100%; object-fit:cover; display:block; }
        .card { border:0; border-radius:16px; box-shadow:0 8px 22px rgba(0,0,0,.05); }
        .badge-soft {
            background:#f0fdf4; color:#166534; border:1px solid #bbf7d0;
            padding:.4rem .6rem; border-radius:999px; font-size:.8rem;
        }
        .pill { border:1px solid #e2e8f0; background:#f8fafc; padding:.35rem .65rem; border-radius:999px; font-size:.85rem; }
        .muted { color:#64748b; }
        .kbd { padding:.15rem .4rem; border:1px solid #cbd5e1; border-bottom-width:2px; border-radius:.35rem; background:#f8fafc; font-family:ui-monospace,Menlo,Consolas,monospace; font-size:.85rem; }
        .label { color:#6b7280; font-size:.9rem; }
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

<!-- Safe name + initials -->
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
            <li class="breadcrumb-item"><a href="${ctx}/agent/profile/dashboard">Dashboard</a></li>
            <li class="breadcrumb-item active" aria-current="page">Profile</li>
        </ol>
    </nav>

    <!-- Flash messages -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-circle-check me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-triangle-exclamation me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Hero -->
    <div class="hero p-4 p-md-5 mb-4">
        <div class="d-flex align-items-start align-items-md-center flex-column flex-md-row gap-4">
            <div class="avatar-xl">
                <c:choose>
                    <c:when test="${not empty agent.agentId}">
                        <img id="heroPhoto"
                             src="${ctx}/agent/profile/photo/${agent.agentId}"
                             alt="Profile photo"
                             onerror="this.classList.add('d-none'); document.getElementById('heroInitials').classList.remove('d-none');" />
                        <div id="heroInitials" class="d-none">${initials}</div>
                    </c:when>
                    <c:otherwise>
                        <div>${initials}</div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="flex-grow-1">
                <h1 class="h3 mb-1">${fullName}</h1>
                <div class="muted mb-2">Welcome to your profile overview</div>
                <span class="pill"><i class="fa-solid fa-user-tie me-1"></i> Agent</span>
                <span class="pill ms-2"><i class="fa-solid fa-shield-halved me-1"></i> Verified</span>
            </div>
            <div class="d-flex gap-2">
                <a href="${ctx}/agent/profile/edit" class="btn btn-success">
                    <i class="fa-solid fa-pen-to-square me-2"></i>Edit Profile
                </a>
                <a href="${ctx}/agent/password" class="btn btn-outline-success">
                    <i class="fa-solid fa-key me-2"></i>Change Password
                </a>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <!-- Left: Details -->
        <div class="col-lg-7">
            <div class="card">
                <div class="card-body p-4">
                    <h2 class="h5 mb-4"><i class="fa-solid fa-circle-info me-2 text-success"></i>Profile Details</h2>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="label">Full Name</div>
                            <div class="fw-semibold"><c:out value="${fullName}"/></div>
                        </div>
                        <div class="col-md-6">
                            <div class="label">Phone</div>
                            <div class="fw-semibold"><c:out value="${empty agent.phoneNumber ? '—' : agent.phoneNumber}"/></div>
                        </div>
                        <div class="col-md-6">
                            <div class="label">Email</div>
                            <div class="fw-semibold"><c:out value="${empty agent.email ? '—' : agent.email}"/></div>
                        </div>
                        <div class="col-12">
                            <div class="label">Address</div>
                            <div class="fw-semibold"><c:out value="${empty agent.address ? 'Not provided' : agent.address}"/></div>
                        </div>
                        <div class="col-12">
                            <div class="label">Milk Type(s)</div>
                            <c:choose>
                                <c:when test="${empty agent.typesOfMilk}">
                                    <div class="fw-semibold">—</div>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="types" value="${fn:split(agent.typesOfMilk, ',')}"/>
                                    <div class="d-flex flex-wrap gap-2">
                                        <c:forEach var="t" items="${types}">
                                            <span class="badge-soft"><i class="fa-solid fa-cow me-1"></i><c:out value="${fn:trim(t)}"/></span>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="alert alert-info mt-4 mb-0">
                        <i class="fa-solid fa-lightbulb me-2"></i>
                        Tip: Press <span class="kbd">Alt</span> + <span class="kbd">/</span> to quickly search actions.
                    </div>
                </div>
            </div>
        </div>

        <!-- Right: Photo + Quick Actions -->
        <div class="col-lg-5">
            <div class="card mb-4">
                <div class="card-body p-4">
                    <h2 class="h6 mb-3 text-uppercase text-muted">Profile Photo</h2>
                    <div class="d-flex align-items-center gap-3">
                        <div class="avatar-xl">
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
                            <div class="mb-2 muted">Update your display picture to keep your profile fresh.</div>
                            <a class="btn btn-outline-success" href="${ctx}/agent/profile/edit">
                                <i class="fa-regular fa-image me-2"></i>Change Photo
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick actions -->
            <div class="card">
                <div class="card-body p-4">
                    <h2 class="h6 mb-3 text-uppercase text-muted">Quick Actions</h2>
                    <div class="d-grid gap-2">
                        <a href="${ctx}/agent/orders" class="btn btn-outline-success">
                            <i class="fa-solid fa-list-check me-2"></i>View Orders
                        </a>
                        <a href="${ctx}/agent/earnings" class="btn btn-outline-success">
                            <i class="fa-solid fa-wallet me-2"></i>Earnings
                        </a>
                        <a href="${ctx}/agent/routes" class="btn btn-outline-success">
                            <i class="fa-solid fa-route me-2"></i>Delivery Routes
                        </a>
                        <a href="${ctx}/agent/support" class="btn btn-outline-success">
                            <i class="fa-solid fa-headset me-2"></i>Support
                        </a>
                        <a href="${ctx}/agent/security" class="btn btn-outline-success">
                            <i class="fa-solid fa-shield-halved me-2"></i>Security Settings
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- CTA row -->
    <div class="mt-5 text-center">
        <a href="${ctx}/agent/profile/dashboard" class="btn btn-success btn-lg">
            <i class="fa-solid fa-gauge-high me-2"></i>Back to Dashboard
        </a>
    </div>
</section>

<jsp:include page="agentFooter.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function () {
    // Adjust body top padding to match current fixed navbar height (+20px)
    function adjustNavOffset() {
        var nav = document.querySelector('.navbar.fixed-top');
        if (!nav) return;
        var h = nav.offsetHeight || 90;
        document.body.style.setProperty('--nav-offset', (h + 20) + 'px');
    }
    window.addEventListener('load', adjustNavOffset);
    window.addEventListener('resize', adjustNavOffset);

    // Ensure initials show if image fails or loads as zero-width
    ['heroPhoto','cardPhoto'].forEach(function(id){
        var img = document.getElementById(id);
        if(!img) return;
        img.addEventListener('load', function(){
            if (img.naturalWidth === 0) { img.dispatchEvent(new Event('error')); }
        });
    });
})();
</script>
</body>
</html>
