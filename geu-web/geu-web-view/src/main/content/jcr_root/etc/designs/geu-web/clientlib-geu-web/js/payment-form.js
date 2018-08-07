$(document).ready(function() {
    var mobileNumber;
    $('#partiallyFilledOTPForm').hide();
    $(".payment-page-wrapper #formNumber").on('keypress focus, keypress, keydown, keyup, mousedown, mouseup', function() {
        $(".payment-page-wrapper .partially-filled-form").addClass('changebg');
    });
    var counter = 0;
    var formType = $("#formType").val();

    function sendOTP(responseText) {
        $.ajax({
            type: "POST",
            url: "/bin/smsOTPServlet",
            data: "formAction=sendotp&phoneNumber=" + responseText,
            success: function(responseText) {

            },
        });
        sendOTP.called = true;
    }
    $("#partiallyFilledPaymentForm").on('click', function(e) {
        e.preventDefault();
        var isFormValid = true;
        var formNumber = $("#formNumber").val();

        if (formNumber.length == 0) {
            $(".partially-filled-form-error-message").text("Please enter Form Id");
            isFormValid = false;
        } else if (formNumber.length != 0) {
            $.ajax({
                type: "GET",
                url: "/bin/geu/paymentFormNumber?formNumber=" + formNumber + "&formType=" + formType,
                success: function(responseText) {
                    if (responseText != "Invalid Form Number") {
                        if (responseText == "Not Submitted") {
                            $(".partially-filled-form-error-message").text("Please submit the admission form first");
                            isFormValid = false;
                        } else if (responseText == "Successful") {
                            $(".partially-filled-form-error-message").text("You have already made the successful payment.");
                            isFormValid = false;
                        } else if (responseText != "") {
                            $('.otp-wrapper').show();
                            $("#enterOtp").css("visibility", "visible");
                            $("#partiallyFilledPaymentForm").val("Verify OTP & Proceed");

                            $(".partially-filled-form-error-message").text('');
                            $("#hideMsg span").html("5");
                            var enterOtp = $("#enterOtp").val();
                            if (enterOtp.length != "6" && enterOtp != '') {
                                $(".partially-filled-form-error-message").text("Please Enter Alleast 6 Digits");
                                isFormValid = false;
                            } else {
                                $.ajax({
                                    type: "POST",
                                    url: "/bin/smsOTPServlet",
                                    data: "formAction=verifyotp&otpcode=" + enterOtp,
                                    success: function(responseText) {
                                        if (responseText == 'valid_otp') {
                                            isFormValid = true;
                                            $(".partially-filled-form").hide();
                                            $(".terms-wrapper").show();
                                            return false;
                                        } else if (enterOtp.length == "6" && enterOtp != responseText) {
                                            $(".partially-filled-form-error-message").text("Please enter valid OTP");
                                            isFormValid = false;
                                        }
                                    },
                                });
                            }
                            var sec = 4;
                            if (!(setInterval.called)) {
                                $('#hideMsg').show();
                                var timer = setInterval(function() {
                                    $('#hideMsg span').text(sec--);
                                    if (sec == -1) {
                                        $('#hideMsg').fadeOut('fast');
                                        $("#sendPaymentOtp").prop("disabled", null);
                                        clearInterval(timer);
                                        $("#partiallyFilledPaymentForm").hide();
                                        $('#partiallyFilledOTPForm').show();
                                    }
                                    setInterval.called = true;
                                }, 1000);
                            }
                            if (!(sendOTP.called)) {
                                mobileNumber = responseText;
                                sendOTP(mobileNumber);
                            }
                        }
                    } else if (responseText == "Invalid Form Number") {
                        $(".partially-filled-form-error-message").text("Please enter valid Form Id");
                        isFormValid = false;
                    } else {
                        $(".partially-filled-form-error-message").text("");
                        isFormValid = false;
                    }
                },
            });
        }

        $.ajax({
            type: "GET",
            url: "/bin/geu/courseFees?formId=" + formNumber + "&formType=" + formType,
            success: function(response) {
                $.each(response, function() {
                    if (counter <= 0) {
                        $("#discountMessage").text("(will get " + this['discount'] + "% discount)");
                        $("#totalFees").append("&#8377; " + this['totalFees'].toLocaleString('en-IN'));
                        $("#discount").text("Discount (" + this['discount'] + "%)");
                        $("#discountAmount").append("&#8377; " + this['discountAmount'].toLocaleString('en-IN'));
                        $("#totalPayment").append("&#8377; " + this['totalPayment'].toLocaleString('en-IN'));
                        $("#vpc_Amount").val(this['totalFees']);
                        counter++;
                    }
                });
            },
        });

    });
    
    $("#sendPaymentOtp").click(function() {
        $("#sendPaymentOtp").prop("disabled", true);
        $("#hideMsg span").html("5");
        $('#hideMsg').show();
        //$("#partiallyFilledOTPForm").val("Verify OTP & Proceed");
        var sec = 4;
        var timer = setInterval(function() {
            $('#hideMsg span').text(sec--);
            if (sec == -1) {
                $('#hideMsg').fadeOut('fast');
                $("#sendPaymentOtp").prop("disabled", null);
                clearInterval(timer);
            }
        }, 1000);
        $.ajax({
            type: "POST",
            url: "/bin/smsOTPServlet",
            data: "formAction=sendotp&phoneNumber=" + mobileNumber,
            success: function(responseText) {
                otp = responseText;
            },
        });
    });


    $("#partiallyFilledOTPForm").on('click', function(e) {
        e.preventDefault();
        var enterOtp = $("#enterOtp").val();
        if (enterOtp.length == "") {
            $(".partially-filled-form-error-message").text("*Please Provide OTP");
            isFormValid = false;
        } else {
            $.ajax({
                type: "POST",
                url: "/bin/smsOTPServlet",
                data: "formAction=verifyotp&otpcode=" + enterOtp,
                success: function(responseText) {
                    if (responseText == 'valid_otp') {
                        isFormValid = true;
                        $(".partially-filled-form").hide();
                        $(".terms-wrapper").show();
                        return false;
                    } else {
                        if (enterOtp.length != "") {
                            $(".partially-filled-form-error-message").text("*Please Provide Valid OTP");
                            isFormValid = false;
                        }
                    }
                },
            });
        }
    });

    $('input[type=radio][name=fee-payment]').change(function() {
        if ($('#fullFeePayment').is(':checked')) {
            $('.part-payment').removeClass('changebg')
            $('.full-payment').addClass('changebg')
            $('#amount').val('')
            $('#amount').prop('required', false)
        }
        if ($('#partialPayment').is(':checked')) {
            $('.full-payment').removeClass('changebg')
            $('.part-payment').addClass('changebg')
        }
    });

    $('input[type=radio][name=payment-mode]').change(function() {
        if ($('#onlinePayment').is(':checked')) {
            $('.payment-gateway-right-content').removeClass('changebg')
            $('.payment-gateway-left-content').addClass('changebg')
        }
        if ($('#offlinePayment').is(':checked')) {
            $('.payment-gateway-left-content').removeClass('changebg')
            $('.payment-gateway-right-content').addClass('changebg')
        }
    });

    $('#proceedTerms').on('click', function() {
        if ($("#termsAndCondition").prop('checked') == false || $("#declaration").prop('checked') == false) {
            $(".terms-error").text("Please Agree Terms and declaration");
            isFormValid = false;
        } else {
            isFormValid = true;
            $(".terms-error").text('');
            $('.payment').show();
            return false;
        }

        return isFormValid;
    });

    $(document).on("keypress", "#enterOtp", function(e) {
        $("#enterOtp").attr('maxlength', '6');
        var regex = new RegExp("^[0-9.]+$");
        var str = String.fromCharCode(!e.charCode && key != 8 && key != 46 ? e.which : e.charCode);
        if (!regex.test(str)) {
            e.preventDefault();
            return false;
        }
    });

    $(document).on("keypress", "#amount", function(e) {
        var regex = new RegExp("^[0-9.]+$");
        var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
        if (!regex.test(str)) {
            e.preventDefault();
            return false;
        }
    });

    $('#paymentSubmit').on('click', function(e) {
        e.preventDefault();
        var isFormValid = true;
        if ($('input[name="fee-payment"]:checked').length <= 0 && $('input[name="payment-mode"]:checked').length <= 0) {
            $(".payment-option-error").text("Please Select Payment Option");
            $(".payment-mode-error").text("Please Select Payment Mode");
            $("body, html").animate({
                scrollTop: $(".payment-option-error").offset().top - 10
            }, 600);
            isFormValid = false;
        } else if ($('input[name="fee-payment"]:checked').length <= 0 && $('input[name="payment-mode"]:checked').length > 0) {
            $(".payment-option-error").text("Please Select Payment Option");
            $(".payment-mode-error").text("");
            $("body, html").animate({
                scrollTop: $(".payment-option-error").offset().top - 10
            }, 600);
            isFormValid = false;
        } else if ($('input[name="fee-payment"]:checked').length >= 0 && $('input[name="payment-mode"]:checked').length <= 0) {
            $(".payment-option-error").text('');
            $(".payment-mode-error").text("Please Select Payment Mode");
            $("body, html").animate({
                scrollTop: $(".payment-option-error").offset().top - 10
            }, 600);
            isFormValid = false;
        } else if ($('#partialPayment:checked').length > 0 && $('#amount').val() == '') {
            $('#amount').prop('required', true);
            $(".payment-option-error").text('');
            $(".payment-mode-error").text("Please Enter The Partial Fee Amount");
            $("body, html").animate({
                scrollTop: $(".payment-mode-error").offset().top - 10
            }, 600);
            if ($("#amount").val().length == '') {
                isFormValid = false;
            }
        } else if ($('#partialPayment:checked').length > 0 && $("#amount").val().length != '' && $('#amount').val() < 20000) {
            $('#amount').prop('required', true);
            $(".payment-mode-error").text("Please enter minimum partial fee amount 20,000");
            $("body, html").animate({
                scrollTop: $(".payment-mode-error").offset().top - 10
            }, 600);
            isFormValid = false;
        }
        if (($('input[name="fee-payment"]:checked').length >= 0 || $('input[name="payment-mode"]:checked').length >= 0) && $('#onlinePayment:checked').length > 0 && isFormValid == true) {
            $(".partially-filled-form, .terms-wrapper").hide();
            var feeType = "";
            var amount = "";
            if ($('#partialPayment:checked').length > 0) {
                feeType = "Partial Fee";
                amount = $('#amount').val();
            } else if ($('#fullFeePayment:checked').length > 0) {
                feeType = "Full Fee";
                amount = $("#vpc_Amount").val();
            }
            $.ajax({
                type: "POST",
                url: "/bin/geu/feePaymentType",
                data: "paymentType=" + feeType,
                success: function(responseText) {

                },
            });
            window.location.href = $("#vpc_ReturnURL").val() + '?vpc_Version=' + $("#vpc_Version").val() + '&vpc_AccessCode=' + $("#vpc_AccessCode").val() + '&vpc_Command=' + $("#vpc_Command").val() + '&vpc_MerchTxnRef=' + $("#vpc_MerchTxnRef").val() + '&vpc_MerchantId=' + $("#vpc_MerchantId").val() + '&vpc_Amount=' + amount + '00&vpc_ReturnURL=' + $("#vpc_ReturnURL").val();
            
            return false;
        } else if (($('input[name="fee-payment"]:checked').length >= 0 || $('input[name="payment-mode"]:checked').length >= 0) && $('#offlinePayment:checked').length > 0 && isFormValid == true) {
            $(".partially-filled-form, .terms-wrapper").hide();
            var feeType = "";
            var amount = "";
            if ($('#partialPayment:checked').length > 0) {
                feeType = "Partial Fee";
                amount = $('#amount').val();
            } else if ($('#fullFeePayment:checked').length > 0) {
                feeType = "Full Fee";
                amount = $("#vpc_Amount").val();
            }

            $.ajax({
                type: "POST",
                url: "/bin/geu/offlinePayment",
                data: "feeAmount=" + amount + "&feeType=" + feeType + "&formType=" + formType,
                success: function(responseText) {
                    window.location.href = '/content/geu/en/admission-aid/payment/offline-thank-you.html';
                },
            });

            return false;
        }
        return isFormValid;
    })

    $('#paymentSubmitGehu').on('click', function(e) {
        e.preventDefault();
        var isFormValid = true;
        if ($('input[name="fee-payment"]:checked').length <= 0 && $('input[name="payment-mode"]:checked').length <= 0) {
            $(".payment-option-error").text("Please Select Payment Option");
            $(".payment-mode-error").text("Please Select Payment Mode");
            $("body, html").animate({
                scrollTop: $(".payment-option-error").offset().top - 10
            }, 600);
            isFormValid = false;
        } else if ($('input[name="fee-payment"]:checked').length <= 0 && $('input[name="payment-mode"]:checked').length > 0) {
            $(".payment-option-error").text("Please Select Payment Option");
            $(".payment-mode-error").text("");
            $("body, html").animate({
                scrollTop: $(".payment-option-error").offset().top - 10
            }, 600);
            isFormValid = false;
        } else if ($('input[name="fee-payment"]:checked').length >= 0 && $('input[name="payment-mode"]:checked').length <= 0) {
            $(".payment-option-error").text('');
            $(".payment-mode-error").text("Please Select Payment Mode");
            $("body, html").animate({
                scrollTop: $(".payment-option-error").offset().top - 10
            }, 600);
            isFormValid = false;
        } else if ($('#partialPayment:checked').length > 0 && $('#amount').val() == '') {
            $('#amount').prop('required', true);
            $(".payment-option-error").text('');
            $(".payment-mode-error").text("Please Enter The Partial Fee Amount");
            $("body, html").animate({
                scrollTop: $(".payment-mode-error").offset().top - 10
            }, 600);
            if ($("#amount").val().length == '') {
                isFormValid = false;
            }
        } else if ($('#partialPayment:checked').length > 0 && $("#amount").val().length != '' && $('#amount').val() < 20000) {
            $('#amount').prop('required', true);
            $(".payment-mode-error").text("Please enter minimum partial fee amount 20,000");
            $("body, html").animate({
                scrollTop: $(".payment-mode-error").offset().top - 10
            }, 600);
            isFormValid = false;
        }
        if (($('input[name="fee-payment"]:checked').length >= 0 || $('input[name="payment-mode"]:checked').length >= 0) && $('#onlinePayment:checked').length > 0 && isFormValid == true) {
            $(".partially-filled-form, .terms-wrapper").hide();
            var feeType = "";
            var amount = "";
            if ($('#partialPayment:checked').length > 0) {
                feeType = "PartialFee";
                amount = $('#amount').val();
            } else if ($('#fullFeePayment:checked').length > 0) {
                feeType = "FullFee";
                amount = 0;
            }
            $.ajax({
                type: "POST",
                url: "/bin/geu/feePaymentType",
                data: "paymentType=" + feeType,
                success: function(responseText) {

                },
            });
            //window.location.href = $("#vpc_ReturnURL").val() + '?merchant_id=' + $("#merchant_id").val() + '&order_id=' + $("#order_id").val() + '&currency=' + $("#currency").val() + '&amount=' + amount + '&redirect_url=' + $("#return_url").val() + '&cancel_url=' + $("#cancel_url").val() + '&language=' + $("#language").val();
            window.location.href = $("#vpc_ReturnURL").val() + '?feeType=' + feeType + '&amount=' + amount +'&formId=' + $("#formNumber").val() + '&selectionMode=admission';
            return false;
        } else if (($('input[name="fee-payment"]:checked').length >= 0 || $('input[name="payment-mode"]:checked').length >= 0) && $('#offlinePayment:checked').length > 0 && isFormValid == true) {
            $(".partially-filled-form, .terms-wrapper").hide();
            var feeType = "";
            var amount = "";
            if ($('#partialPayment:checked').length > 0) {
                feeType = "PartialFee";
                amount = $('#amount').val();
            } else if ($('#fullFeePayment:checked').length > 0) {
                feeType = "FullFee";
                amount = 0;
            }

            $.ajax({
                type: "POST",
                url: "/bin/geu/offlinePayment",
                data: "feeAmount=" + amount + "&feeType=" + feeType + "&formType=" + formType,
                success: function(responseText) {
                    //window.location.href = '/content/gehu/en/admission-aid/payment/thank-you.html';
                    window.location.href = 'https://www.eduqfix.com/PayDirect/#/student';
                },
            });

            return false;
        }
        return isFormValid;
    })
});