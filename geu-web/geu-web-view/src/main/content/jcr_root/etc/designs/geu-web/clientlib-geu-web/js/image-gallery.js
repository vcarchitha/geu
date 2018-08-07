$(document).ready(function() {

    $(".image-list-popup").fancybox();
    
    $(".fancybox").fancybox({
        openEffect  : 'none',
        closeEffect : 'none',
        nextEffect  : 'none',
        prevEffect  : 'none'
    })
    /*Image gallery script*/
    var indexCount;
    var $status = $('.pagingInfo');
    var $slickElement = $('.imggeupcoming-event-slider');
    var totalItemCount = $(".image-gallery-component-wrapper").length;
    var pageViewCount;
    var itemsShown = 0; 
    var itemFrom = 0;
    $slickElement.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide, slider) {
        var pageNumber = (currentSlide ? currentSlide : 0) + 1;
        if(pageNumber == 1){
            $('.image-gallery-prev').hide();
        }else {
            $('.image-gallery-prev').show();
        }

        pageViewCount = $('.image-gallery-wrapper .slick-slide.slick-current.slick-active').find('.image-gallery-component').length
        itemsShown = pageNumber*pageViewCount;
        itemFrom = itemsShown - pageViewCount;
        if(itemsShown > totalItemCount)
        {
            itemsShown = totalItemCount;
            itemFrom = totalItemCount - (totalItemCount % pageViewCount);
        }
        $status.text("Showing: " + (itemFrom+1) + ' to ' +itemsShown+ ' of ' + totalItemCount);
    });
    $slickElement.slick({
        autoplay: false,
        adaptiveHeight: true,
        speed: 500,
        fade: true,
        cssEase: 'linear',
        dots: true,
        infinite: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        slidesPerRow: 5,
        rows: 5,
        arrows: true,
        prevArrow: $(".image-gallery-prev"),
        nextArrow: $(".image-gallery-next"),

        customPaging: function(slider, i) {
            var thumb = $(slider.$slides[i]).data();
            indexCount = i;
            return '<a>' + (i + 1) + '' + '</a>';

        },
        responsive: [{
                breakpoint: 1199,
            settings: {
                dots: true,
                adaptiveHeight: true,
                arrows: false,
                infinite: true,
                slidesToShow: 1,
                slidesToScroll: 1,
                    slidesPerRow: 4,
                    rows: 5
            }
            },

            {
            breakpoint: 767,
            settings: {
                dots: true,
                adaptiveHeight: true,
                arrows: false,
                infinite: true,
                slidesToShow: 1,
                slidesToScroll: 1,
                    slidesPerRow: 3,
                    rows: 5
                }
            },
            {
                breakpoint: 480,
                settings: {
                    dots: true,
                    adaptiveHeight: true,
                    arrows: false,
                    infinite: false,
                    slidesToShow: 1,
                    slidesToScroll: 1,
                    slidesPerRow: 2,
                    rows: 5
                }
            }
        ]
    });


    $slickElement.on('init reInit afterChange', function(event, slick, currentSlide, nextSlide) {
        var pageNumber = (currentSlide ? currentSlide : 0) + 1;
        var item_length = slick.$slides.length
        if(pageNumber == item_length) {
            $('.image-gallery-next').hide();
        }
        else {
            $('.image-gallery-next').show();
        }
        if(currentSlide == "0") {
            pageViewCount = $('.image-gallery-wrapper .slick-slide.slick-current.slick-active').find('.image-gallery-component').length
            itemsShown = pageNumber*pageViewCount;
            itemFrom = itemsShown - pageViewCount;
            if(itemsShown > totalItemCount)
            {
                itemsShown = totalItemCount;
                itemFrom = totalItemCount - (totalItemCount % pageViewCount);
            }
            $status.text("Showing: " + (itemFrom+1) + ' to ' +itemsShown+ ' of ' + totalItemCount);
        }
        else if(pageNumber != item_length) {
            pageViewCount = $('.image-gallery-wrapper .slick-slide.slick-current.slick-active').find('.image-gallery-component').length
            itemsShown = pageNumber*pageViewCount;
            itemFrom = itemsShown - pageViewCount;
            if(itemsShown > totalItemCount)
            {
                itemsShown = totalItemCount;
                itemFrom = totalItemCount - (totalItemCount % pageViewCount);
            }
        $status.text("Showing: " + (itemFrom+1) + ' to ' +itemsShown+ ' of ' + totalItemCount);
        }
        else if(pageNumber == item_length) {
            var i = (currentSlide ? currentSlide : 0) + 1;
            itemFrom = $('.image-gallery-wrapper .slick-slide:not(.slick-current.slick-active)').find('.image-gallery-component').length;
            $status.text("Showing: " + (itemFrom+1) + ' to ' +totalItemCount+ ' of ' + totalItemCount);
        }
    });
});
