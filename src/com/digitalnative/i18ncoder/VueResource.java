package com.digitalnative.i18ncoder;

public class VueResource {

	private String contentTopComment = "";
	private String contentTags = "";
	private String contentScript = "";
	private String contentStyle = "";
	
	
	public String getContentTopComment() {
		return contentTopComment;
	}

	public void setContentTopComment(String contentTopComment) {
		this.contentTopComment = contentTopComment;
	}

	public String getContentTags() {
		return contentTags;
	}

	public void setContentTags(String contentTags) {
		this.contentTags = contentTags;
	}

	public String getContentScript() {
		return contentScript;
	}

	public void setContentScript(String contentScript) {
		this.contentScript = contentScript;
	}

	public String getContentStyle() {
		return contentStyle;
	}

	public void setContentStyle(String contentStyle) {
		this.contentStyle = contentStyle;
	}



	public VueResource analyzerFile(String source) {
		return _analyzerFile(source);
	}
	
	private VueResource _analyzerFile(String source) {
		
		String lines[] = source.split("\n");
		String lineStatus = "topComment";
		
		for(String line : lines) {
			if(line.indexOf("<template>") == 0) {
				lineStatus = "tag";
			}else if(line.indexOf("<script") == 0 || line.indexOf("<script setup") == 0) {
				lineStatus = "script";
			}else if(line.indexOf("<style") == 0) {
				lineStatus = "style";
			}
			
			if(lineStatus.equals("topComment")) {
				contentTopComment += line;
			}else if(lineStatus.equals("tag")) {
				contentTags += line;
			}else if(lineStatus.equals("script")) {
				contentScript += line;
			}else if(lineStatus.equals("style")) {
				contentStyle += line;
			}
		}
		
//		System.out.println("=====================");
//		System.out.println(contentTopComment);
//		System.out.println("=====================");
//		System.out.println(contentTags);
//		System.out.println("=====================");
//		System.out.println(contentScript);
//		System.out.println("=====================");
//		System.out.println(contentStyle);
//		System.out.println("=====================");
		
		
		return null;
	}
}
