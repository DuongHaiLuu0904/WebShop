$(document).ready(function () {
    $(".boxscroll").niceScroll({cursorborder: "", cursorcolor: "#eff3f6", boxzoom: true});

    $('#datatable').DataTable();

    var table = $('#datatable-buttons').DataTable({
        pageLength: 10,
        pagingType: 'full_numbers',
        lengthChange: false,
        buttons: ['excel', 'pdf', 'print']
    });

    table.buttons().container().appendTo('#datatable-buttons_wrapper .col-md-6:eq(0)');
});

// Helper function for image URL - now just returns the URL directly
app.filter('imageUrl', function() {
    return function(imagePath) {
        return imagePath || 'https://res.cloudinary.com/djhidgxfo/image/upload/v1/images/cloud-upload.jpg';
    };
});