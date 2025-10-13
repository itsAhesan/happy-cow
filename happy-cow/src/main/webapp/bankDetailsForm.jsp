<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Bank Details | HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root { --brand:#28a745; --ink:#0f172a; }
        body {
            background:#f7f9fc;
            font-family:"Poppins",sans-serif;
            /* make room for fixed navbar (same approach as edit page) */
            padding-top: var(--nav-offset, 110px);
        }
        .hero {
            background:linear-gradient(135deg, rgba(40,167,69,.10), rgba(20,140,100,.10));
            border-radius:20px;
        }
        .card { border:0; border-radius:16px; box-shadow:0 8px 22px rgba(0,0,0,.05); }
        .form-section-title { font-size:.95rem; letter-spacing:.04em; color:#6b7280; text-transform:uppercase; margin-bottom:.5rem; }
        .required:after { content:" *"; color:#dc3545; }
    </style>
</head>
<body>
<jsp:include page="agentNavbar.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container pb-5"><!-- pb to keep footer spaced nicely -->
    <!-- Page header / not colliding with navbar thanks to --nav-offset -->
    <div class="hero p-4 p-md-5 mb-4">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
            <div>
                <h1 class="h4 mb-1 text-success">
                    <i class="fa-solid fa-building-columns me-2"></i>Add Bank Details
                </h1>
                <div class="text-muted">Provide your payout information. This can be submitted only once.</div>
            </div>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="${ctx}/">Home</a></li>
                    <li class="breadcrumb-item"><a href="${ctx}/agent/profile">Profile</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Add Bank Details</li>
                </ol>
            </nav>
        </div>
    </div>

    <!-- Alerts -->
    <c:if test="${not empty bankError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa-solid fa-triangle-exclamation me-2"></i>${bankError}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Form card -->
    <div class="card">
        <div class="card-body p-4 p-md-5">
            <div class="form-section-title">Bank Information</div>

            <form id="bankInfoForm" action="${ctx}/agent/profile/bank/save" method="post" novalidate class="needs-validation">
                <c:if test="${not empty _csrf}">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </c:if>

                <input type="hidden" name="agentId" value="${bankForm.agentId}"/>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label required">Bank Name</label>
                        <input type="text" class="form-control" name="bankName" maxlength="100" required value="${bankForm.bankName}">
                        <div class="invalid-feedback">Please enter bank name.</div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">Branch Name</label>
                        <input type="text" class="form-control" name="branchName" maxlength="100" required value="${bankForm.branchName}">
                        <div class="invalid-feedback">Please enter branch name.</div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">Account Holder Name</label>
                        <input type="text" class="form-control" name="accountHolderName" maxlength="100" required value="${bankForm.accountHolderName}">
                        <div class="invalid-feedback">Please enter account holder name.</div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">IFSC Code</label>
                        <input type="text"
                               class="form-control"
                               id="ifsc"
                               name="ifsc"
                               maxlength="11"
                               pattern="^[A-Z]{4}0[A-Z0-9]{6}$"
                               required
                               value="${bankForm.ifsc}">
                        <div class="form-text">11 characters (e.g., HDFC0XXXXXX). Auto-uppercased.</div>
                        <div class="invalid-feedback">Enter a valid IFSC (e.g., HDFC0XXXXXX).</div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">Account Number</label>
                        <input type="text"
                               class="form-control"
                               id="accountNumber"
                               name="accountNumber"
                               inputmode="numeric"
                               minlength="6"
                               maxlength="20"
                               required
                               value="${bankForm.accountNumber}">
                        <div class="invalid-feedback">Enter a valid account number.</div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">Confirm Account Number</label>
                        <input type="text"
                               class="form-control"
                               id="confirmAccountNumber"
                               name="confirmAccountNumber"
                               inputmode="numeric"
                               minlength="6"
                               maxlength="20"
                               required
                               value="${bankForm.confirmAccountNumber}">
                        <div class="invalid-feedback">Account numbers must match.</div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label required">Account Type</label>
                        <select class="form-select" name="accountType" required>
                            <option value="" ${empty bankForm.accountType ? 'selected' : ''} disabled>Choose type</option>
                            <option value="SAVINGS" ${bankForm.accountType=='SAVINGS'?'selected':''}>Savings</option>
                            <option value="CURRENT" ${bankForm.accountType=='CURRENT'?'selected':''}>Current</option>
                            <option value="SALARY"  ${bankForm.accountType=='SALARY'?'selected':''}>Salary</option>
                        </select>
                        <div class="invalid-feedback">Please select an account type.</div>
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-success">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Save
                    </button>
                    <a href="${ctx}/agent/profile" class="btn btn-outline-secondary">Cancel</a>
                </div>

                <div class="alert alert-warning mt-4 mb-0">
                    <i class="fa-solid fa-lock me-2"></i>
                    Bank details can be submitted only once. For any corrections later, please contact Payroll Support.
                </div>
            </form>
        </div>
    </div>
</section>

<jsp:include page="agentFooter.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function(){
  // Keep content pushed below fixed navbar (same logic as other pages)
  function adjustNavOffset() {
      var nav = document.querySelector('.navbar.fixed-top');
      if (!nav) return;
      var h = nav.offsetHeight || 90;
      document.body.style.setProperty('--nav-offset', (h + 20) + 'px');
  }
  window.addEventListener('load', adjustNavOffset);
  window.addEventListener('resize', adjustNavOffset);

  const form = document.getElementById('bankInfoForm');
  const acc  = document.getElementById('accountNumber');
  const acc2 = document.getElementById('confirmAccountNumber');
  const ifsc = document.getElementById('ifsc');

  // Auto-uppercase IFSC
  function syncIFSCUpper(){ if (ifsc) ifsc.value = ifsc.value.toUpperCase(); }
  if (ifsc) {
      ifsc.addEventListener('input', syncIFSCUpper);
      ifsc.addEventListener('blur', syncIFSCUpper);
  }

  // Inline validity for account match (no alerts; real-project UX)
  function validateAccountsMatch() {
      if (!acc || !acc2) return;
      if (acc.value && acc2.value && acc.value !== acc2.value) {
          acc2.setCustomValidity('DoesNotMatch');
      } else {
          acc2.setCustomValidity('');
      }
  }
  if (acc)  acc.addEventListener('input', validateAccountsMatch);
  if (acc2) acc2.addEventListener('input', validateAccountsMatch);

  // Bootstrap validation styling
  form.addEventListener('submit', function(e){
    validateAccountsMatch();
    if (!form.checkValidity()) {
      e.preventDefault();
      e.stopPropagation();
    }
    form.classList.add('was-validated');
  });
})();
</script>
</body>
</html>
