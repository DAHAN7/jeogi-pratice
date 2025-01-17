package com.bitc.jeogi.user.vo;

import java.util.List;

import lombok.Data;

@Data
public class UserVO {
	
	private int u_no;
	private String u_id;
	private String u_pw;
	private String u_email;
	private String u_name; 
	private String u_profile;
	private String u_phone;
	private String u_birth;
	private String u_addr;
	private String u_addr_detail;
	private String u_addr_post;
	private String u_withdraw;
	
	// 권한 목록
	private List<String> authList;
	
}