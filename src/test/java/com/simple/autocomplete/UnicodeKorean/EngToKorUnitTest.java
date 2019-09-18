package com.simple.autocomplete.UnicodeKorean;

import com.simple.autocomplete.utils.ConvertEngToKor;
import org.junit.Assert;
import org.junit.Test;

public class EngToKorUnitTest {
    private ConvertEngToKor createEngToKor(){
        return new ConvertEngToKor();
    }

    @Test
    public void givenKor_whenInputEng(){
        ConvertEngToKor etk = createEngToKor();
        Assert.assertEquals("안녕하세요",etk.engToKor("dkssudgktpdy"));
    }

    @Test
    public void givenKorWithoutSpecialChars_whenInputEng(){
        ConvertEngToKor etk = createEngToKor();
        Assert.assertEquals("안녕하세요",etk.engToKor("dkssudgktpdy!!!"));
    }

    @Test
    public void givenKor_whenInputKor(){
        ConvertEngToKor etk = createEngToKor();
        Assert.assertEquals("안녕하세요", etk.engToKor("안녕하세요"));
    }

    @Test
    public void givenKor_whenInputKorAndEng(){
        ConvertEngToKor etk = createEngToKor();
        Assert.assertEquals("안녕하세요오늘", etk.engToKor("안녕하세요dhsmf"));
    }
}
