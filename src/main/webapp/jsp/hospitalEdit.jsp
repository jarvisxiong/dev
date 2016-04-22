<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>Information</title>
  <script language="javascript" type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.js"></script>
   <script language="javascript" type="text/javascript" src="<%=request.getContextPath() %>/js/hospital.js"></script>
  <script language="javascript" type="text/javascript" >
  $.ajax({
      type: "get",
      dataType: "json",
      url: "../hospital/query",
      data:'data={id:${param.id}}',
      success: function (msg) {
       $("#id").val(msg.id);
       $("#hospitalName").val(msg.hospitalName);
       $("#address").val(msg.address);
       $("#level").val(msg.level);
       $("#telephone").val(msg.telephone);
       $("#email").val(msg.email);
       $("#hospitalIM").val(msg.hospitalIM);
       $("#weiChatNumber").val(msg.weiChatNumber);
       $("#longitude").val(msg.longitude);
       $("#latitude").val(msg.latitude);
      }
  });

  </script>
	 <link rel="stylesheet"  type="text/css"  href="<%=request.getContextPath() %>/css/hospital.css"/>
</head>
<body>
<div id="add">
<form action="../hospital/add" id="form1" method="get">  
<p>医院Id：<input type="text" name="id" id="id" /></p>  
<p>医院名：<input type="text" name="hospitalName" id="hospitalName"/></p>  
<p>地址：<input type="text" name="address" id="address"/></p>    
<p>等级：<input type="text" name="level" id="level"/></p>    
<p>电话：<input type="text" name="telephone" id="telephone"/></p>  
<p>邮件：<input type="text" name="email" id="email"/></p>    
<p>及时通信：<input type="text" name="hospitalIM" id="hospitalIM"/></p>    
<p>公众号：<input type="text" name="weiChatNumber"  id="weiChatNumber"/></p>  
<p>经度：<input type="text" name="longitude" id="longitude"/></p>    
<p>纬度：<input type="text" name="latitude"  id="latitude"/></p>  
<p><input type="button" id="send" value="提交" onclick="update();"/></p>  
</form>
</div>

  
</body>
</html>