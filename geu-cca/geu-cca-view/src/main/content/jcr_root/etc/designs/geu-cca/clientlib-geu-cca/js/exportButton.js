$(document).ready(function() {
	var redirectURL = $('#btn-back-url').data("url");

	$(".btn-export-leads").click(function(e) {
		$.ajax({
			type : "POST",
			url : "/bin/exportLeadServlet",
			success : function(msg) {
				document.location.href = msg;

			},
		});

	});
	$(".btn-back").on('click', function() {

		var d = $(this).data('url');
		window.location.href = d;
		return false;

	});

});