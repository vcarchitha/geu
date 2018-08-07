$(document).ready(function() {

    var validateForm = function() {
        var captchaResponse = $('#g-recaptcha-response').val();
        if (captchaResponse == "") {
            $(".captcha-error").show();
            return false;
        }
        if (captchaResponse == true) {
            $(".captcha-error").hide();
        }
        var valid = false;
        var captchaResponse = $('#g-recaptcha-response').val();
        $.ajax({
            type: "POST",
            url: "/bin/geu/landingpage",
            data: "g-recaptcha-response=" + captchaResponse,
            success: function(responseData) {
            	captchaResponse = responseData;
            },

        });
        return captchaResponse;
    };

    if ($('.recaptcha-checkbox-checkmark').attr("style")) {
        $(".captcha-error").hide();
    }
    $("#gehu-submit").on('click', function() {
        validateForm();
    })
    $("#campaign-gehuenquiry").validate({
        rules: {
            firstname: {
                required: true
            },
            lastname: {
                required: true
            },
            mobile: {
                required: true,
                number: true,
                maxlength: 15,
                digits: true
            },
            email: {
                required: true,
                email: false,
                customEmailCheck: true
            },
            city: {
                required: true,
            },
            state: {
                required: true,
            },
            level: {
                required: true,
            },
            course: {
                required: true,
            }
        },
        messages: {
            mobile: {
                number: "Please enter only digits",
                maxlength: "Please enter a valid mobile number",
                customNumberCheck: true
            }
        },

        submitHandler: function(campaignForm) {

            var successMessage = $('#successMessage').val();
            var failureMessage = $('#failureMessage').val();
            var response = validateForm();
            if (response != '') {
                $(".captcha-error").hide();
                $.ajax({
                    type: "POST",
                    url: "/bin/geu/enquiryformgehu",
                    data: $('#campaign-gehuenquiry').serialize(),
                    success: function(msg) {
                        if (msg == "success") {
                            $('#message').html(successMessage);
                            $('#campaign-gehuenquiry').hide();
                            $('#backToHomePage').show();

                        } else {
                            $('#message').html(failureMessage);
                            $('#campaign-gehuenquiry').hide();
                            $('#backToHomePage').show();

                        }
                    },

                });
            }
            return false;
        }
    });
    jQuery.validator.addMethod('selectcheck', function(value) {
        return (value != '-1');
    }, "This field is required.");

    jQuery.validator.addMethod('customEmailCheck', function(value) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(value).toLowerCase());
    }, "Please enter a valid email address.");
    var state = $("#state");
    state.empty().append('<option selected="selected" value="">-Select State-</option>');
    var city = $("#city");
    city.empty().append('<option selected="selected" value="">-Select City-</option>');
    var level = $("#gehuLevel");
    level.empty().append('<option selected="selected" value="">-Select level-</option>');
    var course = $("#gehuCourse");
    course.empty().append('<option selected="selected" value="">-Select Course-</option>');

    $.ajax({
        type: "GET",
        url: "/bin/geu/statelist",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            state.empty().append('<option selected="selected" value="">-Select State-</option>');
            $.each(response, function() {
                state.append($("<option></option>").val(this['value']).html(this['text']));
            });

        },

    });

    $('#state').on('change', function() {
        var selected = $(this).val();
        $.ajax({
            type: "GET",
            url: "/bin/geu/statelist?stateName=" + selected,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(response) {
                city.empty().append('<option selected="selected" value="">-Select City-</option>');
                $.each(response, function() {
                    city.append($("<option></option>").val(this['value']).html(this['text']));
                });

            },

        });
    });

    $.ajax({
        type: "GET",
        url: "/bin/gehu/levelCourseList?type=level",
        success: function(msg) {
            var json = jQuery.parseJSON(msg);
            for (var key in json) {
                if (json.hasOwnProperty(key)) {
                    level.append("<option value='" + json[key] + "' id='" + key + "'>" + json[key] + "</option>");
                }
            }
        },

    });

    $("#gehuLevel").change(function() {
        var levelValue = $('#gehuLevel').val();
        var $select = $('#gehuCourse');
        $select.html('');
        $($select).prepend("<option value=''>--Select Course--</option>");
        $.ajax({
            type: "GET",
            url: "/bin/gehu/levelCourseList?type=program&levelSelected=" + levelValue,
            success: function(msg) {
                var json = jQuery.parseJSON(msg);
                for (var key in json) {
                    if (json.hasOwnProperty(key)) {
                        course.append("<option value='" + json[key] + "' id='" + key + "'>" + json[key] + "</option>");
                    }
                }
            },

        });
    });

});