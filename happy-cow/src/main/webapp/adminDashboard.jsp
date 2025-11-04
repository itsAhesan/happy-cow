<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet"/>

    <style>
        body { font-family: Arial, sans-serif; }
        .sidebar {
            height: 100vh;
            background-color: #f8f9fa;
            border-right: 1px solid #ddd;
            padding-top: 1rem;
            position: sticky; top: 0;
        }
        .sidebar a {
            display: block; padding: 0.75rem 1rem; margin: 0.2rem 0;
            color: #333; text-decoration: none; border-radius: 6px; transition: 0.2s;
        }
        .sidebar a:hover, .sidebar a.active { background-color: #e9ecef; font-weight: bold; }
        .main-content { padding: 2rem; }
        .navbar-custom { background-color: #fff; border-bottom: 1px solid #ddd; }
        .navbar-custom .navbar-brand { font-weight: bold; color: #2ea44f; }
        .search-box { max-width: 400px; width: 100%; }
        .dropdown-menu-notifs { width: 340px; }
        #notificationScroll { max-height: 320px; overflow-y: auto; }
        .notif-item { position: relative; }
        .notif-item small { display: block; }
        .stretched-link { position: absolute; inset: 0; }
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

        <!-- Notification Bell -->
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

            <!-- Dropdown with SSR of notifications -->
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
                        <li class="p-0">
                            <ul id="notificationScroll" class="list-group list-group-flush"></ul>
                        </li>
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
                                               href="<c:url value='${empty n.link ? "/agent/"+n.agentId+"/product-collections?window=13-15" : n.link}'/>">
                                            </a>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>

        <!-- Profile Dropdown -->
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
                <li><a class="dropdown-item" href="<%=ctx%>/adminProfile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
                <li><a class="dropdown-item" href="#"><i class="fa-solid fa-gear me-2"></i>Settings</a></li>
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
            <a href="#" class="active"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="<%=ctx%>/productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="<%=ctx%>/agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>
            <a href="<%=ctx%>/productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="<%=ctx%>/productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
           <a href="${pageContext.request.contextPath}/payments/history"><i class="fa-solid fa-receipt me-2"></i> Payment History</a>

            <a href="<%=ctx%>/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- Main -->
        <div class="col-md-10 main-content">
            <h2 class="fw-bold mb-4">Welcome, <c:out value="${loggedInAdmin.adminName}"/></h2>
            <p class="text-muted">Here’s an overview of your dairy operations.</p>

            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card shadow-sm rounded-4 border-0">
                        <div class="card-body">
                            <h5 class="card-title">Total Products</h5>
                            <p class="display-6 fw-bold text-success">120</p>
                            <p class="text-muted small">Dairy products currently listed</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm rounded-4 border-0">
                        <div class="card-body">
                            <h5 class="card-title">Pending Orders</h5>
                            <p class="display-6 fw-bold text-primary">45</p>
                            <p class="text-muted small">Orders waiting for processing</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm rounded-4 border-0">
                        <div class="card-body">
                            <h5 class="card-title">Customers</h5>
                            <p class="display-6 fw-bold text-warning">350</p>
                            <p class="text-muted small">Registered customers</p>
                        </div>
                    </div>
                </div>
            </div>

            <div id="agentsSection" class="mt-5">
                <h3 class="fw-bold">Agents</h3>
                <p class="text-muted">List of agents will be displayed here.</p>
                <table class="table table-bordered table-hover mt-3">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Region</th>
                        <th>Contact</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>1</td>
                        <td>Ravi Kumar</td>
                        <td>Bangalore</td>
                        <td>+91 9876543210</td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>Anita Sharma</td>
                        <td>Mysore</td>
                        <td>+91 9123456780</td>
                    </tr>
                    </tbody>
                </table>
            </div>

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

    // --- WebSocket ---
    function connectWebSocket() {
        const socket = new SockJS(ctx + "/ws");
        stompClient = Stomp.over(socket);
        // Optional: silence STOMP debug logs
        stompClient.debug = null;

        stompClient.connect({}, function (frame) {
            console.log("✅ WS Connected:", frame);

            // Subscribe to per-user queue
            stompClient.subscribe('/user/queue/notifications', function (message) {
                try {
                    const payload = JSON.parse(message.body);
                    if (Array.isArray(payload)) {
                        payload.forEach(item => renderNotificationItem(item, true));
                    } else {
                        renderNotificationItem(payload, true);
                    }
                } catch (e) {
                    console.warn("Non-JSON notification payload:", message.body);
                }
            });
        }, function (err) {
            console.error("WS error:", err);
        });
    }

    // --- Rendering helpers ---
    function renderNotificationItem(item, bumpCount) {
        const list = document.getElementById("notificationScroll");
        const countEl = document.getElementById("notificationCount");
        const empty = document.getElementById("emptyState");
        if (empty) empty.classList.add("d-none");

        const agentName = safe(item.agentName) || "Agent";
        const message = safe(item.message) || "";
        const email = safe(item.email) || "-";
        const phone = safe(item.phoneNumber) || "-";
        const link = item.link
            ? (item.link.startsWith("/") ? (ctx + item.link) : item.link)
            : (ctx + "/agent/" + encodeURIComponent(item.agentId) + "/product-collections?window=13-15");

        const li = document.createElement("li");
        li.className = "list-group-item notif-item";
        li.innerHTML =
            "<div class='d-flex flex-column pe-4'>" +
              "<div><i class='fa-solid fa-circle-dot text-success me-2'></i><strong>" + agentName + "</strong> — " + (message) + "</div>" +
              "<small class='text-muted'>Email: " + email + " | Phone: " + phone + "</small>" +
              "<a class='stretched-link' href='" + link + "'></a>" +
            "</div>";

        // newest first
        if (list.firstChild) list.insertBefore(li, list.firstChild);
        else list.appendChild(li);

        if (bumpCount) {
            const current = parseInt(countEl.textContent) || 0;
            countEl.textContent = current + 1;
            countEl.classList.remove("d-none");
        }
    }

    function safe(str) {
        if (str == null) return "";
        return ("" + str)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;")
            .replace(/>/g, "&gt;").replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    // --- Initial AJAX load (only if SSR didn't already set items) ---
    function loadInitialNotifications() {
        const countEl = document.getElementById("notificationCount");
        const currentCount = parseInt(countEl.textContent) || 0;

        // If SSR already rendered items, skip reloading
        if (currentCount > 0) {
            return;
        }

        fetch(ctx + "/api/notifications", { credentials: "same-origin" })
            .then(r => r.ok ? r.json() : Promise.reject(r))
            .then(data => {
                const items = data.items || [];
                if (items.length === 0) {
                    const empty = document.getElementById("emptyState");
                    if (empty) empty.classList.remove("d-none");
                    countEl.textContent = "0";
                    countEl.classList.add("d-none");
                    return;
                }
                // Render without bumping per-item (we set count once)
                items.forEach(item => renderNotificationItem(item, false));
                countEl.textContent = items.length;
                countEl.classList.remove("d-none");
            })
            .catch(err => console.error("Failed to load notifications:", err));
    }

    // --- Clear button ---
    function wireClearButton() {
        const clearBtn = document.getElementById("clearNotifsBtn");
        const countEl = document.getElementById("notificationCount");
        const list = document.getElementById("notificationScroll");
        const empty = document.getElementById("emptyState");
        clearBtn.addEventListener("click", function (e) {
            e.preventDefault();
            list.innerHTML = "";
            countEl.textContent = "0";
            countEl.classList.add("d-none");
            if (empty) empty.classList.remove("d-none");
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        connectWebSocket();         // realtime updates (after page load)
        loadInitialNotifications(); // initial fill only if SSR didn't
        wireClearButton();          // clear UI
    });
</script>
</body>
</html>
