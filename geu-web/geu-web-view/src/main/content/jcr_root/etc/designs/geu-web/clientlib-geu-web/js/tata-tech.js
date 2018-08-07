$(function() {

    $('#phoneNumber').focusout(function () {
   var match = 'Please enter at least 10 characters.';
    setTimeout(function(){ 
        if($("#phoneNumber-error").text().length > 0) {
            $("#phoneNumber-error").text('');
            $("#phoneNumber-error").text('Please enter at least 10 digits');
        }

        }, 0);
    });
    
    $("#tataTechEnquireForm").validate({
        rules: {
            name: {
                required: true,
                minlength: 5
            },
            phoneNumber: {
                required: true,
                number: true,
                minlength: 10
            },
            email: {
                required: true,
                email: true,
                emailExt: true
            },
            comment: {
                required: false,
                maxlength: 255
            }
        },
        messages: {
           name: {
                required: "Please enter your name"
            },
            phoneNumber: {
                required: "Please enter your phone number",
                phoneNumber: "Please enter at least 10 characters",
            },
            email: {
                required: "Please enter your email"
            },
            comment: {
                required: "Please enter only 255 char"
            }
        },
        submitHandler: function (tataTechEnquireForm) {

            var successMessage = $('#successMessage').val();
                              var failureMessage = $('#failureMessage').val();
                if($('#tataTechEnquireForm #comment').val().length >= "255") {
                    alert('please not enter more than 255 char');
                    return false;
                 }
            $.ajax({
                        type: "POST",
                        url: "/bin/geu/TataTechServlet",
                        data: $('#tataTechEnquireForm').serialize(),
                        success: function(msg) {
                            if (msg == "success") 
                            {
                                $('#message').html(successMessage);
                                $('#tataTechEnquireForm').hide();
                                $('#backToHomePage').show();

                            } else {
                                $('#message').html(failureMessage);
                                $('#tataTechEnquireForm').hide();
                                $('#backToHomePage').show();

                            }
                        },

                    });

            return false;
        }
    });
    jQuery.validator.addMethod("emailExt", function(value, element, param) {
        return value.match(/^[a-zA-Z0-9_\.%\+\-]+@[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,}$/);
    },'Please enter a valid email address.');
    $('#phoneNumber').keypress(function(e) {
        var regex = new RegExp("^[0-9-]+$");
        var str = String.fromCharCode(!e.charCode && key != 8 && key != 46  ? e.which : e.charCode);
        if (regex.test(str)) {
            return true;
        }
        e.preventDefault();
    });

    $('#backToHomePage').click(function() {
        window.location.reload();
    });
    $('#tataTechEnquireForm #comment').prop('maxLength', 250);

    $(document).find('.geu-fullwidth-page-wrapper .richText, .geu-fullwidth-page-wrapper .cardLayout > section > div').addClass('container');

});
