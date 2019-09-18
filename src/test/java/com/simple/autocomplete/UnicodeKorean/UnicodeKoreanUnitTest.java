package com.simple.autocomplete.UnicodeKorean;

import com.simple.autocomplete.utils.UnicodeKorean;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnicodeKoreanUnitTest {

    private UnicodeKorean createExampleUnicodeKorean(){
        return new UnicodeKorean();
    }

    private UnicodeKorean createExampleUnicodeKorean(String word){
        return new UnicodeKorean(word);
    }

    @Test
    public void givenEmptyString_whenInputEmptyString(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        assertEquals("", unicodeKorean.getResult());
    }

    @Test
    public void givenStem_whenInputKorean(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("공통라운지서비스");
        assertEquals("ㄱㅗㅇㅌㅗㅇㄹㅏㅇㅜㄴㅈㅣㅅㅓㅂㅣㅅㅡ", unicodeKorean.getResult());
    }

    @Test
    public void givenString_whenInputStringWithWiteSpace(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("공통 라운지 서비스");
        assertEquals("공통라운지서비스", unicodeKorean.getWord());
    }

    @Test
    public void givenStem_whenInputStringWithSpecialCharacter(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("공통 라운지 서비스()()()");
        assertEquals("ㄱㅗㅇㅌㅗㅇㄹㅏㅇㅜㄴㅈㅣㅅㅓㅂㅣㅅㅡ", unicodeKorean.getResult());
    }

    @Test
    public void givenStem_whenInputStringWithEnglish(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("ab공통cd 라운지 efg서비스()()()z");
        assertEquals("abㄱㅗㅇㅌㅗㅇcdㄹㅏㅇㅜㄴㅈㅣefgㅅㅓㅂㅣㅅㅡz", unicodeKorean.getResult());
    }

    @Test
    public void givenReverseStem_whenInputStringWithEnglish(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("ab공통cd 라운지 efg서비스()()()z");
        assertEquals("zㅡㅅㅣㅂㅓㅅgfeㅣㅈㄴㅜㅇㅏㄹdcㅇㅗㅌㅇㅗㄱba", unicodeKorean.getReverseResult());
    }

    @Test
    public void givenEngStem_whenInputString(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("안녕");
        assertEquals("dkssud", unicodeKorean.getEngResult());
    }

    @Test
    public void givenEngStem_whenInputStringWithEnglish(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("ab안녕bd");
        assertEquals("abdkssudbd", unicodeKorean.getEngResult());
    }

    @Test
    public void givenEngReverseStem_whenInputStringWithEnglish(){
        UnicodeKorean unicodeKorean = createExampleUnicodeKorean();
        unicodeKorean.setWord("안녕");
        assertEquals("dusskd", unicodeKorean.getEngReverseResult());
    }




}
