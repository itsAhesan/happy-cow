<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!-- Prefer request 'agent'; else fall back to session 'loggedInAgent' -->
<c:set var="agent" value="${empty requestScope.agent ? sessionScope.loggedInAgent : requestScope.agent}" />
<c:set var="firstName" value="${empty agent.firstName ? 'Agent' : agent.firstName}" />
<c:set var="lastName"  value="${empty agent.lastName ? '' : agent.lastName}" />
<c:set var="initials"  value="${fn:toUpperCase(fn:substring(firstName,0,1))}${empty lastName ? '' : fn:toUpperCase(fn:substring(lastName,0,1))}" />

<!-- Navbar (light) with admin-style logo & text -->
<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom fixed-top shadow-sm" style="min-height:90px; z-index:1030;">
  <div class="container">
    <!-- Brand (logo + gradient text like admin) -->
    <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="${ctx}/">
      <img src="<c:url value='/images/happy-cow-logo.png'/>" alt="HappyCow logo"
           width="70" height="70"
           class="rounded-circle border border-3 border-success shadow-lg me-2"
           style="object-fit:cover;" loading="lazy">
      <span class="fs-2 fw-bold"
            style="background: linear-gradient(90deg, #43e97b, #38f9d7);
                   -webkit-background-clip: text; -webkit-text-fill-color: transparent;
                   font-family: 'Montserrat', 'Segoe UI', Arial, sans-serif;
                   letter-spacing: 2px;">
        HappyCow
      </span>
    </a>

    <!-- Toggler -->
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#hcMainNav"
            aria-controls="hcMainNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <!-- Links + Search + Profile -->
    <div class="collapse navbar-collapse" id="hcMainNav">
      <!-- Left: primary routes -->
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link" data-path="/dashboard" href="${ctx}/agent/profile/dashboard">
            <i class="fa-solid fa-gauge-high me-1"></i>Dashboard
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-path="/agent/orders" href="${ctx}/agent/orders">
            <i class="fa-solid fa-list-check me-1"></i>Orders
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-path="/agent/earnings" href="${ctx}/agent/earnings">
            <i class="fa-solid fa-wallet me-1"></i>Earnings
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-path="/agent/routes" href="${ctx}/agent/routes">
            <i class="fa-solid fa-route me-1"></i>Routes
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" data-path="/agent/support" href="${ctx}/agent/support">
            <i class="fa-solid fa-headset me-1"></i>Support
          </a>
        </li>
      </ul>

      <!-- Center/Right: search -->
      <form class="d-flex me-lg-3 my-2 my-lg-0" role="search" action="${ctx}/search" method="get">
        <input class="form-control" name="q" type="search" placeholder="Search orders, routes, earnings…" aria-label="Search">
      </form>

      <!-- Right: auth/profile -->
      <c:choose>
        <c:when test="${not empty agent}">
          <div class="dropdown">
            <button class="btn btn-outline-success d-flex align-items-center" data-bs-toggle="dropdown" aria-expanded="false">
              <!-- Avatar image (if agentId exists) + initials fallback -->
              <c:choose>
                <c:when test="${not empty agent.agentId}">
                  <img id="navAvatarImg"
                       src="${ctx}/agent/profile/photo/${agent.agentId}"
                       alt="Profile"
                       width="32" height="32"
                       class="rounded-circle border border-success me-2"
                       style="object-fit:cover;"
                       onerror="this.classList.add('d-none'); document.getElementById('navAvatarInitials').classList.remove('d-none');">
                </c:when>
                <c:otherwise>
                  <!-- No agentId -> don't call photo endpoint -->
                </c:otherwise>
              </c:choose>
              <!-- Fallback initials bubble -->
              <span id="navAvatarInitials"
                    class="d-none rounded-circle d-inline-flex justify-content-center align-items-center me-2"
                    style="width:32px;height:32px;background:linear-gradient(135deg,#28a745,#16a085);color:#fff;font-weight:700;">
                ${initials}
              </span>
              <span class="d-none d-sm-inline">${firstName}</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm">
              <li class="px-3 py-2 small text-muted">
                <div class="fw-semibold"><c:out value="${firstName}"/> <c:out value="${lastName}"/></div>
                <div class="text-truncate" style="max-width:220px;"><c:out value="${empty agent.email ? '' : agent.email}"/></div>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="${ctx}/agent/profile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
              <li><a class="dropdown-item" href="${ctx}/agent/security"><i class="fa-solid fa-shield-halved me-2"></i>Security</a></li>
              <li><a class="dropdown-item" href="${ctx}/agent/settings"><i class="fa-solid fa-gear me-2"></i>Settings</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item text-danger" href="${ctx}/agentLogout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
            </ul>
          </div>
        </c:when>
        <c:otherwise>
          <div class="d-flex gap-2">
            <a class="btn btn-outline-success" href="${ctx}/agentLogin"><i class="fa-solid fa-right-to-bracket me-1"></i>Login</a>
            <a class="btn btn-success" href="${ctx}/registerAgent"><i class="fa-solid fa-user-plus me-1"></i>Register</a>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</nav>

<!-- Active link + avatar fallback when no agentId -->
<script>
  (function () {
    // Active link highlighter
    var path = location.pathname || '';
    document.querySelectorAll('a.nav-link[data-path]').forEach(function (a) {
      var p = a.getAttribute('data-path');
      if (p && path.indexOf(p) === 0) {
        a.classList.add('active'); a.setAttribute('aria-current', 'page');
      }
    });

    // If there's no agentId (so no <img>), show initials bubble
    var img = document.getElementById('navAvatarImg');
    var initials = document.getElementById('navAvatarInitials');
    if (!img && initials) {
      initials.classList.remove('d-none');
    } else if (img) {
      // If image loads as zero-sized (rare), trigger error to show initials
      img.addEventListener('load', function () {
        if (img.naturalWidth === 0) { img.dispatchEvent(new Event('error')); }
      });
    }
  })();
</script>
