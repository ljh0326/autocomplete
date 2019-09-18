package com.simple.autocomplete.utils;

import org.springframework.stereotype.Component;

@Component
public class ConvertEngToKor {
    enum CodeType {
        chosung, jungsung, jongsung
    }

    private static final String ignoreChars = "`1234567890-=[]\\;',./~!@#$%^&*()_+{}|:\"<>? ";
    private static final String[] arrChoSungEng = {"r", "R", "s", "e", "E", "f", "a", "q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g"};
    private static final String[] arrJungSungEng = {"k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l"};
    private static final String[] arrJongSungEng = {"r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g"};

    public String engToKor(String eng){
        StringBuilder sb = new StringBuilder();
        int initialCode, medialCode, finalCode;
        int tempMedialCode, tempFinalCode;

        for (int i = 0; i < eng.length(); i++) {

            //특수기호에 포함되면 결과에 붙인다.
            if(ignoreChars.contains(eng.substring(i, i + 1))){
//                주석을 풀면 특수문자도 결과에 포함한다.
//                sb.append(eng.substring(i, i+1));
                continue;
            }

            //한국어면 그냥 그대로 문자열에 붙인다.
            if(isKorean(eng, i)){
                sb.append(eng, i, i+1);
                continue;
            }

            //초성코드 추출
            initialCode = getCode(CodeType.chosung, eng.substring(i, i + 1));
            i++;

            //중성코드 추출
            tempMedialCode = getDoubleMedial(i, eng);

            if(tempMedialCode != -1){
                medialCode = tempMedialCode;
                i +=2;
            }else{
                medialCode = getSingleMedial(i, eng);
                i++;
            }

            //종성코드 추출
            tempFinalCode = getDoubleFinal(i, eng);
            if (tempFinalCode != -1){
                finalCode = tempFinalCode;

                tempMedialCode = getSingleMedial(i + 2, eng);
                if (tempMedialCode != -1){
                    finalCode = getSingleFinal(i, eng);
                } else{
                    i++;
                }
            } else{ //코드 값이 없는 경우
                tempMedialCode = getSingleMedial(i + 1, eng);
                if(tempMedialCode != -1){ //다음에 중성 문자가 있는경우, 종성은 없음
                    finalCode = 0;
                    i--;
                } else{
                    finalCode = getSingleFinal(i, eng);
                    if(finalCode == -1){
                        finalCode = 0;
                        i--; //초성, 중성 + 숫자, 특수문자, 기호가 나오는경우 인덱스를 줄임
                    }
                }
            }
            sb.append((char) (0xAC00 + initialCode + medialCode + finalCode));
        }
        return sb.toString();
    }

    private boolean isKorean(String str, int i) {
        return (char)(str.charAt(i) - 0xAC00) >= 0 && (char)(str.charAt(i) - 0xAC00) <= 11172;
    }

    private int getCode(CodeType type, String c) {
        switch (type) {
            case chosung:
                for (int i = 0; i < arrChoSungEng.length; i++) {
                    if (arrChoSungEng[i].equals(c)) {
                        return i * 21 * 28;
                    }
                }
                break;
            case jungsung:
                for (int i = 0; i < arrJungSungEng.length; i++) {
                    if (arrJungSungEng[i].equals(c)) {
                        return i * 28;
                    }
                }
                break;
            case jongsung:
                for (int i = 0; i < arrJongSungEng.length; i++) {
                    if (arrJongSungEng[i].equals(c)) {
                        return i + 1;
                    }
                }
                break;
            default:
                System.out.println("잘못된 타입 입니다.");
        }
        return -1;
    }

    //한 자로 된 중성값을 리턴한다.
    // 인덱스를 벗어났다면 -1을 리턴
    private int getSingleMedial(int i, String eng) {
        if ((i + 1) <= eng.length()) {
            return getCode(CodeType.jungsung, eng.substring(i, i + 1));
        } else {
            return -1;
        }
    }

    //두 자로 된 중성을 체크하고, 있다면 값을 리턴한다.
    private int getDoubleMedial(int i, String eng) {
        int result;
        if ((i + 2) > eng.length()) {
            return -1;
        } else {
            result = getCode(CodeType.jungsung, eng.substring(i, i + 2));
            return result;
        }
    }

    //한 자로된 종성값을 리턴
    private int getSingleFinal(int i, String eng) {
        if ((i + 1) <= eng.length()) {
            return getCode(CodeType.jongsung, eng.substring(i, i + 1));
        } else {
            return -1;
        }
    }

    //두 자로 된 종성을 리턴
    private int getDoubleFinal(int i, String eng) {
        if ((i + 2) > eng.length()) {
            return -1;
        } else {
            return getCode(CodeType.jongsung, eng.substring(i, i + 2));
        }
    }
}
