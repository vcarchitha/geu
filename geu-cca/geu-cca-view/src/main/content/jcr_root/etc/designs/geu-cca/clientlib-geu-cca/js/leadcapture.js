$(function() {

            $.ajax({
                type: "GET",
                url: "/bin/ccaFollowUpServlet",
                data: 'user_id=' + $("#user_id").val(),
                success: function(msg) {
                    var jsonList = jQuery.parseJSON(msg);
                    if (Object.keys(jsonList.followUpJson).length == 0) {
                        $('#followUpTable tr:last').after('<tr><td class="no-followup" colspan="4">' + "No Follow-ups for Today" + '</td></tr>');
                    } else {
                        $(jsonList.followUpJson).each(function(index) {

                            $('#followUpTable tr:last').after('<tr class="clickable-row"><td>' + this.ccaLeadId + '</td><td class="tdrow">' + this.firstName + '</td><td class="tdrowOne">' + this.lastName + '</td><td>' + this.contactNum + '</td></tr>');
                        });

                        $(".clickable-row").click(function() {
                            $("#btn-clear").click();
                            $("#interest").removeClass('selectdisabled');
                            var td = $(this).find('td');
                            if (td.length != 0) {
                                $('#firstName').val(td.eq(1).text());
                                $('#lastName').val(td.eq(2).text());
                                $('#contactNum').val(td.eq(3).text());
                            }
                            $('#btn-get-info').click();
                        });
                    }

                },

            });

            $(".lead-capture-details input, .lead-capture-details select, .lead-capture-details textarea, #ccalead-btn-submit").prop('disabled', true);
            $('.converted-lead-status').hide();

            $(".formGetInfo-form input").prop('disabled', false);
            $('#spinner').hide();

            $("#btn-clear").click(function() {

                $("#ccaLeadForm :input").prop("disabled", false);
                $('.converted-lead-status').hide();
                $('.successMsg, .dateClearbtn, .followUpDateClearbtn, #followReq').hide();
                $("#combined-values").html('');
                $("#email, #father_email, #mother_email, #follow_up_date, #admission_id, #date_of_admission, #paymentMode").removeClass('selectdisabled');
                $("#interest").removeClass('selectdisabled');
                $('#emailerrormsg').hide();
                $('input[name=leadStatus]').each(function() {
                    $(this).attr("checked", false);
                });

                var user = $('#user_type').val();
                if (user == "Caller") {
                    $(".leadStatus[value=Converted]").remove();
                    $("#convertedLabel").remove();
                }
                $("#ccaLeadForm").trigger('reset');
                $('#errormsg, #noRecord').hide();
                $(".get-info input").prop('readonly', false);
                $(".formGetInfo-form button").prop('disabled', false);
                $(".lead-capture-details input, .lead-capture-details select, .lead-capture-details textarea, #ccalead-btn-submit").prop('disabled', true);
                $('.formGetInfo-form input').addClass('reset');

            });

            $(".leadStatus").change(function() {
                if ($(this).val() == "Converted") {
                    $('span.req').hide();
                    $('.converted-lead-status').show();
                } else if ($(this).val() == "Follow Up") {
                    $('.converted-lead-status, .dateOfAdmissionClearbtn').hide();
                    $('#admission_id, #date_of_admission, #paymentMode').val('');
                    $('span.req').show();
                } else {
                    $('.converted-lead-status, .dateOfAdmissionClearbtn, span.req').hide();
                    $('#admission_id, #date_of_admission, #paymentMode').val('');
                }

            });

            $("#ccaLeadForm").validate({
                rules: {
                    firstName: "required",
                    lastName: "required",
                    paymentMode: "required",
                    admission_id: "required",
                    date_of_admission: "required",
                    paymentMode: "required",
                    preboard_year: {
                        number: true,
                        maxlength: 4
                    },
                    year_of_passing_10th: {
                        number: true,
                        maxlength: 4
                    },
                    contactNum: {
                        required: true,
                        minlength: 10
                    }
                },

                focusInvalid: true,
                submitHandler: function() {}
            });

            var error = false;

            $('#contactNum, #mother_contact, #year_of_passing_12th, #year_of_graduation, #preboard_year, #year_of_passing_10th').keypress(function(e) {
                var regex = new RegExp("^[0-9-]+$");
                var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                if (regex.test(str)) {
                    return true;
                }

                e.preventDefault();
            });


            $('#cgpa_percentage').attr('maxlength', '5');

            $('#cgpa_percentage').keypress(function(event) {
                return isNumber(event, this)
            });

            function isNumber(evt, element) {
                var charCode = (evt.which) ? evt.which : event.keyCode

                if (
                    (charCode != 45 || $(element).val().indexOf('-') != -1) && (charCode != 46 || $(element).val().indexOf('.') != -1) &&
                    (charCode < 48 || charCode > 57))
                    return false;

                return true;
            }

            $('#aggregate_marks_10th, #projected_marks_12th, #marks_12th').on('keypress keyup blur', function(event) {
                $(this).val($(this).val().replace(/[^0-9\.]/g, ''));
                if ((event.which != 46 || $(this).val().indexOf('.') != -1) && (event.which < 48 || event.which > 57)) {
                    event.preventDefault();
                }
            });

            $("#year_of_passing_12th, #year_of_graduation, #preboard_year, #year_of_passing_10th").keyup(function(e) {
                $(this).attr('maxlength', '4');
            });


            function ValidateEmail(email) {
                var expr = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
                return expr.test(email);
            }

            function testEmail(e) {


                return error;
            }

            function emailError() {
                $("#email").on('change keyup', function() {
                    $(this).addClass('selectdisabled');
                    error = true;
                });
            }

            function dateCompare(fromFullDate, toFullDate){
                /* 
                    fromFullDate > toFullDate then it will return 1
                    fromFullDate < toFullDate then it will return -1
                    fromFullDate = toFullDate then it will return 0
                */
                var fromDate = fromFullDate.substring(0,2);
                var fromMonth = fromFullDate.substring(3,5);
                var fromYear = fromFullDate.substring(6,10);
                var toDate = toFullDate.substring(0,2);
                var toMonth = toFullDate.substring(3,5);
                var toYear = toFullDate.substring(6,10);
                
                if(fromYear < toYear){
                    return -1;
                }else if(fromYear > toYear){
                    return 1;
                }else if(fromMonth < toMonth){
                    return -1;
                }else if(fromMonth > toMonth){
                    return 1;
                }else if(fromDate < toDate){
                    return -1;
                }else if(fromDate > toDate){
                    return 1;
                }else{
                    return 0;
                }   
            }

            function checkisfollowupdatetoday(leadstatus, followupdate) {
                var isvalid = true;
                var today = new Date();
                var dd = today.getDate();
                var mm = today.getMonth() + 1;
                var yyyy = today.getFullYear();

                if (dd < 10) {
                    dd = '0' + dd;
                }
                if (mm < 10) {
                    mm = '0' + mm;
                }
                var today = dd + '-' + mm + '-' + yyyy;
                if (leadstatus == "Follow Up" && followupdate != '' && dateCompare(followupdate, today) < 1) {
                    $('#follow_up_date').addClass('selectdisabled');
                    $('#futureDataError').show();
                    isvalid = false;
                } else {
                    $('#follow_up_date').removeClass('selectdisabled');
                    $('#futureDataError').hide();
                }

                return isvalid;
            }

            function emailvalidation() {
                var isvalid = true;

                if ($("#email").val() != '' && !ValidateEmail($("#email").val())) {
                    $('#email').addClass('selectdisabled');
                    isvalid = false;
                } else {
                    $("#email").removeClass('selectdisabled');
                }

                if ($("#father_email").val() != '' && !ValidateEmail($("#father_email").val())) {
                        $('#father_email').addClass('selectdisabled');
                        isvalid = false;
                    } else {
                        $("#father_email").removeClass('selectdisabled');
                    }

                    if (($("#mother_email").val() != '') && !ValidateEmail($("#mother_email").val())) {
                        $('#mother_email').addClass('selectdisabled');
                        isvalid = false;
                    } else {
                        $("#mother_email").removeClass('selectdisabled');
                    }

                    if (!isvalid) {
                        $('#emailerrormsg').show();
                    } else {
                        $('#emailerrormsg').hide();
                    }

                    return isvalid;
                }

                function domandatoryValidation() {
                    var isvalid = true;

                    if ($('#interest').val() == '') {
                        $("#interest").addClass('selectdisabled');
                        isvalid = false;
                    } else {
                        $("#interest").removeClass('selectdisabled');
                        }

                        var leadstatus = $('input[name="leadStatus"]:checked').val();
                        var followupdate = $('#follow_up_date').val();
                        var isfollowupdatetoday = checkisfollowupdatetoday(leadstatus, followupdate);
                        var leadStatusInputs = $("#admission_id, #date_of_admission, #paymentMode");

                        if (leadstatus == "Converted") {
                            $(leadStatusInputs).each(function() {
                                    var element = $(this);
                                    if (element.val() == '') {
                                        $(element).addClass('selectdisabled');
                                        isvalid = false;
                                    }
                                    else{
                                        $(element).removeClass('selectdisabled');
                                    }

                                });

                            }
                    else{
                        $(leadStatusInputs).removeClass('selectdisabled');
                        if (leadstatus == "Follow Up" && followupdate == '') {
                            $('#follow_up_date').addClass('selectdisabled');
                            isvalid = false;
                        }
                    }

                                if (!isvalid) {
                                    $('#errormsg').html('Please enter mandatory fields ');
                                    $('#errormsg').show();
                                } else {

                                    $('#errormsg').hide();
                                }

                                return (isvalid && isfollowupdatetoday);
                            }

                            $("#ccalead-btn-submit").on("click", function(e) {
                                $('#spinner').show();
                                $('#noRecord').hide();
                                var isvalidemail = emailvalidation();
                                var ismandatory = domandatoryValidation();
                                if (!isvalidemail || !ismandatory) {
                                    $('html,body').animate({ scrollTop: 0 }, 'slow');
                                    $("#spinner").hide();
                                    return false;
                                }

                                $.ajax({
                                    type: "POST",
                                    url: "/bin/ccaLeadCaptureServlet",
                                    data: $('#ccaLeadForm').serialize() + '&formAction=' + "insert",
                                    success: function(msg) {
                                        if (msg == 0) {
                                            $('#successMsg').html('Something went wrong. Please try again.');
                                            $('#successMsg').show();
                                            $('span#successMsg').css("color", "red");
                                            $("#spinner").hide();
                                        } else {
                                            $('#successMsg').html('Inserted Successfully !');
                                            $('span#successMsg').css("color", "green");
                                            $('#successMsg').show();
                                            //clear date btn
                                            $('.followUpDateClearbtn').hide();
                                            $('.dateOfAdmissionClearbtn').hide();
                                            $('.dateClearbtn').hide();
                                            $('#combined-values').html('');
                                            $('input[name=leadStatus]').each(function() {
                                                $(this).attr("checked", false);
                                            });
                                            $("#ccaLeadForm").trigger('reset');
                                            $("#btn-get-info").prop('disabled', false);
                                            $(".get-info input").prop('readonly', false);
                                            $(".formGetInfo-form input").prop('disabled', false);
                                            $(".lead-capture-details input, .lead-capture-details select, .lead-capture-details textarea, #ccalead-btn-submit").prop('disabled', true);
                                            $(".converted-lead-status, #spinner").hide();
                                        }
                                    },

                                });
                            });
                            $("#btn-get-info").click(function(e) {
                                $('#spinner').show();
                                $('#successMsg, #errormsg, #noRecord').hide();
                                $('.formGetInfo-form input').removeClass('reset');

                                if ($("#ccaLeadForm").valid()) {} else {
                                    $('#errormsg').show();
                                    $('#spinner').hide();
                                    return false;
                                }
                                $.ajax({
                                    type: "POST",
                                    url: "/bin/ccaLeadCaptureServlet",
                                    data: $('#ccaLeadForm').serialize() + '&formAction=' + "get",
                                    success: function(msg) {
                                        $('#spinner').hide();
                                        $('#noRecord').hide();
                                        if ($('.get-info input') !== '') {
                                            $(".lead-capture-details input, .lead-capture-details select, .lead-capture-details textarea, #ccalead-btn-submit").prop('disabled', false);
                                            $("#btn-get-info").prop('disabled', true);
                                            $(".lead-capture-details select").prop('disabled', false).addClass('.selectdisabled');
                                            $(".get-info input").prop('readonly', true);
                                        }
                                        var json = jQuery.parseJSON(msg);

                                        if (Object.keys(json).length == 0) {
                                            $('#noRecord').show();
                                            $('#spinner').hide();
                                            var dt = new Date();
                                            var month = pad2(dt.getMonth() + 1);
                                            var day = pad2(dt.getDate());

                                            var currentDate = day + '-' + month + '-' + dt.getFullYear();

                                            $('#lead_generation_date').val(currentDate);
                                        } else {
                                            $('#errormsg').hide();
                                            var user = $('#user_type').val();
                                            for (var key in json) {
                                                if (json.hasOwnProperty(key)) {

                                                    if (key == "leadStatus" && json[key] == "Converted") {
                                                        if (user == "Caller") {
                                                            var radioBtn = $('<input type="radio" id="leadStatus" class="leadStatus" name="leadStatus" value="Converted" /><label for="leadStatus" id="convertedLabel" class="radioBtn">Converted</label>');
                                                            radioBtn.appendTo('#callerTarget');
                                                        }
                                                        $("#ccaLeadForm :input").prop("disabled", true);
                                                        $(".btn-clear").prop("disabled", false);
                                                        $('.converted-lead-status').show();
                                                    }

                                                    var k = '#' + key;

                                                    if (json[key] != null) {

                                                        if (key == 'leadStatus') {
                                                            var leadStatusValue = json[key];
                                                            $('input:radio[name="leadStatus"]').filter('[value="' + leadStatusValue + '"]').attr('checked', true);

                                                        } else if (key == 'previous_comment') {
                                                            var prev_comments_val = json[key];
                                                            $("#comment").text('');
                                                            var comment = $("#comment").append("<span>" + prev_comments_val + "</span>")

                                                            $('#comment span').each(function() {
                                                                if ($('#comment span').html().indexOf("$") != -1) {
                                                                    $("#combined-values").html('');
                                                                    var str_replace = $('#comment span').text().replace(/[@][#][$][%]/g, ',');
                                                                    strx = str_replace.split(',');
                                                                    array = [];
                                                                    array = array.concat(strx);
                                                                    for (i = 0; i < array.length; i++) {
                                                                        var all_values = $("#combined-values").append('<span class="array-values">' + array[i] + '</span>');
                                                                    }
                                                                } else {
                                                                    var single_val = $('#comment span').text();
                                                                    var all_values = $("#combined-values").append('<span class="single-val">' + single_val + '</span>');
                                                                }
                                                            });

                                                        } else if (key == 'date_of_birth') {
                                                            $(k).val(json[key]);
                                                            $('.dateClearbtn').show();
                                                            if ($(k).val(json[key]).is(':disabled')) {
                                                                $("span.dateClearbtn").hide();
                                                            }

                                                        } else if (key == 'follow_up_date') {
                                                            $("span.req").show();
                                                            $(k).val(json[key]);
                                                            $('span.followUpDateClearbtn').show();
                                                            if ($('#follow_up_date').is(':disabled')) {
                                                                $("span.followUpDateClearbtn").hide();
                                                            }

                                                        } else {
                                                            $(k).val(json[key]);
                                                        }

                                                    } else {
                                                        $(k).val('');
                                                    }
                                                }
                                            }

                                        }
                                    },

                                });

                            });


                            var date = new Date();
                            date.setDate(date.getDate());


                            $('.date-of-admission-wrapper').datepicker({
                                format: 'dd-mm-yyyy',
                                endDate: date,
                                autoclose: true
                            }).on('change', function() {
                                $('.dateOfAdmissionClearbtn').show();
                                $('date-of-admission-wrapper').datepicker('hide');
                            });

                            $('.follow-up-date-wrapper').datepicker({
                                startDate: '+1d',
                                minDate: 1,
                                format: 'dd-mm-yyyy',
                                autoclose: true,
                                onRender: function(date) {
                                    return date.valueOf() < new Date().valueOf() ? 'disabled' : '';
                                }
                            }).on('change', function() {
                                $('.followUpDateClearbtn').show();
                                $('.follow-up-date-wrapper').datepicker('hide');
                            });


                            $(".date-of-birth-wrapper").datepicker({
                                changeMonth: true,
                                changeYear: true,
                                endDate: '+0',
                                showAnim: 'slideDown',
                                format: 'dd-mm-yyyy',
                                autoclose: true,
                            }).on('change', function(selected) {
                                $('.dateClearbtn').show();
                                $('.date-of-birth-wrapper').datepicker('hide');
                                var age = getAge($('#lead_generation_date').val(), $("#date_of_birth").val());
                                $('#age').val(age);
                                if (isNaN(age)) {
                                    var age = 0;
                                    $('#age').val(age);
                                }
                            });
                            $('#date_of_birth').on('changeDate', function(selected) {
                                $('.dateClearbtn').show();
                                $('#date_of_birth').datepicker('setDate', selected.date);
                                $(this).datepicker('hide');
                            });
                            $('.dateClearbtn').on('click', function() {
                                $('.date-of-birth-wrapper').datepicker('setDate', '');
                            });
                            $('.dateClearbtn').click(function() {
                                $("input[name='date_of_birth']").val('');
                                $('.dateClearbtn').hide();
                            });
                            $('.dateOfAdmissionClearbtn').click(function() {
                                $("input[name='date_of_admission']").val('');
                                $('.dateOfAdmissionClearbtn').hide();
                            });
                            $('.followUpDateClearbtn').click(function() {
                                $("input[name='follow_up_date']").val('');
                                $('.followUpDateClearbtn').hide();
                            });

                            function pad2(number) {
                                return (number < 10 ? '0' : '') + number;
                            }



                            function getAge(leadGenerationString, dobString) {
                                var leadString = leadGenerationString;
                                var dobSplit = dobString.split('-');
                                var dobDate = new Date(dobSplit[1] + "/" + dobSplit[0] + "/" + dobSplit[2]);

                                var leadSplit = leadString.split('-');
                                var leadDate = new Date(leadSplit[1] + "/" + leadSplit[0] + "/" + leadSplit[2]);

                                var leadYear = leadDate.getFullYear(); // lead year            
                                var leadMonth = leadDate.getMonth(); // lead month
                                var leadDay = leadDate.getDate(); // lead month
                                var birthdayYear = dobDate.getFullYear(); // birthday year
                                var birthdayMonth = dobDate.getMonth(); // birthday month
                                var birthdayDay = dobDate.getDate(); // birthday day of month

                                var age = (leadMonth == birthdayMonth && leadDay >= birthdayDay) ?
                                    leadYear - birthdayYear : (leadMonth > birthdayMonth) ?
                                    leadYear - birthdayYear : leadYear - birthdayYear - 1;
                                return age;
                            }

                        });