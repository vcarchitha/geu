$(function() { 
    var redirectURL = $('#redirectURL').val();
    var errorMessage = $('#errorMessage').val();
    var loggedUser = $('#loggedUser').val();
    if (loggedUser == "true") {
        if(window.location.href.indexOf("login-page.html") > -1){
            window.location.href = redirectURL + ".html"}
    }
    
    $("#inputPassword").keydown(function() {
            var self = this, $self = $(this);
            setTimeout(function(){
                if ( $.trim(self.value) != "" && !$self.is(".name-error") ) {
                    $("#inputPassword").removeClass('error');
                    return;
                }
                else {
                     $("#inputPassword").addClass('error');
                }
            },0);
        });

    $("#loginform").validate({ 
        rules: { 
            name: "required", 
            inputPassword: "required", 
        }, 
        messages: { 
            name: "", 
            inputPassword: "" 
        }, 
        focusInvalid: false, 
        submitHandler: function() { 
        } 
    });  

    $("#btn-submit").click(function(e){  

         if ($("#loginform").valid()) { 
        $.ajax({
                type: "POST",
                url: $('#url').val(),
                data: {
                    j_username: $("#name").val(),
                    j_password: $("#inputPassword").val(),
                    j_validate: "true"
                },
                success: function(data, textStatus, jqXHR) {
                    $.ajax({
                        type: "POST",
                        url: "/bin/CCALoginServlet",
                        data: 'username=' + $("#name").val(),
                        success: function(msg) {

                            if (msg == "success") {
                                window.location.href = redirectURL + ".html"
                            } else {
                                $('#errormsg').html(errorMessage);

                            }
                        },

                    });
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    $('#errormsg').html(errorMessage);
                }
            });
}else {
            $('#errormsg').html(errorMessage);
        }

    });
});    

