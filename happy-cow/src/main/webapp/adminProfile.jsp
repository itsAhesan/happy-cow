<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Profile - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
        }
        .profile-card {
            max-width: 800px;
            margin: 3rem auto;
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .profile-header {
            background: #2ea44f;
            color: #fff;
            padding: 2rem;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
            text-align: center;
            position: relative;
        }
        .profile-pic-container {
            position: relative;
            display: inline-block;
            margin-bottom: 1rem;
        }
        .profile-pic {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            border: 4px solid #fff;
            object-fit: cover;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .profile-pic:hover {
            opacity: 0.8;
        }
        .form-label {
            font-weight: 600;
        }
        .change-photo-text {
            position: absolute;
            bottom: 10px;
            left: 0;
            right: 0;
            background: rgba(0,0,0,0.5);
            color: white;
            padding: 5px;
            font-size: 12px;
            opacity: 0;
            transition: opacity 0.3s ease;
            border-radius: 0 0 75px 75px;
        }
        .profile-pic-container:hover .change-photo-text {
            opacity: 1;
        }
        .file-input {
            display: none;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-custom px-3" style="background:#fff; border-bottom:1px solid #ddd;">
    <!-- Brand -->
    <a class="navbar-brand fw-bold text-success" href="adminDashboard">HappyCow Dairy</a>

    <!-- Center Search -->
    <div class="mx-auto search-box" style="max-width:400px;">
        <input type="text" class="form-control form-control-sm" placeholder="Search...">
    </div>

    <!-- Right Side: Dashboard Button + Profile -->
    <div class="d-flex align-items-center">
        <!-- ✅ Dashboard Button -->
        <a href="adminDashboard" class="btn btn-outline-success ms-3 rounded-pill px-3">
            <i class="fa-solid fa-gauge-high me-2"></i> Dashboard
        </a>

        <!-- ✅ Profile Dropdown -->
        <div class="dropdown ms-3">
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
                    <strong>${admin.adminName}</strong><br>
                    <small class="text-muted">${admin.emailId}</small>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item active" href="adminProfile.jsp"><i class="fa-solid fa-user me-2"></i>Profile</a></li>
                <li><a class="dropdown-item" href="#"><i class="fa-solid fa-gear me-2"></i>Settings</a></li>
                <li><a class="dropdown-item text-danger" href="logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Logout</a></li>
            </ul>
        </div>
    </div>
</nav>


<!-- Profile Content -->
<div class="profile-card">
    <div class="profile-header">
      <form id="profilePicForm"
            action="${pageContext.request.contextPath}/profile/upload-picture"
            method="post"
            enctype="multipart/form-data">

          <div class="profile-pic-container">
              <c:choose>
                  <c:when test="${not empty admin.profilePicture}">
                      <img id="profilePic"
                           src="data:${admin.profilePictureContentType};base64,${admin.profilePictureBase64}"
                           alt="Profile Picture" class="profile-pic">
                  </c:when>
                  <c:otherwise>
                      <img id="profilePic"
                           src="images/default-profile.png"
                           alt="Profile Picture" class="profile-pic">
                  </c:otherwise>
              </c:choose>

              <div class="change-photo-text">Change Photo</div>
              <input type="file" id="fileInput" name="file" class="file-input" accept="image/*">
          </div>
      </form>

        <h3 class="mb-0">${admin.adminName}</h3>
        <p class="text-light small mb-0">${admin.emailId}</p>
    </div>
    <div class="p-4">
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success text-center">${successMessage}</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger text-center">${errorMessage}</div>
        </c:if>

        <form action="profile/update" method="post">
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="name" class="form-label">Full Name</label>
                    <input type="text" class="form-control rounded-3" id="name" name="adminName"
                           value="${admin.adminName}" required>
                </div>
                <div class="col-md-6">
                    <label for="email" class="form-label">Email Address</label>
                    <input type="email" class="form-control rounded-3" id="email" name="emailId"
                           value="${admin.emailId}" readonly>
                </div>
            </div>

            <div class="mb-3">
                <label for="phone" class="form-label">Phone Number</label>
                <input type="text" class="form-control rounded-3" id="phone" name="phoneNumber"
                       value="${admin.phoneNumber}">
            </div>

            <hr>

            <div class="mb-3">
                <label for="password" class="form-label">Change Password</label>
                <input type="password" class="form-control rounded-3" id="password" name="password"
                       placeholder="Enter new password">
            </div>

            <div class="text-end">
                <button type="submit" class="btn btn-success rounded-pill px-4">
                    <i class="fa-solid fa-floppy-disk me-2"></i> Save Changes
                </button>
            </div>
        </form>
    </div>
</div>

<!-- ✅ Toast Notification -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 9999">
    <div id="toastMessage" class="toast align-items-center text-bg-success border-0" role="alert"
         aria-live="assertive" aria-atomic="true" data-bs-delay="3000">
        <div class="d-flex">
            <div class="toast-body" id="toastBody"></div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Trigger file input when clicking on the picture
  document.querySelector('.profile-pic-container').addEventListener('click', function () {
      document.getElementById('fileInput').click();
  });

  // Show toast helper
  function showToast(message, isSuccess = true) {
      const toastElement = document.getElementById("toastMessage");
      const toastBody = document.getElementById("toastBody");

      toastBody.textContent = message;

      if (isSuccess) {
          toastElement.classList.remove("text-bg-danger");
          toastElement.classList.add("text-bg-success");
      } else {
          toastElement.classList.remove("text-bg-success");
          toastElement.classList.add("text-bg-danger");
      }

      const toast = new bootstrap.Toast(toastElement);
      toast.show();
  }

  // Handle file upload with fetch API
  document.getElementById("fileInput").addEventListener("change", async function() {
      const file = this.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("file", file);

      try {
          const response = await fetch("${pageContext.request.contextPath}/profile/upload-picture", {
              method: "POST",
              body: formData,
              credentials: "same-origin"
          });

          if (!response.ok) throw new Error("Network response was not ok");
          const result = await response.json();

          if (result.success) {
              document.getElementById("profilePic").src =
                  "data:" + result.contentType + ";base64," + result.profilePicture;
              showToast(result.message, true);
          } else {
              showToast(result.message, false);
          }
      } catch (error) {
          console.error("Error:", error);
          showToast("Something went wrong while uploading. Try again!", false);
      }
  });
</script>

</body>
</html>
