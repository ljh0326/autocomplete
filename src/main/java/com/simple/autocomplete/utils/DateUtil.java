package com.simple.autocomplete.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 날짜, 시간관련된 클래스
 */
public class DateUtil {

    public static String defaultDateFormat = "yyyyMMddHHmmss";
    public static String shortDateFormat = "yyyyMMdd";
    public static String shortTimeFormat = "HHmmss";

    /**
     * @param startDate
     *            시작날짜
     * @param endDate
     *            종료 날짜
     * @return 두 날짜에 차이 일수
     */
    public static int dayDiffDate(Date startDate, Date endDate) {
        long diff = (endDate.getTime() - startDate.getTime()) / 1000;
        return (int) (diff / (60 * 60 * 24));
    }

    /**
     * 문자열의 날짜를 Date 타입으로 변환 기본 포맷 형식 "yyyyMMddHHmmss"
     *
     * @param dateString
     *            문자열 날짜
     * @return Date
     */
    public static Date getDate(String dateString) {
        String dateFormat = dateString.length() > 8 ? "yyyyMMddHHmmss" : "yyyyMMdd";
        return getDate(dateString, dateFormat);
    }

    public static Date getDate(String dateString, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date rtnValue = null;
        try {
            rtnValue = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return rtnValue;
    }
}
