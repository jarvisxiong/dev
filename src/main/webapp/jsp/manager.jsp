<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>衡云后台管理页面</title>
  <script language="javascript" type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.js"></script>
   <script language="javascript" type="text/javascript" src="<%=request.getContextPath() %>/js/SimpleTree.js"></script>
	
<script type="text/javascript">
$(function(){
    $(".st_tree").SimpleTree({
        click:function(a){
            if(!$(a).attr("hasChild"))
                alert($(a).attr("ref"));
        }
    });
});
</script>
</head>
<body>
<body>
<div  id="head" style="width:1400px;height:100px;background-color:green;margin:0 auto;">天衡欢迎你!</div>
<div style="width:1400px;background-color:#333;margin:0 auto;">

<div class="st_tree" style="width:20%;height:700px;background-color:yellow;float:left;">
<ul>
    <li><a href="#" ref="hyjm">回到主页</a></li>
    <li><a href="#" ref="xtgl">系统管理</a></li>
    <ul show="true">
        <li><a href="#" ref="yhgl">用户管理</a></li>
          <li><a href="#" ref="rzck">日志查看</a></li>
        <li><a href="#" ref="rzck">登陆管理</a></li>
    </ul>
     <li><a href="#" ref="ckgl">在线会诊</a></li>
      <ul>
            <li><a href="#" ref="yhgl">医生随访</a></li>
            <li><a href="#" ref="rzck">病人回访</a></li>
        </ul>
    <li><a href="#" ref="ckgl">医院管理</a></li>
    <ul>
        <li><a href="#" ref="kcgl">科室管理</a></li>
      	<li><a href="#" ref="fhgl">医院管理</a></li>
        <li><a href="#" ref="fhgl">医生管理</a></li>
        <ul>
            <li><a href="#" ref="yhgl">病人管理</a></li>
            <li><a href="#" ref="rzck">病历查看</a></li>
        </ul>
    </ul>
      <li><a href="#" ref="ckgl">论坛管理</a></li>
       <ul>
            <li><a href="#" ref="yhgl">帖子管理</a></li>
            <li><a href="#" ref="rzck">回复管理</a></li>
             <li><a href="#" ref="rzck">模板管理</a></li>
        </ul>
       <li><a href="#" ref="ckgl">在线商城</a></li>
       <ul>
            <li><a href="#" ref="yhgl">我的订单</a></li>
            <li><a href="#" ref="rzck">商品展览</a></li>
          
        </ul>
</ul>
</div>
<div class="cotent" style="width:80%;height:700px;background-color:red;float:right;">内容</div>
<div style="clear:both;"></div>
</div>
<div  id="footer" style="width:1400px;height:100px;background-color:green;margin:0 auto;">联系我们</div>
</body>
</html>