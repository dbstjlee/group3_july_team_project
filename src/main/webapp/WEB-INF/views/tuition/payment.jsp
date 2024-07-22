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
		<li><a href="${pageContext.request.contextPath}/break/application">휴학 신청</a></li>
		<li><a href="${pageContext.request.contextPath}/break/list">휴학 신청 내역</a></li>
		<li><a href="${pageContext.request.contextPath}/tuition/check">등록금 내역 확인</a></li>
		<li><a href="${pageContext.request.contextPath}/tuition/payment">등록금 고지서</a></li>
	</ul>
	<h1>등록금 납부 페이지</h1>
	<hr>
	<div>
		<h3>등록금 고지서</h3>
		<p>2023년도 1학기</p>
		<table border="1">
			<thead>
				<tr>
					<th>단 과 대</th>
					<td>${tuition.collgeName}</td>
					<th>학 과</th>
					<td>${tuition.deptName}</td>
				</tr>
				<tr>
					<th>학번</th>
					<td>${tuition.studentId}</td>
					<th>성명</th>
					<td>${tuition.studentName}</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<th colspan="2">장 학 유 형</th>
					<th colspan="2">${tuition.scholarType}</th>
				</tr>
				<tr>
					<th colspan="2">등 록 금</th>
					<th colspan="2"><fmt:formatNumber value="${tuition.collAmount}" pattern="#,###" /></th>
				</tr>
				<tr>
					<th colspan="2">장 학 금</th>
					<th colspan="2"><fmt:formatNumber value="${tuition.scholarAmount}" pattern="#,###" /></th>
				</tr>
				<tr>
					<th colspan="2">납 부 금</th>
					<th colspan="2"><fmt:formatNumber value="${tuition.totalAmount}" pattern="#,###" /></th>
				</tr>
				<!-- 고정된 값 ?? -->
				<tr>
					<th colspan="2">납 부 계 좌</th>
					<th colspan="2">그린은행 483-531345-536</th>
				</tr>
				<!-- 고정된 값 ?? -->
				<tr>
					<th colspan="2">납 부 기 간</th>
					<th colspan="2">~ 2023.05.02</th>
				</tr>
			</tbody>
		</table>
		<c:choose>
			<c:when test="${tuition.status == 0}">
				<form action="${pageContext.request.contextPath}/tuition/payment">
					<button type="submit">납부하기</button>
				</form>
			</c:when>
			<c:otherwise>
				<p>이미 납부 하셨습니다.</p>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>