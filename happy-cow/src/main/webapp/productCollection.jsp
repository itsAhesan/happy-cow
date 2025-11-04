<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Product Collection - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        body { font-family: Arial, sans-serif; }
        .sidebar { height: 100vh; background-color: #f8f9fa; border-right: 1px solid #ddd; padding-top: 1rem; }
        .sidebar a { display: block; padding: 0.75rem 1rem; margin: 0.2rem 0; color: #333; text-decoration: none; border-radius: 6px; transition: 0.2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #e9ecef; font-weight: bold; }
        .main-content { padding: 2rem; }
        .navbar-custom { background-color: #fff; border-bottom: 1px solid #ddd; }
        .navbar-custom .navbar-brand { font-weight: bold; color: #2ea44f; }
        .search-box { max-width: 400px; width: 100%; }
        .form-help { font-size: 0.85rem; color: #6c757d; }
        .spinner-border.spinner-xs { width: 1rem; height: 1rem; border-width: .15em; }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-custom px-3">
    <a class="navbar-brand" href="#">HappyCow Dairy</a>
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
        <!-- Sidebar -->
        <div class="col-md-2 sidebar">
            <a href="adminDashboard"><i class="fa-solid fa-gauge-high me-2"></i> Dashboard</a>
            <a href="productDashboard"><i class="fa-solid fa-cow me-2"></i> Products</a>
            <a href="#"><i class="fa-solid fa-cart-shopping me-2"></i> Orders</a>
            <a href="#"><i class="fa-solid fa-users me-2"></i> Customers</a>
            <a href="agentDashboard"><i class="fa-solid fa-user-tie me-2"></i> Agents</a>
            <a href="#"><i class="fa-solid fa-chart-line me-2"></i> Reports</a>
            <a href="productCollection" class="active"><i class="fa-solid fa-boxes-packing me-2"></i> Product Collection</a>
            <a href="productCollectionList"><i class="fa-solid fa-table-list me-2"></i> View Collections</a>
            <a href="${pageContext.request.contextPath}/payments/history"><i class="fa-solid fa-receipt me-2"></i> Payment History</a>
            <a href="logout" class="text-danger"><i class="fa-solid fa-right-from-bracket me-2"></i> Logout</a>
        </div>

        <!-- Main -->
        <div class="col-md-10 main-content">
            <div class="d-flex align-items-center justify-content-between mb-3">
                <div>
                    <h2 class="fw-bold mb-0">Product Collection</h2>
                    <p class="text-muted mb-0">Capture collected milk details from agents.</p>
                </div>
                <div>
                    <a href="productCollection" class="btn btn-outline-secondary btn-sm">
                        <i class="fa-solid fa-rotate-right me-1"></i> Refresh
                    </a>
                </div>
            </div>

            <!-- Alerts -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fa-solid fa-circle-check me-2"></i>${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fa-solid fa-triangle-exclamation me-2"></i>${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Form Card -->
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <form action="saveProductCollection" method="post" novalidate id="pcForm">
                        <!-- Hidden PK + agentId -->
                        <input type="hidden" name="productCollectionId" value="${productCollection.productCollectionId}"/>
                        <input type="hidden" id="agentId" name="agentId" value="${productCollection.agent != null ? productCollection.agent.agentId : ''}"/>

                        <div class="row g-3">
                            <!-- Admin -->
                            <div class="col-md-4">
                                <label class="form-label">Admin</label>
                                <input type="text" class="form-control" value="${loggedInAdmin.adminName}" readonly>
                                <input type="hidden" name="adminId" value="${loggedInAdmin.adminId}">
                                <div class="form-help">Auto-filled from logged-in admin</div>
                            </div>

                            <!-- Phone Number (lookup trigger) -->
                            <div class="col-md-4">
                                <label for="phoneNumber" class="form-label">Phone Number</label>
                                <div class="input-group">
                                    <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber"
                                           value="${productCollection.phoneNumber}"
                                           pattern="^(?:\\+?91[-\\s]?|0)?[6-9]\\d{9}$"
                                           placeholder="e.g. 9876543210 or +91 98765 43210" required>
                                    <button class="btn btn-outline-secondary" type="button" id="lookupBtn">
                                        <span class="default-text"><i class="fa-solid fa-magnifying-glass"></i></span>
                                        <span class="loading d-none">
                                            <span class="spinner-border spinner-xs" role="status" aria-hidden="true"></span>
                                        </span>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Enter a valid Indian mobile (10 digits; +91/0 allowed).</div>
                                <div id="lookupMsg" class="form-help"></div>
                            </div>

                            <!-- Agent Name (DTO-only) -->
                            <div class="col-md-4">
                                <label for="name" class="form-label">Agent Name</label>
                                <input type="text" class="form-control" id="name" name="name"
                                       value="${productCollection.name}" readonly>
                                <div class="form-help">Auto-filled from phone number. Not stored in entity.</div>
                            </div>

                            <!-- Agent Email (DTO-only) -->
                            <div class="col-md-4">
                                <label for="email" class="form-label">Agent Email</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${productCollection.email}" readonly>
                                <div class="form-help">Auto-filled from phone number. Not stored in entity.</div>
                            </div>

                            <!-- Type of Milk (from backend products) -->
                            <div class="col-md-4">
                                <label for="typeOfMilk" class="form-label">Type of Milk</label>
                                <select class="form-select" id="typeOfMilk" name="typeOfMilk" required>
                                    <option value="" disabled <c:if test="${empty productCollection.typeOfMilk}">selected</c:if>>
                                        Select type...
                                    </option>
                                    <c:forEach items="${products}" var="p">
                                        <option
                                            value="${p.productName}"
                                            data-price="${p.productPrice}"
                                            <c:if test="${productCollection.typeOfMilk == p.productName}">selected</c:if>>
                                            ${p.productName} — ₹${p.productPrice}
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Please select a milk type.</div>
                            </div>

                            <!-- Price (auto from selected product) -->
                            <div class="col-md-4">
                                <label for="price" class="form-label">Unit Price (₹/L)</label>
                                <input type="number" step="0.01" min="0" class="form-control" id="price" name="price"
                                       value="${productCollection.price}" readonly>
                                <div class="form-help">Auto-filled from selected product.</div>
                            </div>

                            <!-- Quantity -->
                            <div class="col-md-4">
                                <label for="quantity" class="form-label">Quantity (L)</label>
                                <input type="number" step="0.001" min="0.001" class="form-control" id="quantity" name="quantity"
                                       value="${productCollection.quantity}" required>
                                <div class="invalid-feedback">Enter a valid quantity.</div>
                            </div>

                            <!-- Total Amount (auto) -->
                            <div class="col-md-4">
                                <label for="totalAmount" class="form-label">Total Amount (₹)</label>
                                <input type="number" step="0.01" min="0" class="form-control" id="totalAmount" name="totalAmount"
                                       value="${productCollection.totalAmount}" readonly>
                                <div class="form-help">Auto-calculated as Price × Quantity</div>
                            </div>

                            <!-- NOTE: collectedAt field removed from UI -->
                        </div>

                        <div class="mt-4 d-flex gap-2">
                            <button type="submit" class="btn btn-success">
                                <i class="fa-solid fa-floppy-disk me-1"></i> Save
                            </button>
                            <button type="reset" class="btn btn-outline-secondary">
                                <i class="fa-solid fa-eraser me-1"></i> Reset
                            </button>
                            <a href="productCollectionList" class="btn btn-outline-primary">
                                <i class="fa-solid fa-table-list me-1"></i> View Collections
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Optional: Recent Collections -->
            <c:if test="${not empty recentCollections}">
                <div class="mt-4">
                    <h5 class="fw-bold mb-3">Recent Collections</h5>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                            <tr>
                                <th>#ID</th>
                                <th>Agent</th>
                                <th>Milk Type</th>
                                <th class="text-end">Price (₹/L)</th>
                                <th class="text-end">Qty (L)</th>
                                <th class="text-end">Total (₹)</th>
                                <th>Collected At</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${recentCollections}" var="pc">
                                <tr>
                                    <td>${pc.productCollectionId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${pc.agent != null}">
                                                ${pc.agent.firstName} ${pc.agent.lastName}
                                            </c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${pc.typeOfMilk}</td>
                                    <td class="text-end">${pc.price}</td>
                                    <td class="text-end">${pc.quantity}</td>
                                    <td class="text-end fw-semibold">${pc.totalAmount}</td>
                                    <td>${pc.collectedAt}</td>
                                    <td class="text-end">
                                        <a href="productCollection/view?id=${pc.productCollectionId}" class="btn btn-sm btn-outline-secondary">
                                            <i class="fa-regular fa-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </div>
</div>

<!-- JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // --- Bootstrap validation + normalize phone before submit
    (function () {
        const form = document.getElementById('pcForm');
        const phoneEl = document.getElementById('phoneNumber');

        form.addEventListener('submit', function (event) {
            // normalize phone input before HTML5 validation runs
            phoneEl.value = normalizeIndianPhone(phoneEl.value);

            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    })();

    // --- Helpers for phone normalization/validation
    function normalizeIndianPhone(raw) {
        if (!raw) return '';
        const digits = raw.replace(/\D+/g, '');
        // keep last 10 digits (handles +91 / 0 prefixes)
        return digits.length >= 10 ? digits.slice(-10) : digits;
    }
    function isValidIndianPhone(raw) {
        return /^[6-9]\d{9}$/.test(normalizeIndianPhone(raw));
    }

    // --- Price & total calculation
    const priceEl = document.getElementById('price');
    const qtyEl = document.getElementById('quantity');
    const totalEl = document.getElementById('totalAmount');
    const typeEl  = document.getElementById('typeOfMilk');

    function calcTotal() {
        const p = parseFloat(priceEl.value);
        const q = parseFloat(qtyEl.value);
        totalEl.value = (!isNaN(p) && !isNaN(q)) ? (p * q).toFixed(2) : '';
    }
    function setPriceFromType() {
        const opt = typeEl?.options[typeEl.selectedIndex];
        if (!opt) return;
        const price = opt.getAttribute('data-price');
        priceEl.value = (price !== null && price !== '') ? parseFloat(price).toFixed(2) : '';
        calcTotal();
    }
    typeEl && typeEl.addEventListener('change', setPriceFromType);
    qtyEl && qtyEl.addEventListener('input', calcTotal);
    window.addEventListener('DOMContentLoaded', function () {
        setPriceFromType();
        calcTotal();
    });

    // --- Agent lookup by phone number
    (function agentLookupInit() {
        const phoneEl  = document.getElementById('phoneNumber');
        const nameEl   = document.getElementById('name');
        const emailEl  = document.getElementById('email');
        const agentIdEl = document.getElementById('agentId');
        const lookupBtn = document.getElementById('lookupBtn');
        const msgEl     = document.getElementById('lookupMsg');
        const url       = '<c:url value="/productCollection/getAgentByPhoneNumber"/>';

        const showLoading = (loading) => {
            const def = lookupBtn.querySelector('.default-text');
            const spn = lookupBtn.querySelector('.loading');
            if (loading) { def.classList.add('d-none'); spn.classList.remove('d-none'); }
            else { spn.classList.add('d-none'); def.classList.remove('d-none'); }
        };
        const setMsg = (text, type='muted') => {
            msgEl.className = 'form-help text-' + type;
            msgEl.textContent = text || '';
        };
        const clearAgentFields = () => {
            agentIdEl.value = '';
            nameEl.value = '';
            emailEl.value = '';
        };
        const fillAgent = (dto) => {
            agentIdEl.value = dto.agentId ?? '';
            const first = (dto.firstName || '').trim();
            const last  = (dto.lastName || '').trim();
            nameEl.value = (first + ' ' + last).trim();
            emailEl.value = dto.email || '';
        };

        async function lookup() {
            setMsg('', 'muted');
            clearAgentFields();
            const normalized = normalizeIndianPhone(phoneEl.value);

            if (!/^[6-9]\d{9}$/.test(normalized)) {
                setMsg('Enter a valid Indian mobile (10 digits; +91/0 allowed).', 'danger');
                return;
            }

            try {
                showLoading(true);
                const resp = await fetch(url + '?phoneNumber=' + encodeURIComponent(normalized), {
                    headers: {'Accept': 'application/json'}
                });
                if (resp.ok) {
                    const dto = await resp.json();
                    if (dto && dto.agentId) {
                        fillAgent(dto);
                        // also reflect normalized phone in the input so submit matches server expectations
                        phoneEl.value = normalized;
                        setMsg('Agent found and filled.', 'success');
                    } else {
                        setMsg('No agent data returned.', 'warning');
                    }
                } else if (resp.status === 404) {
                    setMsg('Agent not found for this phone number.', 'danger');
                } else {
                    setMsg('Lookup failed. Try again.', 'danger');
                }
            } catch (e) {
                setMsg('Network error during lookup.', 'danger');
            } finally {
                showLoading(false);
            }
        }

        // Debounce typing
        let t;
        phoneEl && phoneEl.addEventListener('input', function () {
            clearTimeout(t);
            t = setTimeout(lookup, 400);
        });
        // Manual click
        lookupBtn && lookupBtn.addEventListener('click', lookup);
        // Prefill if phone already present
        window.addEventListener('DOMContentLoaded', function () {
            if (isValidIndianPhone(phoneEl.value)) lookup();
        });
    })();
</script>

</body>
</html>
