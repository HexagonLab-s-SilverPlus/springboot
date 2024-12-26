package com.hexalab.silverplus.common;

import java.io.Serializable;

public class Paging implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int startRow;       //해당 페이지에 출력할 시작행 (데이터 조회)
    private int endRow;         //해당 페이지에 출력할 끝행 (데이터 조회)
    private int listCount;      //총 목록 갯수
    private int limit;          //한 페이지에 출력할 갯수
    private int currentPage;    //출력할 현재 페이지
    private int maxPage;        //총 페이지 수
    private int startPage;      //현재 페이지가 속한 페이지 그룹의 시작값 (뷰페이지 사용)
    private int endPage;        //현재 페이지가 속한 페이지 그룹의 끝값 (뷰페이지 사용)

    //기본 생성자 없음

    //매개 변수 4개 있는 생성자만 사용함
    public Paging(int listCount, int limit, int currentPage) {
        super();
        this.listCount = listCount;
        this.limit = limit;
        this.currentPage = currentPage;
    }

    //페이지 계산 메소드
    public void calculate() {
        //총 페이지 수 계산
        this.maxPage = (int)((double)listCount / limit + 0.9);

        //뷰 페이지 출력에 사용할 페이지 그룹의 시작값, 끝값 지정
        this.startPage = (int)(((double)currentPage / limit + 0.9) - 1) * limit + 1;
        this.endPage = startPage + limit - 1;

        //마지막 그룹의 끝값을 마지막 페이지와 맞춤
        if(maxPage < endPage) {
            endPage = maxPage;
        }

        //요청한 현재 페이지에 출력될 목록의 행 번호 계산
        this.startRow = (currentPage - 1) * limit + 1;
        this.endRow = startRow + limit - 1;
    }//calculate end

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

}//Paging end
