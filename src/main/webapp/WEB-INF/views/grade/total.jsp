<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<ul>
		<li><a href="${pageContext.request.contextPath}/grade/thisSemester">이번 학기 성적 조회</a></li>
		<li><a href="${pageContext.request.contextPath}/grade/semester">학기별 성적 조회</a></li>
		<li><a href="${pageContext.request.contextPath}/grade/total">전체 성적 조회</a></li>
	</ul>
	<h1>전체 누계 성적</h1>
	<hr>
	<table border="1">
		<thead>
			<tr>
				<th>연도</th>
				<th>학기</th>
				<th>신청학점</th>
				<th>취득학점</th>
				<th>평점평균</th>
			</tr>
		</thead>
		<tbody>

			<tr>
				<td>2023년</td>
				<td>1학기</td>
				<td>3</td>
				<td>3</td>
				<td>4.50</td>
			</tr>
		</tbody>
	</table>
</body>
</html>