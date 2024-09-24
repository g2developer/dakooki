package dakooki;

import java.io.File;

import com.digitalnative.i18ncoder.HanToI18NCoder;
import com.digitalnative.i18ncoder.I18NCodeService;

//import java.time.LocalDateTime;

public class dakooki {

	public static void main(String[] args) throws Exception {
		System.out.println("한국어 vue 파일 -> i18n code vue파일 ");
		
		
		
//		LocalDateTime dtt = LocalDateTime.now();
//		System.out.println("dtt " + dtt.getYear());
//		System.out.println("dtt " + dtt.getMonthValue());
//		System.out.println("dtt " + dtt.getDayOfMonth());
		

		I18NCodeService codeService = new I18NCodeService();
		
		HanToI18NCoder changer = new HanToI18NCoder();
		changer.setCodeService(codeService);
		
		// 파일하나 바꾸기
		String path = "C:\\work\\workspace_meetoff\\meetoff_front\\src\\views\\home\\HomeTabPage.vue";
		File sourceFile = new File(path);
		changer.replace(sourceFile);

		// 폴더째로 바꾸기
//		String path = "C:\\work\\workspace_meetoff\\meetoff_front\\src\\views\\home\\";
//		File rootDir = new File(path);
//		
//		changer.replaceAll(rootDir);
		
		
		
		// 파일하나 바꾸기
//		String path = "C:\\work\\workspace_meetoff\\meetoff_front\\src\\views\\home\\GroupMeetWritePage.vue";
//		File sourceFile = new File(path);
//		String source = changer.readFileString(sourceFile);
//		
//		VueResource vr = new VueResource();
//		vr.analyzerFile(source);
//		
//		String tags = vr.getContentTags();
//		tags = changer.replaceToCodeTagInScript(tags, true, "\'");
//		tags = changer.replaceToCodeTagInScript(tags, true, "\"");
//		tags = changer.replaceToCodeTag(tags, true);
//		String scripts = changer.replaceToCodeScript(vr.getContentScript(), true);
//
//		System.out.print(tags);
//		System.out.print(scripts);
//		System.out.print(vr.getContentStyle());
		
//		codeService.printNoInsertedCodeList();
	}
	

}
