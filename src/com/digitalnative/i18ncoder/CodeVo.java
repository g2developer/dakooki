package com.digitalnative.i18ncoder;

import java.util.HashMap;
import java.util.Map;

public class CodeVo {

	private String code;
	private String labelKo;	// 한글 label
	private String labelEn;	// 영문 label
	private String labelJp;	// 일어 label
	private String labelCn;	// 중국어 label
	
	private Map<String, String> labelMap;
	
	// 데이터 베이스에 인서트되있는지?
	private String insertedYn;

	public boolean inserted() {
		return "Y".equals(insertedYn);
	}
	

	public void getLabel(String lan, String text) {
		getLabelMap().get(lan);
	}
	
	public void setLabel(String lan, String text) {
		getLabelMap().put(lan, text);
	}
	
	public Map<String, String> getLabelMap() {
		if(labelMap == null) {
			labelMap = new HashMap<>();
		}
		return labelMap;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLabelKo() {
		return labelKo;
	}
	public void setLabelKo(String labelKo) {
		this.labelKo = labelKo;
	}
	public String getLabelEn() {
		return labelEn;
	}
	public void setLabelEn(String labelEn) {
		this.labelEn = labelEn;
	}
	public String getLabelJp() {
		return labelJp;
	}
	public void setLabelJp(String labelJp) {
		this.labelJp = labelJp;
	}
	public String getLabelCn() {
		return labelCn;
	}
	public void setLabelCn(String labelCn) {
		this.labelCn = labelCn;
	}
	public String getInsertedYn() {
		return insertedYn;
	}
	public void setInsertedYn(String insertedYn) {
		this.insertedYn = insertedYn;
	}
	
	
	
}
