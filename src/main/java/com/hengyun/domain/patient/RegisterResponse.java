package com.hengyun.domain.patient;
/**
* @author bob E-mail:panbaoan@thealth.cn
* @version 创建时间：2016年5月10日 上午11:01:50
* 类说明
*/
public class RegisterResponse {

	private String magic;
	private int len;
	private String mac;
	private int opcode;
	private int code;
	private String date;
	private int return_code;
	
	
	public String getMagic() {
		return magic;
	}
	public void setMagic(String magic) {
		this.magic = magic;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}

	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getOpcode() {
		return opcode;
	}
	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getReturn_code() {
		return return_code;
	}
	public void setReturn_code(int return_code) {
		this.return_code = return_code;
	}
	
	
	
}
