package com.lin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import org.junit.Test;

public class IOTest {
	
	@Test
	public void testFileIn() {
		
		File file = getFile();
		
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testFileOut() {
		File file = getFile();
		
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			for (int i = 0; i < 10; ++i) {
				bufferedWriter.write(UUID.randomUUID().toString());
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInputStreamReader() throws Exception {
		InputStream in = new FileInputStream(getFile());
		InputStreamReader inputStreamReader = new InputStreamReader(in , "utf-8");
		
		CharArrayWriter arrayWriter = new CharArrayWriter();
		char[] chars = new char[1024];
		int len = 0;
		while ((len = inputStreamReader.read(chars)) != -1) {
			arrayWriter.write(chars, 0, len);
		}
		
		char[] charArray = arrayWriter.toCharArray();
		System.out.println(new String(charArray));
		
		inputStreamReader.close();
	}
	
	@Test
	public void testOutputStreamReader() throws Exception {
		OutputStream out = new FileOutputStream(getFile());
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out , "utf-8");
		for (int i = 0; i < 10; ++i) {
			outputStreamWriter.write(UUID.randomUUID().toString() + "\n");
		}
		outputStreamWriter.close();
	}
	
	private File getFile() {
		String path = IOTest.class.getResource("").getPath().toString();
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}
		return new File(path + "1.txt");
	}

}
