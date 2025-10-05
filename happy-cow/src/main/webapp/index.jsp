<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">  <%-- ✅ Mobile responsiveness --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

   <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- ✅ Fixed Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-lg py-3 fixed-top" style="min-height:90px; z-index: 1030;">
    <div class="container">
        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="#">
            <img src="<c:url value='/images/happy-cow-logo.png'/>" alt="HappyCow logo" width="70" height="70" class="rounded-circle border border-3 border-success shadow-lg me-3" style="object-fit:cover;" loading="lazy">
            <span class="fs-2 fw-bold" style="background: linear-gradient(90deg, #43e97b, #38f9d7); -webkit-background-clip: text; -webkit-text-fill-color: transparent; font-family: 'Montserrat', 'Segoe UI', Arial, sans-serif; letter-spacing: 2px; text-shadow: 2px 2px 8px #fff, 0 2px 8px #ccc;">HappyCow</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarNav" aria-controls="navbarNav"
                aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto gap-2">
                <li class="nav-item"><a class="nav-link active fs-5 fw-bold px-3 py-2 rounded-pill text-success" href="#" style="background:rgba(255,255,255,0.1); transition:0.3s;">Home</a></li>
               <li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle fs-5 fw-bold px-3 py-2 rounded-pill text-success" href="#"
       id="productsDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false"
       style="background:rgba(255,255,255,0.1); transition:0.3s;">Products</a>
    <ul id="navbar-products" class="dropdown-menu" aria-labelledby="productsDropdown">
        <!-- ✅ Will be filled dynamically -->
    </ul>
</li>

                <li class="nav-item"><a class="nav-link fs-5 fw-bold px-3 py-2 rounded-pill text-success d-flex align-items-center gap-2" href="#about-section" style="background:rgba(255,255,255,0.1); transition:0.3s;"><i class="fa-solid fa-users fa-sm"></i> About Us</a></li>
                <li class="nav-item"><a class="nav-link fs-5 fw-bold px-3 py-2 rounded-pill text-success d-flex align-items-center gap-2" href="#contact-section" style="background:rgba(255,255,255,0.1); transition:0.3s;"><i class="fa-solid fa-envelope fa-sm"></i> Contact</a></li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle fs-5 fw-bold px-3 py-2 rounded-pill text-success d-flex align-items-center gap-2" href="#" id="loginDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false" style="background:rgba(255,255,255,0.1); transition:0.3s;">
                        <i class="fa-solid fa-right-to-bracket fa-sm"></i> Login
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="loginDropdown">
                        <li><a class="dropdown-item d-flex align-items-center gap-2 py-2" href="adminLogin"><i class="fa-solid fa-user-shield fa-sm text-dark"></i> Admin Login</a></li>
                        <li><a class="dropdown-item d-flex align-items-center gap-2 py-2" href="customerLogin.jsp"><i class="fa-solid fa-user fa-sm text-primary"></i> Customer Login</a></li>
                        <li><a class="dropdown-item d-flex align-items-center gap-2 py-2" href="agentLogin.jsp"><i class="fa-solid fa-user-tie fa-sm text-success"></i> Agent Login</a></li>
                        <li><a class="dropdown-item d-flex align-items-center gap-2 py-2" href="#"><i class="fa-solid fa-user-plus fa-sm text-info"></i> Registration</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>


<!-- Hero Section with working background -->
<section class="hero text-center position-relative">
    <img src="<c:url value='/images/cow.jpg'/>" alt="HappyCow Dairy" class="hero-img">

    <!-- Gradient overlay for desktop only -->
    <div class="overlay d-none d-md-block"></div>

    <!-- Text content -->
    <div class="container hero-text">
        <h1 class="display-2 fw-bold mb-3">Welcome to <span style="color:#198754;">HappyCow Dairy</span></h1>
        <p class="lead mb-4">Pure, Fresh, and Healthy Milk Products for Everyone</p>
        <a href="#login-section" class="btn btn-success btn-lg px-5 py-3 shadow-lg fs-4">Get Started</a>
    </div>
</section>



<!-- Featured Products Section -->
<section id="products-section" class="py-5 bg-light">
    <div class="container">
        <h2 class="text-center mb-5 fw-bold">Our Products</h2>
        <div id="products-container" class="row g-4 justify-content-center">
            <!-- ✅ Products will be injected here dynamically from JSON -->
        </div>
    </div>
</section>

<!-- About Us Section -->
<section id="about-section" class="py-5 bg-white border-top">
    <div class="container">
        <h2 class="text-center fw-bold mb-4"><i class="fa-solid fa-users text-success me-2"></i>About Us</h2>
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 text-center">
                <p class="lead mb-4">HappyCow Dairy is dedicated to providing pure, fresh, and healthy milk products directly from our farms to your home. With a legacy of quality and trust, we ensure every product meets the highest standards of nutrition and taste.</p>
            </div>
        </div>
        <div class="row text-center mt-4">
            <div class="col-12 col-md-4 mb-3">
                <i class="fa-solid fa-cow fa-2x text-success mb-2"></i>
                <h5 class="fw-bold">Farm Fresh</h5>
                <p>Directly sourced from our own dairy farms.</p>
            </div>
            <div class="col-12 col-md-4 mb-3">
                <i class="fa-solid fa-leaf fa-2x text-success mb-2"></i>
                <h5 class="fw-bold">100% Natural</h5>
                <p>No preservatives or additives.</p>
            </div>
            <div class="col-12 col-md-4 mb-3">
                <i class="fa-solid fa-truck-fast fa-2x text-success mb-2"></i>
                <h5 class="fw-bold">Fast Delivery</h5>
                <p>Quick and safe delivery to your doorstep.</p>
            </div>
        </div>
    </div>
</section>

<!-- Contact Section -->
<section id="contact-section" class="py-5 bg-light border-top">
    <div class="container">
        <h2 class="text-center fw-bold mb-4"><i class="fa-solid fa-envelope text-success me-2"></i>Contact Us</h2>
        <div class="row justify-content-center">
            <div class="col-12 col-md-6 mb-4">
                <div class="bg-white p-4 rounded shadow-sm">
                    <h5 class="fw-bold mb-3">Get in Touch</h5>
                    <form>
                        <div class="mb-3">
                            <input type="text" class="form-control" placeholder="Your Name" required>
                        </div>
                        <div class="mb-3">
                            <input type="email" class="form-control" placeholder="Your Email" required>
                        </div>
                        <div class="mb-3">
                            <textarea class="form-control" rows="3" placeholder="Your Message" required></textarea>
                        </div>
                        <button type="submit" class="btn btn-success w-100">Send Message</button>
                    </form>
                </div>
            </div>
            <div class="col-12 col-md-5 d-flex flex-column justify-content-center">
                <div class="mb-3">
                    <i class="fa-solid fa-phone fa-lg text-success me-2"></i>
                    <span class="fw-bold">Phone:</span> +91 97495 95125
                </div>
                <div class="mb-3">
                    <i class="fa-solid fa-envelope fa-lg text-success me-2"></i>
                    <span class="fw-bold">Email:</span> chowdhuryahesan@gmail.com
                </div>
                <div class="mb-3">
                    <i class="fa-solid fa-location-dot fa-lg text-success me-2"></i>
                    <span class="fw-bold">Address:</span> Banashankari 3rd stage, Bengaluru, Karnataka, India
                </div>
            </div>
        </div>
    </div>
</section>

<footer>
    <div class="container">
        <p class="mb-0">© 2025 HappyCow Dairy. All Rights Reserved.</p>
    </div>
</footer>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script src="js/products.js"></script>

</body>
</html>

