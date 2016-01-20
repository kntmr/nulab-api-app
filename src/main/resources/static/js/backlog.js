$(function() {
    $("#search").click(function() {
        $.ajax({
            type: "GET",
            url: "/backlog/projects",
            dataType: "json",
            cache: false,
            success: function(data) {
                for (var i=0; i<data.length; i++) {
                    alert(i);
                    alert(data[i].id + ": " + data[i].name);
                }
            }
        });
    });
});
