$(document).ready(function() {
   var one = $(".owl-carousel");
   var tech_carousel = $(".next-generation-carousel");
   var banner_carousel = $(".geu-banner-carousel");
   one.owlCarousel({
        loop: true,
        margin: 5,
        autoplay: true,
        autoplayTimeout: 1000,
        autoplayHoverPause: true,
        responsiveClass:true,
        responsive:{
            0:{
                items:2,
            },
            768:{
                items:3,
            },
            992:{
                items:4,
                dots: true,
            }
        }
    });  

    tech_carousel.owlCarousel({
      items: 1,
      animateIn: 'fadeIn',
      animateOut: 'fadeOut',
      autoplay: true,
      autoplayTimeout: 2000,
      dots: false,
      loop: true
    });

    banner_carousel.owlCarousel({
      items: 1,
      autoplay: true, 
      loop: true,
      autoplayTimeout: 2000,
    });
    $('.next-generation-carousel').addClass('owl-carousel').owlCarousel({
            margin: 10,
            nav: true,
            items: 1,
    });
    $('.geu-banner-carousel').addClass('owl-carousel').owlCarousel({
            margin: 10,
            nav: true,
            items: 1,
    });

    var myNavBar = {
        flagAdd: true,
        elements: [],

        init: function(elements) {
            this.elements = elements;
        },

        add: function() {
            if (this.flagAdd) {
                for (var i = 0; i < this.elements.length; i++) {
                    document.getElementById(this.elements[i]).className += " fixed-theme";
                }
                this.flagAdd = false;
            }
        },

        remove: function() {
            for (var i = 0; i < this.elements.length; i++) {
                document.getElementById(this.elements[i]).className =
                    document.getElementById(this.elements[i]).className.replace(/(?:^|\s)fixed-theme(?!\S)/g, '');
            }
            this.flagAdd = true;
        }

    };

    myNavBar.init([
        "header",
        "header-container",
        "brand"
    ]);

    /**
     * Function that manage the direction
     * of the scroll
     */
    function offSetManager() {

        var yOffset = 0;
        var currYOffSet = window.pageYOffset;

        if (yOffset < currYOffSet) {
            myNavBar.add();
            $(".brandLogo").addClass("shrink");
        } else if (currYOffSet == yOffset) {
            myNavBar.remove();
            $(".brandLogo").removeClass("shrink");
        }

    }

    /**
     * bind to the document scroll detection
     */
    window.onscroll = function(e) {
        offSetManager();

        clearTimeout($.data(this, 'scrollTimer'));

        // window scrolling
        $('.enquire-now-btn').removeClass('active');
        var scrollTop = $(document).scrollTop();
        if (scrollTop === 0) {
            $('.enquire-now-form-content').show();
            $('.enquire-now-btn').addClass('active');
        }
    }
    $('.enquire-now-btn').addClass('active');
    var selectIds = $('#panel1,#panel2,#panel3');
    $(function($) {
        selectIds.on('show.bs.collapse hidden.bs.collapse', function() {
            $(this).prev().find('.glyphicon').toggleClass('glyphicon-plus glyphicon-minus');
        })
    });

    
    /*Increament count on page load*/
    $(".box").scroll(function() {
        if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
            $("span").show();
        } else {
            $("span").hide();
        }
    });

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
});