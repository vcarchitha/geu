$(function () {
	var state = $("#state");
    state.empty().append('<option selected="selected" value="">-Select State-</option>');
    var city = $("#city");
    city.empty().append('<option selected="selected" value="">-Select City-</option>');

    $.ajax({
        type: "GET",
        url: "/bin/geu/statelist",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
        	state.empty().append('<option selected="selected" value="">-Select State-</option>');
            $.each(response, function () {
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
                $.each(response, function () {
                	city.append($("<option></option>").val(this['value']).html(this['text']));
                });

            },

        });
    });
    
	$("#enquiryform-btn-submit").on("click", function(e) {
		e.preventDefault();
		if($("#enquiryForm").valid())
			{	
			$.ajax({
		        type: "POST",
		        url: "/bin/geu/enquiryform",
		        data: $('#enquiryForm').serialize(),
		        success: function(msg) {
	            	$('#enquiryForm').hide();
	            	$('.enquiry-successmsg-wrapper').show();
		        },

		    });
			
			}
	
	});
	
	$("#enquiryform-gehu-btn-submit").on("click", function(e) {
		e.preventDefault();
		if($("#enquiryForm").valid())
			{	
			$.ajax({
		        type: "POST",
		        url: "/bin/geu/enquiryformgehu",
		        data: $('#enquiryForm').serialize(),
		        success: function(msg) {
	            	$('#enquiryForm').hide();
	            	$('.enquiry-successmsg-wrapper').show();
		        },

		    });
			
			}
	
	});
});
