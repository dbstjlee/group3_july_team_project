package com.tenco.group3.repository.interfaces;

import com.tenco.group3.model.College;

import java.util.List;

public interface CollegeRepository {
	
	List<College> getAllCollege(); // 단과대학 전체 조회
	int addCollege(College college); // 단과대학 추가(등록)
	int deleteById(int id); // 단과대학 삭제
}