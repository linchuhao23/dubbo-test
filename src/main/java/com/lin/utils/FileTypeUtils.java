package com.lin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class FileTypeUtils {
	
	private static final char[] HEX_CHARS = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static final Map<String, String> FILE_TYPE_MAP = new LinkedHashMap<>();
	
	private static final int MAX_LEN ;
	
	static {
		initFileType();
		MAX_LEN = FILE_TYPE_MAP.keySet().stream().mapToInt(e -> e != null ? e.length() : 0).max().getAsInt() / 2;
	}

	private static void initFileType() {
		FILE_TYPE_MAP.put("FFD8FF", "jpg");
		FILE_TYPE_MAP.put("89504E47", "png");
		FILE_TYPE_MAP.put("47494638", "gif");
		FILE_TYPE_MAP.put("49492A00", "tif");
		FILE_TYPE_MAP.put("424D", "bmp");
		FILE_TYPE_MAP.put("41433130", "dwg");
		FILE_TYPE_MAP.put("68746D6C3E", "html");
		FILE_TYPE_MAP.put("7B5C727466", "rtf");
		FILE_TYPE_MAP.put("3C3F786D6C", "xml");
		FILE_TYPE_MAP.put("504B0304", "zip");
		FILE_TYPE_MAP.put("52617221", "rar");
		FILE_TYPE_MAP.put("38425053", "psd");
		FILE_TYPE_MAP.put("44656C69766572792D646174653A", "eml");
		FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx");
		FILE_TYPE_MAP.put("2142444E", "pst");
		FILE_TYPE_MAP.put("D0CF11E0", "xls,doc");
		FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");
		FILE_TYPE_MAP.put("FF575043", "wpd");
		FILE_TYPE_MAP.put("252150532D41646F6265", "eps");
		FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
		FILE_TYPE_MAP.put("255044462D312E", "pdf");
		FILE_TYPE_MAP.put("AC9EBD8F", "qdf");
		FILE_TYPE_MAP.put("E3828596", "pwl");
		FILE_TYPE_MAP.put("57415645", "wav");
		FILE_TYPE_MAP.put("41564920", "avi");
		FILE_TYPE_MAP.put("2E7261FD", "ram");
		FILE_TYPE_MAP.put("2E524D46", "rm");
		FILE_TYPE_MAP.put("000001BA", "mpg");
		FILE_TYPE_MAP.put("6D6F6F76", "mov");
		FILE_TYPE_MAP.put("3026B2758E66CF11", "asf");
		FILE_TYPE_MAP.put("4D546864", "mid");
	}
	
	public static String getFileType(File file) {
		Objects.requireNonNull(file, "file must not be null");
		if (!file.exists()) {
			throw new RuntimeException("file does not exists");
		}
		if (!file.isFile()) {
			throw new RuntimeException("file path is not a file document");
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] buf = new byte[1];
			String type = "";
			for (int i = 0; i < MAX_LEN; ++i) {
				if (in.read(buf) == -1) {
					break;
				}
				type += toHexString(buf[0]);
				if (FILE_TYPE_MAP.containsKey(type)) {
					return FILE_TYPE_MAP.get(type);
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					
				}
			}
		}
		return "";
	}
	
	/*private static String toHexString(byte[] bytes) {
		StringBuilder builder = new StringBuilder(bytes.length);
		for (byte b : bytes) {
			builder.append(HEX_CHARS[b >> 4]);
			builder.append(HEX_CHARS[b & 15]);
		}
		return builder.toString();
	}*/
	
	private static String toHexString(byte b) {
		return "" + HEX_CHARS[b >> 4] + HEX_CHARS[b & 15];
	}
	
	public static void main(String[] args) {
		File file = new File("E:\\lin\\work\\sts\\win.zip");
		System.out.println(getFileType(file));
	}

}
