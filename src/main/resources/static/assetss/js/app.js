!function ($) {
    "use strict";
    //Body
    var MainApp = function () {
        this.$body = $("body"),
            this.$wrapper = $("#wrapper"),
            this.$leftMenuButton = $('.button-menu-mobile'),
            this.$menuItem = $('.has_sub > a')
    };

    //Slim Scroll
    MainApp.prototype.initSlimscroll = function () {
        $('.slimscrollleft').slimscroll({
            height: 'auto',
            position: 'right',
            size: "6px",
            color: '#babbde'
        });
    },

        //Left Menu
        MainApp.prototype.initLeftMenuCollapse = function () {
            var $this = this;
            this.$leftMenuButton.on('click', function (event) {
                event.preventDefault();
                $this.$body.toggleClass("fixed-left-void");
                $this.$wrapper.toggleClass("enlarged");
            });
        },

        //Loader
        MainApp.prototype.Preloader = function () {
            $(window).on('load', function () {
                $('#status').fadeOut();
                $('#preloader').delay(350).fadeOut('slow');
                $('body').delay(350).css({
                    'overflow': 'visible'
                });
            });
        },

        MainApp.prototype.init = function () {
            this.initSlimscroll();
            this.initLeftMenuCollapse();
            this.Preloader();
        },

        //init
        $.MainApp = new MainApp, $.MainApp.Constructor = MainApp
}(window.jQuery),
    //initializing
    function ($) {
        "use strict";
        $.MainApp.init();
    }(window.jQuery);