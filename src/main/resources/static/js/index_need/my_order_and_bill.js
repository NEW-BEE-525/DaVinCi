$(document).ready(function () {

        $("#order_date").text(new Date().getFullYear());
        $(".ord_id").text(new Date().getTime())

        $("#above_info2").hover(function () {
            $("#above_info2").css("color", "#FFAC00")
        }, function () {
            $("#above_info2").css("color", "#050505");
        });

        $("#above_info").hover(function () {
            $("#above_info").css("color", "#FFAC00")
        }, function () {
            $("#above_info").css("color", "#050505");
        });
    }
);

function slide_1() {

    $("#bar_1").css("background-color", "#FFAC00");
    $("#bar_2").css("background-color", "#e4e5e8");
    $("#bar_3").css("background-color", "#e4e5e8");
    $("#bar_4").css("background-color", "#e4e5e8");
    $("#bar_5").css("background-color", "#e4e5e8");
}

function slide_2() {

    $("#bar_2").css("background-color", "#FFAC00");
    $("#bar_1").css("background-color", "#e4e5e8");
    $("#bar_3").css("background-color", "#e4e5e8");
    $("#bar_4").css("background-color", "#e4e5e8");
    $("#bar_5").css("background-color", "#e4e5e8");
}

function slide_3() {

    $("#bar_3").css("background-color", "#FFAC00");
    $("#bar_2").css("background-color", "#e4e5e8");
    $("#bar_1").css("background-color", "#e4e5e8");
    $("#bar_4").css("background-color", "#e4e5e8");
    $("#bar_5").css("background-color", "#e4e5e8");
}

function slide_4() {

    $("#bar_4").css("background-color", "#FFAC00");
    $("#bar_2").css("background-color", "#e4e5e8");
    $("#bar_3").css("background-color", "#e4e5e8");
    $("#bar_1").css("background-color", "#e4e5e8");
    $("#bar_5").css("background-color", "#e4e5e8");
}

function slide_5() {

    $("#bar_5").css("background-color", "#FFAC00");
    $("#bar_2").css("background-color", "#e4e5e8");
    $("#bar_3").css("background-color", "#e4e5e8");
    $("#bar_4").css("background-color", "#e4e5e8");
    $("#bar_1").css("background-color", "#e4e5e8");
}

$(".product_pic").click(
    function () {
        windows.location.href = "/#" + this.id


    }
)







