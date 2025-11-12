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

    <!-- jsQR for static image decoding (reliable for uploaded images) -->
    <script src="https://cdn.jsdelivr.net/npm/jsqr@1.4.0/dist/jsQR.js"></script>

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
        /* scanner modal tweaks */
        #qr-reader { width: 100%; min-height: 320px; background: #000; display:flex; align-items:center; justify-content:center; color:#fff; }
        .qr-modal .modal-dialog { max-width: 820px; }
        .file-scan-input { display: none; }
        .camera-select { min-width: 200px; }
    </style>
</head>
<body>

<!-- Navbar -->
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

                            <!-- Phone Number (lookup trigger + QR) -->
                            <div class="col-md-4">
                                <label for="phoneNumber" class="form-label">Phone Number</label>
                                <div class="input-group">
                                    <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber"
                                           value="${productCollection.phoneNumber}"
                                           pattern="^(?:\\+?91[-\\s]?|0)?[6-9]\\d{9}$"
                                           placeholder="e.g. 9876543210 or +91 98765 43210" required>
                                    <button class="btn btn-outline-secondary" type="button" id="lookupBtn" title="Lookup by phone">
                                        <span class="default-text"><i class="fa-solid fa-magnifying-glass"></i></span>
                                        <span class="loading d-none">
                                            <span class="spinner-border spinner-xs" role="status" aria-hidden="true"></span>
                                        </span>
                                    </button>

                                    <!-- QR Scan button -->
                                    <button class="btn btn-outline-primary" type="button" id="scanQrBtn" title="Scan Agent QR">
                                        <i class="fa-solid fa-qrcode"></i>
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
                                <div class="form-help">Auto-filled from phone number or QR scan. Not stored in entity.</div>
                            </div>

                            <!-- Agent Email (DTO-only) -->
                            <div class="col-md-4">
                                <label for="email" class="form-label">Agent Email</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${productCollection.email}" readonly>
                                <div class="form-help">Auto-filled from phone number or QR scan. Not stored in entity.</div>
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

<!-- Scanner Modal -->
<div class="modal fade qr-modal" id="qrModal" tabindex="-1" aria-labelledby="qrModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header d-flex align-items-center gap-2">
                <h5 class="modal-title">Scan Agent QR</h5>
                <select id="cameraSelect" class="form-select camera-select ms-3" style="width:auto;">
                    <option value="">Detecting cameras...</option>
                </select>
                <button type="button" class="btn-close ms-auto" data-bs-dismiss="modal" aria-label="Close" id="closeScannerBtn"></button>
            </div>
            <div class="modal-body">
                <div id="qr-reader" class="w-100">Please allow camera access to scan QR.</div>

                <!-- hidden canvas used for image decoding -->
                <canvas id="qr-canvas" style="display:none;"></canvas>

                <div class="mt-3 text-center">
                    <small class="text-muted">If your camera isn't supported, upload a QR image instead.</small>
                    <div class="mt-2">
                        <input type="file" accept="image/*" id="qrFileInput" class="file-scan-input">
                        <button class="btn btn-sm btn-outline-secondary" id="uploadQrBtn"><i class="fa-solid fa-image me-1"></i> Scan from image</button>
                    </div>
                </div>
                <div id="qrStatus" class="mt-2"></div>
            </div>
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
    window.addEventListener('DOMContentLoaded', function () { setPriceFromType(); calcTotal(); });

    // --- Agent lookup by phone number (existing code)
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

    /**************************************************************************
     * QR scanner integration: dynamically load html5-qrcode library on demand
     * and use jsQR for image uploads.
     **************************************************************************/
    (function qrScannerInit() {
        const scanBtn = document.getElementById('scanQrBtn');
        const modalEl = document.getElementById('qrModal');
        const qrStatus = document.getElementById('qrStatus');
        const qrFileInput = document.getElementById('qrFileInput');
        const uploadQrBtn = document.getElementById('uploadQrBtn');
        const closeScannerBtn = document.getElementById('closeScannerBtn');
        const cameraSelect = document.getElementById('cameraSelect');
        const qrReaderDiv = document.getElementById('qr-reader');

        // CDN URL - change to a local path if you host the file yourself
        const HTML5_QR_LIB = 'https://unpkg.com/html5-qrcode@2.3.8/minified/html5-qrcode.min.js';

        let html5Qr = null;
        let currentCameraId = null;
        let modal = new bootstrap.Modal(modalEl, {keyboard: true});
        let libLoaded = false;
        let libLoadPromise = null;

        // helper: dynamically load a script once and return a Promise
        function loadScriptOnce(url) {
            if (libLoadPromise) return libLoadPromise;
            libLoadPromise = new Promise((resolve, reject) => {
                if (window.Html5Qrcode) {
                    libLoaded = true;
                    return resolve();
                }
                const s = document.createElement('script');
                s.src = url;
                s.async = true;
                s.onload = () => {
                    if (window.Html5Qrcode) {
                        libLoaded = true;
                        resolve();
                    } else {
                        reject(new Error('Html5Qrcode loaded but global not found'));
                    }
                };
                s.onerror = (e) => {
                    reject(new Error('Failed to load script: ' + url));
                };
                document.head.appendChild(s);
            });
            return libLoadPromise;
        }

        // show modal and start camera scanner
        scanBtn && scanBtn.addEventListener('click', function () {
            qrStatus.innerHTML = '<small class="text-muted">Detecting cameras...</small>';
            modal.show();

            // Ensure lib is loaded before trying to get cameras
            loadScriptOnce(HTML5_QR_LIB)
                .then(() => initCamerasAndStart())
                .catch(err => {
                    console.error('Failed to load html5-qrcode lib', err);
                    qrStatus.innerHTML = '<div class="text-danger">Camera library failed to load. Use image upload or allow loading external scripts.</div>';
                    cameraSelect.innerHTML = '<option value="">Camera access failed</option>';
                });
        });

        // stop scanner when modal closed
        modalEl.addEventListener('hidden.bs.modal', function () {
            stopScanner();
            cameraSelect.innerHTML = '<option value="">Detecting cameras...</option>';
            qrStatus.innerHTML = '';
            qrFileInput.value = '';
        });

        closeScannerBtn.addEventListener('click', function () {
            modal.hide();
        });

        // upload-from-image button
        uploadQrBtn.addEventListener('click', function () {
            qrFileInput.click();
        });

        // file input -> scan image using jsQR routine
        qrFileInput.addEventListener('change', function (e) {
            const file = e.target.files && e.target.files[0];
            if (!file) return;
            qrStatus.innerHTML = '<small class="text-muted">Scanning uploaded image...</small>';

            scanImageFile(file)
                .then(decodedText => {
                    if (decodedText) {
                        handleScanSuccess(decodedText);
                    } else {
                        qrStatus.innerHTML = '<div class="text-danger">No QR code found in the image.</div>';
                    }
                })
                .catch(err => {
                    console.error('scanImageFile error', err);
                    qrStatus.innerHTML = '<div class="text-danger">Failed to scan image: ' + (err.message || '') + '</div>';
                })
                .finally(() => { qrFileInput.value = ''; });
        });

        // camera select change
        cameraSelect.addEventListener('change', function () {
            const id = cameraSelect.value;
            if (!id) return;
            if (id === currentCameraId) return;
            startScannerWithCamera(id);
        });

        // init cameras AND start scanner; called after library loaded
        async function initCamerasAndStart() {
            try {
                if (!window.Html5Qrcode || !Html5Qrcode.getCameras) {
                    throw new Error('Html5Qrcode library not available');
                }
                const devices = await Html5Qrcode.getCameras();
                cameraSelect.innerHTML = '';
                if (!devices || devices.length === 0) {
                    cameraSelect.innerHTML = '<option value="">No camera found</option>';
                    qrStatus.innerHTML = '<div class="text-danger">No camera detected. Use "Scan from image" instead.</div>';
                    return;
                }

                devices.forEach((cam, idx) => {
                    const opt = document.createElement('option');
                    opt.value = cam.id;
                    opt.text = cam.label || ('Camera ' + (idx + 1));
                    cameraSelect.appendChild(opt);
                });

                let preferred = devices[0].id;
                for (const d of devices) {
                    if (/back|rear|environment|camera 1/i.test(d.label)) {
                        preferred = d.id;
                        break;
                    }
                }
                cameraSelect.value = preferred;
                startScannerWithCamera(preferred);
            } catch (err) {
                console.error('getCameras error', err);
                cameraSelect.innerHTML = '<option value="">Camera access failed</option>';
                qrStatus.innerHTML = '<div class="text-danger">Unable to access cameras. Give permission or use image upload.</div>';
            }
        }

        function startScannerWithCamera(cameraId) {
            stopScanner();
            currentCameraId = cameraId;
            qrStatus.innerHTML = '<small class="text-muted">Starting camera...</small>';

            try {
                html5Qr = new Html5Qrcode("qr-reader", { verbose: false });
            } catch (e) {
                console.error('Html5Qrcode constructor error', e);
                qrStatus.innerHTML = '<div class="text-danger">Camera init failed.</div>';
                return;
            }

            const config = { fps: 10, qrbox: { width: 280, height: 280 }, experimentalFeatures: { useBarCodeDetectorIfSupported: true } };

            html5Qr.start(
                cameraId,
                config,
                (decodedText, decodedResult) => {
                    qrStatus.innerHTML = '<div class="text-success"><i class="fa-solid fa-circle-check me-1"></i> QR scanned</div>';
                    stopScanner();
                    handleScanSuccess(decodedText);
                },
                (errorMessage) => {
                    // per-frame errors ignored
                }
            ).then(() => {
                qrStatus.innerHTML = '<div class="text-success">Camera started. Point to a QR code.</div>';
            }).catch(err => {
                console.error('html5Qr.start error', err);
                qrStatus.innerHTML = '<div class="text-danger">Camera start failed: ' + (err.message || err) + '</div>';
            });
        }

        function stopScanner() {
            if (html5Qr) {
                try {
                    html5Qr.stop().then(()=> html5Qr.clear()).catch(()=>{});
                } catch (e) { /* ignore */ }
                html5Qr = null;
            }
            currentCameraId = null;
            const qrReader = document.getElementById('qr-reader');
            if (qrReader) { qrReader.innerHTML = 'Please allow camera access to scan QR.'; }
        }

        /***************** helper functions (vCard parse, image scan, fill form) *****************/

        // parse minimal vCard fields we need (FN, TEL, EMAIL)
        function parseVCard(vcardText) {
            const raw = vcardText.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
            const lines = raw.split('\n');
            const result = {};
            for (let i=0;i<lines.length;i++) {
                let line = lines[i].trim();
                if (!line) continue;
                // unfold simple folded lines
                while (i < lines.length - 1 && /^[ \t]/.test(lines[i+1])) {
                    line += lines[i+1].trim();
                    i++;
                }
                if (/^TEL/i.test(line)) {
                    const parts = line.split(':');
                    if (parts.length >= 2) result.tel = parts.slice(1).join(':').replace(/[^+\d]/g,'');
                } else if (/^FN:/i.test(line)) {
                    result.fn = line.substring(line.indexOf(':')+1).trim();
                } else if (/^EMAIL/i.test(line)) {
                    const parts = line.split(':');
                    if (parts.length >= 2) result.email = parts.slice(1).join(':').trim();
                } else if (/^N:/i.test(line) && !result.fn) {
                    const val = line.substring(line.indexOf(':')+1).trim();
                    const parts = val.split(';');
                    const first = (parts[1] || '').trim();
                    const last  = (parts[0] || '').trim();
                    const fullname = (first + ' ' + last).trim();
                    if (fullname) result.fn = fullname;
                }
            }
            return result;
        }

        // handle result text (vCard parsing and filling fields)
        function handleScanSuccess(text) {
            const phoneEl  = document.getElementById('phoneNumber');
            const nameEl   = document.getElementById('name');
            const emailEl  = document.getElementById('email');
            const agentIdEl = document.getElementById('agentId');
            const lookupMsg = document.getElementById('lookupMsg');

            // If it's a vCard
            if (/BEGIN:VCARD/i.test(text)) {
                const v = parseVCard(text);
                if (v.tel) phoneEl.value = normalizeIndianPhone(v.tel);
                if (v.fn) nameEl.value = v.fn;
                if (v.email) emailEl.value = v.email;
                agentIdEl.value = '';

                // try server lookup by phone
                if (phoneEl.value && /^[6-9]\d{9}$/.test(phoneEl.value)) {
                    lookupByPhoneAfterScan(phoneEl.value);
                } else {
                    lookupMsg.className = 'form-help text-warning';
                    lookupMsg.textContent = 'Scanned contact filled. If phone is invalid for this region, adjust manually.';
                }
                setTimeout(()=> modal.hide(), 700);
                return;
            }

            // plain phone digits?
            const onlyDigits = text.replace(/\D/g,'');
            if (onlyDigits.length >= 10) {
                const last10 = onlyDigits.slice(-10);
                document.getElementById('phoneNumber').value = last10;
                lookupByPhoneAfterScan(last10);
                setTimeout(()=> modal.hide(), 500);
                return;
            }

            // unknown content
            qrStatus.innerHTML = '<div class="text-danger">Scanned QR content not recognized. Supported: vCard or phone number.</div>';
        }

        // call existing phone lookup endpoint after scan
        function lookupByPhoneAfterScan(phone) {
            const url = '<c:url value="/productCollection/getAgentByPhoneNumber"/>';
            const phoneEl = document.getElementById('phoneNumber');
            const nameEl = document.getElementById('name');
            const emailEl = document.getElementById('email');
            const agentIdEl = document.getElementById('agentId');
            const lookupMsg = document.getElementById('lookupMsg');

            fetch(url + '?phoneNumber=' + encodeURIComponent(phone), { headers: {'Accept':'application/json'} })
                .then(resp => {
                    if (resp.ok) return resp.json();
                    if (resp.status === 404) throw new Error('notfound');
                    throw new Error('lookup failed');
                })
                .then(dto => {
                    if (dto && dto.agentId) {
                        agentIdEl.value = dto.agentId;
                        nameEl.value = (dto.firstName || '') + (dto.lastName ? (' ' + dto.lastName) : '');
                        emailEl.value = dto.email || emailEl.value;
                        lookupMsg.className = 'form-help text-success';
                        lookupMsg.textContent = 'Agent found and filled from server.';
                    } else {
                        lookupMsg.className = 'form-help text-warning';
                        lookupMsg.textContent = 'No agent record found for this phone. Filled contact only.';
                    }
                })
                .catch(err => {
                    if (err.message === 'notfound') {
                        lookupMsg.className = 'form-help text-warning';
                        lookupMsg.textContent = 'No agent record found for this phone. Filled contact only.';
                    } else {
                        lookupMsg.className = 'form-help text-danger';
                        lookupMsg.textContent = 'Server lookup failed. Contact filled locally.';
                    }
                });
        }

        /**
         * Read image file, draw to canvas, decode with jsQR.
         * Returns Promise<string|null> resolved with decoded text or null if none.
         */
        function scanImageFile(file) {
            return new Promise((resolve, reject) => {
                if (!file.type.match(/^image\//)) {
                    return reject(new Error('Selected file is not an image'));
                }
                const reader = new FileReader();
                reader.onerror = () => reject(new Error('Failed to read file'));
                reader.onload = () => {
                    const img = new Image();
                    img.onerror = () => reject(new Error('Invalid image file'));
                    img.onload = () => {
                        try {
                            const canvas = document.getElementById('qr-canvas');
                            const ctx = canvas.getContext('2d');
                            // limit size to avoid memory issues
                            const maxDim = 1200;
                            let w = img.naturalWidth;
                            let h = img.naturalHeight;
                            const scale = Math.min(1, maxDim / Math.max(w, h));
                            w = Math.floor(w * scale);
                            h = Math.floor(h * scale);
                            canvas.width = w;
                            canvas.height = h;
                            ctx.clearRect(0, 0, w, h);
                            ctx.drawImage(img, 0, 0, w, h);
                            const imageData = ctx.getImageData(0, 0, w, h);
                            const code = jsQR(imageData.data, imageData.width, imageData.height, { inversionAttempts: "attemptBoth" });
                            if (code && code.data) resolve(code.data);
                            else resolve(null);
                        } catch (ex) { reject(ex); }
                    };
                    img.src = reader.result;
                };
                reader.readAsDataURL(file);
            });
        }

    })();
</script>

</body>
</html>
