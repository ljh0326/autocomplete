package com.simple.autocomplete.title.domain;

import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TitleInfo {

    //컨텐츠 넘버
    @CsvBindByPosition(position = 0)
    private String contentsNo;
    //제목
    @CsvBindByPosition(position = 1)
    private String autoKeyword;
    //등록일
    @CsvBindByPosition(position = 2)
    private String regDate;
    //조회수
    @CsvBindByPosition(position = 3)
    private String hit;
    //스코어
    private float score;

    public TitleInfo(){

    }

    public TitleInfo(String contentNo, String autoKeyword, String regDate){
        this(contentNo, autoKeyword, regDate, "0");
    }

    public TitleInfo(String contentsNo, String autoKeyword, String regDate, String hit) {
        this(contentsNo, autoKeyword, regDate, hit, 0);
    }

    public TitleInfo(String contentsNo, String autoKeyword, String regDate, String hit, float score) {
        this.contentsNo = contentsNo;
        this.autoKeyword = autoKeyword;
        this.regDate = regDate;
        this.hit = hit;
        this.score = score;
    }

    public String getAutoKeyword() {
        return this.autoKeyword;
    }

    public void setAutoKeyword(String autoKeyword) {
        this.autoKeyword = autoKeyword;
    }

    public String getContentsNo() {
        return this.contentsNo;
    }

    public void setContentsNo(String contentsNo) {
        this.contentsNo = contentsNo;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

