<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>Your Earnings • HappyCow Dairy</title>

  <!-- Bootstrap & Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet"/>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet"/>

  <style>
      :root { --brand:#28a745; --muted:#6b7280; }
      body { font-family:'Poppins',sans-serif;background:#f5f7fb;padding-top:110px; }
      .page-title { color:var(--brand);font-weight:600; }
      .card-ghost { border:0;border-radius:12px;box-shadow:0 6px 18px rgba(14,30,37,.06); }
      .small-muted { color:var(--muted);font-size:.9rem; }
      .badge-status-paid { background:#ECFDF3;color:#027A48; }
      .badge-status-pending { background:#FFFBEB;color:#92400E; }
  </style>
</head>
<body>

<jsp:include page="agentNavbar.jsp"/>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<section class="container">
    <div class="d-flex align-items-center justify-content-between mb-4">
        <div>
            <h1 class="h4 mb-0 page-title"><i class="fa-solid fa-wallet me-2"></i>Earnings</h1>
            <div class="small-muted">All your settled payment windows and invoices</div>
        </div>
        <a class="btn btn-outline-secondary" href="${ctx}/agent/profile/dashboard">
            <i class="fa-solid fa-arrow-left me-2"></i>Back
        </a>
    </div>

    <!-- Optional debug (remove later) -->
    <div class="alert alert-info small">
        payments size: <c:out value="${payments != null ? payments.size() : 0}"/>
    </div>

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card card-ghost p-3">
                <div class="small-muted">Total Payouts</div>
                <div class="h5 mb-0">₹<fmt:formatNumber value="${totalPayouts}" type="number" minFractionDigits="2"/></div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-ghost p-3">
                <div class="small-muted">Pending</div>
                <div class="h5 mb-0">${pendingCount} windows</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-ghost p-3">
                <div class="small-muted">Last Settled</div>
                <div class="h6 mb-0">${lastSettledDate}</div>
            </div>
        </div>
    </div>

    <div class="card card-ghost">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="small-muted">
                        <tr>
                            <th>#</th>
                            <th>Reference</th>
                            <th>Window</th>
                            <th>Gross</th>
                            <th>Settled At</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty payments}">
                                <c:forEach var="p" items="${payments}" varStatus="s">
                                    <tr>
                                        <td>${s.index + 1}</td>
                                        <td><strong>${p.referenceNo}</strong><br><small class="text-muted">PID: ${p.paymentId}</small></td>
                                        <td>${p.windowStartDate} → ${p.windowEndDate}</td>
                                        <td>₹<fmt:formatNumber value="${p.grossAmount}" type="number" minFractionDigits="2"/></td>
                                        <td>${p.settledAt}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.status == 'SUCCESS'}">
                                                    <span class="badge badge-status-paid px-2 py-1">Settled</span>
                                                </c:when>
                                                <c:when test="${p.status == 'PENDING'}">
                                                    <span class="badge badge-status-pending px-2 py-1">Pending</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary px-2 py-1">${p.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-primary"
                                               href="${ctx}/agent/profile/earnings/${p.paymentId}/invoice">
                                                <i class="fa-solid fa-file-invoice-dollar me-1"></i>Invoice
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center py-4 text-muted">No earnings yet.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>

<jsp:include page="agentFooter.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
