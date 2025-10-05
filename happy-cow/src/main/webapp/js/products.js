document.addEventListener("DOMContentLoaded", function () {
    fetch('data/products.json')
        .then(response => response.json())
        .then(products => {
            const productContainer = document.getElementById("products-container");
            const navbarDropdown = document.getElementById("navbar-products");

            products.forEach(p => {
                // ✅ Add product card in Products section
                productContainer.innerHTML += `
                    <div class="col-12 col-sm-6 col-md-4 col-lg-3">
                        <div class="card h-100 shadow-sm">
                            <img src="${p.image}" class="card-img-top img-fluid" alt="${p.name}">
                            <div class="card-body text-center">
                                <h5 class="card-title">${p.name}</h5>
                                <p class="card-text">${p.price}</p>
                                <a href="#" class="btn btn-outline-primary">Order Now</a>
                            </div>
                        </div>
                    </div>
                `;

                // ✅ Add product item in Navbar dropdown
                navbarDropdown.innerHTML += `
                    <li>
                        <a class="dropdown-item d-flex align-items-center gap-2 py-2" href="#products-section">
                            <i class="fa-solid fa-circle fa-sm text-success"></i> ${p.name}
                        </a>
                    </li>
                `;
            });
        })
        .catch(err => console.error("Failed to load products:", err));
});
