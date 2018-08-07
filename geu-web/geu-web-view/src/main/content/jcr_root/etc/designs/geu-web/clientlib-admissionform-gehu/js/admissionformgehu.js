$(document).ready(function() {
        var pcm =0;
        var jee = 0;
        var sslcAggregate  = 0;
        var hscPercentage = 0;
        var ugPercentage = 0;
        var state;
        var campus;
        var levelValue;
        var videoCheck;
        var uploadHscSectionLabel;
        var uploadUgSectionLabel;
        var hscmandateLabels;
        var ugmandateLabels; 
        var ispcmorjeemandatory;
        var myTimer;
        var formIdSet;

    if ($(".form-two-column-wrapper").is(':visible')) {
        $('.form-tabs ul li:first').addClass('selected');
    }
    var resourcePath = $('#resourcePath').val();

    $(document).on("keypress", "#mobile, #enterOtp, #enterOtpPartially", function(e) {
        $("#mobile").attr('maxlength', '10');
        $("#enterOtp, #enterOtpPartially").attr('maxlength', '6');
        var regex = new RegExp("^[0-9.]+$");
        var str = String.fromCharCode(!e.charCode && key != 8 && key != 46  ? e.which : e.charCode);
        if (!regex.test(str)) {
            e.preventDefault();
            return false;
        }
    });

    var validationInputs = $("#fname, #lname, #email, #mobile, #area-of-interest");

    //Label Star Mark
    var validationLabels = $(".fname label, .lname label, .email label, .mobile label");
    $(validationLabels).addClass("required");

    $(validationInputs).on('change focusin focus focusout keypress keydown keyup mousedown mouseup', function() {
        clearInterval(myTimer);
        $(".login-component-wrapper").addClass('changebg');
        $(".partially-filled-form").removeClass('changebg');
        $(".partially-filled-form #formNumber, #enterOtpPartially").val('');
        $("#partiallyFilledForm").val('Proceed');
        $(" #formNumber, #enterOtpPartially").prop("required", false);
        $(".partially-filled-form-error-message").text("");
        $("#enterOtpPartially, #sendOtpPartially").css("visibility", "hidden");
        $('#hideMsgPartially').hide();
    });

    //GetOTP
    function getOTP() {
        $.ajax({
            type: "POST",
            url: "/bin/gehu/loginAdmissionForm",
            data: $('#registration-form').serialize()+ '&formAction=sendotp' + '&type=applyFresh',
            success: function(msg) {
            },
        });
        getOTP.called = true;
    }

    //getDetails
    function getDetails() {
        $.ajax({
            type: "POST",
            url: "/bin/gehu/loginAdmissionForm",
            data: $('#registration-form').serialize() + '&resourcePath=' + resourcePath + '&type=applyFresh' + '&formAction=verifyotp' + '&otpcode=' + $("#enterOtp").val() + '&phoneNumber=' + $('#mobile').val(),
            success: function(msg) {
                if (msg == "You have already registered. Please use continue Partially Filled Form.") {
                    $('#hideMsg').hide();
                    $(".form-error-message").text("You have already registered. Please use continue Partially Filled Form.");
                    $(".basic-details-section").hide();
                    $(".form-two-column-wrapper").show();
                    return false;
                }
                else {
                    if(!(getOTP.called)) {
                        getOTP();
                        $("#login-form-submit").val("Verify OTP & Proceed");
                        $("#enterOtp, #sendOtp").css("visibility", "visible");
                    }
                }
            }
        });
        getDetails.called = true;
    }
    $("#login-form-submit").on('click', function(e) {
        $(".login-component-wrapper").addClass('changebg');
        var validateEmail = function(elementValue) {
            var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
            return emailPattern.test(elementValue);
        }
        $('.login-component-wrapper input').prop('required', false).each(function(){
            $(this).val() || $(this).closest('div').find('input').prop('required', true);
        });
        var value = $('#email').val(); 
        var valid = validateEmail(value);

        var isFormValid = true;
        $(".form-error-message, .invalid-error-message, .custom-error-message").text("");
            if ($("#fname").val().length == 0 && $("#lname").val().length == 0 && $("#email").val().length == 0 && $("#mobile").val().length == 0) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                isFormValid = false;
            } 
            else if ($("#fname").val().length != 0 && $("#lname").val().length == 0 &&  $("#email").val().length == 0 && $("#mobile").val().length == 0) {
                $(".form-error-message").text('Please enter all the Mandatory Fields');
                $("#lname,#email, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length != 0 && $("#lname").val().length == 0 &&  (!valid) && $("#email").val().length != 0 && $("#mobile").val().length != 0) {
                $(".form-error-message").text('Please enter all the Mandatory Fields');
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#lname,#email, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length == 0 && $("#lname").val().length != 0 &&  (!valid) && $("#email").val().length != 0 && $("#mobile").val().length < 10) {
                $(".form-error-message").text('Please enter all the Mandatory Fields');
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#fname,#email, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length == 0 && $("#lname").val().length != 0 &&  (!valid) && $("#mobile").val().length == 10) {
                $(".form-error-message").text('Please enter all the Mandatory Fields');
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $("#fname,#email").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length != 0 && $("#lname").val().length != 0 &&  (!valid) && $("#email").val().length != 0 && $("#mobile").val().length != 0) {
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#fname,#email, #mobile").prop('required', true);
                isFormValid = false;
            } 
             else if ($("#fname").val().length != 0 && $("#lname").val().length != 0 && $("#email").val().length == 0 && $("#mobile").val().length == 0) {
                $(".invalid-error-message").text("Please enter Email ID");
                $(".custom-error-message").text("Please enter  Mobile Number");
                $("#fname,#email, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length == 0 && $("#lname").val().length != 0 &&  $("#email").val().length == 0 && $("#mobile").val().length == 0) {
                $(".form-error-message").text('Please enter all the Mandatory Fields');
                $("#fname,#email, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length != 0 && $("#lname").val().length != 0 && (valid) && $("#mobile").val() == '') {
                $(".custom-error-message").text("Please enter Mobile Number");
                $("#mobile").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length != 0 && $("#lname").val().length != 0 && (valid) && $("#mobile").val() == '' && $("#mobile").val().length != 10) {
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#mobile").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length != 0 && $("#lname").val().length == 0 && (valid) && $("#mobile").val().length != 10) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#lname, #mobile").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length == 0 && $("#lname").val().length == 0 && (valid) && $("#mobile").val().length == 10) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $(".invalid-error-message,.custom-error-message").text("");
                $("#fname,#lname").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length != 0 && $("#lname").val().length == 0 && (valid) && $("#mobile").val().length == 10) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $("#lname").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length == 0 && $("#lname").val().length != 0 && (valid) && $("#mobile").val().length == 10) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $("#fname").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length == 0 && $("#lname").val().length != 0 && (!valid) && $("#mobile").val().length == 10) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $("#fname").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length != 0 && $("#lname").val().length != 0 && (!valid) && $.trim($("#email").val()).length != 0 && $("#mobile").val() == '') {
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $(".custom-error-message").text("Please enter Mobile Number");
                $("#email, #mobile").prop('required', true);
                isFormValid = false;
            }
            else if ($("#fname").val().length == 0 && $("#lname").val().length == 0 && (!valid) && $("#mobile").val().length != 10) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $(".custom-error-message").text("Please Provide Valid Mobile Number");
                $("#fname,#lname, #email, #mobile").prop('required', true);
                isFormValid = false;
            } 

            else if ($("#fname").val().length == 0 && $("#lname").val().length == 0 && (valid) && $("#mobile").val().length < 10 ) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#fname,#lname, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length == 0 && $("#lname").val().length != 0 && (!valid) && $("#mobile").val().length == 10 ) {
                $(".form-error-message").text("Please enter all the Mandatory Fields");
                $(".invalid-error-message").text("Please enter Valid Email ID");
                $("#fname,#lname, #mobile").prop('required', true);
                isFormValid = false;
            } 
            else if ($("#fname").val().length != 0 && $("#lname").val().length != 0 && (valid) && $("#mobile").val().length < 10 ) {
                $(".custom-error-message").text("Please enter Valid Mobile Number");
                $("#fname,#lname, #mobile").prop('required', true);
                isFormValid = false;
            }
            else if ($("#mobile").val().length == 10 && $('#email').val().length != 0  && $('#fname').val().length != 0 && $('#lname').val().length != 0) {
                if ($("#enterOtp").val().length != "6" && $("#enterOtp").val().length != '') { 
                    $(".form-error-message").text("Please Provide OTP");
                    $(".invalid-error-message, .custom-error-message").text("");
                }
                if(!(getDetails.called)) {
                    getDetails();
                }
                $("#sendOtp").click(function() {
                    $('#hideMsg').show();
                    $("#sendOtp").prop("disabled", true);
                    $("#hideMsg span").html("5");
                    getOTP();
                    if(!(setInterval.called)) {
                        setInterval();
                    }
                    var sec = 4;
                    var timer = setInterval(function() {
                        $('#hideMsg span').text(sec--);
                        if (sec == -1) {
                            $('#hideMsg').fadeOut('fast');
                            $("#sendOtp").prop("disabled", null);
                            clearInterval(timer);
                        }
                    }, 1000);            
                });
                var sec = 4;
                if(!(setInterval.called)) {
                    $('#hideMsg').show();
                    var timer = setInterval(function() {
                        $('#hideMsg span').text(sec--);
                        if (sec == -1) {
                            $('#hideMsg').fadeOut('fast');
                            $("#sendOtp").prop("disabled", null);
                            clearInterval(timer);
                        }
                    }, 1000);
                    setInterval.called = true;
                }
                if($("#login-form-submit").val() == "Verify OTP & Proceed") {
                    $(".login-component-wrapper input").prop('required', false);
                }
                if ($("#enterOtp").val().length == "6") {
                    isFormValid = true;
                    getBasicDetails();
                    $("#login-form-submit").prop('disabled', null);
                    $(".form-two-column-wrapper").hide();
                    $(".selection-mode").show();
                    $('.form-tabs ul#myLinks li:nth-child(2)').addClass('selected');
                    $(".form-tabs #myLinks li:first-child").removeClass('selected').addClass("changebg");
                    return false;
                } else {
                    isFormValid = false;
                }
            } else {
                isFormValid = true;
                $(".form-two-column-wrapper .login-component-wrapper").removeClass("changebg");
            }
            return isFormValid;
    });

    function getBasicDetails() {
        $.ajax({
            type: "POST",
            url: "/bin/gehu/loginAdmissionForm",
            data: $('#registration-form').serialize() + '&resourcePath=' + resourcePath + '&type=applyFresh' + '&formAction=verifyotp' + '&otpcode=' + $("#enterOtp").val() + '&phoneNumber=' + $('#mobile').val(),
            success: function(msg) {
                if (msg == "invalid_otp") {
                    $(".basic-details-section, .selection-mode").hide();
                    $(".form-two-column-wrapper").show();
                    $(".form-error-message").text("Please Provide Valid OTP");
                    isFormValid = false;
                    return false;
                } else if (msg != "") {
                    /*$(".form-two-column-wrapper").hide();
                    $(".basic-details-section").show();
                    $('.form-tabs ul#myLinks li:nth-child(3)').addClass('selected');
                    $(".form-tabs #myLinks li:nth-child(2)").removeClass('selected').addClass("changebg");*/
                    //var json = msg;
                    var json = JSON.parse(msg);
                    var name;
                    for (var key in json) {
                        if (json.hasOwnProperty(key)) {
                            if (key == "firstName") {
                                name = json[key];
                            } else if (key == "lastName") {
                                name = name + " " + json[key];
                                document.getElementById("name").innerHTML = name;
                            } else if (key == "email") {
                                document.getElementById("basicEmail").innerHTML = json[key];
                            } else if (key == "formId") {
                                formIdSet = json[key];
                                $('#formId').text(formIdSet);
                            } else if (key != "interest") {
                                document.getElementById(key).innerHTML = json[key];
                            } else if (key == "interest") {
                                var selectedVal = json[key];
                                $('#areaOfInterestForm').find("option[value='" + selectedVal + "']").prop('selected', 'selected');
                            }
                        }
                    }
                    $('#selection-mode-submit').on('click', function() {
                        if ($('input[name="selectionMode"]:checked').length) {
                            selectionMode();
                            if($("#selection-mode-form input[type='radio']:checked").val() == "registration") {
                                $(".upload-registration-wrapper").show();
                                $('.form-tabs ul#myLinks li:nth-child(3)').addClass('selected');
                                $(".form-tabs #myLinks li:first-child, .form-tabs #myLinks li:nth-child(2)").removeClass('selected').addClass("changebg");
                                $(".form-two-column-wrapper, .selection-mode").hide();
                                $(".basic-details-section").show();
                                return false;
                            }
                            else if($("#selection-mode-form input[type='radio']:checked").val() == "direct-admission")  {
                                $(".upload-registration-wrapper").hide();
                                $('.form-tabs ul#myLinks li:nth-child(3)').addClass('selected');
                                $(".form-tabs #myLinks li:first-child, .form-tabs #myLinks li:nth-child(2)").removeClass('selected').addClass("changebg");
                                $(".form-two-column-wrapper, .selection-mode").hide();
                                $(".basic-details-section").show();
                                return false;
                            }
                        }
                        else {
                            $('.selection-mode-error').show();
                            return false; 
                        }
                    });
                }
            },

        });
    }

    //Partially Filled Form
    var partailFormLabel = $(".formNumber label");
    $(partailFormLabel).addClass("required");

    $("#formNumber").on('keypress focus, keypress, keydown, keyup, mousedown, mouseup', function() {
        $(".partially-filled-form").addClass('changebg');
        $(".form-two-column-wrapper .login-component-wrapper, #fname, #lname, #email, #mobile, #enterOtp, #area-of-interest").val("");
        $(".form-two-column-wrapper .login-component-wrapper, #fname, #lname, #email, #mobile").prop("required", false);
        $("#login-form-submit").val("Proceed");
        $(".form-error-message, .invalid-error-message, .custom-error-message").text("");
        $(".form-two-column-wrapper .login-component-wrapper").removeClass("changebg");
        $("#enterOtp, #sendOtp").css("visibility", "hidden");
        $('#hideMsg').hide();
    });

    function selectionMode() {
        $.ajax({
            type: "POST",
            async: false,
            url: "/bin/gehu/selectionModeServlet",
            data: 'formId=' + formIdSet + '&selectionMode=' + $("#selection-mode-form input[type='radio']:checked").val(),
            success: function(msg) {
            },
        });
    }

    function saveSelection() {
        $.ajax({
            type: "POST",
            async: false,
            url: "/bin/gehu/selectionModeServlet",
            data: 'formId=' + $('#formId').val() + '&selectionMode=' + $("#selection-mode-form input[type='radio']:checked").val(),
            success: function(msg) {
            },
        });
    }

    function getSavedData(e) {
        var formVal = $('#formNumber').val();
        var otpVal = $("#enterOtpPartially").val();
        e.preventDefault();
        var dataToPass = {formID: formVal, resourcePath: resourcePath, type: 'partiallyFilled', formAction: 'verifyotp', otpcode: otpVal}
        $.ajax({
            type:"POST", 
            headers: {'cache-control': 'no-cache'},
            cache: false,
            url: "/bin/gehu/loginAdmissionForm/",
            //data: 'formID=' + $('#formNumber').val() + '&resourcePath=' + resourcePath + '&type=partiallyFilled' + '&formAction=verifyotp' + '&otpcode=' + $("#enterOtpPartially").val() ,
            data: dataToPass,
            dataType : 'text',
            success: function(responseText) {
                if(responseText == '') {
                    alert('no data from json');
                }
                else if (responseText == "invalid_otp") {
                    $(".basic-details-section,.selection-mode").hide();
                    $(".form-two-column-wrapper").show();
                    $(".partially-filled-form-error-message").text("Please Provide Valid OTP");
                    isFormValid = false;
                    return false;
                } else {
                    if(responseText != '') {
                        $('.form-tabs ul#myLinks li:nth-child(2)').addClass('selected');
                        $(".form-tabs #myLinks li:first-child").removeClass('selected').addClass("changebg");
                        $(".form-two-column-wrapper").hide();
                        $(".selection-mode").show();
                        $('#selection-mode-submit').on('click', function() {
                            if ($('input[name="selectionMode"]:checked').length) {
                                saveSelection();
                                if($("#selection-mode-form input[type='radio']:checked").val() == "registration") {
                                $(".upload-registration-wrapper").show();
                                $('.form-tabs ul#myLinks li:nth-child(3)').addClass('selected');
                                $(".form-tabs #myLinks li:first-child, .form-tabs #myLinks li:nth-child(2)").removeClass('selected').addClass("changebg");
                                $(".form-two-column-wrapper, .selection-mode").hide();
                                $(".basic-details-section").show();
                                return false;
                            }
                            else if($("#selection-mode-form input[type='radio']:checked").val() == "direct-admission")  {
                                $(".upload-registration-wrapper").hide();
                                $('.form-tabs ul#myLinks li:nth-child(3)').addClass('selected');
                                $(".form-tabs #myLinks li:first-child, .form-tabs #myLinks li:nth-child(2)").removeClass('selected').addClass("changebg");
                                $(".form-two-column-wrapper, .selection-mode").hide();
                                $(".basic-details-section").show();
                                return false;
                            }
                            }
                            else {
                                $('.selection-mode-error').show();
                                return false; 
                            }
                        });
                    }
                    
                    var json = jQuery.parseJSON(responseText);
                    var eligibilityText;
                    var programValue;
                    var illnessValue;
                    var i = 0;
                    $('.course-details-wrapper').show();
                    for (var key in json) {
                        i = i + 1;
                        if (json.hasOwnProperty(key)) {
                            var k = '#' + key;

                            if (key == "Name") {
                                var name = json[key];                                
                                $("#name").text(name);
                            } else if (key == "email") {
                                $("#basicEmail").text(json[key]);
                            } else if (key == "contactNum") {
                                $("#contactNum").text(json[key]);
                            } else if (key == "formId") {
                                $("#formId").text(json[key]);
                            } else if (key == "interest") {
                                var selectedVal = json[key];
                                $('#areaOfInterestForm').find("option[value='" + selectedVal + "']").prop('selected', 'selected');
                            } else if (key == "marking_system_10th") {
                                var sslcMarksValue = json[key]; 
                                $("#marking_system_10th option[value='" + sslcMarksValue + "']").prop('selected', 'selected');                                
                            } else if (key == "resultsDeclared_10th") {
                                var sslcResultsDeclare = json[key];
                                $('input:radio[name="resultsDeclared_10th"]').filter('[value="' + sslcResultsDeclare + '"]').attr('checked', true);
                            } else if (key == "selectionMode") {
                                var chosenValue = json[key];
                                $('input:radio[name="selectionMode"]').filter('[value="' + chosenValue + '"]').prop('checked', true);
                                /*if($('input:radio[name="selectionMode"]').is(':checked')) { 
                                    //$('input:radio[name="selectionMode"]').attr('disabled',true);
                                    console.log("label", $(this).closest("div.registration").show());
                                    $('#registration').closest("div.registration").attr('disabled',true);
                                    $('#registration').attr('disabled',true);
                                }
                                if(chosenValue == "registration") {
                                    $('#registration').hide();
                                }*/
                            } else if (key == "registrationPaymentStatus") {
                                var regPaymentStatus = json[key];
                                if(regPaymentStatus == "Successful") {
                                    $('.registration').text('');
                                    $('.registration').text('You have completed the registration successfully. Please select the mode Direct Admission and continue');
                                }
                                /*if($('input:radio[name="selectionMode"]').is(':checked')) { 
                                    //$('input:radio[name="selectionMode"]').attr('disabled',true);
                                    console.log("label", $(this).closest("div.registration").show());
                                    $('#registration').closest("div.registration").attr('disabled',true);
                                    $('#registration').attr('disabled',true);
                                }
                                if(chosenValue == "registration") {
                                    $('#registration').hide();
                                }*/
                            } else if (key == "resultsDeclared_12th") {
                                var pucResultsDeclare = json[key];
                                $('input:radio[name="resultsDeclared_12th"]').filter('[value="' + pucResultsDeclare + '"]').attr('checked', true);
                               
                            } else if (key == "aggregate_marks_10th") {
                                 sslcAggregate  = json[key];
                                 $('#aggregate_marks_10th').val(sslcAggregate );
                            } else if (key == "projected_marks_12th") {
                                 hscPercentage = json[key];
                                 $('#projected_marks_12th').val(hscPercentage);
                            } else if (key == "pcmPercentage") {
                                 pcm = json[key];
                            } else if (key == "cgpa_percentage") {
                                 ugPercentage = json[key];
                                 $('#cgpa_percentage').val(ugPercentage);
                            } else if (key == "jeeScore") {
                                 jee = json[key];
                            } else if (key == "resultsDeclaredGraduation") {
                                var ugResultsDeclare = json[key];
                                $('input:radio[name="resultsDeclaredGraduation"]').filter('[value="' + ugResultsDeclare + '"]').attr('checked', true);
                            } else if (key == "resultsDeclaredGraduation") {
                                var ugResultsDeclare = json[key];
                                $('input:radio[name="resultsDeclaredGraduation"]').filter('[value="' + ugResultsDeclare + '"]').attr('checked', true);
                            }else if (key == "gender") {
                                var gender = json[key];
                                $('input:radio[name="gender"]').filter('[value="' + gender + '"]').prop('checked', true);
                            } else if (key == "caste") {
                                var casteValue = json[key];
                                $('input:radio[name="caste"]').filter('[value="' + casteValue + '"]').prop('checked', true);
                            } else if (key == "defence") {
                                var defence = json[key];
                                $('input:radio[name="defence"]').filter('[value="' + defence + '"]').prop('checked', true);
                            } else if (key == "copyAddress") {
                                var permanentCheck = json[key];
                                if(permanentCheck == "on") {
                                    $('input#copyAddress:checkbox[value="' + permanentCheck + '"]').prop('checked', true);
                                    $('#permanent_address').prop("readonly", true).addClass('changebg');
                                }
                                else {
                                    $('input#copyAddress:checkbox[value="' + permanentCheck + '"]').prop('checked', false);
                                    $('#permanent_address').prop("readonly", false).removeClass('changebg');
                                }
                            } else if (key == "illness") {
                                illnessValue = json[key];
                                $('input:radio[name="illness"]').filter('[value="' + illnessValue + '"]').prop('checked', true);
                            } else if (key == "name_illness") {
                                var illDetails = json[key];
                                if(illnessValue == "Yes") {
                                    $('#suffering').show();
                                    $('#suffering').val(illDetails);
                                }
                            } else if (key == "association_GEU") {
                                var associateValue = json[key];
                                $('input:radio[name="association_GEU"]').filter('[value="' + associateValue + '"]').prop('checked', true);
                            } else if (key == "campus") {
                                campus = json[key]; 
                                $("#campus option[value='" + campus + "']").prop('selected', 'selected'); 
                                getLevel();
                                if(levelValue != ''){
                                    $("#level option[value='" + levelValue + "']").prop('selected', 'selected');
                                    loadProgramList();
                                }
                            } else if (key == "level") {
                                levelValue = json[key];
                                if($("#campus").val() != ''){
                                    $("#level option[value='" + levelValue + "']").prop('selected', 'selected');
                                    loadProgramList();
                                }
                            } else if (key == "course_preference_1") {
                                programValue = json[key]; 
                                $("#course_preference_1 option[value='" + programValue + "']").prop('selected', 'selected');                                
                                $('.course-details-wrapper .heading').html(json[key]);
                            } else if (key == "eligibility") {
                                eligibilityText = json[key];
                                $('.course-description #eligibility').html(json[key]);
                            } else if (key == "duration") {
                                $('.course-duration #duration').html(json[key]);
                            } else if (key == "totalFees") {
                                $('.course-toalFees #totalFees').html('&#8377; ' + json[key]);
                            } /*else if (key == "discountRate") {
                                console.log("discountrate", json[key]);
                                $('.course-discountPercentage #discountRate').html(json[key]);                  
                            } else if (key == "discountedFees") {
                                console.log("discountedfees", json[key]);
                                $('.course-discountAmount #discountedFees').html('&#8377; ' + json[key]);
                            }*/ else if (key == "statename") {
                                state = json[key]; 
                                $("#statename option[value='" + state + "']").prop('selected', 'selected');                                
                            } 
                            else if (key == "photo_name") {
                                $("#photoWrapper #photo-name").text(json[key]);
                                if ($("#photoWrapper #photo-name").text() != '') {
                                    $("span.photo").show();
                                    $(".upload-option, .upload-photo-content").addClass('changebg');
                                }
                            } else if (key == "marksheet_10th_name") {
                                $("#sslcWrapper #sslc-marksheet-name").text(json[key]);
                                if ($("#sslcWrapper #sslc-marksheet-name").text() != '') {
                                    $("span.sslc").show();
                                    $(".upload-sslc-option, .upload-sslc-marksheet-content").addClass('changebg');
                                }
                            } else if (key == "marksheet_12th_name") {
                                $(".upload-wrapper #hsc-marksheet-name").text(json[key]);
                                if ($(".upload-wrapper #hsc-marksheet-name").text() != '') {
                                    $("span.hsc").show();
                                    $(".upload-hsc-option, .upload-hsc-marksheet-content").addClass('changebg');
                                }
                            } else if (key == "marksheet_ug_name") {
                                $(".upload-wrapper.UgMarksheet #ug-marksheet-name").text(json[key]);
                                if ($(".upload-wrapper.UgMarksheet #ug-marksheet-name").text() != '') {
                                    $("span.ug").show();
                                    $(".upload-ug-option, .upload-ug-marksheet-content").addClass('changebg');
                                }
                            } else if (key == "videoWhatsApp") {
                                videoCheck = json[key];
                                if(videoCheck == "on") {
                                    $('input#videoWhatsApp:checkbox[value="' + videoCheck + '"]').prop('checked', true);
                                }
                                else {
                                    $('input#videoWhatsApp:checkbox[value="' + videoCheck + '"]').prop('checked', false);
                                }
                            } else if (key == "selfVideoURL") {
                                var selfVideoURL = json[key];
                                $('#selfVideoURL').val(selfVideoURL);
                            } else if (key == "registrationMarksheet_name") {
                                $("#upload-registration #registration-marksheet-name").text(json[key]);
                                if ($("#upload-registration #registration-marksheet-name").text() != '') {
                                    $("span.upload-registration-close-button").show();
                                    $(".upload-registration-option, .upload-registration-marksheet-content").addClass('changebg');
                                }
                            }
                            $(k).val(json[key]);

                        }
                    }

                    if(permanentCheck == "on") {
                        $('input:checkbox[name="copyAddress"]').filter('[value="' + permanentCheck + '"]').prop('checked', true);
                        $('#permanent_address').prop("readonly", true).addClass('changebg');
                    }
                    else {
                        $('input:checkbox[name="copyAddress"]').filter('[value="' + permanentCheck + '"]').prop('checked', false);
                        $('#permanent_address').prop("readonly", false).removeClass('changebg');
                    }   
                    if(pcm == 0) {
                        $('#pcmPercentage').val("");
                    }
                    else {
                        $('#pcmPercentage').val(pcm);
                    }
                    if(jee == 0) {
                        $('#jeeScore').val("");
                    }
                    else {
                        $('#jeeScore').val(jee);
                    }
                    if($('#level').val() == "Under-Graduate Programmes") {
                        $('.hsc-wrapper').show();
                        hscmandateLabels = $(".hsc-wrapper label, .hsc-wrapper .select-option .heading").not(".hsc-wrapper .select-option label, .hsc-wrapper .pcmPercentage label, .hsc-wrapper .jeeScore label");
                        $(hscmandateLabels).addClass("required");
                        uploadHscSectionLabel = $(".upload-hsc-marksheet-content h4");
                        $(uploadHscSectionLabel).addClass("required");
                    }
                    if($('#level').val() == "Post-Graduate Programmes") {
                        $('.hsc-wrapper, .graduation-wrapper').show();
                        hscmandateLabels = $(".hsc-wrapper label, .hsc-wrapper .select-option .heading").not(".hsc-wrapper .select-option label, .hsc-wrapper .pcmPercentage label, .hsc-wrapper .jeeScore label");
                        $(hscmandateLabels).addClass("required");
                        uploadHscSectionLabel = $(".upload-hsc-marksheet-content h4");
                        $(uploadHscSectionLabel).addClass("required");
                        ugmandateLabels = $(".graduation-wrapper label, .graduation-wrapper .select-option .heading").not(".graduation-wrapper .select-option label");
                        $(ugmandateLabels).addClass("required");
                        uploadUgSectionLabel = $(".upload-ug-marksheet-content h4");
                        $(uploadUgSectionLabel).addClass("required");
                    }
                }
            },
            error: function(){
                alert("The request failed");
            }

        })
    }

    function partialOTP() {
        $.ajax({
            type: "POST",
            url: "/bin/gehu/loginAdmissionForm",
            data: 'formID=' + $('#formNumber').val() + '&resourcePath=' + resourcePath + '&type=partiallyFilled' + '&formAction=sendotp',
            success: function(data) {
                if(data == "Payment is successful for this form ID. Please contact admin for required changes.") {
                    $('.partially-filled-form').hide();
                    $('.payment-success').html('Payment is successful for this form ID. Please contact admin for required changes.');
                    $('.payment-success').show();
                }
            },
        });
        partialOTP.called = true;
    }

    function partialOTPResend() {
        $.ajax({
            type: "POST",
            url: "/bin/gehu/loginAdmissionForm",
            data: 'formID=' + $('#formNumber').val() + '&resourcePath=' + resourcePath + '&type=partiallyFilled' + '&formAction=sendotp',
            success: function(data) {
            },
        });
    }

    function checkFormExist() {
        $.ajax({
            type: "POST",
            url: "/bin/gehu/loginAdmissionForm",
            data: 'formID=' + $('#formNumber').val() + '&resourcePath=' + resourcePath + '&type=partiallyFilled',
            success: function(msg) {
                if (msg == "Form ID doesnot exists!!") {
                    $(".partially-filled-form-error-message").text("*Please Enter Valid Form ID");
                    isFormValid = false;
                }
                else {
                    $("#partiallyFilledForm").val("Verify OTP & Proceed");
                    $("#enterOtpPartially, #sendOtpPartially").css("visibility", "visible");
                    $("#hideMsgPartially span").html("5");
                    if(!(partialOTP.called)) {
                        partialOTP();
                        $("#sendOtpPartially").prop("disabled", true);
                    }
                    /*var sec = 4;
                    if(!(setInterval.called)) {
                        $('#hideMsgPartially').show();
                        var timer = setInterval(function() {
                            $('#hideMsgPartially span').text(sec--);
                            if (sec == -1) {
                                $('#hideMsgPartially').fadeOut('fast');
                                $("#sendOtpPartially").prop("disabled", null);
                                clearInterval(timer);
                            }
                        }, 1000);
                        setInterval.called = true;
                    }*/
                    var sec = 4;
                    function makeTimer() {
                        $('#hideMsgPartially').show();
                        $('#hideMsgPartially span').text(sec--);
                        if (sec == -1) {
                            $('#hideMsgPartially').fadeOut('fast');
                            $("#sendOtpPartially").prop("disabled", null);
                            clearInterval(myTimer);
                            $("#partiallyFilledForm").hide();
                            $('#partiallySubmit').show();
                        }
                    }
                    if(!(setInterval.called)) {
                        myTimer = setInterval(function() { makeTimer(); myTimer.called = true;}, 1000);
                        setInterval.called = true;
                    }
                }
            },

        });
        checkFormExist.called = true;
    }
    $("#partiallyFilledForm").click(function(e) {
        e.preventDefault();
        var isFormValid = true;
        if ($("#formNumber").val().length == 0) {
            $(".partially-filled-form-error-message").text("*Please enter Form ID");
            $(".partially-filled-form").addClass('changebg');
            isFormValid = false;
        } else if ($("#formNumber").val().length != 0) {
            checkFormExist();
            if ($("#enterOtpPartially").val().length == "6") {
                isFormValid = true;
                if(isFormValid == true) {
                    $(".form-two-column-wrapper").hide();
                    $(".selection-mode").show();
                    getSavedData(e);
                }
                return false;
            } 
            else if($("#enterOtp").val().length != '6'){
                $(".partially-filled-form-error-message").text("");
                return false;
                isFormValid = false;
            }
            else {
                isFormValid = false;
            }
        }
        $("#sendOtpPartially").prop("disabled", null);
        return isFormValid;
    });
    $("#sendOtpPartially").click(function() {
        $("#hideMsgPartially span").html("5");
        $('#hideMsgPartially').show();
        partialOTPResend();
        $("#sendOtpPartially").prop("disabled", true);
        /*var sec = 4;
        var timer = setInterval(function() {
            $('#hideMsgPartially span').text(sec--);
            if (sec == -1) {
                $('#hideMsgPartially').fadeOut('fast');
                $("#sendOtpPartially").prop("disabled", null);
                clearInterval(timer);
            }
        }, 1000);*/
        var sec = 4;
        function makeTimer() {
            $('#hideMsgPartially').show();
            $('#hideMsgPartially span').text(sec--);
            if (sec == -1) {
                $('#hideMsgPartially').fadeOut('fast');
                $("#sendOtpPartially").prop("disabled", null);
                clearInterval(myTimer);
            }
        }
        myTimer = setInterval(function() { makeTimer(); }, 1000);
    })

    $("#partiallySubmit").click(function(e) {
            e.preventDefault();
            var isFormValid = true;
            if ($("#formNumber").val().length == 0) {
                $(".partially-filled-form-error-message").text("*Please enter Form ID");
            }
            else if ($("#formNumber").val().length != 0 && $("#enterOtpPartially").val() == "") { 
                checkFormExist();
                $("#sendOtpPartially").prop("disabled", null);
                $(".partially-filled-form-error-message").text("");
                $(".partially-filled-form-error-message").text('Please Provide OTP');
            }
            else if ($("#enterOtpPartially").val() != "") {
                isFormValid = true;
                if(isFormValid == true) {   
                    $(".form-two-column-wrapper").hide();
                    $(".selection-mode").show();
                    getSavedData(e);
                }
                return false;
            } 
            else {
                isFormValid = true;
            }
            $("#sendOtpPartially").prop("disabled", null);
            return isFormValid;
        });

    //$('input:radio[name="resultsDeclared_12th"]').attr('disabled', true);

    //On Change clear Program Values
    $("#pcmPercentage, #jeeScore").on("change paste keyup", function() {
        if($('#level').val() == "Under-Graduate Programmes" && $('#course_preference_1').val() != '' 
                && $('#course_preference_1').find('option:selected').attr('ispcmorjeemandatory') == 1){
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    $("#projected_marks_12th").on("change paste keyup", function() {
        if($('#level').val() == "Under-Graduate Programmes" && $('#course_preference_1').val() != '' 
                && $('#course_preference_1').find('option:selected').attr('ispcmorjeemandatory') == 0){
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    $("#cgpa_percentage").on("change paste keyup", function() {
        if($('#level').val() == "Post-Graduate Programmes" && $('#course_preference_1').val() != ''){
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    $('input:radio[name="resultsDeclaredGraduation"]').on("change", function() {
        if($('#level').val() == "Post-Graduate Programmes" && $('#course_preference_1').val() != ''){
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    $('input:radio[name="gender"]').on("change", function() {
        $('#course_preference_1').val('');
        $('.course-details-wrapper').hide();
        $('.no-course-found').hide();
        $('.no-course-found .heading').text('');
    });
    
    $('#statename').on("change", function() {
        $('#course_preference_1').val('');
        $('.course-details-wrapper').hide();
        $('.no-course-found').hide();
        $('.no-course-found .heading').text('');
    });

    $('input:radio[name="resultsDeclared_10th"]').on("change", function() {
        if($('#level').val() == "Diploma Programmes" && $('#course_preference_1').val() != ''){
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    $('#aggregate_marks_10th').on("change", function() {
        if($('#level').val() == "Diploma Programmes" && $('#course_preference_1').val() != ''){
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    $('#course_preference_1').on('change', function() {
        var isFormValid = true;
        $('.course-details-wrapper').hide();
        $('.no-course-found').hide();
        $('.no-course-found .heading').text('');
        if($('#course_preference_1').val() != '') {
            if($('#level').val() == "Post-Graduate Programmes") {
                var ugResultsDeclared = $('input[name="resultsDeclaredGraduation"]:checked').val();
                var cgpa_percentage = $('#cgpa_percentage').val();
                if(ugResultsDeclared == undefined || cgpa_percentage == '' ) {
                    $(".basic-details-mobile-error-message, .basic-pcm-error-message").html('');
                    $(".basic-pcm-error-message").text("Please enter Graduation Result Declared and Graduation Aggregate Percentage");
                    isFormValid = false;
                }
                else {
                    $(".basic-pcm-error-message").html('');
                    isFormValid = true;
                }
            }
            else if($('#level').val() == "Under-Graduate Programmes") {
                if($('#course_preference_1').find('option:selected').attr('ispcmorjeemandatory') == 1){
                    if ($("#pcmPercentage").val() == '' && $("#jeeScore").val() == '' && $('#status_12th').val() == '') {
                        $(".basic-pcm-error-message").html('');
                        $(".basic-pcm-error-message").text("Please enter XII Status and PCM Percentage or JEE Score");
                        isFormValid = false;
                    }
                    else if ($("#pcmPercentage").val() == '' && $("#jeeScore").val() == '') {
                        $(".basic-pcm-error-message").html('');
                        $(".basic-pcm-error-message").text("Please enter PCM Percentage or JEE Score");
                        isFormValid = false;
                    }
                    else if ($('#status_12th').val() == '') {
                        $(".basic-pcm-error-message").html('');
                        $(".basic-pcm-error-message").text("Please XII Status");
                        isFormValid = false;
                    }
                    else {
                        $(".basic-pcm-error-message").html('');
                        isFormValid = true;
                    }
                }
                else{
                    if ($("#projected_marks_12th").val() == '' || $('#status_12th').val() == '') {
                        $(".basic-pcm-error-message").html('');
                        $(".basic-pcm-error-message").text("Please enter XII Aggregate Percentage and XII Status");
                        isFormValid = false;
                    }
                    else {
                        $(".basic-pcm-error-message").html('');
                        isFormValid = true;
                    }
                }
            }
            if(isFormValid) {
                getFeeDetails();
            }
        }
    });

    hscmandateLabels = $(".hsc-wrapper label,.hsc-wrapper .select-option .heading").not(".hsc-wrapper .select-option label, .hsc-wrapper .pcmPercentage label, .hsc-wrapper .jeeScore label");
    ugmandateLabels = $(".graduation-wrapper label, .graduation-wrapper .select-option .heading").not(".graduation-wrapper .select-option label");

    $('#campus').on('change', function() {
        $('#course_preference_1').html('');
        $("#course_preference_1").append("<option value=''>--Select Program--</option>");
        $('.course-details-wrapper').hide();
        $('.no-course-found').hide();
        $('.no-course-found .heading').text('');
        $('.basic-details-mobile-error-message, .basic-pcm-error-message').text('');
        $(hscmandateLabels).removeClass("required");
        $(ugmandateLabels).removeClass("required");
        getLevel();
    });
    
    function getFeeDetails(){
        if($('#aggregate_marks_10th').val() != '') {
          sslcAggregate = $('#aggregate_marks_10th').val();
        }
        else {
          sslcAggregate = 0;
        }
         
        if($('#projected_marks_12th').val() != '') {
          hscPercentage = $('#projected_marks_12th').val();
        }
        else {
          hscPercentage = 0;
        }
        if($('#pcmPercentage').val() != '') {
          pcm = $('#pcmPercentage').val();
        }
        else {
          pcm = 0;
        }
                               
        if($('#cgpa_percentage').val() != '') {
          ugPercentage = $('#cgpa_percentage').val();
        }
        else {
          ugPercentage = 0;
        }
      
        if($('#jeeScore').val() != '') {
          jee = $('#jeeScore').val();
        }
        else {
          jee = 0;
        } 

        var gender = $('input[name="gender"]:checked').val(); 
        var sslcResultsDeclared = $('input[name="resultsDeclared_10th"]:checked').val();
        var hscResultsDeclared = $('input[name="resultsDeclared_12th"]:checked').val();
        var ugResultsDeclared = $('input[name="resultsDeclaredGraduation"]:checked').val();
        ispcmorjeemandatory = $('#course_preference_1').find('option:selected').attr('ispcmorjeemandatory');
        var selectedProgram = $('#course_preference_1').val();
        $.ajax({
            type: "POST",
            url: "/bin/gehu/levelProgramList",
            data: 'resourcePath=' + resourcePath + '&programSelected=' + encodeURIComponent(selectedProgram) +  '&type=fee' + '&pcm=' + pcm + '&jee=' + jee + '&gender=' + gender + '&sslcPercentage=' + sslcAggregate + '&hscPercentage=' + hscPercentage + '&hscResultsDeclared=' + hscResultsDeclared + '&sslcResultsDeclared=' + sslcResultsDeclared +'&ugPercentage=' + ugPercentage +'&ugResultsDeclared=' + ugResultsDeclared +'&levelSelected=' + $('#level').val() + '&isPcmOrJeeMandatory=' + ispcmorjeemandatory +'&campus='+ $('#campus').val() +'&state=' + $('#statename').val(),
            dataType: 'text',
            success: function(msg) {
                if(msg == "[]") {
                    $('.no-course-found .heading').html('You are not eligible to Apply for the selected program (' + selectedProgram + ') through Online. Please contact university campus or nearby centers.');
                    $('.no-course-found').show();
                }
                else {
                    var json = jQuery.parseJSON(msg);
                    $.each(json, function(arrKey, arrVal) {
                        $('.course-details-wrapper').show();
                        $('.course-details-wrapper .heading').html(selectedProgram);
                        $('.course-description #eligibility').html(arrVal.eligibilityText);
                        $('.course-duration #duration').html(arrVal.duration);
                        $('.course-toalFees #totalFees').html('&#8377; ' +  arrVal.totalFees);
                        //$('.course-discountPercentage #discountRate').html(arrVal.discountRate);
                        //$('.course-discountAmount #discountedFees').html('&#8377; ' +  arrVal.discountedFees);
                    });
                }
            }
        });
    };


    //Get Level and Program Values
    function loadProgramList() {      
        $.ajax({
            type: "POST",
            async: false,
            url: "/bin/gehu/levelProgramList",
            data: 'resourcePath=' + resourcePath + '&levelSelected=' + $('#level').val() + '&campus=' + $('#campus').val() + '&type=program',
            dataType: 'text',
            success: function(msg) {
                var json = jQuery.parseJSON(msg);
                var $select = $('#course_preference_1');
                $select.html('');
                $($select).prepend("<option value=''>--Select Program--</option>");
                if ($('#level').val() == "") {
                    $('#course_preference_1').html('');
                    $("#course_preference_1").append("<option value=''>--Select Program--</option>");
                    $('.course-details-wrapper').hide();
                }
                $.each(json, function(arrKey, arrVal) {
                    $select.append("<option  id='" + arrVal.nodename + "' value='" + arrVal.programTitle + "' isPcmOrJeeMandatory='" + arrVal.isPcmOrJeeMandatory + "' >" + arrVal.programTitle + "</option>");
                });
            },
        })
    }

    function getLevel() {
        $.ajax({
            type: "POST",
            async: false,
            url: "/bin/gehu/levelProgramList",
            data: 'resourcePath=' + resourcePath + '&type=level' +'&campus='+ $('#campus').val(),
            success: function(msg) {
                var json = jQuery.parseJSON(msg);
                var $select = $('#level');
                $select.html('');
                $($select).prepend("<option value=''> -- Select Level --</option>");
                for (var key in json) {
                    if (json.hasOwnProperty(key)) {
                        $select.append("<option value='" + json[key] + "' id='" + key + "'>" + json[key] + "</option>");
                    }
                }
            },

        });
    }
    
    uploadHscSectionLabel = $(".upload-hsc-marksheet-content h4");
    uploadUgSectionLabel = $(".upload-ug-marksheet-content h4");

    $("#level").change(function(e) {
        $('.course-details-wrapper').hide();
        $('.no-course-found').hide();
        $('.no-course-found .heading').text('');
        $('.basic-details-mobile-error-message, .basic-pcm-error-message').text('');
      
        hscmandateLabels = $(".hsc-wrapper label,.hsc-wrapper .select-option .heading").not(".hsc-wrapper .pucRollNo label, .hsc-wrapper .select-option label, .hsc-wrapper .pcmPercentage label, .hsc-wrapper .jeeScore label");
        ugmandateLabels = $(".graduation-wrapper label, .graduation-wrapper .select-option .heading").not(".graduation-wrapper .select-option label");

        if($('#level').val() == "Diploma Programmes") {
            $('.graduation-wrapper, .hsc-wrapper').hide();
            var hscInputs = $("#board_12th, #intermediate_senior, #status_12th, #rollno_12th, #year_of_passing_12th, #projected_marks_12th, #city_12th, #pincode_12th, #jeeScore, #pcmPercentage");
            $(hscInputs).each(function() {
                var hscElement = $(this);
                hscElement.val('');
            });
            $(hscmandateLabels).removeClass("required");
            $(ugmandateLabels).removeClass("required");
        }

        if($('#level').val() == "Under-Graduate Programmes") {
            $('.hsc-wrapper').show();
            $('.graduation-wrapper').hide();
            $(hscmandateLabels).addClass("required");
            $(uploadHscSectionLabel).addClass("required");
            $(ugmandateLabels).removeClass("required");
        }
        else {
            $(hscmandateLabels,uploadHscSectionLabel).removeClass("required");
        }

        if($('#level').val() == "Post-Graduate Programmes") {
            $('.hsc-wrapper, .graduation-wrapper').show();
            var ugInputs = $("#higher_education_institute, #grduationUniversityName, #graduationCourse, #graduationSpecialisation, #year_of_graduation, #cityGraduation, #pincodeGraduation, #cgpa_percentage");
            $(ugInputs).each(function() {
                var ugElement = $(this);
                ugElement.val('');
            });
            $(hscmandateLabels).addClass("required");
            $(ugmandateLabels).addClass("required");
            $(uploadUgSectionLabel).addClass("required");
        }
        else {
            $(hscmandateLabels,uploadUgSectionLabel).removeClass("required");
        }
        if ($('#level option:selected').val() != null && $('#level option:selected').val().length > 0) {
            loadProgramList();
        }
        if($('#level').val() == '') {
            var $select = $('#course_preference_1');
            $select.html('');
            $($select).prepend("<option value=''>--Select Program--</option>");
            $('.graduation-wrapper, .course-details-wrapper, .no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
    });

    //Label Star Mark
    var mandateLabels = $(".choose-campus label, .choose-level label, .program-wrapper label, .sslcBoard label, .sslcPassingYear label, .sslcMarks label, .sslcPercentage label, .sslc-wrapper .select-option .heading, .dob label, .aadharNo label, .communication-address .address-heading, .address-heading .heading, .fatherName label, .motherName label");
    $(mandateLabels).addClass("required");



    //General Valdation For Basic Details Step before submit
    var passingYear = $("#year_of_passing_10th, #year_of_passing_12th, #year_of_graduation").attr('maxlength', '4');
    var sslcPercentage = $("#aggregate_marks_10th,#projected_marks_12th, #cgpa_percentage, #pcmPercentage").attr('maxlength', '5');
    var jeeScoreMaxLength = $("#jeeScore").attr('maxlength', 6);
    var aadharNo = $("#aadharNo").attr('maxlength', 12);
    var mobileNo = $("#father_contact, #mother_contact").attr('maxlength', 10)
    $(document).on("keypress", "#year_of_passing_10th, #aggregate_marks_10th, #year_of_passing_12th, #pincode_12th, #projected_marks_12th, #year_of_graduation, #pincodeGraduation, #cgpa_percentage, #pcmPercentage, #jeeScore, #aadharNo, #father_contact, #mother_contact", function(e) {
        var regex = new RegExp("^[0-9.]+$");
        var str = String.fromCharCode(!e.charCode && key != 8 && key != 46  ? e.which : e.charCode);
        if (!regex.test(str)) {
            e.preventDefault();
            return false;
        }
    });

    $(document).on("keypress", "#city_12th", function(e) {
        var regex = new RegExp(/^[a-zA-Z]*$/);
        var str = String.fromCharCode(!e.charCode && key != 8 && key != 46  ? e.which : e.charCode);
        if (!regex.test(str)) {
            e.preventDefault();
            return false;
        }
    });

    //PUC Status Change Enable Aggregation Status
    $("#status_12th").change(function() {
        $('input:radio[name="resultsDeclared_12th"]').prop('checked',false);
        if($('#level').val() == "Under-Graduate Programmes") {
            $('#course_preference_1').val('');
            $('.course-details-wrapper').hide();
            $('.no-course-found').hide();
            $('.no-course-found .heading').text('');
        }
        if ($(this).val() == "Passed") {
            $('input:radio[name="resultsDeclared_12th"][value="Yes"]').prop('checked',true);
            $(".pucRollNo label").addClass('required');
            isFormValid = false;
        } 
        else if ($(this).val() == "") {
            $(".pucRollNo label").removeClass('required');
            isFormValid = false;
        } else if ($(this).val() == "Appearing") {
            $('input:radio[name="resultsDeclared_12th"][value="No"]').prop('checked',true);
            $(".pucRollNo label").removeClass('required');
            isFormValid = true;
        }
    });

    //Copy Address 
    $('#copyAddress').click(function() {
        if ($(this).is(':checked')) {
            $('#permanent_address').attr("readonly", true).addClass('changebg');
            $('#permanent_address').val($('#correspondence_address').val());
        } else {
            $('#permanent_address').attr("readonly", false).removeClass('changebg');
            $('#permanent_address').val('');
        }
    });


    //Suffer Change Status
    $('#basic-details-form input[name=illness]').on('change', function() {
        if ($('input[name=illness]:checked', '#basic-details-form').val() == "Yes") {
            $("#suffering").show();
        } else {
            $("#suffering").val('');
            $("#suffering").hide();
        }
    });

    //DatePicker
    $("#date_of_birth").datepicker({
        //dateFormat: 'dd-mm-yy',
        changeYear: true,
        changeMonth: true,
        yearRange: '1900:2018',
        maxDate: 0
    });
    $("#date_of_birth").each(function() {
        $(this).datepicker('setDate', $(this).val());
    });

    $("input:checkbox[name='copyAddress']").on('change', function(){
        $(this).val(this.checked ? "on" : "off");
    });
    
    $("input:checkbox[name='videoWhatsApp']").on('change', function(){
        $(this).val(this.checked ? "on" : "off");
    });

    $("#registrationMarksheet").bind('change', function() {
        var isFormValid = true;
        var attach_id = "registrationMarksheet";
        var fileLength = $('#' + attach_id)[0].files.length;
        var registrationFileSize = $('#registrationMarksheet')[0].files[0].size;

        if ($("#registrationMarksheet").val() == '') {
            $("#registrationMarksheet, .upload-registration-marksheet-content").removeClass('changebg');
        }
        if ($("#registrationMarksheet").val() == '' || $("#registration-marksheet-name").text().length > 0) {
            $("#registrationMarksheet, .upload-registration-marksheet-content").removeClass('changebg hideText');
            $('#registration-marksheet-name').text('');
        }
        if ($("#registrationMarksheet").val() != '') {
            $(".upload-registration-option, .upload-registration-marksheet-content").addClass('changebg');
            $("span.upload-registration-close-button").show();
            var _URL = window.URL || window.webkitURL;
            var file, img;
            if ($("#registration-marksheet-name").text() == '') {
                var _URL = window.URL || window.webkitURL;
                var file, img;
                if ($("#file-name").text() == '') {
                    if ((file = this.files[0])) {
                        img = new Image();
                        img.src = _URL.createObjectURL(file);
                    }
                    var filename = $("#registrationMarksheet").val().replace("C:\\fakepath\\", '')
                    $("#registration-marksheet-name").text(filename);
                }
            }
            $("#file-name").text(this.files[0].name);
            if (!(/\.(gif|jpg|jpeg|tiff|png|bmp|pdf)$/i).test($("#registrationMarksheet").val())) {
                $(".upload-registration-error").text("Please upload valid format files which is mentioned in the description");
                $("#registrationMarksheet").val('');
                $('#registration-marksheet-name').text('');
                $('span.upload-registration-close-button').hide();
                if ($("#registrationMarksheet").val() == '') {
                    $(".upload-registration-option, .upload-registration-marksheet-content").removeClass('changebg');
                }
            }
            if (registrationFileSize > 2000000) {
                $(".upload-registration-error").text('');
                $(".upload-registration-error").text("Please upload upto 2 MB file");
                $(".upload-registration-option, .upload-registration-marksheet-content").removeClass('changebg');
                $('#registration-marksheet-name').text('')
                $('span.upload-registration-close-button').hide();
            }
            else if (registrationFileSize < 2000000 && $("#registrationMarksheet").val() != '') { 
                $(".upload-registration-error").text('');
            }
            isFormValid = false;
        }
        return isFormValid
    });

    $(".upload-registration-option span").click(function() {
        $(".upload-registration-option span").hide();
        $("#registrationMarksheet").val('');
        $("#registration-marksheet-name").text('');
        $(".upload-registration-option, .upload-registration-marksheet-content").removeClass('changebg');
    });

    var regMandateLabel = $(".upload-registration-marksheet h4");
    $(regMandateLabel).addClass("required");

    //Step To Proceed To Upload Section
    $("#save").click(function() {
        var isFormValid = true;
        var hscFormValid = true;
        var isInputValid = true;
        var isUgValid = true;
        var validationInputs = $("#board_10th, #year_of_passing_10th, #marking_system_10th, #aggregate_marks_10th, #date_of_birth, #aadharNo, #correspondence_address, #permanent_address, #father_name, #mother_name, #campus, #level, #course_preference_1");
        var hscInputs = $("#board_12th, #intermediate_senior, #status_12th, #year_of_passing_12th, #projected_marks_12th, #city_12th, #pincode_12th");
        var ugInputs = $("#higher_education_institute, #grduationUniversityName, #graduationCourse, #graduationSpecialisation, #year_of_graduation, #cityGraduation, #pincodeGraduation, #cgpa_percentage");

        var element, hscElement, ugElement;
        $(validationInputs).each(function() {
            element = $(this);
            if (element.val() == "") {
                $(element).prop('required', true);
                $("body, html").animate({
                    scrollTop: $(".basic-details-section").offset().top - 10
                }, 600);
                $(".basic-details-form-error-message").text("Please Enter Mandatory Fields");
                isInputValid = false;
            } else if ( this.value.trim() !== '' ) {
                $(element).prop('required', false);
            } else {
                if($(".education-wrapper .form-group input:empty").length == 0 && element.val() != "") {
                    $(element).prop('required', false);
                    isInputValid = true;
                }
            }
        });

        $(hscInputs).each(function() {
            hscElement = $(this);
            if($("#level").val() == "Under-Graduate Programmes" || $("#level").val() == "Post-Graduate Programmes") {
                if (hscElement.val() == "") {
                    $(hscElement).prop('required', true);
                    hscFormValid = false;
                    $("body, html").animate({
                        scrollTop: $(".basic-details-section").offset().top - 10
                    }, 600);
                    $(".basic-details-form-error-message").text("Please Enter HSC Details");
                } else if ( this.value.trim() !== '' ) {
                    $(hscElement).prop('required', false);
                } else {
                    if($(".hsc-wrapper .form-group input:empty").length == 0 && hscElement.val() != "") {
                        $(hscElement).prop('required', false);
                        hscFormValid = true;
                    }
                }
            }
        });

        $(ugInputs).each(function() {
            ugElement = $(this);
            if($("#level").val() == "Post-Graduate Programmes") {
                if (ugElement.val() == "") {
                    $(ugElement).prop('required', true);
                    isInputValid = false;
                    $("body, html").animate({
                        scrollTop: $(".basic-details-section").offset().top - 10
                    }, 600);
                    $(".basic-details-form-error-message").text("Please Enter Graduation Details");
                } else if ( this.value.trim() !== '' ) {
                    $(ugElement).prop('required', false);
                } else {
                    if($(".graduation-wrapper .form-group input:empty").length == 0 && ugElement.val() != "") {
                        $(uploadUgSectionLabel).removeClass("required");
                        $(ugElement).prop('required', false);
                        isInputValid = true;
                    }
                }
            }
        });


        if($('#level').val() == "Diploma Programmes") {
            $(uploadHscSectionLabel, uploadUgSectionLabel).removeClass("required");
        }

        function scrollToBasic(){
            $("body, html").animate({
                scrollTop: $(".basic-details-section").offset().top - 10
            }, 600);
        }
        $(".aadhar-error-message").html('');
        if($('#aadharNo').val() != "" && $('#aadharNo').val().length != "12") {
            $(".aadhar-error-message").text('')
            scrollToBasic();
            $('#aadharNo').prop('required', true);
            $(".aadhar-error-message").text('Please enter valid aadhaar No');
            return false;
        }

        if($("#selection-mode-form input[type='radio']:checked").val() == "registration") {
            if (($("#registrationMarksheet").val().length === 0 && $("#registration-marksheet-name").text().length === 0)) {
                $(".aadhar-error-message").text('')
                scrollToBasic();
                $(".aadhar-error-message").text('Please Upload Latest Marksheet');
                return false;
            }
        }
        //Suffer Required Add on change
        if ($('input[name=suffer]:checked', '#basic-details-form').val() == "suffer-yes" && $("#suffering").val() == '') {
            $("#suffering").prop('required', true);
        } else {
            $("#suffering").prop('required', false);
        }

        if ($('#status_12th').val() == "Passed" && $("#rollno_12th").val() == '') {
            $("#rollno_12th").prop('required', true);
        } else {
            $("#rollno_12th").prop('required', false);
        }

        if ($('.no-course-found .heading').text() != '') {
            $(".basic-details-form-error-message").text('');
            $(".basic-details-form-error-message").text("Please select valid program to apply online");
            isFormValid = false;
            scrollToBasic();
        } 
        var validateEmail = function(elementValue) {
            var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
            return emailPattern.test(elementValue);
        }
        var value = $('#father_email').val();
        var valid = validateEmail(value);
        $(".basic-details-mobile-error-message, .basic-pcm-error-message").text('');
        if((!valid) && $.trim($("#father_email").val()).length != 0) {
            $(".basic-details-mobile-error-message").text("Please Enter Valid Email");
            isFormValid = false;
        }
        var value = $('#mother_email').val();
        var valid = validateEmail(value);
        if((!valid) && $.trim($("#mother_email").val()).length != 0) {
            $(".basic-details-mobile-error-message").text("Please Enter Valid Email");
            isFormValid = false;
        }

        if($('#course_preference_1').find('option:selected').attr('ispcmorjeemandatory') == 1) {
            if ($("#pcmPercentage").val() == '' && $("#jeeScore").val() == '') {
                $(".basic-pcm-error-message").text("Please Enter PCM or JEE Score");
                isFormValid = false;
                scrollToBasic();
            }
            else {
                isFormValid = true;
            }
        }

        if($('#pcmPercentage').val() == '') {
          $('#pcmPercentage').val(0);
        }
        
        if($('#jeeScore').val() == '') {
          $('#jeeScore').val(0)
        }

        if($('#jeeScore').val() > 360 ) {
          $(".basic-details-form-error-message").text("Please Enter Valid JEE Score");     
          $('#jeeScore').prop('required', true);
          isFormValid = false;
          scrollToBasic();   
        }
        else {
            $('#jeeScore').prop('required', false);
            isFormValid = true;
        }

        //Family Details One Mobile Number Mandate
        if ($("#father_contact").val() == '' && $("#mother_contact").val() == '') {
            $(".basic-details-mobile-error-message").text("Please Enter Father or Mother Mobile Number");
            scrollToBasic();
            isFormValid = false;
            return false;
        }
        else if (($("#father_contact").val().length != 10 && $("#father_contact").val() != '') || ($("#mother_contact").val().length != 10 && $("#mother_contact").val() != '')){
            $(".basic-details-mobile-error-message").text("Please Enter father or mother valid mobile number");
            scrollToBasic(); 
            isFormValid = false;
            return false;
        }

        if(videoCheck == "on") {
            $('input#videoWhatsApp:checkbox[value="' + videoCheck + '"]').prop('checked', true);
        }
        else {
            $('input#videoWhatsApp:checkbox[value="' + videoCheck + '"]').prop('checked', false);
        }

        if($('#jeeScore').val() > 360 ) {
          $(".basic-details-form-error-message").text("Please Enter Valid JEE Score");    
          $('#jeeScore').prop('required', true); 
          isFormValid = false;
          scrollToBasic();   
        }
        else {
            $('#jeeScore').prop('required', false);
            isFormValid = true;
        }
        
        if(element.val() == '') {
            return isInputValid;
        }
        else if(element.val() == '' && hscElement.val() == '') {
            return isInputValid;
            return hscFormValid;
        }
        else if(element.val() != '' && $("#level").val() == "Under-Graduate Programmes" && hscElement.val() == '') {
            return hscFormValid;
        }
        else if(element.val() == '' && $("#level").val() == "Under-Graduate Programmes" && hscElement.val() == '' && ugElement.val() == '') {
            return isInputValid;
            return hscFormValid;
            return isUgValid;
        }
        else if(element.val() != '' && $("#level").val() == "Under-Graduate Programmes" && hscElement.val() == '' && ugElement.val() == '') {
            return hscFormValid;
            return isUgValid;
        }
        else if($("#level").val() == "Post-Graduate Programmes" && element.val() == '' && hscElement.val() == '' && ugElement.val() == '') {
            return isInputValid;
            return hscFormValid;
            return isUgValid;
        }
        else if($("#level").val() == "Post-Graduate Programmes" && element.val() != '' && hscElement.val() == '' && ugElement.val() == '') {
            return hscFormValid;
            return isUgValid;
        }
        else if($("#level").val() == "Post-Graduate Programmes" && element.val() != '' && hscElement.val() != '' && ugElement.val() == '') {
            return isUgValid;
        }

        var eligibilityVal;
        if (isFormValid == true && hscFormValid == true && isInputValid == true && isUgValid == true)  {
             var formData;

            if($('#eligibility').text() != '') {
                eligibilityVal = $('#eligibility').text().replace('%', '');
            }
            if($('#eligibility').text() ==  'undefined' || $('#eligibility').text() == '') {
                eligibilityVal = '';
            }
            if($('#pcmPercentage').val() ==  'undefined' || $('#pcmPercentage').val() == '') {
                $('#pcmPercentage').val('');
            }
            if($('#jeeScore').val() ==  'undefined' || $('#jeeScore').val() == '') {
                $('#jeeScore').val('');
            }
            var totalFees = $('#totalFees').text().split(" ")[1].replace(/,/g, "");
            //var discountRate = $('#discountRate').text().split(" ")[0].replace('%', '');
            //var discountedFees = $('#discountedFees').text().split(" ")[1].replace(/,/g, "");
            var duration = $('#duration').text().split(" ")[0];

            formData = new FormData();

            var registrationMarksheet;
            if(($("#registration-marksheet-name").text().length == 0) || ($('#registrationMarksheet').val().length != 0)) {
                registrationMarksheet = $('#registrationMarksheet').get(0).files.item(0);
                formData.append('registrationMarksheet', registrationMarksheet);
            }
            var other_data = $('#basic-details-form').serializeArray();
            var formIdValue = $('#formId').text();
            var copyAddressVal = $('input[name="copyAddress"]').val();
            var data = {};
            var loop_values = [];
            $(other_data).each(function(index, obj) {
                data[obj.name] = obj.value;
                loop_values.push({
                    key: obj.name,
                    value: obj.value
                });
                formData.append(obj.name, obj.value);
            });

            formData.append("eligibility", $('#eligibility').text());
            formData.append("totalFees", totalFees);
            formData.append("duration", duration);
            formData.append("formId", formIdValue);
            formData.append("copyAddress", copyAddressVal);

            $.ajax({
                type: "POST",
                url: "/bin/gehu/basicAdmissionForm",
                //data: $('#basic-details-form').serialize() + '&eligibility=' + encodeURIComponent(eligibilityVal) + '&totalFees=' + encodeURIComponent(totalFees) + '&discountRate=' + encodeURIComponent(discountRate) + '&discountedFees=' + encodeURIComponent(discountedFees) + '&duration=' + encodeURIComponent(duration) +'&formId=' + $('#formId').text() + '&copyAddress=' +$('input[name="copyAddress"]').val(),
                //data: $('#basic-details-form').serialize() + '&eligibility=' + encodeURIComponent(eligibilityVal) + '&totalFees=' + encodeURIComponent(totalFees)  + '&duration=' + encodeURIComponent(duration) +'&formId=' + $('#formId').text() + '&copyAddress=' +$('input[name="copyAddress"]').val(),
                data: formData,
                processData: false,
                contentType: false,
                success: function(msg) {
                },
            });

            $(".form-two-column-wrapper, .basic-details-section").hide();
            if($("#selection-mode-form input[type='radio']:checked").val() == "registration") {
                $("body, html").animate({
                    scrollTop: $(".form-tabs").offset().top - 10
                }, 600);
                $(".registration-payment").show();
                $('.form-tabs ul li:first, .form-tabs ul li:nth-child(2), .form-tabs ul li:nth-child(3)').removeClass('selected');
                $('.form-tabs ul li:first, .form-tabs ul li:nth-child(2), .form-tabs ul li:nth-child(3)').addClass('changebg');
                $("#myLinks li:last-child").addClass("selected");
                return false;
            }
            else {
                $('.form-tabs ul li:first, .form-tabs ul li:nth-child(2), .form-tabs ul li:nth-child(3)').removeClass('selected');
                $('.form-tabs ul li:first, .form-tabs ul li:nth-child(2), .form-tabs ul li:nth-child(3)').addClass('changebg');
                $("#myLinks li:last-child").addClass("selected");
                $("body, html").animate({
                    scrollTop: $(".form-tabs").offset().top - 10
                }, 600);
                $(".upload-section").show();
                return false;
            }

            $("body, html").animate({
                scrollTop: $(".form-tabs").offset().top - 10
            }, 600);

            if ($(".upload-section").is(':visible') && $(".form-two-column-wrapper:not(:visible)") && $(".basic-details-section:not(:visible)")) {
                $('.form-tabs ul li:last-child').addClass('selected');
                $('.form-tabs ul li:first, .form-tabs ul li:nth-child(3)').removeClass('selected');
                $("#myLinks li:nth-child(3)").addClass("changebg");
            }
            return false;
        }
        return isFormValid;
    });

    $(".upload-option span").click(function() {
        $(".upload-option span").hide();
        $("#uploadPhoto").val('');
        $("#photo-name").text('');
        $(".upload-option, .upload-photo-content").removeClass('changebg');
        return false;
    });
    $(".upload-sslc-option span").click(function() {
        $(".upload-sslc-option span").hide();
        $("#uploadSslcMarksheet").val('');
        $("#sslc-marksheet-name").text('');
        $(".upload-sslc-option, .upload-sslc-marksheet-content").removeClass('changebg');
        return false;
    });
    $(".upload-hsc-option span").click(function() {
        $(".upload-hsc-option span").hide();
        $("#uploadHscMarksheet").val('');
        $("#hsc-marksheet-name").text('');
        $(".upload-hsc-option, .upload-hsc-marksheet-content").removeClass('changebg');
        return false;
    });
    $(".upload-ug-option span").click(function() {
        $(".upload-ug-option span").hide();
        $("#uploadUgMarksheet").val('');
        $("#ug-marksheet-name").text('');
        $(".upload-ug-option, .upload-ug-marksheet-content").removeClass('changebg');
    });
    $("#uploadPhoto").bind('change', function() {
        var isFormValid = true;
        var attach_id = "uploadPhoto";
        var fileLength = $('#' + attach_id)[0].files.length;
        var photoFileSize = $('#uploadPhoto')[0].files[0].size;

        if ($("#uploadPhoto").val() == '') {
            $("#uploadPhoto, .upload-photo-content").removeClass('changebg');
        }
        if ($("#uploadPhoto").val() == '' || $("#photo-name").text().length > 0) {
            $("#uploadPhoto, .upload-photo-content").removeClass('changebg hideText');
            $('#photo-name').text('');
        }
        if ($("#uploadPhoto").val() != '') {
            $(".upload-option, .upload-photo-content").addClass('changebg');
            $("span.photo").show();
            var _URL = window.URL || window.webkitURL;
            var file, img;
            if ($("#photo-name").text() == '') {
                var _URL = window.URL || window.webkitURL;
                var file, img;
                if ($("#file-name").text() == '') {
                    if ((file = this.files[0])) {
                        img = new Image();
                        img.src = _URL.createObjectURL(file);
                    }
                    var filename = $("#uploadPhoto").val().replace("C:\\fakepath\\", '')
                    $("#photo-name").text(filename);
                }
            }
            $("#file-name").text(this.files[0].name);
            if (!(/\.(gif|jpg|jpeg|tiff|png|bmp)$/i).test($("#uploadPhoto").val())) {
                $(".upload-error-message").text("Please upload valid format files which is mentioned in the description");
                $("#uploadPhoto").val('');
                $('#photo-name').text('');
                $('span.photo').hide();
                if ($("#uploadPhoto").val() == '') {
                    $(".upload-option, .upload-photo-content").removeClass('changebg');
                }
            }
            if (photoFileSize > 1000000) {
                $(".upload-error-message").text('');
                $(".upload-error-message").text("Please upload upto 1 MB file");
                $(".upload-option, .upload-photo-content").removeClass('changebg');
                $('#photo-name').text('')
                $('span.photo').hide();
            }
            else if (photoFileSize < 1000000 && $("#uploadPhoto").val() != '') { 
                $(".upload-error-message").text('');
            }
            isFormValid = false;
        }
        return isFormValid
    });
    $("#uploadSslcMarksheet").bind('change', function() {
        var attach_id = "uploadSslcMarksheet";
        var sslcFileSize = $('#uploadSslcMarksheet')[0].files[0].size;
        if ($("#uploadSslcMarksheet").val() == '' || $("#sscl-marksheet-name").text().length > 0) {
            $("#uploadSslcMarksheet, .upload-sslc-marksheet-content").removeClass('changebg hideText');
            $('#sslc-marksheet-name').text('');
        }
        if ($("#uploadSslcMarksheet").val() != '') {
            $("span.sslc").show();
            $(".upload-sslc-option, .upload-sslc-marksheet-content").addClass('changebg');
            var filename = $("#uploadSslcMarksheet").val().replace("C:\\fakepath\\", "");
            $("#sslc-marksheet-name").text(filename);
            $("#uploadSslcMarksheet, .upload-sslc-marksheet-content").addClass('changebg');
            if (!(/\.(gif|jpg|jpeg|tiff|png|bmp|pdf)$/i).test($("#uploadSslcMarksheet").val())) {
                $(".upload-error-message").text("Please upload valid format files which is mentioned in the description");
                $(".upload-sslc-option, .upload-sslc-marksheet-content").removeClass('changebg');
                $("#uploadSslcMarksheet").val('');
                $('#sslc-marksheet-name').text('');
                $('span.sslc').hide();
            }               
            if (sslcFileSize > 2000000) {
                $(".upload-error-message").text('');
                $(".upload-error-message").text("Please upload upto 2 MB file");
                $(".upload-sslc-option, .upload-sslc-marksheet-content").removeClass('changebg');
                $('#sslc-marksheet-name').text('');
                $('span.sslc').hide();
            }
            else if (sslcFileSize < 2000000 && $("#uploadSslcMarksheet").val() != '') { 
                $(".upload-error-message").text('');
            }
            isFormValid = false;
        }
        return isFormValid;
    });
    $("#uploadHscMarksheet").bind('change', function() {
        var attach_id = "uploadHscMarksheet";
        var hscFileSize = $('#uploadHscMarksheet')[0].files[0].size;
        $(".upload-error-message").html('');
        if ($("#uploadHscMarksheet").val() == '' || $("#hsc-marksheet-name").text().length > 0) {
            $("#uploadHscMarksheet, .upload-hsc-marksheet-content").removeClass('changebg hideText');
            $('#hsc-marksheet-name').text('');
        }
        if ($("#uploadHscMarksheet").val() != '') {
            $("span.hsc").show();
            $(".upload-hsc-option, .upload-hsc-marksheet-content").addClass('changebg');
            var filename = $("#uploadHscMarksheet").val().replace("C:\\fakepath\\", "");
            $("#hsc-marksheet-name").text(filename);
            $(".upload-hsc-option, .upload-hsc-marksheet-content").addClass('changebg');
            if (!(/\.(gif|jpg|jpeg|tiff|png|bmp|pdf)$/i).test($("#uploadHscMarksheet").val())) {
                $(".upload-error-message").text("Please upload valid format files which is mentioned in the description");
                $(".upload-hsc-option, .upload-hsc-marksheet-content").removeClass('changebg');
                $("#uploadHscMarksheet").val('');
                $('#hsc-marksheet-name').text('');
                $('span.hsc').hide();
            }
            if (hscFileSize > 2000000) {
                $('span.hsc').hide();
                $(".upload-error-message").text('');
                $(".upload-error-message").text("Please upload upto 2 MB file");
                $(".upload-hsc-option, .upload-hsc-marksheet-content").removeClass('changebg');
                $('#hsc-marksheet-name').text('');
            }
            else if (hscFileSize < 2000000 && $("#uploadHscMarksheet").val() != '') { 
                $(".upload-error-message").text('');
            }
            isFormValid = false;
        }
        return isFormValid;
    });
    $("#uploadUgMarksheet").bind('change', function() {
        var attach_id = "uploadUgMarksheet";
        var fileLength = $('#' + attach_id)[0].files.length;
        var ugFileSize = $('#uploadUgMarksheet')[0].files[0].size;
        $(".upload-error-message").html('');
        if ($("#uploadUgMarksheet").val() == '' || $('#ug-marksheet-name').text().length > 0) {
            $(".upload-ug-option, .upload-ug-marksheet-content").removeClass('changebg hideText');
            $('#ug-marksheet-name').text('');
        }
        if ($("#uploadUgMarksheet").val() != '' || ugFileSize > 200000) {
            $(".upload-ug-option, .upload-ug-marksheet-content").addClass('changebg');
            $("span.ug").show();
            var filename = $("#uploadUgMarksheet").val().replace(/C:\\fakepath\\/i, '')
            $("#ug-marksheet-name").text(filename);
            if (!(/\.(gif|jpg|jpeg|tiff|png|bmp|pdf)$/i).test($("#uploadUgMarksheet").val())) {
                $(".upload-error-message").text("Please upload valid format files which is mentioned in the description");
                $(".upload-ug-option, .upload-ug-marksheet-content").removeClass('changebg hideText');
                $("#uploadUgMarksheet").val('');
                $('#ug-marksheet-name').text('');
                $('span.ug').hide();
            }
            if (ugFileSize > 2000000 && $("#uploadUgMarksheet").val() != '') {
                $(".upload-error-message").text("Please upload upto 2 MB file");
                $(".upload-ug-option, .upload-ug-marksheet-content").removeClass('changebg hideText');
                $('#uploadUgMarksheet').val('');
                $('#ug-marksheet-name').text('');
                $('span.ug').hide();
            }
            else if (ugFileSize < 2000000) { 
                $(".upload-error-message").text('');
            }

            isFormValid = false;
        } else {
            isFormValid = true;
        }
        return isFormValid;
    });

    //Upload Document Validation
    var uploadSectionLabel = $(".upload-section-wrapper h4:not(.upload-ug-marksheet-content h4, .upload-section-wrapper .essay-question)");
    $(uploadSectionLabel).addClass("required");
    var formData;
    var videoUrl;
    $("#uploadSubmit").click(function() {

        //$(".essay-question").addClass("required");
        var isFormValid = true;
        //$('.essay-question-first textarea, .essay-question-second textarea').prop('minLength', 100);
        //$('.essay-question-first textarea, .essay-question-second textarea').prop('maxLength', 2000);
        /*if ($("#level").val() == "Post Graduate") {
            var attach_id = "uploadUgMarksheet ";
            var fileLength = $('#' + attach_id)[0].files.length;
            var fileSize = fileLength;
            if ($("#uploadUgMarksheet ").val() != '' || fileSize > 20000000) {
                $("#uploadUgMarksheet , .upload-ug-marksheet-content").addClass('changebg');
                if (!(/\.(gif|jpg|jpeg|tiff|png|bmp|pdf)$/i).test($("#uploadUgMarksheet").val())) {
                    $(".upload-error-message").text("*Upload only Image Format Files");
                    this.value = null;
                }
                if (fileSize > 20000000) {
                    $(".upload-error-message").text("*Allowed file size exceeded. (Max. 2 MB)");
                    this.value = null;
                }
                isFormValid = false;
            } else {
                isFormValid = true;
            }
        }*/
        if ($("#level").val() == "Diploma Programmes") {
            //if (($("#uploadPhoto").val().length != 0 || $("#photoWrapper #photo-name").text().length != 0) && ($("#uploadSslcMarksheet").val().length == 0 && $("#sslcWrapper #sslc-marksheet-name").text().length == 0)  && ($(".essay-question-first textarea").val().length != 0 && $(".essay-question-second textarea").val().length != 0)) {
                if (($("#uploadPhoto").val().length != 0 || $("#photoWrapper #photo-name").text().length != 0) && ($("#uploadSslcMarksheet").val().length == 0 && $("#sslcWrapper #sslc-marksheet-name").text().length == 0)) {
                isFormValid = false;
                scrollTopUpload();
                $(".upload-error-message").text('');
                $(".upload-error-message").text("Please Upload SSLC Document");
            }
            else {
                $(".upload-error-message").text('');
                $(uploadHscSectionLabel, uploadUgSectionLabel).removeClass("required");
                isFormValid = true;
                //if (($("#uploadPhoto").val().length === 0 && $("#photoWrapper #photo-name").text().length === 0) || ($(".essay-question-first textarea").val().length === 0 && $(".essay-question-second textarea").val().length === 0)) {
                if (($("#uploadPhoto").val().length === 0 && $("#photoWrapper #photo-name").text().length === 0)) {
                    scrollTopUpload();
                    $(".upload-error-message").text("Please Upload Mandatory Documents & Please Enter Mandatory Fields");
                    isFormValid = false;
                }
            }
        }

        if ($("#level").val() == "Under-Graduate Programmes") {
            //if (($("#uploadPhoto").val().length != 0 || $("#photoWrapper #photo-name").text().length != 0) && ($("#uploadSslcMarksheet").val().length != 0 || $("#sslcWrapper #sslc-marksheet-name").text().length != 0) && ($("#uploadHscMarksheet").val().length == 0 && $(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length == 0) && ($(".essay-question-first textarea").val().length != 0 && $(".essay-question-second textarea").val().length != 0)) {
            if (($("#uploadPhoto").val().length != 0 || $("#photoWrapper #photo-name").text().length != 0) && ($("#uploadSslcMarksheet").val().length != 0 || $("#sslcWrapper #sslc-marksheet-name").text().length != 0) && ($("#uploadHscMarksheet").val().length == 0 && $(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length == 0)) {
                isFormValid = false;
                scrollTopUpload();
                $(".upload-error-message").text('');
                $(".upload-error-message").text("Please Upload HSC Document");
            }
            else {
                $(".upload-error-message").text('');
                $(uploadUgSectionLabel).removeClass("required");
                isFormValid = true;
                //if (($("#uploadPhoto").val().length === 0 && $("#photoWrapper #photo-name").text().length === 0) || ($("#uploadSslcMarksheet").val().length === 0 && $("#sslcWrapper #sslc-marksheet-name").text().length === 0) || ($(".essay-question-first textarea").val().length === 0 && $(".essay-question-second textarea").val().length === 0)) {
                if (($("#uploadPhoto").val().length === 0 && $("#photoWrapper #photo-name").text().length === 0) || ($("#uploadSslcMarksheet").val().length === 0 && $("#sslcWrapper #sslc-marksheet-name").text().length === 0)) {
                    scrollTopUpload();
                    $(".upload-error-message").text("Please Upload Mandatory Documents & Please Enter Mandatory Fields");
                    isFormValid = false;
                }
            }
        }
        if ($("#level").val() == "Post-Graduate Programmes") {
            //if ($("#uploadUgMarksheet").val() == '' && $('#ug-marksheet-name').text().length == 0 && ($("#uploadPhoto").val().length != 0 || $("#photoWrapper #photo-name").text().length != 0) && ($("#uploadSslcMarksheet").val().length != 0 || $("#sslcWrapper #sslc-marksheet-name").text().length != 0) && ($("#uploadHscMarksheet").val().length != 0 || $(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length != 0) && ($(".essay-question-first textarea").val().length != 0 && $(".essay-question-second textarea").val().length != 0)) {
            if ($("#uploadUgMarksheet").val() == '' && $('#ug-marksheet-name').text().length == 0 && ($("#uploadPhoto").val().length != 0 || $("#photoWrapper #photo-name").text().length != 0) && ($("#uploadSslcMarksheet").val().length != 0 || $("#sslcWrapper #sslc-marksheet-name").text().length != 0) && ($("#uploadHscMarksheet").val().length != 0 || $(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length != 0)) {
                isFormValid = false;
                scrollTopUpload();
                $(".upload-error-message").text('');
                $(".upload-error-message").text("Please Upload UG Document");
            }
            else {
                $(".upload-error-message").text('');
                isFormValid = true;
                //if (($("#uploadPhoto").val().length === 0 && $("#photoWrapper #photo-name").text().length === 0) || ($("#uploadSslcMarksheet").val().length === 0 && $("#sslcWrapper #sslc-marksheet-name").text().length === 0) || ($("#uploadHscMarksheet").val().length === 0 && $(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length === 0) || ($(".essay-question-first textarea").val().length === 0 && $(".essay-question-second textarea").val().length === 0)) {
                if (($("#uploadPhoto").val().length === 0 && $("#photoWrapper #photo-name").text().length === 0) || ($("#uploadSslcMarksheet").val().length === 0 && $("#sslcWrapper #sslc-marksheet-name").text().length === 0) || ($("#uploadHscMarksheet").val().length === 0 && $(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length === 0)) {
                    scrollTopUpload();
                    $(".upload-error-message").text("Please Upload Mandatory Documents & Please Enter Mandatory Fields");
                    isFormValid = false;
                }
            }
        }
        if($("#admissionDeclaration").prop('checked') == false){
            $(".declaration-error").text("Please Accept the Declaration");
            scrollTopUpload();
            return false;
        }

        function scrollTopUpload() {
            $("body, html").animate({
                scrollTop: $(".upload-section-wrapper").offset().top - 10
            }, 600);
        }
        /*videoUrl = $('#selfVideoURL').val();
        url_validate = /^(http|https|ftp):\/\/[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/i;
        if (($(".essay-question-first textarea").val().length === 0 || $(".essay-question-second textarea").val().length === 0)) {
            scrollTopUpload();
            $(".upload-error-message").text("Please Enter Mandatory Fields");
            isFormValid = false;
        }
        else if ($("#selfVideoURL").val() == '' && $("#videoWhatsApp").prop('checked') == false) {
            $(".one-field-mandate").text("Please Provide atleaset one input");
            isFormValid = false;
        } 

        else if ($("#selfVideoURL").val() != '' && (!url_validate.test(videoUrl))) {
            $(".one-field-mandate").text("Please enter valid URL");
            isFormValid = false;
        }
        else if (($(".essay-question-first textarea").val().length != 0 && $(".essay-question-first textarea").val().length < 100) || $(".essay-question-second textarea").val().length != 0 && $(".essay-question-second textarea").val().length < 100) {
            scrollTopUpload();
            $(".upload-error-message").text("Please enter answer with minimum 100 chars for each question");
            isFormValid = false;
        }*/
        if(isFormValid) {
            $(".upload-error-message, .one-field-mandate").text('');
            isFormValid = true;
            if (window.FormData !== undefined) {
                formData = new FormData();
                var passport;
                if(($("#photoWrapper #photo-name").text().length == 0) || ($('#photoWrapper #uploadPhoto').val().length != 0)) {
                    passport = $('#uploadPhoto').get(0).files.item(0);
                    formData.append('photo', passport);
                }
                if($("#sslcWrapper #sslc-marksheet-name").text().length == 0 || $("#sslcWrapper #uploadSslcMarksheet").val().length != 0) {
                    var tenMarksheet = $('#uploadSslcMarksheet').get(0).files.item(0);
                    formData.append('marksheet_10th', tenMarksheet);
                }
                if($(".upload-wrapper.HscMarksheet #hsc-marksheet-name").text().length == 0 || $('.upload-wrapper.HscMarksheet #uploadHscMarksheet').val().length != 0) {
                    var twelveMarksheet = $('#uploadHscMarksheet').get(0).files.item(0);
                    formData.append('marksheet_12th', twelveMarksheet);
                }
                if($(".upload-wrapper.UgMarksheet #ug-marksheet-name").text().length == 0 || $('.upload-wrapper.UgMarksheet #uploadUgMarksheet').val().length != 0) {
                    var ugMarksheet = $('#uploadUgMarksheet').get(0).files.item(0);
                    formData.append('marksheet_ug', ugMarksheet);
                }
                var other_data = $('#upload-details-form').serializeArray();
                var data = {};
                var loop_values = [];
                $(other_data).each(function(index, obj) {
                    data[obj.name] = obj.value;
                    loop_values.push({
                        key: obj.name,
                        value: obj.value
                    });
                    formData.append(obj.name, obj.value);
                });

                var formIdValue = $('#formId').text();
                formData.append('formId', formIdValue);
                var videoWhatsApp = $('#videoWhatsApp').val();
                formData.append('videoWhatsApp', videoWhatsApp);
                $.ajax({
                    url: '/bin/gehu/uploadAdmissionForm',
                    data: formData,
                    contentType: false,
                    processData: false,
                    type: 'POST',
                    success: function(data) {
                    }
                });
                }
            $(".basic-details-section, .form-two-column-wrapper, .upload-section").hide();
            $(".thanks-wrapper").show();
            if ($(".thanks-wrapper").is(':visible') && $(".form-two-column-wrapper .basic-details-section .upload-section:not(:visible)")) {
                $('.form-tabs ul li').removeClass('selected');
                $("#myLinks li:last-child").addClass("changebg");
            }
            return false;
        }
        return isFormValid;
    });

    //Registraion Payment Validation
    $('#proceedTermsRegistration').on('click', function() {
        if($("#termsAndCondition").prop('checked') == false || $("#declaration").prop('checked') == false) {
            $(".terms-error").text("Please Agree Terms and declaration");
            isFormValid = false;
        }
        else {
            isFormValid = true;
            $(".terms-error").text('');
            $('.payment').show();
            return false;
        }
          
        return isFormValid;
    });

    $('input[type=radio][name=payment-mode]').change(function() {
        if($('#onlinePayment').is(':checked')) { 
            $('.payment-gateway-right-content').removeClass('changebg')
            $('.payment-gateway-left-content').addClass('changebg')
        }
        if( $('#offlinePayment').is(':checked')) { 
            $('.payment-gateway-left-content').removeClass('changebg')
            $('.payment-gateway-right-content').addClass('changebg')
        }
    });

    $('#paymentSubmitRegistration').on('click', function(e) {
        e.preventDefault();
        var isFormValid = true;
        if($('input[name="payment-mode"]:checked').length <= 0) {
            $(".payment-mode-error").text("Please Select Payment Mode");
            $("body, html").animate({ 
              scrollTop: $(".payment-option-error").offset().top -10
            }, 600);
            isFormValid = false;
        }
        else if(($('input[name="payment-mode"]:checked').length >= 0) && $('#onlinePayment:checked').length > 0 && isFormValid == true) {
            $(".partially-filled-form, .terms-wrapper").hide();
            var feeType = "";
            var amount = $("#vpc_Amount").val();
            /*if ($('#partialPayment:checked').length > 0) {
                feeType = "Partial Fee";
                amount = $('#amount').val();
            } else if ($('#fullFeePayment:checked').length > 0) {
                feeType = "Full Fee";
                amount = $("#vpc_Amount").val();
            }*/
            $.ajax({
                type: "POST",
                url: "/bin/geu/feePaymentType",
                data: "paymentType=" + feeType,
                success: function(responseText) {
                    
                },
            });
            //window.location.href = $("#vpc_ReturnURL").val() + '?merchant_id=' + $("#merchant_id").val() + '&order_id=' + $("#order_id").val() + '&currency=' + $("#currency").val() + '&amount=' + amount + '&redirect_url=' + $("#return_url").val() + '&cancel_url=' + $("#cancel_url").val() + '&language=' + $("#language").val();
            window.location.href = $("#vpc_ReturnURL").val() + '?formId=' + $('#formId').text() + '&selectionMode=registration';
            return false;
        }
        else if(($('input[name="payment-mode"]:checked').length >= 0) && $('#offlinePayment:checked').length > 0 && isFormValid == true) {
            $(".partially-filled-form, .terms-wrapper").hide();
            //var feeType = "";
            var amount = $("#vpc_Amount").val();
            var formType = $("#formType").val();
            /*if ($('#partialPayment:checked').length > 0) {
                feeType = "Partial Fee";
                amount = $('#amount').val();
            } else if ($('#fullFeePayment:checked').length > 0) {
                feeType = "Full Fee";
                amount = $("#vpc_Amount").val();
            }*/
            
            $.ajax({
                type: "POST",
                url: "/bin/geu/registrationOfflinePayment",
                data: "feeAmount=" + amount + "&formType=" + formType,
                success: function(responseText) {
                    window.location.href = '/content/gehu/en/offline-success.html';
                },
            });
            $('.form-tabs ul li').removeClass('selected');
            $("#myLinks li:last-child").addClass("changebg");
            return false;
        }
        return isFormValid;
    });
});
