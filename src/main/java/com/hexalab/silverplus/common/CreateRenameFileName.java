package com.hexalab.silverplus.common;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;

public class CreateRenameFileName {
	
	public static String create(UUID uuid,String originalFileName) {
		String renameFileName = uuid.toString();
		
		//바꿀 파일명에 대한 문자열 만들기
		//전달받은 포멧문자열 이용해서 만듦 => 년월일시분초 형식의 포멧을 이용할 것이므로
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		//현재 시스템으로 부터 날짜와 시간 정보를 가지고 와서 변경할 파일명을 만들기함
		renameFileName +="_"+sdf.format(new java.sql.Date(System.currentTimeMillis()));

		//랜덤 숫자 3자리 추가
		Random random = new Random();
		int randomNum = 100 + random.nextInt(900);
		renameFileName += "_" + randomNum;

		//원본 파일의 확장자를 추출해서, 바꿀 파일명 뒤에 추가 연결함
		renameFileName += "." + originalFileName.substring(originalFileName.indexOf(".") + 1);
		
		return renameFileName;
	}
}
