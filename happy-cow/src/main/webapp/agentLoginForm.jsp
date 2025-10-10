<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Agent Login - HappyCow Dairy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

    <style>
        body { background-color:#f9fafc; font-family:"Poppins", sans-serif; padding-top:80px; }
        .input-icon { position:absolute; right:15px; top:50%; transform:translateY(-50%); font-size:1.3rem; }
        .valid-icon { color:#28a745; } .invalid-icon { color:#dc3545; }
        #sendOtpBtn,#loginBtn { transition:all .3s ease; }
        #sendOtpBtn:disabled,#loginBtn:disabled { opacity:.7; cursor:not-allowed; }
        @keyframes fadeIn { from{opacity:0; transform:translateY(-5px);} to{opacity:1; transform:translateY(0);} }
        .alert { animation: fadeIn .5s; }
    </style>
</head>

<body data-ctx="${pageContext.request.contextPath}">
    <jsp:include page="navbar.jsp"/>

    <section id="login-section" class="py-5 bg-light" style="min-height:80vh; display:flex; align-items:center;">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-md-6 col-lg-5">
                    <div class="card shadow-lg border-0 rounded-4">
                        <div class="card-body p-5">
                            <h2 class="text-center mb-4 fw-bold text-success">
                                <i class="fa-solid fa-user-tie me-2"></i>Agent Login
                            </h2>

                            <c:if test="${not empty successMessage}">
                                            <div class="alert alert-success">${successMessage}</div>
                                        </c:if>
                                        <c:if test="${not empty errorMessage}">
                                            <div class="alert alert-danger">${errorMessage}</div>
                                        </c:if>

                            <div id="message-area" class="mb-3"></div>

                            <form id="agentLoginForm" method="post" novalidate>
                                <div class="mb-4 position-relative">
                                    <label for="email" class="form-label fw-semibold">Email Address</label>
                                    <input type="email" class="form-control form-control-lg rounded-3" id="email"
                                           name="email" placeholder="Enter your registered email" required autocomplete="off">
                                    <span id="emailStatusIcon" class="input-icon"></span>
                                    <div class="form-text text-muted">Enter the email you registered with.</div>
                                </div>

                                <div class="d-grid mb-4">
                                    <button type="button" id="sendOtpBtn"
                                            class="btn btn-outline-primary btn-lg rounded-3 shadow-sm"
                                            disabled>
                                        <i class="fa-solid fa-paper-plane me-2"></i>Send OTP
                                    </button>
                                </div>

                                <div class="mb-4">
                                    <label for="otp" class="form-label fw-semibold">Enter OTP</label>
                                    <input type="text" class="form-control form-control-lg rounded-3"
                                           id="otp" name="otp" placeholder="Enter 6-digit OTP"
                                           maxlength="6" disabled>
                                    <small id="timer" class="text-muted mt-2 d-block"></small>
                                </div>

                                <div class="d-grid">
                                    <button type="submit" id="loginBtn"
                                            class="btn btn-success btn-lg rounded-3 shadow"
                                            disabled>
                                        <i class="fa-solid fa-right-to-bracket me-2"></i>Login
                                    </button>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <jsp:include page="footer.jsp"/>

    <script>
    (function() {
        let otpTimer;
        let remainingTime = 300; // 5 minutes
        var ctx = document.body.getAttribute('data-ctx') || '';

        function showMessage(message, type) {
            type = type || 'info';
            var html =
              '<div class="alert alert-' + type + ' alert-dismissible fade show text-center shadow-sm" role="alert">' +
                message +
                '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
              '</div>';
            $('#message-area').html(html);

            var el = document.querySelector('#message-area .alert');
            if (el && window.bootstrap && bootstrap.Alert) {
                var bsAlert = bootstrap.Alert.getOrCreateInstance(el);
                setTimeout(function(){ try { bsAlert.close(); } catch(e) { $('#message-area').empty(); } }, 4000);
            } else {
                setTimeout(function(){ $('#message-area').empty(); }, 4000);
            }
        }

        var debounceTimer;
        $('#email').on('input', function () {
            clearTimeout(debounceTimer);
            var email = $(this).val().trim();
            var emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

            $('#emailStatusIcon').html('');
            $('#sendOtpBtn').prop('disabled', true);

            if (!emailRegex.test(email)) return;

            debounceTimer = setTimeout(function () {
                $.ajax({
                    url: ctx + '/agentLogin/check-email',
                    type: 'GET',
                    dataType: 'json',
                    data: { email: email },
                    success: function (response) {
                        var exists = (response && (response.exists === true || response.exists === 'true')) ||
                                     response === true || response === 'true';

                        if (!exists) {
                            $('#emailStatusIcon').html('<i class="fa-solid fa-circle-xmark invalid-icon"></i>');
                            showMessage('❌ Email not found in our system.', 'danger');
                            $('#sendOtpBtn').prop('disabled', true);
                        } else {
                            $('#emailStatusIcon').html('<i class="fa-solid fa-circle-check valid-icon"></i>');
                            showMessage('✅ Email verified successfully! You can now send OTP.', 'success');
                            $('#sendOtpBtn').prop('disabled', false);
                        }
                    },
                    error: function () {
                        showMessage('⚠️ Server error while checking email.', 'warning');
                    }
                });
            }, 500);
        });

        $('#sendOtpBtn').on('click', function () {
            var email = $('#email').val().trim();
            if (email === '') return;

            $('#sendOtpBtn').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Sending...');

            $.ajax({
                url: ctx + '/agentLogin/sendAgentOtp',
                type: 'POST',
                dataType: 'json',
                data: { email: email },
                success: function (response) {
                    var sent = (response && (response.sent === true || response.sent === 'true')) ||
                               response === true || response === 'true';
                    if (sent) {
                        showMessage('📩 OTP sent successfully! Please check your email.', 'success');
                        $('#otp').prop('disabled', false).focus();
                        $('#loginBtn').prop('disabled', false);
                        startTimer();
                    } else {
                        showMessage('❌ Failed to send OTP. Try again.', 'danger');
                        $('#sendOtpBtn').prop('disabled', false);
                    }
                },
                error: function () {
                    showMessage('⚠️ Server error while sending OTP.', 'warning');
                    $('#sendOtpBtn').prop('disabled', false);
                },
                complete: function () {
                    $('#sendOtpBtn').html('<i class="fa-solid fa-paper-plane me-2"></i>Send OTP');
                }
            });
        });

        function startTimer() {
            remainingTime = 300;
            clearInterval(otpTimer);
            otpTimer = setInterval(function () {
                remainingTime--;
                var minutes = Math.floor(remainingTime / 60);
                var seconds = remainingTime % 60;
                $('#timer').text('OTP valid for ' + minutes + ':' + String(seconds).padStart(2, '0') + ' minutes');

                if (remainingTime <= 0) {
                    clearInterval(otpTimer);
                    $('#timer').text('OTP expired. Please request again.');
                    $('#otp').prop('disabled', true).val('');
                    $('#loginBtn').prop('disabled', true);
                    $('#sendOtpBtn').prop('disabled', false);
                }
            }, 1000);
        }

        $('#agentLoginForm').on('submit', function (e) {
            e.preventDefault();
            var email = $('#email').val().trim();
            var otp = $('#otp').val().trim();

            if (otp.length !== 6 || !/^\d{6}$/.test(otp)) {
                showMessage('⚠️ Please enter a valid 6-digit OTP.', 'warning');
                return;
            }

            $('#loginBtn').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Verifying...');

            $.ajax({
                url: ctx + '/agentLogin/verifyAgentOtp',
                type: 'POST',
                dataType: 'json',
                data: { email: email, otp: otp },
                success: function (response) {
                    var verified = (response && (response.verified === true || response.verified === 'true')) ||
                                   response === true || response === 'true';
                    if (verified) {
                        showMessage('🎉 Login successful! Redirecting...', 'success');
                        setTimeout(function () { window.location.href = ctx + '/agentLoginSuccess'; }, 1500);
                    } else {
                        showMessage('❌ Invalid or expired OTP.', 'danger');
                        $('#loginBtn').prop('disabled', false).html('<i class="fa-solid fa-right-to-bracket me-2"></i>Login');
                    }
                },
                error: function () {
                    showMessage('⚠️ Server error during login verification.', 'warning');
                    $('#loginBtn').prop('disabled', false).html('<i class="fa-solid fa-right-to-bracket me-2"></i>Login');
                }
            });
        });
    })();
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
