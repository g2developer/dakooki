package com.digitalnative.i18ncoder;

import java.util.ArrayList;
import java.util.List;

public class I18NCodeService {

	private static String codePrefix = "MSG";
	
	private static List<CodeVo> list;
	
	public I18NCodeService() {
		loadCodeList();
	}
	
	// ��� load
	public void loadCodeList() {
		list = new ArrayList<>();
	}
	
	// �ѱ۷� �ڵ� �������� ������ list�� ���
	public String getCode(String han) {
		String code = getCodeFromList(han);
		
		if(code == null) {
			code = createCodeVo(han);
		}
		return code;
	}

	// �ڵ�� �ѱ� ��������
	public String getLabelKo(String code) {
		for(CodeVo vo : list) {
			if(code.equals(vo.getCode())) {
				return vo.getLabelKo();
			}
		}
		return null;
	}
	
	
	public void setCodeList(List<CodeVo> list) {
		I18NCodeService.list = list;
	}
	
	
	// ��Ͽ��� code ã��
	private String getCodeFromList(String han) {
		for(CodeVo vo : list) {
			if(han.equals(vo.getLabelKo())) {
				return vo.getCode();
			}
		}
		return null;
	}
	
	// �ڵ�vo ����
	private String createCodeVo(String han) {
		CodeVo vo = new CodeVo();
		vo.setLabelKo(han);
		String newCode = genMsgCode();
		vo.setCode(newCode);
		list.add(vo);
		return newCode;
	}
	
	// �ڵ� ä��
	private String genMsgCode() {
		int num = 0;
		if(list.size() > 0) {
			CodeVo vo = list.get(list.size()-1);
			num = Integer.parseInt(vo.getCode().replace(codePrefix, ""));
		}
		return codePrefix + String.format("%06d", num+1);
	}
	
	public void printNoInsertedCodeList() {
		for(CodeVo vo : list) {
			System.out.println("code:" + vo.getCode() + ", ko:" + vo.getLabelKo());
		}
	}
	
	public void setCodePrefix(String str) {
		codePrefix = str;
	}
}
