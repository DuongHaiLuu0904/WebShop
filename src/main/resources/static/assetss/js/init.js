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