<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Collections - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        body { font-family: Arial, sans-serif; }
        .sidebar { height: 100vh; background:#f8f9fa; border-right:1px solid #ddd; padding-top:1rem; }
        .sidebar a { display:block; padding:0.75rem 1rem; margin:0.2rem 0; color:#333; text-decoration:none; border-radius:6px; transition:0.2s; }
        .sidebar a:hover, .sidebar a.active { background:#e9ecef; font-weight:bold; }
        .main-content { padding:2rem; }
        .navbar-custom { background:#fff; border-bottom:1px solid #ddd; }
        .navbar-custom .navbar-brand { font-weight:bold; color:#2ea44f; }
        .search-box { max-width:400px; width:100%; }
        .badge-type { font-weight:500; }
        .btn-view { white-space: nowrap; }
        .kv { display:flex; margin-bottom:.4rem; }
        .kv .k { width:140px; color:#6c757d; }
        .kv .v { flex:1; font-weight:500; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-custom px-3">
    <a class="navbar-brand" href="adminDashboard">HappyCow Dairy</a>
    <div class="mx-auto search-box">
        <input type="text" class="form-control form-control-sm" placeholder="Search...">
    </div>
    <div class="dropdown">
        <a href="#" class="d-flex align-items-center text-decoration-none dropdown-toggle"
           id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
            <c:choose>
                <c:when test="${not empty loggedInAdmin.profilePicture}">
                    <img src="data:${loggedInAdmin.profilePictureContentType};base64,${loggedInAdmin.profilePictureBase64}"
                         alt="Profile" class="rounded-circle" width="35" height="35">
                </c:when>
                <c:otherwise>
                    <img src="images/default-profile.png" alt="Profile" class="rounded-circle" width="35" height="35">
                </c:otherwise>
            </c:choose>
        </a>
        <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="profileDropdown">
            <li class="dropdown-header text-center">
                <strong>${loggedInAdmin.adminName}</strong><br>
                <small class="text-muted">${loggedInAdmin.emailId}</small>
            </li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="adminProfile"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
            <li><a class="dropdown-item" href="#"><i class="fa-solid fa-gear me-2"></i>Settings</a></li>
            <li><a class="dropdown-item text-danger" href="logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
        </ul>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 sidebar">
            <a href="adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>

            <a href="productCollection"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="productCollectionList" class="active"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
            <a href="${pageContext.request.contextPath}/payments/history"><i class="fa-solid fa-receipt me-2"></i> Payment History</a>
            <a href="logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <div class="col-md-10 main-content">
            <div class="d-flex align-items-center justify-content-between mb-3">
                <div>
                    <h2 class="fw-bold mb-1">Collections</h2>
                    <div class="text-muted">
                        <c:choose>
                            <c:when test="${empty date}">Showing all collections.</c:when>
                            <c:otherwise>Showing collections for <strong>${date}</strong>.</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div>
                    <a href="productCollection" class="btn btn-success btn-sm">
                        <i class="fa-solid fa-plus me-1"></i> New Collection
                    </a>
                </div>
            </div>

            <!-- Single-date filter -->
            <form class="row gy-2 gx-2 align-items-end mb-3" action="productCollectionList" method="get">
                <div class="col-auto">
                    <label for="date" class="form-label mb-0">Date</label>
                    <input type="date" id="date" name="date" class="form-control form-control-sm"
                           value="${date}">
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary btn-sm">
                        <i class="fa-solid fa-magnifying-glass me-1"></i> Search
                    </button>
                    <a href="productCollectionList" class="btn btn-outline-secondary btn-sm">Show All</a>
                </div>
            </form>

            <!-- Table -->
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                            <tr>
                                <!-- ID hidden -->
                                <th>Agent</th>
                                <th>Phone</th>
                                <th>Milk Type</th>
                                <th class="text-end">Price (₹/L)</th>
                                <th class="text-end">Qty (L)</th>
                                <th class="text-end">Total (₹)</th>
                                <th class="text-end">Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty collections}">
                                    <tr>
                                        <td colspan="7" class="text-center text-muted py-4">
                                            No collections found.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${collections}" var="pc">
                                        <tr>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${pc.agent != null}">
                                                        ${pc.agent.firstName} ${pc.agent.lastName}
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${pc.agent != null ? pc.agent.phoneNumber : ''}</td>
                                            <td><span class="badge text-bg-light badge-type">${pc.typeOfMilk}</span></td>
                                            <td class="text-end"><fmt:formatNumber value="${pc.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                            <td class="text-end"><fmt:formatNumber value="${pc.quantity}" minFractionDigits="3" maxFractionDigits="3"/></td>
                                            <td class="text-end fw-semibold"><fmt:formatNumber value="${pc.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                            <td class="text-end">
                                                <button type="button"
                                                        class="btn btn-sm btn-outline-primary btn-view"
                                                        data-id="${pc.productCollectionId}">
                                                    <i class="fa-regular fa-eye me-1"></i> View
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div> <!-- /main -->
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="viewModal" tabindex="-1" aria-labelledby="viewModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="viewModalLabel">Collection Details</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <div id="modalError" class="alert alert-danger d-none" role="alert"></div>

        <div class="row g-3">
          <div class="col-lg-6">
            <div class="card border-0 shadow-sm">
              <div class="card-body">
                <h6 class="fw-bold mb-2">Agent</h6>
                <div class="kv"><div class="k">Name</div><div class="v" id="mAgentName">—</div></div>
                <div class="kv"><div class="k">Email</div><div class="v" id="mAgentEmail">—</div></div>
                <div class="kv"><div class="k">Phone</div><div class="v" id="mAgentPhone">—</div></div>
                <div class="kv"><div class="k">Address</div><div class="v" id="mAgentAddress">—</div></div>
              </div>
            </div>
          </div>
          <div class="col-lg-6">
            <div class="card border-0 shadow-sm">
              <div class="card-body">
                <h6 class="fw-bold mb-2">Milk Collection</h6>
                <div class="kv"><div class="k">Type of Milk</div><div class="v" id="mType">—</div></div>
                <div class="kv"><div class="k">Quantity (L)</div><div class="v" id="mQty">—</div></div>
                <div class="kv"><div class="k">Unit Price (₹/L)</div><div class="v" id="mPrice">—</div></div>
                <div class="kv"><div class="k">Total Amount (₹)</div><div class="v fw-semibold" id="mTotal">—</div></div>
              </div>
            </div>
          </div>
        </div>

      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function () {
  const viewModal = new bootstrap.Modal(document.getElementById('viewModal'));
  const modalError = document.getElementById('modalError');

  // Fill helpers
  function setText(id, val) {
    document.getElementById(id).textContent = (val && String(val).trim().length) ? val : '—';
  }
  function money(n) {
    if (n === null || n === undefined || isNaN(n)) return '—';
    return Number(n).toFixed(2);
  }
  function qty(n) {
    if (n === null || n === undefined || isNaN(n)) return '—';
    return Number(n).toFixed(3);
  }
  function showError(msg) {
    modalError.classList.remove('d-none');
    modalError.textContent = msg || 'Failed to load details.';
  }
  function hideError() {
    modalError.classList.add('d-none');
    modalError.textContent = '';
  }

  // Delegate clicks on all .btn-view
  document.addEventListener('click', async function (e) {
    const btn = e.target.closest('.btn-view');
    if (!btn) return;

    hideError();
    const id = btn.getAttribute('data-id');
    const url = '<c:url value="/productCollection/details"/>' + '?id=' + encodeURIComponent(id);

    try {
      const resp = await fetch(url, { headers: { 'Accept': 'application/json' }});
      if (!resp.ok) {
        showError(resp.status === 404 ? 'Record not found.' : 'Unable to fetch details.');
      } else {
        const d = await resp.json();
        setText('mAgentName', d.agentName);
        setText('mAgentEmail', d.agentEmail);
        setText('mAgentPhone', d.agentPhone);
        setText('mAgentAddress', d.agentAddress);

        setText('mType', d.typeOfMilk);
        setText('mQty', qty(d.quantity));
        setText('mPrice', money(d.price));
        setText('mTotal', money(d.totalAmount));
      }
      viewModal.show();
    } catch (err) {
      showError('Network error.');
      viewModal.show();
    }
  });
})();
</script>

</body>
</html>
