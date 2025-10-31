<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Product Collections · Agent ${agentId} · ${windowLabel}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet"/>

    <style>
        body { font-family: Arial, sans-serif; }
        .sidebar {
            height: 100vh; background-color: #f8f9fa; border-right: 1px solid #ddd;
            padding-top: 1rem; position: sticky; top: 0;
        }
        .sidebar a { display: block; padding: .75rem 1rem; margin: .2rem 0; color: #333; text-decoration: none; border-radius: 6px; transition: .2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #e9ecef; font-weight: bold; }
        .main-content { padding: 2rem; }
        .navbar-custom { background-color: #fff; border-bottom: 1px solid #ddd; }
        .navbar-custom .navbar-brand { font-weight: bold; color: #2ea44f; }
        .search-box { max-width: 400px; width: 100%; }
        .dropdown-menu-notifs { width: 340px; }
        #notificationScroll { max-height: 320px; overflow-y: auto; }
        .notif-item { position: relative; }
        .notif-item .stretched-link { position: absolute; inset: 0; }

        /* small stat cards */
        .stat { font-size: 1.35rem; font-weight: 700; }
        .muted { color: #6c757d; }
        .badge-soft { background: #eef2ff; color: #3b5bdb; }
        .badge-paid { background: #e6fcf5; color: #099268; }
        .badge-due { background: #fff5f5; color: #c92a2a; }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-custom px-3">
    <a class="navbar-brand" href="#">HappyCow Dairy</a>

    <div class="mx-auto search-box d-none d-md-block">
        <input type="text" class="form-control form-control-sm" placeholder="Search...">
    </div>

    <div class="d-flex align-items-center">
        <!-- Notifications -->
        <div class="dropdown me-3">
            <a href="#" class="text-decoration-none position-relative" id="notificationDropdown"
               data-bs-toggle="dropdown" aria-expanded="false" aria-label="Notifications">
                <i class="fa-solid fa-bell fa-lg text-secondary"></i>
                <span id="notificationCount"
                      class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger
                      ${empty sessionScope.BELL_ITEMS ? ' d-none' : ''}">
                    <c:out value="${empty sessionScope.BELL_ITEMS ? 0 : fn:length(sessionScope.BELL_ITEMS)}"/>
                </span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow dropdown-menu-notifs"
                aria-labelledby="notificationDropdown">
                <li class="dropdown-header fw-bold d-flex justify-content-between align-items-center">
                    <span>Notifications</span>
                    <a class="small text-decoration-none" href="#" id="clearNotifsBtn">Clear</a>
                </li>
                <li><hr class="dropdown-divider"></li>

                <c:choose>
                    <c:when test="${empty sessionScope.BELL_ITEMS}">
                        <li id="emptyState" class="px-3 py-2 text-muted small">No notifications</li>
                        <li class="p-0"><ul id="notificationScroll" class="list-group list-group-flush"></ul></li>
                    </c:when>
                    <c:otherwise>
                        <li class="p-0">
                            <ul id="notificationScroll" class="list-group list-group-flush">
                                <c:forEach var="n" items="${sessionScope.BELL_ITEMS}">
                                    <li class="list-group-item notif-item">
                                        <div class="d-flex flex-column pe-4">
                                            <div>
                                                <i class="fa-solid fa-circle-dot text-success me-2"></i>
                                                <strong><c:out value="${n.agentName}"/></strong>
                                                — <c:out value="${n.message}"/>
                                            </div>
                                            <small class="text-muted">
                                                Email: <c:out value="${n.email}"/> |
                                                Phone: <c:out value="${n.phoneNumber}"/>
                                            </small>
                                            <a class="stretched-link"
                                               href="<c:url value='${empty n.link ? "/agent/"+n.agentId+"/product-collections" : n.link}'/>"></a>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>

        <!-- Profile -->
        <div class="dropdown">
            <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle"
               id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <c:choose>
                    <c:when test="${not empty loggedInAdmin.profilePicture}">
                        <img src="data:${loggedInAdmin.profilePictureContentType};base64,${loggedInAdmin.profilePictureBase64}"
                             alt="Profile" class="rounded-circle" width="35" height="35">
                    </c:when>
                    <c:otherwise>
                        <img src="<%=ctx%>/images/default-profile.png" alt="Profile" class="rounded-circle" width="35" height="35">
                    </c:otherwise>
                </c:choose>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="profileDropdown">
                <li class="dropdown-header text-center">
                    <strong>${loggedInAdmin.adminName}</strong><br>
                    <small class="text-muted">${loggedInAdmin.emailId}</small>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="<%=ctx%>/adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i>Dashboard</a></li>
                <li><a class="dropdown-item" href="<%=ctx%>/adminProfile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
                <li><a class="dropdown-item text-danger" href="<%=ctx%>/logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
            </ul>
        </div>
    </div>
</nav>

<!-- Layout -->
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-2 sidebar">
            <a href="<%=ctx%>/adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="<%=ctx%>/productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="<%=ctx%>/agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>
            <a href="<%=ctx%>/productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="<%=ctx%>/productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
            <a href="<%=ctx%>/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- Main -->
        <div class="col-md-10 main-content">

            <!-- Header: Agent details -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <a class="btn btn-link p-0" href="<%=ctx%>/adminDashboard">&larr; Back to Dashboard</a>
                    <h3 class="mb-1">Product Collections</h3>
                    <div class="text-muted">
                        <span class="me-3">
                            <strong>Agent:</strong>
                            <c:choose>
                                <c:when test="${not empty agent}">
                                    <c:out value="${agent.firstName}"/> <c:out value="${agent.lastName}"/>
                                </c:when>
                                <c:otherwise>#<c:out value="${agentId}"/></c:otherwise>
                            </c:choose>
                        </span>
                        <c:if test="${not empty agent}">
                            <span class="me-3"><strong>Email:</strong> <c:out value="${agent.email}"/></span>
                            <span class="me-3"><strong>Phone:</strong> <c:out value="${agent.phoneNumber}"/></span>
                        </c:if>
                    </div>
                </div>
                <div class="text-end">
                    <span class="badge rounded-pill badge-soft">${windowLabel}</span><br/>
                    <small class="muted">Window: <c:out value="${dueStart}"/> to <c:out value="${dueEnd}"/></small>
                </div>
            </div>

            <!-- Lifetime Summary -->
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="card shadow-sm border-0">
                        <div class="card-body">
                            <div class="muted">Lifetime Collections</div>
                            <div class="stat">₹ <fmt:formatNumber value="${lifetimeTotal}" minFractionDigits="2"/></div>
                            <div class="small">Total Qty: <fmt:formatNumber value="${lifetimeQty}" maxFractionDigits="2"/></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm border-0">
                        <div class="card-body">
                            <div class="muted">Lifetime Paid</div>
                            <div class="stat text-success">₹ <fmt:formatNumber value="${lifetimePaid}" minFractionDigits="2"/></div>
                            <div class="small">All settled windows</div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm border-0">
                        <div class="card-body">
                            <div class="muted">Pending Overall</div>
                            <div class="stat text-danger">₹ <fmt:formatNumber value="${lifetimePending}" minFractionDigits="2"/></div>
                            <div class="small">Will be settled in future windows</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Current Window Summary + Payment CTA -->
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="card shadow-sm border-0">
                        <div class="card-body">
                            <div class="muted">Current Window Amount</div>
                            <div class="stat">₹ <fmt:formatNumber value="${winTotal}" minFractionDigits="2"/></div>
                            <div class="small">Quantity: <fmt:formatNumber value="${winQty}" maxFractionDigits="2"/></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-5">
                    <div class="card shadow-sm border-0">
                        <div class="card-body">
                            <div class="muted">Status</div>
                            <c:choose>
                                <c:when test="${not empty payment && payment.status == 'SUCCESS'}">
                                    <div class="stat text-success">PAID</div>
                                    <div class="small">Txn: <c:out value="${payment.paymentRef}"/></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="stat text-danger">DUE</div>
                                    <div class="small">To be paid for <strong>${windowLabel}</strong></div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 d-flex align-items-stretch">
                    <div class="card shadow-sm border-0 w-100">
                        <div class="card-body d-flex flex-column justify-content-between">
                            <div class="muted mb-2">Actions</div>
                            <c:choose>
                                <c:when test="${not empty payment && payment.status == 'SUCCESS'}">
                                    <button class="btn btn-outline-success w-100" disabled>
                                        <i class="fa-solid fa-check me-1"></i> Payment Completed
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <!-- Adjust action URL to your controller handling payment -->
                                    <form method="post" action="<%=ctx%>/pay/agent-window" class="d-grid gap-2">
                                        <input type="hidden" name="agentId" value="${agentId}"/>
                                        <input type="hidden" name="from" value="${dueStart}"/>
                                        <input type="hidden" name="to" value="${dueEnd}"/>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fa-solid fa-indian-rupee-sign me-1"></i> Settle Payment
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Next Half Preview -->
            <div class="alert alert-secondary d-flex justify-content-between align-items-center">
                <div>
                    <strong>Next half:</strong> ${nextWindowLabel}
                    <span class="ms-2">Collections: <strong>${nextWindowCount}</strong></span>
                    <span class="ms-2">Est. Amount: <strong>₹ <fmt:formatNumber value="${nextWindowTotal}" minFractionDigits="2"/></strong></span>
                </div>
                <a class="btn btn-sm btn-outline-secondary"
                   href="<%=ctx%>/agent/${agentId}/product-collections?from=${nextWindowStart}&to=${nextWindowEnd}">
                    Preview Window
                </a>
            </div>

            <!-- Table -->
            <c:choose>
                <c:when test="${empty rowsWindow}">
                    <div class="alert alert-info">
                        No product collections found for this window (<c:out value="${windowLabel}"/>).
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-sm table-striped align-middle">
                                    <thead class="table-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Collected Date</th>
                                        <th>Type of Milk</th>
                                        <th class="text-end">Quantity</th>
                                        <th class="text-end">Price</th>
                                        <th class="text-end">Total Amount</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="row" items="${rowsWindow}" varStatus="st">
                                        <tr>
                                            <td><c:out value="${st.index + 1}"/></td>
                                            <td><c:out value="${row.collectedAt}"/></td>
                                            <td><c:out value="${row.typeOfMilk}"/></td>
                                            <td class="text-end"><fmt:formatNumber value="${row.quantity}" maxFractionDigits="2"/></td>
                                            <td class="text-end">₹ <fmt:formatNumber value="${row.price}" minFractionDigits="2"/></td>
                                            <td class="text-end">₹ <fmt:formatNumber value="${row.totalAmount}" minFractionDigits="2"/></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                    <tfoot>
                                    <tr>
                                        <th colspan="3" class="text-end">Totals for ${windowLabel}:</th>
                                        <th class="text-end"><fmt:formatNumber value="${winQty}" maxFractionDigits="2"/></th>
                                        <th class="text-end">—</th>
                                        <th class="text-end">₹ <fmt:formatNumber value="${winTotal}" minFractionDigits="2"/></th>
                                    </tr>
                                    </tfoot>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>

<!-- JS libs -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2/dist/stomp.min.js"></script>

<script>
    let stompClient = null;
    const ctx = "<%=ctx%>";

    function connectWebSocket() {
        const socket = new SockJS(ctx + "/ws");
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({}, function () {
            stompClient.subscribe('/user/queue/notifications', function (message) {
                try {
                    const payload = JSON.parse(message.body);
                    if (Array.isArray(payload)) payload.forEach(p => renderNotificationItem(p, true));
                    else renderNotificationItem(payload, true);
                } catch (e) { /* ignore */ }
            });
        });
    }

    function renderNotificationItem(item, bump) {
        const list = document.getElementById("notificationScroll");
        const count = document.getElementById("notificationCount");
        const empty = document.getElementById("emptyState");
        if (empty) empty.classList.add("d-none");

        const agentName = safe(item.agentName) || "Agent";
        const message = safe(item.message) || "";
        const email = safe(item.email) || "-";
        const phone = safe(item.phoneNumber) || "-";
        const link = item.link ? (item.link.startsWith("/") ? (ctx + item.link) : item.link)
            : (ctx + "/agent/" + encodeURIComponent(item.agentId) + "/product-collections");

        const li = document.createElement("li");
        li.className = "list-group-item notif-item";
        li.innerHTML = "<div class='d-flex flex-column pe-4'>"
            + "<div><i class='fa-solid fa-circle-dot text-success me-2'></i><strong>"
            + agentName + "</strong> — " + message + "</div>"
            + "<small class='text-muted'>Email: " + email + " | Phone: " + phone + "</small>"
            + "<a class='stretched-link' href='" + link + "'></a>"
            + "</div>";

        if (list.firstChild) list.insertBefore(li, list.firstChild); else list.appendChild(li);

        if (bump) {
            const cur = parseInt(count.textContent) || 0;
            count.textContent = cur + 1;
            count.classList.remove("d-none");
        }
    }

    function safe(s) {
        if (s == null) return "";
        return ("" + s).replace(/&/g, "&amp;").replace(/</g, "&lt;")
            .replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
    }

    function loadInitialNotifications() {
        const count = document.getElementById("notificationCount");
        const current = parseInt(count.textContent) || 0;
        if (current > 0) return;

        fetch(ctx + "/api/notifications", { credentials: "same-origin" })
            .then(r => r.ok ? r.json() : Promise.reject(r))
            .then(data => {
                const items = data.items || [];
                if (items.length === 0) {
                    const empty = document.getElementById("emptyState");
                    if (empty) empty.classList.remove("d-none");
                    count.textContent = "0";
                    count.classList.add("d-none");
                    return;
                }
                items.forEach(i => renderNotificationItem(i, false));
                count.textContent = items.length;
                count.classList.remove("d-none");
            })
            .catch(() => {});
    }

    function wireClearButton() {
        const btn = document.getElementById("clearNotifsBtn");
        if (!btn) return;
        const count = document.getElementById("notificationCount");
        const list = document.getElementById("notificationScroll");
        const empty = document.getElementById("emptyState");
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            if (list) list.innerHTML = "";
            count.textContent = "0";
            count.classList.add("d-none");
            if (empty) empty.classList.remove("d-none");
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        connectWebSocket();
        loadInitialNotifications();
        wireClearButton();
    });
</script>
</body>
</html>
