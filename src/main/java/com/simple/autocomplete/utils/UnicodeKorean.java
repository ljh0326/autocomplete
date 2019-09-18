package com.simple.autocomplete.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UnicodeKorean {

    /**
     * 초성 -가(ㄱ), 날(ㄴ) 닭(ㄷ)
     */
    final private static char[] arrChoSung = {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
            0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
    /**
     * 중성 -가(ㅏ),야(ㅑ), 뺨(ㅑ)
     */
    final private static char[] arrJungSung = {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a,
            0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163};
    /**
     * 종성 -가(없음), 갈(ㄹ) 천(ㄴ)
     */
    final private static char[] arrJongSung = {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c,
            0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

    final private static String[] arrChoSungEng = {"r", "R", "s", "e", "E", "f", "a", "q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g"};
    final private static String[] arrJungSungEng = {"k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l"};
    final private static String[] arrJongSungEng = {"r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g"};

    final private static Map<Character, String> korToEngMap = new HashMap<>();
    private String word;

    //생성시 할글 키와 영어키를 매핑한다.
    public UnicodeKorean() {
        for (int i = 0; i < arrChoSung.length; i++) {
            korToEngMap.put(arrChoSung[i], arrChoSungEng[i]);
        }

        for (int i = 0; i < arrJungSung.length; i++) {
            korToEngMap.put(arrJungSung[i], arrJungSungEng[i]);
        }

        for (int i = 0; i < arrJongSungEng.length; i++) {
            korToEngMap.put(arrJongSung[i + 1], arrJongSungEng[i]);
        }
    }

    public UnicodeKorean(String word) {
        this();
        this.setWord(word);
    }

    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the word to set
     */
    public void setWord(String word) {
        //문자열 모든 공백제거
        word = word.replaceAll("\\p{Z}", "");
        this.word = word;
    }

    /**
     * @return the result
     */
    public String getResult() {
        if (StringUtils.isEmpty(this.word)) return "";
        return getResult(this.word);
    }

    /**
     * @return the result
     */
    public String getResult(String word) {
        String result = "";
        word = word.toLowerCase();

        for (int i = 0; i < word.length(); i++) {
            char chars = (char) (word.charAt(i) - 0xAC00);

            //1. 자음과 모음이 합쳐진 글자인경우
            if (chars >= 0 && chars <= 11172) {

                //1.1 초/중/종성 분리
                int chosung = chars / (21 * 28);
                int jungsung = chars % (21 * 28) / 28;
                int jongsung = chars % (21 * 28) % 28;

                //1.2 result에 담기
//				result = result + UnicodeKorean.arrChoSung[chosung] + UnicodeKorean.arrJungSung[jungsung] + UnicodeKorean.arrJongSung[jongsung];
                result = result + UnicodeKorean.arrChoSung[chosung] + UnicodeKorean.arrJungSung[jungsung];

                //자음분리
                if (jongsung != 0x0000) {
                    //1.3 종성이 존재할 경우 reuslt에 담는다
                    result = result + UnicodeKorean.arrJongSung[jongsung];
                }
            }
            //2.영어인경우 그외에는 다 무시
            else if ((word.charAt(i) >= 'A' && (char) word.charAt(i) <= 'Z') ||
                    (word.charAt(i) >= 'a' && (char) word.charAt(i) <= 'z') ||
                    (word.charAt(i) >= '1' && (char) word.charAt(i) <= '9')
            ) {
                result += word.charAt(i);
            }
        }
        return result;
    }

    public String getReverseResult(){
        if (StringUtils.isEmpty(this.word)) return "";
        return getReverseResult(this.word);
    }

    public String getReverseResult(String word){
        String result = getResult(word);
        return StringUtils.reverse(result);
    }

    public String getEngResult(){
        if (StringUtils.isEmpty(this.word)) return "";
        return getEngResult(this.word);
    }

    public String getEngResult(String word) {
        StringBuilder sb = new StringBuilder();
        String result = getResult(word);
        for (int i = 0; i < result.length(); i++) {
            if(StringUtils.isNotEmpty(korToEngMap.get(result.charAt(i)))){
                sb.append(korToEngMap.get(result.charAt(i)));
            }else{
                sb.append(result.charAt(i));
            }
        }

        return sb.toString();
    }

    public String getEngReverseResult(){
        if (StringUtils.isEmpty(this.word)) return "";
        return getEngReverseResult(this.word);
    }

    public String getEngReverseResult(String word) {
        String result = getEngResult(word);
        return StringUtils.reverse(result);
    }
}