<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String ctx = request.getContextPath();
    // build relative returnUrl for forms (strip context)
    String fullUri = request.getRequestURI();
    String relativeUri = fullUri.startsWith(ctx) ? fullUri.substring(ctx.length()) : fullUri;
    String qs = request.getQueryString();
    if (qs != null && !qs.isEmpty()) relativeUri = relativeUri + "?" + qs;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>Payment History · HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width,initial-scale=1"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet"/>
    <style>
        body { font-family: Arial, sans-serif; }
        .sidebar { height:100vh; background:#f8f9fa; border-right:1px solid #ddd; padding-top:1rem; position:sticky; top:0; }
        .sidebar a { display:block; padding:.75rem 1rem; color:#333; text-decoration:none; margin:.2rem 0; border-radius:6px; }
        .sidebar a:hover, .sidebar a.active { background:#e9ecef; font-weight:600; }
        .main-content { padding:2rem; }
        .muted-small { font-size:.85rem; color:#6c757d; }
        .table-small th, .table-small td { padding:.55rem .75rem; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-custom px-3" style="background:#fff;border-bottom:1px solid #ddd;">
    <a class="navbar-brand" href="<%=ctx%>/adminDashboard" style="font-weight:700;color:#2ea44f;">HappyCow Dairy</a>
    <div class="mx-auto" style="max-width:480px;">
        <input class="form-control form-control-sm" placeholder="Search payments, agents..."/>
    </div>
    <div class="d-flex align-items-center">
        <div class="dropdown me-3">
            <a href="#" id="notificationDropdown" data-bs-toggle="dropdown" class="position-relative text-decoration-none">
                <i class="fa-solid fa-bell fa-lg text-secondary"></i>
                <span id="notificationCount" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger ${empty sessionScope.BELL_ITEMS ? ' d-none' : ''}">
                    <c:out value="${empty sessionScope.BELL_ITEMS ? 0 : fn:length(sessionScope.BELL_ITEMS)}"/>
                </span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="notificationDropdown" style="width:340px;">
                <li class="dropdown-header fw-bold d-flex justify-content-between align-items-center">
                    <span>Notifications</span>
                    <a id="clearNotifsBtn" class="small text-decoration-none" href="#">Clear</a>
                </li>
                <li><hr class="dropdown-divider"/></li>
                <c:choose>
                    <c:when test="${empty sessionScope.BELL_ITEMS}">
                        <li id="emptyState" class="px-3 py-2 text-muted small">No notifications</li>
                        <li class="p-0"><ul id="notificationScroll" class="list-group list-group-flush"></ul></li>
                    </c:when>
                    <c:otherwise>
                        <li class="p-0"><ul id="notificationScroll" class="list-group list-group-flush">
                            <c:forEach var="n" items="${sessionScope.BELL_ITEMS}">
                                <li class="list-group-item notif-item">
                                    <div class="d-flex flex-column pe-4">
                                        <div><i class="fa-solid fa-circle-dot text-success me-2"></i><strong><c:out value="${n.agentName}"/></strong> — <c:out value="${n.message}"/></div>
                                        <small class="text-muted">Email: <c:out value="${n.email}"/> | Phone: <c:out value="${n.phoneNumber}"/></small>
                                        <a class="stretched-link" href="<c:url value='${empty n.link ? "/agent/"+n.agentId+"/product-collections?window=13-15" : n.link}'/>"></a>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>

        <div class="dropdown">
            <a class="d-flex align-items-center text-decoration-none dropdown-toggle" id="profileDropdown" data-bs-toggle="dropdown" href="#">
                <c:choose>
                    <c:when test="${not empty loggedInAdmin.profilePictureBase64}">
                        <img src="data:${loggedInAdmin.profilePictureContentType};base64,${loggedInAdmin.profilePictureBase64}" class="rounded-circle" width="35" height="35" alt="Profile"/>
                    </c:when>
                    <c:otherwise>
                        <img src="<%=ctx%>/images/default-profile.png" class="rounded-circle" width="35" height="35" alt="Profile"/>
                    </c:otherwise>
                </c:choose>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="profileDropdown">
                <li class="dropdown-header text-center">
                    <strong><c:out value="${loggedInAdmin.adminName}"/></strong><br/>
                    <small class="text-muted"><c:out value="${loggedInAdmin.emailId}"/></small>
                </li>
                <li><hr class="dropdown-divider"/></li>
                <li><a class="dropdown-item" href="<%=ctx%>/adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i>Dashboard</a></li>
                <li><a class="dropdown-item" href="<%=ctx%>/adminProfile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
                <li><a class="dropdown-item text-danger" href="<%=ctx%>/logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 sidebar">
            <a href="<%=ctx%>/adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="<%=ctx%>/productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="<%=ctx%>/orders"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="<%=ctx%>/customers"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="<%=ctx%>/agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>

            <a href="<%=ctx%>/productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="<%=ctx%>/productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
            <a href="<%=ctx%>/payments/history" class="active"><i class="fa-solid fa-receipt me-2"></i> Payment History</a>
            <a href="<%=ctx%>/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <div class="col-md-10 main-content">
            <h2 class="fw-bold mb-2">Payment History</h2>
            <p class="text-muted mb-4">All agent payment windows — recent first.</p>

            <c:if test="${not empty paymentSuccess}"><div class="alert alert-success">${paymentSuccess}</div></c:if>
            <c:if test="${not empty paymentError}"><div class="alert alert-danger">${paymentError}</div></c:if>

            <c:choose>
                <c:when test="${empty rows}">
                    <div class="alert alert-info">No payment windows found.</div>
                </c:when>
                <c:otherwise>
                    <table class="table table-sm table-bordered table-small mb-3">
                        <thead class="table-light">
                            <tr>
                                <th>#</th><th>Agent</th><th>Window</th><th class="text-end">Amount</th><th>Status</th><th>Settled At</th><th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:set var="totalWindows" value="0"/>
                        <c:set var="totalGrossAll" value="0"/>
                        <c:set var="totalPaid" value="0"/>
                        <c:set var="totalPending" value="0"/>

                        <c:forEach var="row" items="${rows}" varStatus="st">
                            <c:set var="totalWindows" value="${totalWindows + 1}"/>
                            <c:set var="totalGrossAll" value="${totalGrossAll + (row.grossAmount != null ? row.grossAmount : 0)}"/>
                            <c:choose>
                                <c:when test="${row.status == 'SUCCESS'}">
                                    <c:set var="totalPaid" value="${totalPaid + (row.grossAmount != null ? row.grossAmount : 0)}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="totalPending" value="${totalPending + (row.grossAmount != null ? row.grossAmount : 0)}"/>
                                </c:otherwise>
                            </c:choose>

                            <tr>
                                <td><c:out value="${st.index + 1}"/></td>
                                <td style="min-width:220px;">
                                    <div><strong>
                                        <a href="<%=ctx%>/agent/${row.agentId}/product-collections?from=${row.windowStartDate}&to=${row.windowEndDate}"><c:out value="${row.agentName}"/></a>
                                    </strong></div>
                                    <div class="muted-small"><c:out value="${row.agentEmail}"/> • <c:out value="${row.agentPhone}"/></div>
                                    <div class="muted-small">Ref: <c:out value="${row.referenceNo}"/></div>
                                </td>
                                <td style="min-width:200px;"><c:out value="${row.windowStartDate}"/> → <c:out value="${row.windowEndDate}"/></td>
                                <td class="text-end">₹ <fmt:formatNumber value="${row.grossAmount}" minFractionDigits="2"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${row.status == 'SUCCESS'}"><span class="badge bg-success">Paid</span></c:when>
                                        <c:otherwise><span class="badge bg-warning text-dark">Pending</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty row.settledAt}">
                                            <c:set var="settledStr" value="${row.settledAt}"/>
                                            <c:out value="${fn:replace(fn:substring(settledStr,0,16),'T',' ')}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="min-width:160px;">
                                    <c:choose>
                                        <c:when test="${row.status == 'SUCCESS'}">
                                            <button class="btn btn-sm btn-outline-success" disabled><i class="fa-solid fa-check me-1"></i> Paid</button>
                                        </c:when>
                                        <c:otherwise>
                                            <form class="actions-form" method="post" action="<%=ctx%>/agent/${row.agentId}/payments/settle" onsubmit="return confirm('Settle payment for this window?');">
                                                <input type="hidden" name="from" value="${row.windowStartDate}"/>
                                                <input type="hidden" name="to" value="${row.windowEndDate}"/>
                                                <input type="hidden" name="amount" value="${row.grossAmount}"/>
                                                <input type="hidden" name="returnUrl" value="<%=relativeUri%>"/>
                                                <button type="submit" class="btn btn-sm btn-primary"><i class="fa-solid fa-indian-rupee-sign me-1"></i> Pay ₹ <fmt:formatNumber value="${row.grossAmount}" minFractionDigits="2"/></button>
                                            </form>
                                            <form class="actions-form ms-1" method="post" action="<%=ctx%>/agent/${row.agentId}/payments/settle" style="display:inline;">
                                                <input type="hidden" name="from" value="${row.windowStartDate}"/>
                                                <input type="hidden" name="to" value="${row.windowEndDate}"/>
                                                <input type="hidden" name="amount" value="${row.grossAmount}"/>
                                                <input type="hidden" name="simulate" value="true"/>
                                                <input type="hidden" name="returnUrl" value="<%=relativeUri%>"/>
                                                <button type="submit" class="btn btn-sm btn-outline-secondary" title="Mark as paid (simulation)"><i class="fa-solid fa-check-double"></i></button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr class="table-active">
                                <th colspan="2">Summary</th>
                                <th>Total windows: <strong><c:out value="${totalWindows}"/></strong></th>
                                <th class="text-end">₹ <fmt:formatNumber value="${totalGrossAll}" minFractionDigits="2"/></th>
                                <th colspan="3">Paid: <strong>₹ <fmt:formatNumber value="${totalPaid}" minFractionDigits="2"/></strong> &nbsp;|&nbsp; Pending: <strong>₹ <fmt:formatNumber value="${totalPending}" minFractionDigits="2"/></strong></th>
                            </tr>
                        </tfoot>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2/dist/stomp.min.js"></script>

<script>
    const ctx = "<%=ctx%>";
    let stompClient = null;
    function connectWebSocket() {
        try {
            const socket = new SockJS(ctx + '/ws');
            stompClient = Stomp.over(socket);
            stompClient.debug = null;
            stompClient.connect({}, function() {
                stompClient.subscribe('/user/queue/notifications', function(msg) {
                    try {
                        const payload = JSON.parse(msg.body);
                        if (Array.isArray(payload)) payload.forEach(p => renderNotificationItem(p, true));
                        else renderNotificationItem(payload, true);
                    } catch(e) { console.warn('Invalid notification payload', e); }
                });
            }, function(err) { console.error('WS err', err); });
        } catch (e) {
            console.warn('WS unsupported', e);
        }
    }

    function renderNotificationItem(item, bump) {
        const list = document.getElementById('notificationScroll');
        const count = document.getElementById('notificationCount');
        const empty = document.getElementById('emptyState');
        if (empty) empty.classList.add('d-none');
        const name = item.agentName || 'Agent';
        const message = item.message || '';
        const email = item.email || '-';
        const phone = item.phoneNumber || '-';
        const link = item.link ? (item.link.startsWith('/') ? (ctx + item.link) : item.link) : (ctx + '/agent/' + encodeURIComponent(item.agentId) + '/product-collections');

        const li = document.createElement('li');
        li.className = 'list-group-item notif-item';
        li.innerHTML = "<div class='d-flex flex-column pe-4'><div><i class='fa-solid fa-circle-dot text-success me-2'></i><strong>" + escapeHtml(name) + "</strong> — " + escapeHtml(message) + "</div><small class='text-muted'>Email: " + escapeHtml(email) + " | Phone: " + escapeHtml(phone) + "</small><a class='stretched-link' href='" + link + "'></a></div>";

        if (list.firstChild) list.insertBefore(li, list.firstChild); else list.appendChild(li);
        if (bump && count) {
            const cur = parseInt(count.textContent) || 0;
            count.textContent = cur + 1;
            count.classList.remove('d-none');
        }
    }

    function escapeHtml(s) { return String(s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;'); }

    document.addEventListener('DOMContentLoaded', function() {
        connectWebSocket();
        const clearBtn = document.getElementById('clearNotifsBtn');
        if (clearBtn) {
            clearBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const list = document.getElementById('notificationScroll');
                const count = document.getElementById('notificationCount');
                if (list) list.innerHTML = '';
                if (count) { count.textContent = '0'; count.classList.add('d-none'); }
                const empty = document.getElementById('emptyState');
                if (empty) empty.classList.remove('d-none');
            });
        }
    });
</script>
</body>
</html>
