<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Profile | HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root { --brand:#28a745; --ink:#0f172a; }
        body { background:#f7f9fc; font-family:"Poppins",sans-serif; padding-top: var(--nav-offset, 110px); }
        .hero { background:linear-gradient(135deg, rgba(40,167,69,.10), rgba(20,140,100,.10)); border-radius:20px; }
        .card { border:0; border-radius:16px; box-shadow:0 8px 22px rgba(0,0,0,.05); }
        .shadow-soft { box-shadow:0 6px 18px rgba(0,0,0,.08); }
        .muted { color:#64748b; }
        .avatar-wrap { width:110px; height:110px; border-radius:50%; overflow:hidden; background:#e9f7ef; display:flex; align-items:center; justify-content:center; position:relative; }
        .avatar-wrap img { width:100%; height:100%; object-fit:cover; display:block; }
        .avatar-initials { position:absolute; inset:0; display:none; align-items:center; justify-content:center; font-weight:700; font-size:1.75rem; color:#fff; background:linear-gradient(135deg, var(--brand), #16a085); }
        .pill { border:1px solid #e2e8f0; background:#f8fafc; padding:.35rem .65rem; border-radius:999px; font-size:.85rem; }
        .kbd { padding:.15rem .4rem; border:1px solid #cbd5e1; border-bottom-width:2px; border-radius:.35rem; background:#f8fafc; font-family:ui-monospace,Menlo,Consolas,monospace; font-size:.85rem; }
        .form-section-title { font-size: .95rem; letter-spacing:.04em; color:#6b7280; text-transform:uppercase; margin-bottom:.5rem; }
        .required:after { content:" *"; color:#dc3545; }
        .divider { height:1px; background:#e5e7eb; margin:1.25rem 0; }
        .subtle { color:#6b7280; font-size:.9rem; }
    </style>
</head>

<body data-ctx="${pageContext.request.contextPath}">
<jsp:include page="agentNavbar.jsp"/>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- Pick agent from request, else session -->
<c:choose>
    <c:when test="${not empty requestScope.agent}">
        <c:set var="agent" value="${requestScope.agent}" />
    </c:when>
    <c:otherwise>
        <c:set var="agent" value="${sessionScope.loggedInAgent}" />
    </c:otherwise>
</c:choose>

<!-- Initials (fallback avatar) -->
<c:set var="firstName" value="${empty agent.firstName ? 'Agent' : agent.firstName}" />
<c:set var="initialFirst" value="${fn:toUpperCase(fn:substring(firstName,0,1))}" />
<c:set var="initialLast"  value="${empty agent.lastName ? '' : fn:toUpperCase(fn:substring(agent.lastName,0,1))}" />
<c:set var="initials"     value="${initialFirst}${initialLast}" />

<section class="container">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item"><a href="${ctx}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${ctx}/agent/profile/dashboard">Dashboard</a></li>
            <li class="breadcrumb-item"><a href="${ctx}/agent/profile">Profile</a></li>
            <li class="breadcrumb-item active" aria-current="page">Edit</li>
        </ol>
    </nav>

    <!-- Header -->
    <div class="hero p-4 p-md-5 mb-4">
        <div class="d-flex align-items-start align-items-md-center flex-column flex-md-row gap-4">
            <div class="avatar-wrap shadow-sm">
                <c:choose>
                    <c:when test="${not empty agent.agentId}">
                        <img id="photoPreview"
                             src="${ctx}/agent/profile/photo/${agent.agentId}"
                             alt="Profile photo"
                             onerror="this.style.display='none'; document.getElementById('avatarInitialsTop').style.display='flex';">
                    </c:when>
                    <c:otherwise>
                        <img id="photoPreview"
                             src="${ctx}/images/default-profile.png"
                             alt="Profile photo"
                             onerror="this.style.display='none'; document.getElementById('avatarInitialsTop').style.display='flex';">
                    </c:otherwise>
                </c:choose>
                <div id="avatarInitialsTop" class="avatar-initials">${initials}</div>
            </div>
            <div class="flex-grow-1">
                <h1 class="h3 mb-1 text-success">
                    <i class="fa-solid fa-user-pen me-2"></i>Edit Profile
                </h1>
                <p class="mb-2 muted">Keep your information up to date. Changes apply immediately after saving.</p>
                <span class="pill"><i class="fa-solid fa-user-tie me-1"></i> Agent</span>
                <span class="pill ms-2"><i class="fa-solid fa-shield-halved me-1"></i> Secure</span>
            </div>
            <div class="d-flex gap-2">
                <a href="${ctx}/agent/profile" class="btn btn-outline-secondary">
                    <i class="fa-solid fa-arrow-left-long me-2"></i>Back to Profile
                </a>
            </div>
        </div>
    </div>

    <!-- Flash Alerts -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-circle-check me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-triangle-exclamation me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Bank module messages (from /agent/profile/bank or /bank/save) -->
    <c:if test="${not empty bankInfoMsg}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-circle-info me-2"></i>${bankInfoMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty bankError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-triangle-exclamation me-2"></i>${bankError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty bankSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-circle-check me-2"></i>${bankSuccess}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- =========================
         Main Edit Form
         ========================= -->
    <div class="card mb-5">
        <div class="card-body p-4 p-md-5">
            <form id="agentEditForm"
                  action="${ctx}/agent/profile/update"
                  method="post"
                  enctype="multipart/form-data"
                  novalidate>

                <!-- CSRF -->
                <c:if test="${not empty _csrf}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </c:if>

                <!-- Photo Row -->
                <div class="mb-4">
                    <div class="form-section-title">Profile Photo</div>
                    <div class="row g-3 align-items-center">
                        <div class="col-auto">
                            <div class="avatar-wrap shadow-sm">
                                <img id="photoPreviewInline" src="" alt="Inline preview" style="display:none;">
                                <div id="avatarInitialsInline" class="avatar-initials" style="display:none;">${initials}</div>
                            </div>
                        </div>
                        <div class="col-12 col-md-7">
                            <label for="imageFile" class="form-label">Change profile picture</label>
                            <input class="form-control" type="file" id="imageFile" name="imageFile"
                                   accept="image/png,image/jpeg,image/webp">
                            <div class="form-text">JPG, PNG, or WEBP. Max 2 MB.</div>
                            <div id="imageError" class="text-danger small mt-2" style="display:none;"></div>
                        </div>
                        <div class="col-12 col-md">
                            <c:if test="${not empty agent.agentId}">
                                <!-- Open confirmation modal -->
                                <button type="button"
                                        class="btn btn-outline-danger w-100 mt-2 mt-md-0"
                                        data-bs-toggle="modal"
                                        data-bs-target="#confirmRemovePhotoModal">
                                    <i class="fa-regular fa-trash-can me-2"></i>Remove Photo
                                </button>
                            </c:if>
                        </div>
                    </div>
                </div>

                <hr class="my-4">

                <!-- Basic Info -->
                <div class="mb-3">
                    <div class="form-section-title">Basic Information</div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="firstName" class="form-label required">First Name</label>
                            <input type="text" class="form-control" id="firstName" name="firstName"
                                   value="<c:out value='${agent.firstName}'/>"
                                   maxlength="50" required>
                            <div class="invalid-feedback">First name is required.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="lastName" class="form-label">Last Name</label>
                            <input type="text" class="form-control" id="lastName" name="lastName"
                                   value="<c:out value='${agent.lastName}'/>" maxlength="50">
                        </div>
                    </div>
                </div>

                <!-- Contact (read-only) -->
                <div class="mb-4">
                    <div class="form-section-title">Contact</div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email"
                                   value="<c:out value='${agent.email}'/>" readonly>
                            <div class="form-text">Email changes are managed by support for security.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="text" class="form-control" id="phone"
                                   value="<c:out value='${agent.phoneNumber}'/>" readonly>
                            <div class="form-text">Contact admin to update your phone number.</div>
                        </div>
                    </div>
                </div>

                <!-- Address & Preferences -->
                <div class="mb-2">
                    <div class="form-section-title">Address & Preferences</div>
                    <div class="row g-3">
                        <div class="col-12">
                            <label for="address" class="form-label">Address</label>
                            <textarea class="form-control" id="address" name="address"
                                      rows="3" maxlength="255"><c:out value="${agent.address}"/></textarea>
                        </div>
                        <div class="col-12">
                            <label for="typesOfMilk" class="form-label">Milk Type(s)</label>
                            <input type="text" class="form-control" id="typesOfMilk" name="typesOfMilk"
                                   value="<c:out value='${agent.typesOfMilk}'/>" readonly>
                            <div class="form-text">Contact admin to update your Milk Type(s).</div>
                        </div>
                    </div>
                </div>

                <div class="divider"></div>

                <!-- Actions -->
                <div class="d-flex flex-wrap gap-2 align-items-center">
                    <button type="submit" class="btn btn-success">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Save Changes
                    </button>
                    <a href="${ctx}/agent/profile" class="btn btn-outline-secondary">Cancel</a>

                    <!-- Bank Details CTA (moved AFTER Save/Cancel; “real project” placement) -->
                    <div class="ms-auto d-flex align-items-center gap-3">
                        <span class="subtle d-none d-md-inline">Manage payout details</span>
                        <a href="${ctx}/agent/profile/bank" class="btn btn-outline-success">
                            <i class="fa-solid fa-building-columns me-2"></i>Bank Details
                        </a>
                    </div>
                </div>

            </form>
        </div>
    </div>
</section>

<!-- Remove Photo: Confirmation Modal -->
<c:if test="${not empty agent.agentId}">
<div class="modal fade" id="confirmRemovePhotoModal" tabindex="-1" aria-labelledby="confirmRemovePhotoLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content shadow-soft">
      <div class="modal-header">
        <h5 class="modal-title" id="confirmRemovePhotoLabel">
            <i class="fa-regular fa-trash-can me-2 text-danger"></i>Remove Profile Photo
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        Are you sure you want to remove your current profile photo? This action cannot be undone.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
        <form id="removePhotoForm" action="${ctx}/agent/profile/photo/${agent.agentId}/delete" method="post" class="m-0">
            <c:if test="${not empty _csrf}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </c:if>
            <button type="submit" class="btn btn-danger">
                <i class="fa-regular fa-trash-can me-2"></i>Yes, Remove
            </button>
        </form>
      </div>
    </div>
  </div>
</div>
</c:if>

<jsp:include page="agentFooter.jsp"/>

<!-- JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function () {
    // Adjust body padding for fixed navbar height
    function adjustNavOffset() {
        var nav = document.querySelector('.navbar.fixed-top');
        if (!nav) return;
        var h = nav.offsetHeight || 90;
        document.body.style.setProperty('--nav-offset', (h + 20) + 'px');
    }
    window.addEventListener('load', adjustNavOffset);
    window.addEventListener('resize', adjustNavOffset);

    const form = document.getElementById('agentEditForm');
    const imageInput = document.getElementById('imageFile');
    const previewTop = document.getElementById('photoPreview');
    const previewInline = document.getElementById('photoPreviewInline');
    const initialsInline = document.getElementById('avatarInitialsInline');
    const imageError = document.getElementById('imageError');

    const MAX_BYTES = 2 * 1024 * 1024; // 2MB
    const ALLOWED = ['image/jpeg', 'image/png', 'image/webp'];

    function showError(msg) {
        imageError.textContent = msg;
        imageError.style.display = 'block';
    }
    function clearError() {
        imageError.textContent = '';
        imageError.style.display = 'none';
    }

    // Fallback to initials if top image fails
    if (previewTop) {
        previewTop.addEventListener('error', function(){
            previewTop.style.display = 'none';
            var el = document.getElementById('avatarInitialsTop');
            if (el) el.style.display = 'flex';
        });
        previewTop.addEventListener('load', function(){
            if (previewTop.naturalWidth === 0) {
                previewTop.dispatchEvent(new Event('error'));
            }
        });
    }

    // Image preview + validation
    if (imageInput) {
        imageInput.addEventListener('change', function () {
            clearError();
            const file = this.files && this.files[0];
            if (!file) return;

            if (file.size > MAX_BYTES) {
                showError('Image too large. Max size is 2 MB.');
                this.value = '';
                return;
            }
            if (!ALLOWED.includes(file.type)) {
                showError('Only JPG, PNG, or WEBP allowed.');
                this.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                const dataUrl = e.target.result;
                if (previewTop) {
                    previewTop.src = dataUrl;
                    previewTop.style.display = 'block';
                    var el = document.getElementById('avatarInitialsTop');
                    if (el) el.style.display = 'none';
                }
                if (previewInline) {
                    previewInline.src = dataUrl;
                    previewInline.style.display = 'block';
                    if (initialsInline) initialsInline.style.display = 'none';
                }
            };
            reader.readAsDataURL(file);
        });
    }

    // Profile form validation styling
    form.addEventListener('submit', function (e) {
        if (!form.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        form.classList.add('was-validated');
    }, false);
})();
</script>
</body>
</html>
