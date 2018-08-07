$(function () {
            var facultyDepartments = $("#faculty-department");
            var facultyType = $("#facultyType").val();
            facultyDepartments.empty().append('<option selected="selected" value="0">-Select Department-</option>');
            var engineeringDepartments = $("#faculty-department-selection");
            engineeringDepartments.empty().append('<option selected="selected" value="0">-Select Department-</option>');
            $.ajax({
                type: "GET",
                url: "/bin/geu/facultyDepartment",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (response) {
                    facultyDepartments.empty().append('<option selected="selected" value="0">-Select Department-</option>');
                    $.each(response, function () {
                        facultyDepartments.append($("<option></option>").val(this['departmentValue']).html(this['departmentTitle']));
                    });
                },
        failure: function(response) {},
        error: function(response) {}
            });
            
            $.ajax({
                type: "GET",
                url: "/bin/geu/faculty?facultyType=" + facultyType,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (response) {
                    $('.faculty-content-page-component').empty();
                        $.each(response, function () {
                                $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper"><div class="faculty-content-page-image">' +
                                        '<img src="' + this['imageUrl'] + '" alt="img" class="img-responsive"></div>' +
                                  '<div class="content-page-descriptions textWrapper"><h3>' + this['name'] + '</h3><p>' + this['description'] + '</p></div></div>');
                        });
            facultyNavigation();
                },
        failure: function(response) {
                $('.faculty-content-page-component').empty();
                $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
            },
        error: function(response) {
                $('.faculty-content-page-component').empty();
                $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
            }
            });
            
            $('#faculty-department').on('change', function() {
                $('.faculty-slider').slick('destroy');
                var selected = $(this).val();
                if (selected == 'geu:faculty-departments/engineering-technology') {
                    $.ajax({
                        type: "GET",
                        url: "/bin/geu/levelTwoDepartment",
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (response) {
                            engineeringDepartments.empty().append('<option selected="selected" value="0">-Select Department-</option>');
                            $.each(response, function () {
                                engineeringDepartments.append($("<option></option>").val(this['departmentValue']).html(this['departmentTitle']));
                            });
                        },
                failure: function(response) {},
                error: function(response) {}
                    });
                } else {
                    engineeringDepartments.empty().append('<option selected="selected" value="0">-Select Department-</option>');
                }
                $.ajax({
                    type: "GET",
                    url: "/bin/geu/faculty?departmentName=" + selected + "&facultyType=" + facultyType,
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                        $('.faculty-content-page-component').empty();
                        $.each(response, function () {
                                $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper"><div class="faculty-content-page-image">' +
                                        '<img src="' + this['imageUrl'] + '" alt="img" class="img-responsive"></div>' +
                                  '<div class="content-page-descriptions textWrapper"><h3>' + this['name'] + '</h3><p>' + this['description'] + '</p></div></div>');
                        });
                        facultyNavigation();
                    },

            failure: function(response) {
                $('.faculty-content-page-component').empty();
                $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
            },
            error: function(response) {
                $('.faculty-content-page-component').empty();
                $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
            }

                });
             });
            $('#faculty-department-selection').on('change', function() {
                $('.faculty-slider').slick('destroy');
                var selectedDropdown1 = $(this).val();
                var selected = $(this).val();
                if(selectedDropdown1 == "0") {
                    if ($('#faculty-department').val() == 'geu:faculty-departments/engineering-technology') {
                        $.ajax({
                        type: "GET",
                        url: "/bin/geu/faculty?departmentName=" + $('#faculty-department').val() + "&facultyType=" + facultyType,
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (response) {
                            $('.faculty-content-page-component').empty();
                            $.each(response, function () {
                                    $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper"><div class="faculty-content-page-image">' +
                                            '<img src="' + this['imageUrl'] + '" alt="img" class="img-responsive"></div>' +
                                      '<div class="content-page-descriptions textWrapper"><h3>' + this['name'] + '</h3><p>' + this['description'] + '</p></div></div>');
                            });
                            facultyNavigation();
                        },

                        failure: function(response) {
                            $('.faculty-content-page-component').empty();
                            $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                            $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
                        },
                        error: function(response) {
                            $('.faculty-content-page-component').empty();
                            $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                            $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
                        }
                        });
                    }
                }
                else {
                    $.ajax({
                        type: "GET",
                        url: "/bin/geu/faculty?departmentName=" + selectedDropdown1 + "&departmentName2=" + selected + "&facultyType=" + facultyType,
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (response) {
                            $('.faculty-content-page-component').empty();
                            $.each(response, function () {
                                    $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper"><div class="faculty-content-page-image">' +
                                            '<img src="' + this['imageUrl'] + '" alt="img" class="img-responsive"></div>' +
                                      '<div class="content-page-descriptions textWrapper"><h3>' + this['name'] + '</h3><p>' + this['description'] + '</p></div></div>');
                            });
                            facultyNavigation();
                        },
                        failure: function(response) {
                            $('.faculty-content-page-component').empty();
                            $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                            $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
                        },
                        error: function(response) {
                            $('.faculty-content-page-component').empty();
                            $('.faculty-content-page-component').append('<div class="faculty-content-page-component-wrapper">No faculties available</div>');
                            $('.faculty .pagingInfo, .faculty-next, .faculty-prev').hide();
                        }
                    });
                }
            });
        });
function facultyNavigation() {

    /*faculty component pagination*/
    var indexCount;

    var $status = $('.pagingInfo');
    var $slickElement = $('.faculty-slider');
    var totalItemCount = $(".faculty-content-page-component-wrapper").length;
    var pageViewCount;
    var itemsShown = 0;
    var itemFrom = 0;
    $slickElement.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide, slider) {
        var pageNumber = (currentSlide ? currentSlide : 0) + 1;
        if (pageNumber == 1) {
            $('.faculty-prev').hide();
            if($(".faculty-prev:not(:visible)")) {
                $(".faculty-next").addClass('reduce-space');
            }
        } else {
            $('.faculty-prev').show();
            if($(".faculty-prev").is(':visible')) {
                $(".faculty-next").removeClass('reduce-space');
            }
        }
        pageViewCount = $('.faculty-content-page-component .slick-slide.slick-current.slick-active').find('.faculty-content-page-component-wrapper').length
        itemsShown = pageNumber * pageViewCount;
        itemFrom = itemsShown - pageViewCount;
        
        if (itemsShown > totalItemCount) {
            itemsShown = totalItemCount;
            itemFrom = totalItemCount - (totalItemCount % pageViewCount);
        }
        $status.text("Showing: " + (itemFrom + 1) + ' to ' + itemsShown + ' of ' + totalItemCount);

    });

    $slickElement.slick({
        autoplay: false,
        speed: 500,
        fade: true,
        cssEase: 'linear',
        dots: true,
        infinite: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        slidesPerRow: 4,
        rows: 3,
        arrows: true,
        prevArrow: $(".faculty-prev"),
        nextArrow: $(".faculty-next"),

        customPaging: function(slider, i) {
            var thumb = $(slider.$slides[i]).data();
            indexCount = i;
            return '<a>' + (i + 1) + '' + '</a>';
        },
        responsive: [{
                breakpoint: 1199,
                settings: {
                    dots: true,
                    arrows: true,
                    prevArrow: $(".faculty-prev"),
                    nextArrow: $(".faculty-next"),
                    infinite: true,
                    slidesToShow: 1,
                    slidesToScroll: 1,
                    slidesPerRow: 3,
                    rows: 2
                }
            },
            {
            breakpoint: 767,
            settings: {
                dots: true,
                arrows: true,
                 prevArrow: $(".faculty-prev"),
                nextArrow: $(".faculty-next"),
                infinite: true,
                slidesToShow: 1,
                slidesToScroll: 1,
                    slidesPerRow: 1,
                    rows: 3
                }
            }
        ]
    });

    $slickElement.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide) {
        var pageNumber = (currentSlide ? currentSlide : 0) + 1;
        var item_length = slick.$slides.length;
            pageViewCount = $('.faculty-content-page-component .slick-slide.slick-current.slick-active').find('.faculty-content-page-component-wrapper').length
        if (pageNumber == item_length) {
            $('.faculty-next').hide();
        } else {
            $('.faculty-next').show();
        }
        if (currentSlide == "0") {
            pageViewCount = $('.faculty-content-page-component .slick-slide.slick-current.slick-active').find('.faculty-content-page-component-wrapper').length
            itemsShown = pageNumber * pageViewCount;
            itemFrom = itemsShown - pageViewCount;
            if (itemsShown > totalItemCount) {
                itemsShown = totalItemCount;
                itemFrom = totalItemCount - (totalItemCount % pageViewCount);
            }
            $status.text("Showing: " + (itemFrom + 1) + ' to ' + itemsShown + ' of ' + totalItemCount);
            $('.pagingInfo').show();
        } 
        else if (pageViewCount == "0") {
            itemsShown = pageNumber * pageViewCount;
            itemFrom = itemsShown - pageViewCount;
            if (itemsShown == "0") { 
                $('.faculty-next, .faculty-prev, .pagingInfo').hide();
            }
        }
        else if (pageNumber != item_length) {
            pageViewCount = $('.faculty-content-page-component .slick-slide.slick-current.slick-active').find('.faculty-content-page-component-wrapper').length
            itemsShown = pageNumber * pageViewCount;
            itemFrom = itemsShown - pageViewCount;
            if (itemsShown > totalItemCount) {
                itemsShown = totalItemCount;
                itemFrom = totalItemCount - (totalItemCount % pageViewCount);
            }
            $status.text("Showing: " + (itemFrom + 1) + ' to ' + itemsShown + ' of ' + totalItemCount);
            $('.pagingInfo').show();
        } else if (pageNumber == item_length) {
            var i = (currentSlide ? currentSlide : 0) + 1;
            itemFrom = $('.faculty-content-page-component .slick-slide:not(.slick-current.slick-active)').find('.faculty-content-page-component-wrapper').length;
            $status.text("Showing: " + (itemFrom + 1) + ' to ' + totalItemCount + ' of ' + totalItemCount);
            $('.pagingInfo').show();
        }
});
};