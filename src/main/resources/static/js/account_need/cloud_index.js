// JavaScript Document
$(function(){
  //MOOC学院-新版 注册页面选项卡
  var $ul = $(".register_nav").children();
  for (var i = 0, l = $ul.length; i < l; i++) {    
    $ul[i].onclick= (function(e){ 
      return function(){
       $(this).addClass("curr").siblings("a").removeClass("curr");
       $(".register_data").eq(e).show().siblings('div').hide();
      }
    })(i);
  }
});
