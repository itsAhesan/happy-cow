<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String ctx = request.getContextPath();
    // server-safe label (replace en-dash with HTML entity so it renders consistently)
    String rawWindowLabel = (String) request.getAttribute("windowLabel");
    String safeWindowLabel = rawWindowLabel == null ? "" : rawWindowLabel.replace("\u2013", "&#8211;");
    request.setAttribute("safeWindowLabel", safeWindowLabel);

    // Build a canonical controller return URL (important: point back to controller mapping,
    // NOT to a JSP file. This ensures flash attributes are picked up after redirect.)
    String controllerReturnUrl = ctx + "/agent/" + request.getAttribute("agentId")
            + "/product-collections?from=" + request.getAttribute("dueStart")
            + "&to=" + request.getAttribute("dueEnd");
%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1"/>
  <title>Product Collections · Agent ${agentId} · ${windowLabel}</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet"/>

  <style>
    body { font-family: Arial, sans-serif; }
    .sidebar { height:100vh; background:#f8f9fa; border-right:1px solid #ddd; padding-top:1rem; position:sticky; top:0; }
    .sidebar a { display:block; padding:.75rem 1rem; margin:.2rem 0; color:#333; text-decoration:none; border-radius:6px; }
    .sidebar a:hover, .sidebar a.active { background:#e9ecef; font-weight:700; }
    .main-content { padding:2rem; }
    .navbar-custom { background:#fff; border-bottom:1px solid #ddd; }
    .navbar-custom .navbar-brand { font-weight: bold; color: #2ea44f; }
    .muted-small { font-size:.85rem; color:#6c757d; }
    .stat { font-size:1.35rem; font-weight:700; }
    .table-small th, .table-small td { padding:.55rem .75rem; }
    .notif-item small { display:block; }
    #notificationScroll { max-height:320px; overflow-y:auto; }
    .stretched-link { position:absolute; inset:0; }
  </style>
</head>
<body>




<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-custom px-3">
  <a class="navbar-brand" href="<%=ctx%>/adminDashboard">HappyCow Dairy</a>



  <div class="mx-auto" style="max-width:480px;">
    <input class="form-control form-control-sm" placeholder="Search..."/>
  </div>

  <div class="d-flex align-items-center">
    <!-- Notifications -->
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
            <li class="p-0">
              <ul id="notificationScroll" class="list-group list-group-flush">
                <c:forEach var="n" items="${sessionScope.BELL_ITEMS}">
                  <li class="list-group-item notif-item">
                    <div class="d-flex flex-column pe-4">
                      <div>
                        <i class="fa-solid fa-circle-dot text-success me-2"></i>
                        <strong><c:out value="${n.agentName}"/></strong> — <c:out value="${n.message}"/>
                      </div>
                      <small class="text-muted">Email: <c:out value="${n.email}"/> | Phone: <c:out value="${n.phoneNumber}"/></small>
                      <a class="stretched-link" href="<c:url value='${empty n.link ? "/agent/"+n.agentId+"/product-collections" : n.link}'/>"></a>
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
      <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle" id="profileDropdown" data-bs-toggle="dropdown">
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
        <li><a class="dropdown-item" href="<%=ctx%>/adminProfile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
        <li><a class="dropdown-item text-danger" href="<%=ctx%>/logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
      </ul>
    </div>
  </div>
</nav>

<!-- Page layout -->
<div class="container-fluid">
  <div class="row">
    <div class="col-md-2 sidebar">
      <a href="<%=ctx%>/adminDashboard" class="active"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
      <a href="<%=ctx%>/productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
      <a href="<%=ctx%>/orders"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
      <a href="<%=ctx%>/customers"><i class="fa-solid fa-users me-2"></i> Customers</a>
      <a href="<%=ctx%>/agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
      <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>

      <a href="<%=ctx%>/productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
      <a href="<%=ctx%>/productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
      <a href="<%=ctx%>/payments/history"><i class="fa-solid fa-receipt me-2"></i> Payment History</a>
      <a href="<%=ctx%>/logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
    </div>

    <div class="col-md-10 main-content">
      <a class="btn btn-link p-0 mb-2" href="<%=ctx%>/adminDashboard">&larr; Back to Dashboard</a>
      <h3>Product Collections</h3>

      <div class="text-muted mb-3">
        <strong>Agent:</strong>
        <c:choose>
          <c:when test="${not empty agent}">
            <c:out value="${agent.firstName}"/> <c:out value="${agent.lastName}"/>
          </c:when>
          <c:otherwise>
            #<c:out value="${agentId}"/>
          </c:otherwise>
        </c:choose>

        <c:if test="${not empty agent}">
          &nbsp;&middot;&nbsp;<strong>Email:</strong> <c:out value="${agent.email}"/>
          &nbsp;&middot;&nbsp;<strong>Phone:</strong> <c:out value="${agent.phoneNumber}"/>
        </c:if>
      </div>

      <!-- Flash messages (ensure they appear after redirect) -->
      <c:if test="${not empty paymentSuccess}">
        <div class="alert alert-success">${paymentSuccess}</div>
      </c:if>
      <c:if test="${not empty paymentError}">
        <div class="alert alert-danger">${paymentError}</div>
      </c:if>

      <!-- Summary cards -->
      <div class="row g-3 mb-4">
        <div class="col-md-4">
          <div class="card"><div class="card-body">
            <div class="muted">Lifetime Collections</div>
            <div class="stat">₹ <fmt:formatNumber value="${lifetimeTotal}" minFractionDigits="2"/></div>
            <div class="small">Total Qty: <fmt:formatNumber value="${lifetimeQty}" maxFractionDigits="2"/></div>
          </div></div>
        </div>
        <div class="col-md-4">
          <div class="card"><div class="card-body">
            <div class="muted">Lifetime Paid</div>
            <div class="stat text-success">₹ <fmt:formatNumber value="${lifetimePaid}" minFractionDigits="2"/></div>
            <div class="small">All settled windows</div>
          </div></div>
        </div>
        <div class="col-md-4">
          <div class="card"><div class="card-body">
            <div class="muted">Pending Overall</div>
            <div class="stat text-danger">₹ <fmt:formatNumber value="${lifetimePending}" minFractionDigits="2"/></div>
            <div class="small">Will be settled in future windows</div>
          </div></div>
        </div>
      </div>

      <!-- Current window and actions -->
      <div class="row g-3 mb-4">
        <div class="col-md-4">
          <div class="card"><div class="card-body">
            <div class="muted">Current Window Amount</div>
            <div class="stat">₹ <fmt:formatNumber value="${winTotal}" minFractionDigits="2"/></div>
            <div class="small">Quantity: <fmt:formatNumber value="${winQty}" maxFractionDigits="2"/></div>
          </div></div>
        </div>

        <div class="col-md-5">
          <div class="card"><div class="card-body">
            <div class="muted">Status</div>
            <c:choose>
              <c:when test="${not empty payment && payment.status == 'SUCCESS'}">
                <div class="stat text-success">PAID</div>
                <div class="small">Txn: <c:out value="${payment.referenceNo}"/></div>
              </c:when>
              <c:otherwise>
                <div class="stat text-danger">DUE</div>
                <div class="small">To be paid for <strong><c:out value="${safeWindowLabel}" escapeXml="false"/></strong></div>
              </c:otherwise>
            </c:choose>
          </div></div>
        </div>

        <div class="col-md-3">
          <div class="card h-100"><div class="card-body d-flex flex-column justify-content-between">
            <div class="muted mb-2">Actions</div>

            <c:choose>
              <c:when test="${not empty payment && payment.status == 'SUCCESS'}">
                <button class="btn btn-outline-success w-100" disabled><i class="fa-solid fa-check me-1"></i> Payment Completed</button>
              </c:when>
              <c:otherwise>
                <!-- IMPORTANT: returnUrl points to controller mapping (not the JSP). -->
                <form method="post" action="<%=ctx%>/agent/${agentId}/payments/settle" class="d-grid gap-2">
                  <input type="hidden" name="from" value="${dueStart}"/>
                  <input type="hidden" name="to" value="${dueEnd}"/>
                  <input type="hidden" name="amount" value="${winTotal}"/>
                  <input type="hidden" name="returnUrl" value="<%=controllerReturnUrl%>" />
                  <button type="submit" class="btn btn-primary">
                    <i class="fa-solid fa-indian-rupee-sign me-1"></i> Settle Payment (₹ <fmt:formatNumber value="${winTotal}" minFractionDigits="2"/>)
                  </button>
                </form>
              </c:otherwise>
            </c:choose>

          </div></div>
        </div>
      </div>

      <div class="alert alert-secondary d-flex justify-content-between align-items-center">
        <div>
          <strong>Next half:</strong> <c:out value="${nextWindowLabel}"/> &nbsp; Collections: <strong><c:out value="${nextWindowCount}"/></strong>
          &nbsp; Est. Amount: <strong>₹ <fmt:formatNumber value="${nextWindowTotal}" minFractionDigits="2"/></strong>
        </div>
        <a class="btn btn-sm btn-outline-secondary" href="<%=ctx%>/agent/${agentId}/product-collections?from=${nextWindowStart}&to=${nextWindowEnd}">Preview Window</a>
      </div>

      <c:choose>
        <c:when test="${empty rowsWindow}">
          <div class="alert alert-info">No product collections found for this window (<c:out value="${safeWindowLabel}" escapeXml="false"/>).</div>
        </c:when>
        <c:otherwise>
          <div class="card"><div class="card-body">
            <div class="table-responsive">
              <table class="table table-sm table-striped align-middle">
                <thead class="table-light">
                  <tr><th>#</th><th>Collected Date</th><th>Type of Milk</th><th class="text-end">Quantity</th><th class="text-end">Price</th><th class="text-end">Total Amount</th></tr>
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
                    <th colspan="3" class="text-end">Totals for <c:out value="${safeWindowLabel}" escapeXml="false"/>:</th>
                    <th class="text-end"><fmt:formatNumber value="${winQty}" maxFractionDigits="2"/></th>
                    <th class="text-end">—</th>
                    <th class="text-end">₹ <fmt:formatNumber value="${winTotal}" minFractionDigits="2"/></th>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div></div>
        </c:otherwise>
      </c:choose>

    </div>
  </div>
</div>

<!-- JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2/dist/stomp.min.js"></script>

<script>
  const ctx = "<%=ctx%>";
  let stompClient = null;

  function escapeHtml(s) {
    if (!s && s !== 0) return "";
    return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;');
  }

  function renderNotificationItem(item, bump) {
    const list = document.getElementById('notificationScroll');
    const count = document.getElementById('notificationCount');
    const empty = document.getElementById('emptyState');
    if (empty) empty.classList.add('d-none');

    const name = escapeHtml(item.agentName || 'Agent');
    const message = escapeHtml(item.message || '');
    const email = escapeHtml(item.email || '-');
    const phone = escapeHtml(item.phoneNumber || '-');
    const link = item.link ? (item.link.startsWith('/') ? (ctx + item.link) : item.link) : (ctx + '/agent/' + encodeURIComponent(item.agentId) + '/product-collections');

    const li = document.createElement('li');
    li.className = 'list-group-item notif-item';
    li.innerHTML = "<div class='d-flex flex-column pe-4'>"
        + "<div><i class='fa-solid fa-circle-dot text-success me-2'></i><strong>" + name + "</strong> — " + message + "</div>"
        + "<small class='text-muted'>Email: " + email + " | Phone: " + phone + "</small>"
        + "<a class='stretched-link' href='" + link + "'></a>"
        + "</div>";

    if (list.firstChild) list.insertBefore(li, list.firstChild); else list.appendChild(li);

    if (bump && count) {
      const cur = parseInt(count.textContent) || 0;
      count.textContent = cur + 1;
      count.classList.remove('d-none');
    }
  }

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
          } catch (e) { console.warn('invalid notification payload', e); }
        });
      }, function(err) { console.warn('ws err', err); });
    } catch (e) { console.warn('ws not available', e); }
  }

  document.addEventListener('DOMContentLoaded', function () {
    connectWebSocket();

    // wire clear button
    const clearBtn = document.getElementById('clearNotifsBtn');
    if (clearBtn) {
      clearBtn.addEventListener('click', function (e) {
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
