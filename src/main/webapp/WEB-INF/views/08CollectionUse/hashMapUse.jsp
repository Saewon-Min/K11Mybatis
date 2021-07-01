<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="../resources/css/bootstrap.css"/>
<script src="../resources/jquery/jquery-3.6.0.js"></script>
	
<title>Insert title here</title>
</head>
<body>
<div class="container">
	<h2>HashMap 사용하기</h2>
	<c:forEach items="${lists }" var="row">
		작성자:${row.name }(${row.id })
		<p>${row.contents }</p>
		<br />
	</c:forEach>


</div>
</body>
</html>