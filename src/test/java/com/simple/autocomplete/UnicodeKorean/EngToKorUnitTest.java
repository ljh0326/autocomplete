package com.simple.autocomplete.UnicodeKorean;

import org.junit.Assert;
import org.junit.Test;

public class EngToKorUnitTest {
    private Eng2KorConverter createEngToKor(){
        return new Eng2KorConverter();
    }

    @Test
    public void givenKor_whenInputEng(){
        Eng2KorConverter etk = createEngToKor();
        Assert.assertEquals("안녕하세요",etk.engToKor("dkssudgktpdy"));
    }

    @Test
    public void givenKorWithoutSpecialChars_whenInputEng(){
        Eng2KorConverter etk = createEngToKor();
        Assert.assertEquals("안녕하세요",etk.engToKor("dkssudgktpdy!!!"));
    }

    @Test
    public void givenKor_whenInputKor(){
        Eng2KorConverter etk = createEngToKor();
        Assert.assertEquals("안녕하세요", etk.engToKor("안녕하세요"));
    }

    @Test
    public void givenKor_whenInputKorAndEng(){
        Eng2KorConverter etk = createEngToKor();
        Assert.assertEquals("안녕하세요오늘", etk.engToKor("안녕하세요dhsmf"));
    }
}
