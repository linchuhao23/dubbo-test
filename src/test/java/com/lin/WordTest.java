package com.lin;

import java.util.List;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.junit.Test;

public class WordTest {
	
	@Test
	public void test() {
		List<Word> words = WordSegmenter.seg("林楚豪是APDPlat应用级产品开发平台的作者");
		words.forEach(e -> System.out.println(e.getText()));
	}

}
