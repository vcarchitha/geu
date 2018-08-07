$(window).load(function() {
    $('.richText img').each(function() {
        var imgWidth = $(this).width();
        console.log('all img width', imgWidth);
        if (imgWidth > 750) {
            $(this).addClass('larger-image');
        }
    });
    if($(".slick-dots").is(":visible")) {
        var a=$(".slick-dots").width();
        $(".slider-arrows .prev, .slider-arrows .faculty-prev, .slider-arrows .image-gallery-prev").css({left:-a-80+"px"})
    }

});
$(document).ready(function() {

    var id = '#popup-message';

    var overlayHeight = $(document).height();
    var overlayWidth = $(window).width();

    $('#overlay').css({ 'width': overlayWidth, 'height': overlayHeight });

    $('#overlay').fadeIn(500);
    $('#overlay').fadeTo("slow", 0.9);

    var winH = $(window).height();
    var winW = $(window).width();

    $(id).css('top', winH / 2 - $(id).height() / 2);
    if(winW < 768) {
        $(id).css('top', winH / 10);
    }
    $(id).css('left', winW / 2 - $(id).width() / 2);

    $(id).fadeIn(2000);

    $('.popup .close').click(function (e) {
        e.preventDefault();
        $('#overlay').hide();
        $('.popup').hide();
    });

    $('#overlay').click(function () {
        $(this).hide();
        $('.popup').hide();
    });

    $(window).scroll(function () {
        if ($(this).scrollTop() > 100) {
            $('.scrollup').fadeIn();
        } else {
            $('.scrollup').fadeOut();
        }
    });
    
    $("a[href='#top']").click(function() {
        $("html, body").animate({ scrollTop: 0 }, "slow");
        return false;
    });


    if ($(window).width() < 1024) {
        $('.richText table').parent().addClass('table-responsive');
        $('.richText table').addClass('table');
    }
    $(".search-form").submit(function() {
        if ($(this).find('input[type="search"]').val() == "") {
            return false;
        }
    })

    $(".right-side-nav-bar i.fa-search").click(function() {
        $(this).parents(".search-form").submit();
    })

    /*Mobile Sub menu Toggle Changes*/
    if ($('.mobile-header-wrapper #navbar ul.dropdown-main-menu li').find('.dropdown-menu')) {
        $('.mobile-header-wrapper #navbar ul.dropdown-main-menu > li').find('.dropdown-menu').before('<i class="fa fa-caret-down dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"></i>')
    }
    $(".mobile-header-wrapper #navbar ul.dropdown-main-menu li i").click(function() {
        if ($(".mobile-header-wrapper #navbar ul.dropdown-main-menu li").hasClass("open")) {
            $(this).removeClass('fa-caret-up').addClass('fa-caret-down');
        } else if (!$(".mobile-header-wrapper #navbar ul.dropdown-main-menu li").hasClass("open")) {
            $(this).removeClass('fa-caret-down').addClass('fa-caret-up');
        }
    });
    $('.mobile-header-wrapper .right-side-wrapper .search').click(function(e) {
        e.stopPropagation()
        $('.mobile-header-wrapper .right-side-wrapper .search input').show();
    });

    // Hide the "info_image_click" by clicking outside container
    $(document).click(function() {
        $('.mobile-header-wrapper .right-side-wrapper .search input').hide();
    });
    /*Target Parent Menu Which is have sub menu*/
    $('.main-menu ul li > ul li').closest('ul').closest('li').addClass('active');
    $(".main-menu ul > li").not(".main-menu ul li ul").addClass('main');

    if ($('.content-page-banner > img').attr('src') == '' || $('.content-page-banner > img').attr('src') == undefined) {
        $('.content-page-banner').addClass('placeholder');
        $('.content-page-banner img').addClass('hide');
    }

    /*Timeline Carousel*/
    var sync1 = $(".glorious-carousel-image");
    var sync2 = $(".glorious-carousel-year");
    var slidesPerPage = 8; //globaly define number of elements per page
    var syncedSecondary = true;

    sync1.owlCarousel({
        items: 1,
        nav: false,
        dots: false,
        animateIn: 'fadeIn',
        animateOut: 'fadeOut',
    }).on('changed.owl.carousel', syncPosition);

    sync2
        .on('initialized.owl.carousel', function() {
            sync2.find(".owl-item").eq(0).addClass("current");
        })
        .owlCarousel({
            items: 9,
            margin: 107,
            dots: false,
            nav: false,
            URLhashListener: true,
            startPosition: 'URLHash',
        }).on('changed.owl.carousel', syncPosition2);

    function syncPosition(el) {
        var current = el.item.index;
        sync2.find(".owl-item").removeClass("current").eq(current).addClass("current");
        var onscreen = sync2.find('.owl-item.active').length - 1;
        var start = sync2.find('.owl-item.active').first().index();
        var end = sync2.find('.owl-item.active').last().index();

        if (current > end) {
            sync2.data('owl.carousel').to(current, 100, true);
        }
        if (current < start) {
            sync2.data('owl.carousel').to(current - onscreen, 100, true);
        }
    }

    function syncPosition2(el) {
        if (syncedSecondary) {
            var number = el.item.index;
            sync1.data('owl.carousel').to(number, 100, true);
        }
    }

    sync2.on("click", ".owl-item", function(e) {
        e.preventDefault();
        var number = $(this).index();
        sync1.data('owl.carousel').to(number, 300, true);
    });

    var checkWidth = $(window).width();

    if(checkWidth < 992) {
        sync2.owlCarousel('destroy');
        sync2.owlCarousel({
            items: 5,
            touchDrag: true,
            mouseDrag: true,
            margin: 80,
            nav: false,
            dots: false
        });
    }
    if(checkWidth < 767) {
        sync2.owlCarousel('destroy');
        sync2.owlCarousel({
            items: 3,
            touchDrag: true,
            mouseDrag: true,
            margin: 80,
            nav: false,
            dots: false
        });
    }
    /*Banner Carousel*/
    var one = $(".owl-carousel");
    var banner_carousel = $(".geu-banner-carousel");
    one.owlCarousel({
        items: 4,
        loop: true,
        margin: 5,
        autoplay: true,
        autoplayTimeout: 1000,
        autoplayHoverPause: true
    });

    banner_carousel.owlCarousel({
        items: 1,
        autoplay: true,
        loop: true,
        autoplayTimeout: 2000,
    });

    $('.geu-banner-carousel').addClass('owl-carousel').owlCarousel({
        margin: 10,
        nav: true,
        items: 1,
    });

    $('.main-menu ul li > ul li').closest('ul').closest('li').addClass('active');

    //Accordion
    $('.accordion-container .panel-collapse.collapse.in:first').css("display", "block");
    $('.accordion-container .panel-collapse.collapse.in:first').prevAll('div.panel-heading').addClass('active');
    $('.accordion-container .panel-heading.active').find(".plusminus").text('-');

    $(".panel-heading").click(function() {
        if ($('.panel-collapse').is(':visible')) {
            $(".panel-collapse").slideUp(300);
            $(".plusminus").text('+');
            $(".panel-heading").removeClass('active');

        }
        if ($(this).next(".panel-collapse").is(':visible')) {
            $(this).next(".panel-collapse").slideUp(300);
            $(this).children(".plusminus").text('+');
            $(".panel-heading").removeClass('active');
        } else {
            $(this).next(".panel-collapse").slideDown(300);
            $(this).children(".plusminus").text('-');
            $(this).addClass('active');
        }
    });
    
    var one = $(".owl-carousel");
    var banner_carousel = $(".geu-banner-carousel");
    one.owlCarousel({
        items: 4,
        loop: true,
        margin: 5,
        autoplay: true,
        autoplayTimeout: 1000,
        autoplayHoverPause: true
    });
    banner_carousel.owlCarousel({
        items: 1,
        autoplay: true,
        loop: true,
        autoplayTimeout: 2000,
    });
    $('.geu-banner-carousel').addClass('owl-carousel').owlCarousel({
        margin: 10,
        nav: true,
        items: 1,
    });

    /*Text Carousel*/
    /*Banner Carousel*/
    var one = $(".text-carousel");
    $('.text-carousel').addClass('owl-carousel').owlCarousel({
        items: 1,
        loop: true,
        nav: true,
        dots: false,
        autoplay: true,
        autoplayTimeout: 2000,
        autoplayHoverPause: true,
        navText: ["<i class='fas fa-angle-left'></i>", "<i class='fas fa-angle-right'></i>"]
    });
    //success-stories
    var successStories = $(".text-carousel");
    $('.success-stories-carousel').addClass('owl-carousel').owlCarousel({
        items: 2,
        loop: true,
        nav: true,
        dots: false,
        autoplay: true,
        autoplayTimeout: 2000,
        autoplayHoverPause: true,
        responsiveClass: true,
        navText: ["<i class='fas fa-angle-left'></i>", "<i class='fas fa-angle-right'></i>"],
        responsive: {
            0: {
                items: 1,
                nav: true
            },
            600: {
                items: 2,
                nav: false
            }
        }
    });

    var owl = $(".text-carousel");
    $('.text-carousel').on('mouseover', function() {
        owl.trigger('stop.owl.autoplay');
    });
    $('.text-carousel').on('mouseleave', function() {
        owl.trigger('play.owl.autoplay');
    });

    /*Counter*/
    $(window).on("scroll", function() {
        if ($('.statisticsCounter').length == "1") {
            var hT = $('.statisticsCounter').offset().top - 100,
                hH = $('.statisticsCounter').outerHeight(),
                wH = $(window).height(),
                wS = $(this).scrollTop();
            if (wS > (hT + hH - wH)) {
                $('.count').each(function() {
                    $(this).prop('Counter', 0).animate({
                        Counter: $(this).text()
                    }, {
                        duration: 4000,
                        easing: 'swing',
                        step: function(now) {
                            $(this).text(Math.ceil(now));
                        }
                    });
                });
                $(window).off('scroll');
            }
        }
    });


    //Counter Slider on Mobile
    function postsCarousel() {
        var checkWidth = $(window).width();
        var owlPost = $("#counter_mobile_slider");
        if (checkWidth > 768) {
            if (owlPost.length == 1) {
                if (typeof owlPost.data('owl.carousel') != 'undefined') {
                    owlPost.data('owl.carousel').destroy();
                }
            }
            owlPost.removeClass('owl-carousel');
        } else if (checkWidth < 768) {
            owlPost.addClass('owl-carousel');
            owlPost.owlCarousel({
                items: 1,
                slideSpeed: 1000,
                touchDrag: true,
                mouseDrag: true,
                autoplay: true,
                autoplaySpeed: 1000,
                dots: true,
                loop: true
            });
        }
    }

    postsCarousel();
    $(window).resize(postsCarousel);

    //Tabs Click to Center
    $('.tab-nav-wrapper li').on('click', function() {

        var pos = $(this).position().left;
        var currentscroll = $(".tab-nav-wrapper").scrollLeft();
        var divwidth = $(".tab-nav-wrapper").width();
        pos = (pos + currentscroll) - (divwidth / 6);
        $('.tab-nav-wrapper').animate({
            scrollLeft: pos
        });

    });

    var indexCount;

    /*News and Events Pagination Script*/
    var $status = $('.pagingInfo');
    var $slickElement = $('.upcoming-event-slider');
    var totalItemCount = $(".upcoming-events-component-wrapper").length;
    var pageViewCount = $(window).width() > 767 ? 6 : 3; //number of items in a page 
    var itemsShown = 0;
    var itemFrom = 0;
    $slickElement.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide, slider) {
        var pageNumber = (currentSlide ? currentSlide : 0) + 1;
        if (pageNumber == 1) {
            $('.prev').hide();
        } else {
            $('.prev').show();
        }
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
        slidesPerRow: 3,
        rows: 2,
        arrows: true,
        adaptiveHeight: true,
        prevArrow: $(".prev"),
        nextArrow: $(".next"),

        customPaging: function(slider, i) {
            var thumb = $(slider.$slides[i]).data();
            indexCount = i;
            return '<a>' + (i + 1) + '' + '</a>';
        },
        responsive: [{
            breakpoint: 767,
            settings: {
                dots: true,
                arrows: false,
                infinite: true,
                slidesToShow: 1,
                slidesToScroll: 1,
                slidesPerRow: 1,
                rows: 3
            }
        }]
    });




    $slickElement.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide) {
        var i = (currentSlide ? currentSlide : 0) + 1;
        //var i = currentSlide;
        //$status.text("Showing: " + i + '/' + slick.slideCount);
        $status.text("Showing: " + (itemFrom + 1) + ' to ' + itemsShown + ' of ' + totalItemCount);
        var pageNumber = (currentSlide ? currentSlide : 0) + 1;
        var item_length = slick.$slides.length
        if (pageNumber == item_length) {
            $('.next').hide();
        } else {
            $('.next').show();
        }
    });


        // Search Page Pagination
    var searchCount;
    var $statusSearch = $('.search-pagingInfo');
    var $slickElementsearch = $('.search-result-slider');
    var totalItemCountSearch = $(".search-result-item a").length;
    var pageViewCountSearch = $(window).width() > 767 ? 6 : 3; //number of items in a page 
    var itemsShownSearch = 0;
    var itemFromSearch = 0;
    if (totalItemCountSearch == 0) {
        $('.search-pagination-right').hide();
        $('.search-pagingInfo').hide();
    } else {
        $('.search-pagination-right').show();
        $('.search-pagingInfo').show();
    }
    $slickElementsearch.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide, slider) {
        var pageNumberSearch = (currentSlide ? currentSlide : 0) + 1;
        if (pageNumberSearch == 1) {
            $('.search-prev').hide();
        } else {
            $('.search-prev').show();
        }
        itemsShownSearch = pageNumberSearch * pageViewCountSearch;
        itemFromSearch = itemsShownSearch - pageViewCountSearch;
        if (itemsShownSearch > totalItemCountSearch) {
            itemsShownSearch = totalItemCountSearch;
            itemFromSearch = totalItemCountSearch - (totalItemCountSearch % pageViewCountSearch);
        }
        $statusSearch.text("Showing: " + (itemFromSearch + 1) + ' to ' + itemsShownSearch + ' of ' + totalItemCountSearch);
    });
    $slickElementsearch.slick({
        autoplay: false,
        speed: 500,
        fade: true,
        cssEase: 'linear',
        dots: true,
        infinite: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        slidesPerRow: 1,
        rows: 6,
        arrows: true,
        adaptiveHeight: true,
        prevArrow: $(".search-prev"),
        nextArrow: $(".search-next"),
        customPaging: function(slider, i) {
            var thumb = $(slider.$slides[i]).data();
            searchCount = i;
            return '<a>' + (i + 1) + '' + '</a>';
        }
    });
    $slickElementsearch.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide) {
        var i = (currentSlide ? currentSlide : 0) + 1;
        //var i = currentSlide;
        //$status.text("Showing: " + i + '/' + slick.slideCount);
        $statusSearch.text("Showing: " + (itemFromSearch + 1) + ' to ' + itemsShownSearch + ' of ' + totalItemCountSearch);
        var pageNumberSearch = (currentSlide ? currentSlide : 0) + 1;
        var item_length_search = slick.$slides.length
        if (pageNumberSearch == item_length_search) {
            $('.search-next').hide();
        } else {
            $('.search-next').show();
        }
    });

    var listItem = $('.slick-dots').width();
    $('.slider-arrows .prev, .slider-arrows .image-gallery-prev').css({
        left : -listItem - '80' + 'px'
    });


    GEU.enquiryForm.init();
    //loading youtube video player
    GEU.videoplayer.init();

    $('.tabs-nav a:first').trigger('click');

    $.getJSON($('#jsonPath').val(), function(json) {
        var consolidateAddress = json;
        var locationBtn = "";
        var locationDropdown = "";
        for (var i = 0; i < consolidateAddress.length; i++) {
            locationBtn += '<li data-id="' + i + '">' + consolidateAddress[i].addressheading + '</li>'
            locationDropdown += '<option value="' + i + '">' + consolidateAddress[i].addressheading + '</option>'
        }
        $(".address-details ul").html(locationBtn);
        $(".mobile-address-list").html(locationDropdown);
        $('.address-details ul li:first').addClass("active");
        setTimeout(function() {
            $('.address-details ul li:nth-child(1)').click();
        }, 0);
        $('.address-details ul li').on('click', function() {
            $(this).addClass("active").siblings().removeClass('active');
            var addressIndex = $(this).data("id");
            //showMap(addressIndex);
            showAdress(addressIndex);
        });
        $(document).on('change', '.mobile-address-list', function() {
            var addressIndex = $(this).val();
            //showMap(addressIndex);
            showAdress(addressIndex);
            $(this).find(':selected').addClass('selected').siblings('option').removeClass('selected');
        });

        function showMap(addressIndex) {
            var lat = parseFloat(consolidateAddress[addressIndex].contactLattitude);
            var lon = parseFloat(consolidateAddress[addressIndex].contacctLongitude);
            createMarker(map, { lat: lat, lng: lon });
        }

        function showAdress(addressIndex) {
            var contactDetails = '<div class="address-wrapper"><div class="heading">' + consolidateAddress[addressIndex].addressheading + '</div>' + '<div >' + consolidateAddress[addressIndex].contactName + '</div>' + '<div class="contact-address">' + consolidateAddress[addressIndex].contactAddress + '</div>' + '<a class="contact-email" href="mailTo:' + consolidateAddress[addressIndex].contactEmail + '">' + consolidateAddress[addressIndex].contactEmail + '</a>' + '<br>' + '<a class="contact-enquiry" href="mailTo:' + consolidateAddress[addressIndex].contactEnquiry + '">' + consolidateAddress[addressIndex].contactEnquiry + '</a>' + '<br>' + '<div>' + consolidateAddress[addressIndex].contactCorporateText + '</div>' + '<a class="contact-corporate" href="mailTo:' + consolidateAddress[addressIndex].contactCorporate + '">' + consolidateAddress[addressIndex].contactCorporate + '</a>' + '<div> Telephone: ' + consolidateAddress[addressIndex].contactPhone + '</div>' + '<div class="fax"> Fax:' + consolidateAddress[addressIndex].contactFax + '</div></div>';
            $(".address").html(contactDetails);
            $('.address-wrapper').find('*').each(function() {
                if ($(this).text() == 'undefined') {
                    $(this).hide();
                    $(".fax").hide();
                }
            });
            var lat = parseFloat(consolidateAddress[addressIndex].contactLattitude);
            var lon = parseFloat(consolidateAddress[addressIndex].contacctLongitude);
            map = new google.maps.Map(document.getElementById('map'), {
                zoom: 8
            });
            createMarker(map, { lat: lat, lng: lon });
        }
    });

});

var map;
//Map Generation 
function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8,
        center: { lat: 30.2734235, lng: 77.9976334 }
    });

}

function createMarker(resultsMap, latLong) {

    var marker = new google.maps.Marker({
        map: resultsMap,
        position: latLong
    });

    map.setCenter(latLong);
}

var GEU = GEU || {};

GEU.videoplayer = {
    videoPagerCount: 6, //pagination size
    videoItemsCount: 0,
    videosShown: 0,

    init: function(id, container) {
        $(".video-item-wrapper .video-image").click(function() {
            var container = ""; //video container
            var id = $(this).data("videoid");
            $(".video-item-wrapper").removeClass("selected");
            $(this).addClass("selected");

            container = $(".video-player-gallery")
            //GEU.videoplayer.createPlayer(id, container);
            GEU.videoplayer.createPlayerGallery(id, container);
        })

        $(".video-player .video-placeholder").click(function() {
            var id = $(this).data("videoid");
            // GEU.videoplayer.createPlayer(id, $(".video-player"));
            GEU.videoplayer.createPlayer(id, $(".video-player" + '' + id));
        })

        GEU.videoplayer.videoItemsCount = $(".video-list-component .video-item-wrapper").length

        GEU.videoplayer.loadMore();

        $(".load-more-videos").click(function() {
            GEU.videoplayer.loadMore()
        });
    },

    createPlayer: function(id, container) {
        $('#' + id).hide();
        container.find("iframe").remove();

        var src = 'https://www.youtube.com/embed/' + id + '?rel=0&amp;showinfo=0&amp;autoplay=1';
        var iframe = '<iframe width="100%" src="' + src + '" height="610" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>'

        $('.video-player' + '.' + id).append(iframe);
        $('html, body').animate({
            //scrollTop: $('.video-player' + '.' + id).offset().top - 20
        });
    },
    createPlayerGallery: function(id, container) {
        $('.video-placeholder-gallery').hide();
        container.find("iframe").remove();
        var src = 'https://www.youtube.com/embed/' + id + '?rel=0&amp;showinfo=0&amp;autoplay=1';
        var iframe = '<iframe width="100%" src="' + src + '" height="610" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>'
        container.append(iframe);
        $('html, body').animate({
            //scrollTop: $('.video-player' + '.' + id).offset().top - 20
            scrollTop: $('.content-page-wrapper').offset().top - 50
        });

    },

    loadMore: function() {

        GEU.videoplayer.videosShown += GEU.videoplayer.videoPagerCount;

        for (var i = 0; i < GEU.videoplayer.videosShown; i++) {
            $(".video-list-component .video-item-wrapper").eq(i).show();
        }

        if (GEU.videoplayer.videosShown >= GEU.videoplayer.videoItemsCount) {
            $(".load-more-videos").hide();
        }
    }
}
GEU.enquiryForm = {

    init: function() {
        GEU.enquiryForm.validations();
    },

    validations: function() {

        jQuery.validator.addMethod('selectcheck', function(value) {
            return (value != '-1');
        }, "This field is required.");

        jQuery.validator.addMethod('customEmailCheck', function(value) {
            var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(String(value).toLowerCase());
        }, "Please enter a valid email address.");

        $("#enquiryForm").validate({
            rules: {
                firstname: {
                    required: true
                },
                lastname: {
                    required: true
                },
                email: {
                    required: true,
                    email: false,
                    customEmailCheck: true
                },
                mobile: {
                    required: true,
                    number: true,
                    maxlength: 15,
                },
                areaInterest: {
                    selectcheck: true
                },
                query: {
                    required: true,
                    maxlength: 1000
                },
            },
            messages: {
                mobile: {
                    number: "Please enter a valid mobile number",
                    maxlength: "Please enter a valid mobile number"
                },
                query: {
                    maxlength: "Query should not be more than 1000 characters"
                }
            },

            SubmitHandler: function(form) {
                form.submit();
            }
        });

        $("#enquiryForm #country").change(function() {
            if ($(this).val() == "India") {
                $("#enquiryForm .state-row, #enquiryForm .city-row").show();
            } else {
                $("#enquiryForm .state-row, #enquiryForm .city-row").hide();
            }
        });
        $('.close').click(function() {
            $('#enquiryForm').trigger("reset");
            $('#enquiryForm').show();
            $('.enquiry-successmsg-wrapper').hide();
            $('#enquiryForm').find('label.error').hide();
            $("#enquiryForm .state-row, #enquiryForm .city-row").show();
        });
        $("#enquiryModel").on("hidden.bs.modal", function() {
            $('#enquiryForm').trigger("reset");
            $('#enquiryForm').show();
            $('.enquiry-successmsg-wrapper').hide();
            $('#enquiryForm').find('label.error').hide();
            $("#enquiryForm .state-row, #enquiryForm .city-row").show();
        });
    }
}