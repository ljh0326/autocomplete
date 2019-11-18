package com.simple.autocomplete.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *  @author LEE
 *  @contact ljh0326s@gmail.com
 *  csv 파일을 생성하는 클래스
 */
public class CsvWriter {
    private static Logger LOGGER = LoggerFactory.getLogger(CsvWriter.class);
    //기본 구분자
    private static final char DEFAULT_SEPARATOR = ',';
    //csv파일 저장되는 경로
    private static final String FILE_PATH = PropertyLoader.getInstance().getPropertyValue("CSV_SAVE_PATH");

    //기본 구분자를 사용하여 Csv파일 생성
    //ex) (ArrayList<> (a, b, c, d)) => a, b, c, d
    public static void writeLine(FileWriter writer, List<String> values) throws IOException {
        writeLine(writer, values, DEFAULT_SEPARATOR, ' ');
    }

    //구분자 지정 Csv파일 생성
    //ex) (Arrays.asList("b", "c", "d"),'|') => a|b|c
    public static void writeLine(FileWriter writer, List<String> values, char separators) throws IOException {
        writeLine(writer, values, separators, ' ');
    }

    private static String followCVSformat(String value){
        String result = value;
        if (result.contains("\"")){
            result = result.replace("\"", "\"\"");
        }
        return result;
    }

    //Quote지정 Csv파일 생성
    //ex) Arrays.asList("b", "c", "d"),'|', '"') => "b"|"c"|"d"
    public static void writeLine(FileWriter writer, List<String> values, char separators, char customQuote) throws IOException {
        boolean first = true;

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        writer.append(sb.toString());
    }
}
