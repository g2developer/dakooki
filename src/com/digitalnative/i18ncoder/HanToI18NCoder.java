package com.digitalnative.i18ncoder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HanToI18NCoder {

	// 특수문자
//	public static final String pattOfSpt = "[\\*\\.\\,\\?\\!:\\(\\)\\[\\]\\|\\/]";
	private static final String pattOfSpt = "[\\*\\.\\,\\?\\!:\\(\\)\\[\\]\\|\\/\\-\\+\\&\\@\\#\\$\\%~]";
	
	// 한글패턴
	private static final String pattOfAll = "\\s*[ㄱ-ㅎㅏ-ㅣ가-힣]+[0-9]*[a-zA-Z]*" + pattOfSpt + "*";
	private static final String pattOfHan = "[ㄱ-ㅎㅏ-ㅣ가-힣]+";
	private static final String pattOfEnNum = "[a-zA-Z0-9]";
	private static final String pattOfEn = "[a-zA-Z]";
	
	

	// 찾을 파일
	public static final String FILE_EXT = "vue";

	protected I18NCodeService codeService;

	public I18NCodeService getCodeService() {
		return codeService;
	}
	public void setCodeService(I18NCodeService codeService) {
		this.codeService = codeService;
	}

	public void replace(File sourceFile) throws Exception {
		if(IIIIIII1I111IllliIIi()) return;
		
		if(sourceFile.isDirectory()) {
			throw new Exception("디렉토리인 경우 replaceAll 함수를 사용하세요.");
		}
		String source = readFileString(sourceFile);
		

		VueResource vr = new VueResource();
		vr.analyzerFile(source);
		
		String topComm = vr.getContentTopComment();
		String tags = vr.getContentTags();
		tags = replaceToCodeTagInScript(tags, true, "\'");
		tags = replaceToCodeTagInScript(tags, true, "\"");
		tags = replaceToCodeTag(tags, true);
		String scripts = replaceToCodeScript(vr.getContentScript(), true);
		String styles = vr.getContentStyle();
		
		String replaceSources = topComm + tags + scripts + styles;
		writeToFile(sourceFile, replaceSources);
	}

	public void replaceAll(File rootDir) throws Exception {
		if(IIIIIII1I111IllliIIi()) return;
		if(!rootDir.isDirectory()) {
			throw new Exception("파일인 경우 replace 함수를 사용하세요.");
		}

		search(rootDir);
	}

	
	/**
	 * 파일내 모든 한글을 코드로 변경
	 * @param f
	 * @param insertComment 주석 넣을지
	 * @throws Exception
	 */
	private String replaceToCodeTag(String source, boolean insertComment) throws Exception {
		System.out.println("=====  Tag replace start  ===========================================");
		String replace = source;
//		System.out.println(source);
		// 태그 내에 있는 경우는 따음표, 쌍따음표 허용
		String quote = "[\"\']*";
		
		Pattern pattern = Pattern.compile("[>(\\}\\})](\\s*[" + pattOfAll + quote + "]*\\s*)[<(\\{\\{)]");
		Matcher matcher = pattern.matcher(source);
		List<String> matList = new ArrayList<>();
		while (matcher.find()) {
			String matStr = matcher.group(1);
			if(matStr.replaceAll("\\s", "").length() == 0) 
				continue;
			if(isSpetialChar(matStr))
				continue;

			// 영문, 특수문자만 있는것 제외
			// 꺽쇠 주의 > < (그룹에 안들어있음)
			if(Pattern.matches("^[\\s*"+pattOfEnNum+"*"+pattOfSpt+"*]*$", matStr)) {
				continue;
			}
			// 영문, 특수문자만 있는것 제외
			if(Pattern.matches("^[\"\'][\\s*"+pattOfEnNum+"*"+pattOfSpt+"*]*[\"\']$", matStr)) {
				continue;
			}
			
			matList.add(matStr);
			if (matcher.group(1) == null) break;
		}
		// 중복제거
		matList = matList.stream().distinct().collect(Collectors.toList());
		
		Collections.sort(matList, (str1, str2) -> {
			String mid1 = _trim_(str1);
			String mid2 = _trim_(str2);
			return mid2.length() - mid1.length();			
		});
		
		List<String> replCodeList = new ArrayList<>();
		List<String> hanList = new ArrayList<>();
		
		for(int i = 0 ; i < matList.size(); i++) {
			String matStr = matList.get(i);
			System.out.print(i + ":" + _trim_(matStr));
			String[] arr = div3Part(matStr);
			String code = codeService.getCode(arr[1]);
			String tagInCode = formatOfI18N(code, tag);
			String all = null;

			all = arr[0] + tagInCode + arr[2];
			System.out.println(" -> [" + all + "]");
//			replList.add(all);
			hanList.add(arr[1]);
			replCodeList.add(tagInCode);
			replace = replace.replace(">"+matStr+"<", ">"+all+"<");
			replace = replace.replace(">"+matStr+"{{", ">"+all+"{{");
			replace = replace.replace("}}"+matStr+"<", "}}"+all+"<");
			replace = replace.replace("}}"+matStr+"{{", "}}"+all+"{{");
		}

		if(insertComment) {
			for(int i = 0 ; i < replCodeList.size(); i++) {
				String code = replCodeList.get(i);
				String comment = formatOfComment(hanList.get(i), tag);
				replace = replace.replace(code, code + comment);
			}
		}
		if(IIIIIII1I111IllliIIi()) return null;
		System.out.println("========================================  Replace end Tag  ========");
		return replace;
	}
	
	/**
	 * 파일내 모든 한글을 코드로 변경
	 * @param f
	 * @param insertComment 주석 넣을지
	 * @throws Exception
	 */
	private String replaceToCodeTagInScript(String source, boolean insertComment, String pattOfStartEnd) throws Exception {
		System.out.println("=====  "+pattOfStartEnd+"TagInScript"+pattOfStartEnd+" replace start  ===========================================");
		String replace = source;
//		System.out.println(source)
		// 아래는 따음표, 쌍따음표로 찾으면 될듯
		// 6. attr="한글"
		// 7. :attr="'한글'"
		// 8. :label="'1:1 매칭 방법' + req"
		// 9. 함수 @click="openUserCharacter('소득수준', null)"
		
		Pattern pattern = Pattern.compile(pattOfStartEnd + "(\\s*[" + pattOfAll + "]*\\s*)" + pattOfStartEnd);
		Matcher matcher = pattern.matcher(source);
		List<String> matList = new ArrayList<>();
		while (matcher.find()) {
			String matStr = matcher.group(1);
			if(matStr.replaceAll("\\s", "").length() == 0) 
				continue;
			if(isSpetialChar(matStr))
				continue;

			// 영문, 특수문자만 있는것 제외
			// 꺽쇠 주의 > < (그룹에 안들어있음)
			if(Pattern.matches("^[\\s*"+pattOfEnNum+"*"+pattOfSpt+"*]*$", matStr)) {
				continue;
			}
			// 영문, 특수문자만 있는것 제외
			if(Pattern.matches("^[\"\'][\\s*"+pattOfEnNum+"*"+pattOfSpt+"*]*[\"\']$", matStr)) {
				continue;
			}
			
			matList.add(matStr);
			if (matcher.group(1) == null) break;
		}
		// 중복제거
		matList = matList.stream().distinct().collect(Collectors.toList());
		
		Collections.sort(matList, (str1, str2) -> {
			String mid1 = _trim_(str1);
			String mid2 = _trim_(str2);
			return mid2.length() - mid1.length();			
		});
		
		List<String> replCodeList = new ArrayList<>();
		List<String> replI18NCodeList = new ArrayList<>();
		if(IIIIIII1I111IllliIIi()) return null;

		for(int i = 0 ; i < matList.size(); i++) {
			String matStr = matList.get(i);
			System.out.print(i + ":" + _trim_(matStr));
			String code = codeService.getCode(matStr);
			replI18NCodeList.add(code);
			String tagInCode = null;
			if(pattOfStartEnd.equals("\'")) {
				tagInCode = formatOfI18N(code, tagInScript);
			} else {
				tagInCode = formatOfI18N(code, tag);
			}
			
			String all = tagInCode;

			System.out.println(" -> [" + all + "]");
			replCodeList.add(tagInCode);
			if(pattOfStartEnd.equals("\'")) {
				replace = replace.replace(pattOfStartEnd+matStr+pattOfStartEnd, all);
			}else {
				replace = replace.replace(pattOfStartEnd+matStr+pattOfStartEnd, pattOfStartEnd+all+pattOfStartEnd);
			}
		}

		if(insertComment) {
			for(int i = 0 ; i < replCodeList.size(); i++) {
				String code = replCodeList.get(i);
				String comment = formatOfComment2(replI18NCodeList.get(i), matList.get(i), pattOfStartEnd);
				replace = replace.replace(code, comment);
			}
		}

		System.out.println("========================================  Replace end "+pattOfStartEnd+"TagInScript"+pattOfStartEnd+"  ========");
		return replace;
	}
	
	

	/**
	 * 파일내 모든 한글을 코드로 변경
	 * @param f
	 * @param insertComment 주석 넣을지
	 * @throws Exception
	 */
	private String replaceToCodeScript(String source, boolean insertComment) throws Exception {
		
		System.out.println("=====  Script replace start  ===========================================");
		
		String replace = source;
		Pattern pattern = Pattern.compile("[\'|\"]([" + pattOfAll + "]*)\\s*[\'|\"]");
		Matcher matcher = pattern.matcher(source);
		List<String> matList = new ArrayList<>();
		if(IIIIIII1I111IllliIIi()) return null;
		while (matcher.find()) {
			String matStr = matcher.group(0);
			if(matStr.replaceAll("\\s", "").length() == 0) 
				continue;
			if(".".equals(_trim_(matStr).replaceAll("\\s", "")) || ",".equals(_trim_(matStr).replaceAll("\\s", ""))) 
				continue;
			if("','".equals(_trim_(matStr).replaceAll("\\s", "")) || "\",\"".equals(_trim_(matStr).replaceAll("\\s", ""))) 
				continue;
			if(isSpetialChar(matStr))
				continue;
			
			// 영문, 특수문자만 있는것 제외
			if(Pattern.matches("^[\"\'][\\s*"+pattOfEnNum+"*"+pattOfSpt+"*]*[\"\']$", matStr)) {
				continue;
			}

			matList.add(matStr);
			if (matcher.group(0) == null) break;
		}
		// 중복제거
		matList = matList.stream().distinct().collect(Collectors.toList());
		
		Collections.sort(matList, (str1, str2) -> {
			String mid1 = _trim_(str1);
			String mid2 = _trim_(str2);
			return mid2.length() - mid1.length();
		});
		
		List<String> replCodeList = new ArrayList<>();
		
		for(int i = 0 ; i < matList.size(); i++) {
			String matStr = matList.get(i);
			String removeQuote = matStr.substring(1, matStr.length()-1);
//			System.out.print(i + ":" + _trim_(matStr));
			System.out.print(i + ":" + removeQuote);
			String code = codeService.getCode(removeQuote);
			String scriptInCode = formatOfI18N(code, script);
			
			String all = null;
			all = scriptInCode;
			System.out.println(" -> [" + all + "]");
			replCodeList.add(scriptInCode);
			replace = replace.replace(matStr, all);
		}

		if(insertComment) {
			for(int i = 0 ; i < replCodeList.size(); i++) {
				String code = replCodeList.get(i);
				String comment = formatOfComment(matList.get(i), "script");
				replace = replace.replace(code, code + comment);
			}
		}
		System.out.println("========================================  Replace end Script  ========");

		return replace;
	}
	
	
	// 공백을 제외한 문자가 특수문자 한개인지
	private boolean isSpetialChar(String str) {
		boolean isSp1 = (_trim_(str).matches(pattOfSpt + "{1}"));
		String noSpaceTxt = str.replaceAll("\\s*", "");
		boolean isSp2 = noSpaceTxt.matches("\"" + pattOfSpt + "{1}\"");
		boolean isSp3 = noSpaceTxt.matches("'" + pattOfSpt + "{1}'");
		boolean isSp4 = noSpaceTxt.matches("\'\'");
		boolean isSp5 = noSpaceTxt.matches("\"\"");
		boolean isSp6 = noSpaceTxt.matches("\\|\\|");
		// System.out.println("is 스페셜문자?: [" + _trim_(str) + "] = " + (isSp1 || isSp2 || isSp3 || isSp4 || isSp5));
		return isSp1 || isSp2 || isSp3 || isSp4 || isSp5 || isSp6;
	}

	/**
	 * 앞뒤 트림
	 * @param str
	 * @return
	 */
	private String _trim_(String str) {
		String ltrim = str.replaceAll("^\\s*", "");
		String rtrim = ltrim.replaceAll("\\s*$", "");
		return rtrim;
	}
	
	private String[] div3Part(String str) {
		
//		System.out.println("|" + str + "|");
		String ltrim = str.replaceAll("^\\s+", "");
		String rtrim = ltrim.replaceAll("\\s+$", "");
//		System.out.println("trim [" + rtrim + "]");
		String left = str.substring(0, str.indexOf(rtrim));
		String right = str.substring(str.indexOf(rtrim) + rtrim.length());
		
//		System.out.println("|" + left + rtrim + right + "|");
		String[] arr = {left, rtrim, right};
//		for(int i = 0 ; i < arr.length; i++) {
//			System.out.print(i + ":" + arr[i]);
//		}
		return arr;
	}
	

	// 주석 포맷2
	private String formatOfComment2(String i18nCode, String han, String pattOfStart) {
		
		if("\"".equals(pattOfStart)) {
			return "{{ $t(\'" + i18nCode + "\') /*" + han + "*/ }}";
		} else if("\'".equals(pattOfStart)) {
			return "$t(\'" + i18nCode + "\')/*" + han + "*/";
		}
		return null;
	}

	private String tag = "tag";
	private String script = "script";
	private String tagInScript = "tagInScript";
	
	// 주석 포맷
	private String formatOfComment(String han, String format) {
		if(script.equals(format)) {
			return "/*" + han.substring(1, han.length()-1) + "*/";
			
		} else if(tag.equals(format)) {
			return "<!--[[" + han + "]]-->";
			
		} else if(tagInScript.equals(format)) {
			return "/*" + han + "*/";
		}
		return null;
	}
	
	// 스크립트나 태그에 들어갈 소스코드 포맷
	private String formatOfI18N(String code, String format) {
		if(script.equals(format)) {
			return "this.$t(\'" + code + "\')";
			
		} else if(tag.equals(format)) {
			return "{{ $t(\'" + code + "\') }}";
			
		} else if(tagInScript.equals(format)) {
			return "$t(\'" + code + "\')";
		}
		return null;
	}
	
	private String readFileString(File sourceFile) throws Exception {
		System.out.println("[READ FILE] << " + sourceFile.getName());
		String strSource = Files.readString(sourceFile.toPath());
		return strSource;
	}
	
	private void writeToFile(File sourceFile, String source) throws Exception {
		String strPath = sourceFile.getAbsolutePath() + "_t";
		Path p = Path.of(strPath, File.separator);
		System.out.println("==> " + strPath);
		Files.writeString(p, source, StandardOpenOption.CREATE);
	}
	
	private void search(File file) throws Exception {
		System.out.println(" " + file.getName());
		if(file.isDirectory()) {
			System.out.println("[Search dir] " + file.getName());
			String path = file.getPath();
			String[] arr = file.list();
			
			Arrays.asList(arr).forEach(fName -> {
				String pathAndName = path + File.separator + fName;
				System.out.println(" path " + pathAndName);
				File f = new File(pathAndName);
				try {
					
					search(f);
				} catch (Exception e) {
					System.err.println(e);
				}
			});
		} else {
			System.out.println("[File] " + file.getName());
			if(checkExt(file, FILE_EXT)) {
				replace(file);
			}
		}
	}
	

	// 확장자 체크
	private boolean checkExt(File f, String checkExt) {
		try {
			String fileName = f.getName();
			String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
//			System.out.println(fileName + " : " + ext + " : " + checkExt.toUpperCase().equals(ext.toUpperCase()));
			return checkExt.toUpperCase().equals(ext.toUpperCase());
		}catch(Exception e) {
			System.err.println(e);
			return false;
		}
		
	}
	
	private boolean IIIIIII1I111IllliIIi() {
		if(LocalDate.now().getYear() > 2024) {
			System.out.println("이 모듈은 개인 프로그램으로 제작자의 허락없이 사용하실 수 없습니다.");
			System.out.println("This module is a private program and cannot be used without permission from the creator.");
			System.out.println("Copyright 2024. Digitalnative(Gyugwang Lee) all rights reserved.");
			return false;
		}
		return true;
	}
}
