<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<footer class="mt-5 bg-white border-top">
  <div class="container py-5">
    <div class="row g-4">
      <div class="col-md-4">
        <!-- Brand: logo + gradient text (matches admin navbar style) -->
        <div class="d-flex align-items-center mb-2">
          <img src="<c:url value='/images/happy-cow-logo.png'/>"
               alt="HappyCow logo" width="56" height="56"
               class="rounded-circle border border-2 border-success shadow-sm me-2"
               style="object-fit:cover;" loading="lazy">
          <span class="fw-bold fs-4"
                style="background: linear-gradient(90deg, #43e97b, #38f9d7);
                       -webkit-background-clip: text; -webkit-text-fill-color: transparent;
                       font-family: 'Montserrat','Segoe UI', Arial, sans-serif;
                       letter-spacing: 1.5px;">
            HappyCow
          </span>
        </div>

        <p class="text-muted mb-3">
          Freshness delivered daily. Manage your orders, routes, and earnings with a smooth agent dashboard.
        </p>
        <div class="d-flex gap-2">
          <a class="btn btn-sm btn-outline-secondary" href="#"><i class="fa-brands fa-facebook-f"></i></a>
          <a class="btn btn-sm btn-outline-secondary" href="#"><i class="fa-brands fa-x-twitter"></i></a>
          <a class="btn btn-sm btn-outline-secondary" href="#"><i class="fa-brands fa-instagram"></i></a>
          <a class="btn btn-sm btn-outline-secondary" href="#"><i class="fa-brands fa-linkedin-in"></i></a>
        </div>
      </div>

      <div class="col-6 col-md-2">
        <div class="fw-semibold mb-2">Product</div>
        <ul class="list-unstyled text-muted">
          <li><a class="text-reset text-decoration-none" href="${ctx}/milk">Milk Types</a></li>
          <li><a class="text-reset text-decoration-none" href="${ctx}/pricing">Pricing</a></li>
          <li><a class="text-reset text-decoration-none" href="${ctx}/delivery">Delivery</a></li>
          <li><a class="text-reset text-decoration-none" href="${ctx}/faq">FAQs</a></li>
        </ul>
      </div>

      <div class="col-6 col-md-2">
        <div class="fw-semibold mb-2">Company</div>
        <ul class="list-unstyled text-muted">
          <li><a class="text-reset text-decoration-none" href="${ctx}/about">About</a></li>
          <li><a class="text-reset text-decoration-none" href="${ctx}/careers">Careers</a></li>
          <li><a class="text-reset text-decoration-none" href="${ctx}/news">News</a></li>
          <li><a class="text-reset text-decoration-none" href="${ctx}/contact">Contact</a></li>
        </ul>
      </div>

      <div class="col-md-4">
        <div class="fw-semibold mb-2">Need help?</div>
        <p class="text-muted mb-2"><i class="fa-solid fa-headset me-2"></i>Agent Support: <a class="text-decoration-none" href="${ctx}/agent/support">Open a ticket</a></p>
        <p class="text-muted mb-2"><i class="fa-solid fa-envelope me-2"></i>Email: support@happycow.example</p>
        <p class="text-muted mb-3"><i class="fa-solid fa-phone me-2"></i>+91 90000 00000</p>

        <form class="d-flex" action="${ctx}/subscribe" method="post">
          <input class="form-control me-2" type="email" name="email" placeholder="Subscribe for updates" required>
          <button class="btn btn-success" type="submit">Join</button>
        </form>
      </div>
    </div>
  </div>

  <div class="bg-light py-3 border-top">
    <div class="container d-flex flex-column flex-md-row align-items-center justify-content-between">
      <small class="text-muted">&copy; <span id="year"></span> HappyCow Dairy. All rights reserved.</small>
      <div class="d-flex gap-3 mt-2 mt-md-0">
        <a class="text-muted text-decoration-none" href="${ctx}/terms">Terms</a>
        <a class="text-muted text-decoration-none" href="${ctx}/privacy">Privacy</a>
        <a class="text-muted text-decoration-none" href="${ctx}/cookies">Cookies</a>
      </div>
    </div>
  </div>
</footer>

<script>
  // set current year
  document.getElementById('year').textContent = new Date().getFullYear();
</script>
