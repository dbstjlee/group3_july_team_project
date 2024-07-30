package com.tenco.group3.controller;

import java.io.IOException;
import java.util.List;

import com.tenco.group3.model.Sugang;
import com.tenco.group3.model.User;
import com.tenco.group3.repository.SugangRepositoryImpl;
import com.tenco.group3.repository.interfaces.SugangRepository;
import com.tenco.group3.util.AlertUtil;
import com.tenco.group3.util.Define;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder.Default;

@WebServlet("/sugang/*")
public class SugangController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SugangRepository sugangRepository;

	@Override
	public void init() throws ServletException {
		sugangRepository = new SugangRepositoryImpl();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 0 - 수강 신청 이전 기간 , 1 - 수강 신청 기간, 2 - 수강 신청 마감 이후 기간, 3 - 예비 수강 신청 기간
		int sugangDay = (int) getServletContext().getAttribute("sugang");
		HttpSession session = request.getSession();
		String action = request.getPathInfo();
		// TODO - 수강 신청 기간이 아닐 때 접근 막고, 수강 신청 기간으로 변경시 신청 값이 강의 인원수 제한을
		// 넘길 경우 해당 강의 초기화
		switch (action) {
		case "/subjectList":
			showSubjectList(request, response, session);
			break;
		case "/subjectList/search":
			showSearchSubject(request, response, session);
			break;
		case "/pre":
			// TODO - 예비 수강 신청 기간 처리
			if (sugangDay == 3) {
				showPreliminaryList(request, response, session);
			} else {
				AlertUtil.backAlert(response, "예비 수강 신청 기간이 아닙니다.");
			}
			break;
		case "/pre/search":
			// TODO - 예비 수강 신청 기간 처리
			if (sugangDay == 3) {
				showSearchPreliminary(request, response, session);
			} else {
				AlertUtil.backAlert(response, "예비 수강 신청 기간이 아닙니다.");
			}
			break;
		case "/preAppList":
			// 수강 신청 기간 내부 처리
			showPreliminaryAppList(request, response, session);
			break;
		case "/application":
			// TODO - 수강신청 기간 설정
			if (sugangDay == 1) {
				showApplicationList(request, response, session);
			} else {
				AlertUtil.backAlert(response, "수강 신청 기간이 아닙니다.");
			}
			break;
		case "/application/search":
			// TODO - 수강신청 기간 설정
			if (sugangDay == 1) {
				showSearchApplication(request, response, session);
			} else {
				AlertUtil.backAlert(response, "수강 신청 기간이 아닙니다.");
			}
			break;
		case "/list":
			// TODO - 수강신청 기간 설정
			if (sugangDay != 1 && sugangDay != 3) {
				showListAppSubject(request, response, session);
			} else {
				AlertUtil.backAlert(response, "수강 신청 기간이 아닙니다.");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 수강 신청 내역 조회
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showListAppSubject(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		User user = (User) session.getAttribute("principal");
		List<Sugang> sugangList = sugangRepository.getApplicatedSubjectList(user.getId());
		int totalGrade = sugangRepository.getSubjectGrade(user.getId());
		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalGrade", totalGrade);
		request.getRequestDispatcher("/WEB-INF/views/sugang/list.jsp").forward(request, response);
	}

	/**
	 * 수강 신청 페이지 검색
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException
	 */
	private void showSearchApplication(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		int deptId;
		try {
			deptId = Integer.parseInt(request.getParameter("deptId"));
		} catch (NumberFormatException e) {
			deptId = -1;
			e.printStackTrace();
		}
		String name = request.getParameter("name");
		if (name == null || name.trim().isEmpty()) {
			name = null;
		}
		if (type.equals("전체") && deptId == -1 && name == null || name.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/sugang/application");
			return;
		}
		int page = 1; // 기본 페이지 번호
		int pageSize = 20; // 한 페이지당 보여질 게시글의 수
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 입력 받은 경우
			page = 1;
			e.printStackTrace();
		}
		int offset = (page - 1) * pageSize;
		Sugang sugang = Sugang.builder().subjectType(type).deptId(deptId).subjectName(name).build();
		int totalCount = sugangRepository.getSearchSubjectCount(sugang);
		int totalPages = (int) Math.ceil((double) totalCount / pageSize);
		List<Sugang> sugangList = sugangRepository.getAppSubjectBySearch(sugang, pageSize, offset);
		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		request.getRequestDispatcher("/WEB-INF/views/sugang/applicationSearch.jsp").forward(request, response);
	}

	/**
	 * 수강 신청 페이지 이동
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException
	 */
	private void showApplicationList(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		User user = (User) session.getAttribute("principal");
		int page = 1; // 기본 페이지 번호
		int pageSize = 20; // 한 페이지당 보여질 게시글의 수
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 입력 받은 경우
			page = 1;
			e.printStackTrace();
		}
		int offset = (page - 1) * pageSize;
		int totalCount = sugangRepository.getAllSubjectCount();
		int totalPages = (int) Math.ceil((double) totalCount / pageSize);
		List<Sugang> subSugangs = sugangRepository.resultStudentCount();
		for (Sugang sugang : subSugangs) {
			sugangRepository.resetStudentCount(sugang.getSubjectId());
		}
		List<Sugang> sugangList = sugangRepository.getApplicationSubject(user.getId(), pageSize, offset);
//		System.out.println(sugangList);
		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		request.getRequestDispatcher("/WEB-INF/views/sugang/application.jsp").forward(request, response);
	}

	/**
	 * 수강 신청 내역 (예비 포함)
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException
	 */
	private void showPreliminaryAppList(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		User user = (User) session.getAttribute("principal");
		int listType = 0;
		int sugangDay = (int) getServletContext().getAttribute("sugang");
		// TODO - 수강신청 기간
		if (sugangDay == 1) {
			listType = 1;
		} else if (sugangDay == 3){
			listType = 0;
		}
		if (listType == 0) {
			List<Sugang> sugangPreList = sugangRepository.getPreApplicatedSubjectList(user.getId());
			int totalGrade = sugangRepository.getSubjectGrade(user.getId());
			request.setAttribute("sugangPreList", sugangPreList);
			request.setAttribute("totalGrade", totalGrade);
			request.setAttribute("listType", listType);
			request.getRequestDispatcher("/WEB-INF/views/sugang/preAppList.jsp").forward(request, response);
		} else if (listType == 1) {
			List<Sugang> sugangList = sugangRepository.getApplicatedSubjectList(user.getId());
			List<Sugang> resetList = sugangRepository.getResetPreSubject(user.getId());
			int totalGrade = sugangRepository.getSubjectGrade(user.getId());
			request.setAttribute("sugangList", sugangList);
			request.setAttribute("resetList", resetList);
			request.setAttribute("totalGrade", totalGrade);
			request.setAttribute("listType", listType);
			request.getRequestDispatcher("/WEB-INF/views/sugang/preAppList.jsp").forward(request, response);
		}
	}

	/**
	 * 예비 수강 신청 검색
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException
	 */
	private void showSearchPreliminary(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		int deptId;
		try {
			deptId = Integer.parseInt(request.getParameter("deptId"));
		} catch (NumberFormatException e) {
			deptId = -1;
			e.printStackTrace();
		}
		String name = request.getParameter("name");
		if (name == null || name.trim().isEmpty()) {
			name = null;
		}

		if (type.equals("전체") && deptId == -1 && name == null || name.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/sugang/pre");
			return;
		}

		int page = 1; // 기본 페이지 번호
		int pageSize = 20; // 한 페이지당 보여질 게시글의 수
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 입력 받은 경우
			page = 1;
			e.printStackTrace();
		}
		int offset = (page - 1) * pageSize;
		Sugang sugang = Sugang.builder().subjectType(type).deptId(deptId).subjectName(name).build();
		int totalCount = sugangRepository.getSearchSubjectCount(sugang);
		int totalPages = (int) Math.ceil((double) totalCount / pageSize);
		List<Sugang> sugangList = sugangRepository.getPreSubjectBySearch(sugang, pageSize, offset);
		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		request.getRequestDispatcher("/WEB-INF/views/sugang/preSearch.jsp").forward(request, response);
	}

	/**
	 * 예비 수강 신청 페이지 이동
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws ServletException
	 */
	private void showPreliminaryList(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		User user = (User) session.getAttribute("principal");
		int page = 1; // 기본 페이지 번호
		int pageSize = 20; // 한 페이지당 보여질 게시글의 수
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 입력 받은 경우
			page = 1;
			e.printStackTrace();
		}
		int offset = (page - 1) * pageSize;
		int totalCount = sugangRepository.getAllSubjectCount();

		int totalPages = (int) Math.ceil((double) totalCount / pageSize);
		List<Sugang> sugangList = sugangRepository.getPreApplicationSubject(user.getId(), pageSize, offset);
//		System.out.println(sugangList);
		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		request.getRequestDispatcher("/WEB-INF/views/sugang/pre.jsp").forward(request, response);
	}

	/**
	 * 강의 검색
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showSearchSubject(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		int deptId;
		try {
			deptId = Integer.parseInt(request.getParameter("deptId"));
		} catch (NumberFormatException e) {
			deptId = -1;
		}
		String name = request.getParameter("name");
		if (name == null || name.trim().isEmpty()) {
			name = null;
		}

		if (type.equals("전체") && deptId == -1 && name == null || name.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/sugang/subjectList");
			return;
		}
		int page = 1; // 기본 페이지 번호
		int pageSize = 20; // 한 페이지당 보여질 게시글의 수
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 입력 받은 경우
			page = 1;
			e.printStackTrace();
		}
		Sugang sugang = Sugang.builder().subjectType(type).deptId(deptId).subjectName(name).build();
		int offset = (page - 1) * pageSize;
		int totalCount = sugangRepository.getSearchSubjectCount(sugang);
		int totalPages = (int) Math.ceil((double) totalCount / pageSize);

		List<Sugang> sugangList = sugangRepository.getSubjectBySearch(sugang, pageSize, offset);

		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		request.setAttribute("sugangSearch", sugang);
		request.getRequestDispatcher("/WEB-INF/views/sugang/subjectSearch.jsp").forward(request, response);
	}

	/**
	 * 강의 리스트 출력
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showSubjectList(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		int page = 1; // 기본 페이지 번호
		int pageSize = 20; // 한 페이지당 보여질 게시글의 수
		try {
			String pageStr = request.getParameter("page");
			if (pageStr != null) {
				page = Integer.parseInt(pageStr);
			}
		} catch (Exception e) {
			// 유효하지 않은 번호를 입력 받은 경우
			page = 1;
			e.printStackTrace();
		}
		int offset = (page - 1) * pageSize;
		int totalCount = sugangRepository.getAllSubjectCount();

		int totalPages = (int) Math.ceil((double) totalCount / pageSize);

		List<Sugang> sugangList = sugangRepository.getAllSubject(pageSize, offset);
		request.setAttribute("sugangList", sugangList);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
//		System.out.println(sugangList);
		request.getRequestDispatcher("/WEB-INF/views/sugang/subjectList.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String action = request.getPathInfo();
		switch (action) {
		case "/pre":
			handlerPreliminaryList(request, response, session);
			break;
		case "/application":
			handlerApplicationList(request, response, session);
			break;
		default:
			break;
		}
	}

	/**
	 * 수강 신청
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	private void handlerApplicationList(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		User user = (User) session.getAttribute("principal");
		int subjectId = Integer.parseInt(request.getParameter("subjectId"));
		String type = request.getParameter("type");
		int grade = 0;
		if (request.getParameter("grades") != null) {
			grade = Integer.parseInt(request.getParameter("grades"));
		}
		boolean totalGrade = sugangRepository.getTotalGrade(user.getId());
		if (type.equals("1")) {
			int rowCount = sugangRepository.deleteConfirmSubject(subjectId);
			if (rowCount != 0) {
				response.sendRedirect(request.getContextPath() + "/sugang/application");
			} else {
				AlertUtil.backAlert(response, "신청 내역이 존재하지 않습니다.");
			}
		} else {
			if (totalGrade) {
				int rowCount = sugangRepository.addEnrolment(user.getId(), subjectId, grade);
				if (rowCount != 0) {
					response.sendRedirect(request.getContextPath() + "/sugang/application");
				} else {
					AlertUtil.backAlert(response, "정원 초과된 강의입니다.");
				}
			} else {
				AlertUtil.backAlert(response, "18학점 이상으로는 신청이 불가능 합니다.");
			}
		}

	}

	/**
	 * 예비 수강 신청
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	private void handlerPreliminaryList(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		User user = (User) session.getAttribute("principal");
		int subjectId = Integer.parseInt(request.getParameter("subjectId"));
		String type = request.getParameter("type");
		int totalGrade = sugangRepository.getSubjectGrade(user.getId());
		System.out.println(totalGrade);
		if (type.equals("1")) {
			int rowCount = sugangRepository.deletePreConfirmSubject(subjectId);
			if (rowCount != 0) {
				response.sendRedirect(request.getContextPath() + "/sugang/pre");
			} else {
				AlertUtil.backAlert(response, "신청 내역이 존재하지 않습니다.");
			}
		} else {
			if (totalGrade <= Define.MAX_GRADES) {
				sugangRepository.addPreEnrolment(user.getId(), subjectId);
				response.sendRedirect(request.getContextPath() + "/sugang/pre");
			} else {
				AlertUtil.backAlert(response, "18학점 이상으로는 신청이 불가능 합니다.");
			}
		}
	}

}