package com.tenco.group3.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Student {

	private int id;
	private String name;
	private Date birthDate;
	private String gender;
	private String address;
	private String tel;
	private String email;
	private int deptId;
	private int grade;
	private int semester;
	private Date entranceDate;
	private Date graduationDate;
	private String deptname;
	private String collname;
	private String status;
	private Date fromDate;
	private String description;

}
