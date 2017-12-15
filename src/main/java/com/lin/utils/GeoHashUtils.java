package com.lin.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域搜索
 * 
 * @author Administrator
 *
 */
public final class GeoHashUtils {

	private GeoHashUtils() {
		throw new RuntimeException("GeoHashUtils can not instance");
	}

	public static final double MIN_LAT = -90;
	public static final double MAX_LAT = 90;
	public static final double MIN_LNG = -180;
	public static final double MAX_LNG = 180;

	private static final char[] CHARS = { //
			'0', '1', '2', '3', '4', '5', '6', '7', //
			'8', '9', 'b', 'c', 'd', 'e', 'f', 'g', //
			'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', //
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private static final char[] BASE64_CHARS = { //
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', //
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', //
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', //
			'4', '5', '6', '7', '8', '9', '+', '/'//
	};

	public static void main(String[] args) {
		//System.out.println(getGeoHashBase64(39.92324, 116.3906, 8));
		listGeoHashBase64(39.92324, 110.3906, 10).forEach(e -> {
			System.out.println(e);
		});
		listGeoHashBase32(39.92324, 110.3906, 10).forEach(e -> {
			System.out.println(e);
		});
	}
	
	public static String getGeoHashBase64(double lat, double lng, int hashLen) {
		checkLat(lat);
		checkLng(lng);
		checkLen(hashLen);
		byte[] hashArray = createHashArray64(lat, lng, hashLen);
		StringBuilder builder = new StringBuilder(hashLen);
		for (int i = 0; i < hashLen; i++) {
			int start = i * 6;
			int index = (hashArray[start++] << 5) //
					| (hashArray[start++] << 4) //
					| (hashArray[start++] << 3) //
					| (hashArray[start++] << 2) //
					| (hashArray[start++] << 1)//
					| (hashArray[start]);//
			builder.append(BASE64_CHARS[index]);
		}
		return builder.toString();
	}
	
	private static byte[] createHashArray64(double lat, double lng, int hashLen) {
		int lat_len  = hashLen * 3, lng_len = lat_len;
		byte[] latbytes = getHashArray(lat, MIN_LAT, MAX_LAT, lat_len);
		byte[] lngbytes = getHashArray(lng, MIN_LNG, MAX_LNG, lng_len);
		byte[] rt = new byte[hashLen * 6];
		for (int i = 0; i < lat_len; ++i) {
			rt[i * 2 + 1] = latbytes[i];
		}
		for (int i = 0; i < lng_len; ++i) {
			rt[i * 2] = lngbytes[i];
		}
		return rt;
	}
	
	public static List<String> listGeoHashBase64(double lat, double lng, int hashLen) {
		double minLat = initMinLat64(hashLen);
		double minLng = initMinLng64(hashLen);
		double leftLat = lat - minLat;
		double rightLat = lat + minLat;
		double upLng = lng - minLng;
		double downLng = lng + minLng;
		List<String> values = new ArrayList<String>(9);

		// 左侧从上到下 3个
		String leftUp = getGeoHashBase64(leftLat, upLng, hashLen);
		if (!(leftUp == null || "".equals(leftUp))) {
			values.add(leftUp);
		}

		String leftMid = getGeoHashBase64(leftLat, lng, hashLen);
		if (!(leftMid == null || "".equals(leftMid))) {
			values.add(leftMid);
		}
		String leftDown = getGeoHashBase64(leftLat, downLng, hashLen);
		if (!(leftDown == null || "".equals(leftDown))) {
			values.add(leftDown);
		}
		// 中间从上到下 3个
		String midUp = getGeoHashBase64(lat, upLng, hashLen);
		if (!(midUp == null || "".equals(midUp))) {
			values.add(midUp);
		}
		String midMid = getGeoHashBase64(lat, lng, hashLen);
		if (!(midMid == null || "".equals(midMid))) {
			values.add(midMid);
		}
		String midDown = getGeoHashBase64(lat, downLng, hashLen);
		if (!(midDown == null || "".equals(midDown))) {
			values.add(midDown);
		}
		// 右侧从上到下 3个
		String rightUp = getGeoHashBase64(rightLat, upLng, hashLen);
		if (!(rightUp == null || "".equals(rightUp))) {
			values.add(rightUp);
		}
		String rightMid = getGeoHashBase64(rightLat, lng, hashLen);
		if (!(rightMid == null || "".equals(rightMid))) {
			values.add(rightMid);
		}
		String rightDown = getGeoHashBase64(rightLat, downLng, hashLen);
		if (!(rightDown == null || "".equals(rightDown))) {
			values.add(rightDown);
		}
		return values;
	}
	
	private static double initMinLat64(int hashLen) {
		double minLat = MAX_LAT - MIN_LAT;
		int lat_len = hashLen * 3;
		for (int i = 0; i < lat_len; i++) {
			minLat /= 2.0;
		}
		return minLat;
	}

	private static double initMinLng64(int hashLen) {
		double minLng = MAX_LNG - MIN_LNG;
		int lng_len = hashLen * 3;
		for (int i = 0; i < lng_len; i++) {
			minLng /= 2.0;
		}
		return minLng;
	}

	public static String getGeoHashBase32(double lat, double lng, int hashLen) {
		checkLat(lat);
		checkLng(lng);
		checkLen(hashLen);
		// double minLat = initMinLat(hashLen), minLng = initMinLng(hashLen);
		byte[] hashArray = createHashArray(lat, lng, hashLen);
		StringBuilder builder = new StringBuilder(hashLen);
		for (int i = 0; i < hashLen; i++) {
			int start = i * 5;
			int index = (hashArray[start] << 4) //
					| (hashArray[start + 1] << 3) //
					| (hashArray[start + 2] << 2) //
					| (hashArray[start + 3] << 1) //
					| (hashArray[start + 4]);//
			builder.append(CHARS[index]);
		}
		return builder.toString();
	}

	/**
	 * 获取节点周围9个位置的点
	 * 
	 * @param lat
	 * @param lng
	 * @param hashLen
	 * @return
	 */
	public static List<String> listGeoHashBase32(double lat, double lng, int hashLen) {
		double minLat = initMinLat(hashLen);
		double minLng = initMinLng(hashLen);
		double leftLat = lat - minLat;
		double rightLat = lat + minLat;
		double upLng = lng - minLng;
		double downLng = lng + minLng;
		List<String> values = new ArrayList<String>(9);

		// 左侧从上到下 3个
		String leftUp = getGeoHashBase32(leftLat, upLng, hashLen);
		if (!(leftUp == null || "".equals(leftUp))) {
			values.add(leftUp);
		}

		String leftMid = getGeoHashBase32(leftLat, lng, hashLen);
		if (!(leftMid == null || "".equals(leftMid))) {
			values.add(leftMid);
		}
		String leftDown = getGeoHashBase32(leftLat, downLng, hashLen);
		if (!(leftDown == null || "".equals(leftDown))) {
			values.add(leftDown);
		}
		// 中间从上到下 3个
		String midUp = getGeoHashBase32(lat, upLng, hashLen);
		if (!(midUp == null || "".equals(midUp))) {
			values.add(midUp);
		}
		String midMid = getGeoHashBase32(lat, lng, hashLen);
		if (!(midMid == null || "".equals(midMid))) {
			values.add(midMid);
		}
		String midDown = getGeoHashBase32(lat, downLng, hashLen);
		if (!(midDown == null || "".equals(midDown))) {
			values.add(midDown);
		}
		// 右侧从上到下 3个
		String rightUp = getGeoHashBase32(rightLat, upLng, hashLen);
		if (!(rightUp == null || "".equals(rightUp))) {
			values.add(rightUp);
		}
		String rightMid = getGeoHashBase32(rightLat, lng, hashLen);
		if (!(rightMid == null || "".equals(rightMid))) {
			values.add(rightMid);
		}
		String rightDown = getGeoHashBase32(rightLat, downLng, hashLen);
		if (!(rightDown == null || "".equals(rightDown))) {
			values.add(rightDown);
		}
		return values;
	}

	private static byte[] createHashArray(double lat, double lng, int hashLen) {
		int len = hashLen * 5;
		int lat_len = len / 2, lng_len = len / 2 + len % 2;
		byte[] latbytes = getHashArray(lat, MIN_LAT, MAX_LAT, lat_len);
		byte[] lngbytes = getHashArray(lng, MIN_LNG, MAX_LNG, lng_len);
		byte[] rt = new byte[len];
		for (int i = 0; i < lat_len; ++i) {
			rt[i * 2 + 1] = latbytes[i];
		}
		for (int i = 0; i < lng_len; ++i) {
			rt[i * 2] = lngbytes[i];
		}
		return rt;
	}

	private static byte[] getHashArray(double value, double min, double max, int len) {
		byte[] buf = new byte[len];
		for (int i = 0; i < len; i++) {
			double mid = (min + max) / 2.0;
			if (value > mid) {
				buf[i] = 1;
				min = mid;
			} else {
				buf[i] = 0;
				max = mid;
			}
		}
		return buf;
	}

	private static double initMinLat(int hashLen) {
		double minLat = MAX_LAT - MIN_LAT;
		int len = hashLen * 5;
		int lat_len = len / 2;
		for (int i = 0; i < lat_len; i++) {
			minLat /= 2.0;
		}
		return minLat;
	}

	private static double initMinLng(int hashLen) {
		double minLng = MAX_LNG - MIN_LNG;
		int len = hashLen * 5;
		int lng_len = len / 2 + len % 2;
		for (int i = 0; i < lng_len; i++) {
			minLng /= 2.0;
		}
		return minLng;
	}

	private static void checkLat(double lat) {
		if (lat < MIN_LAT || lat > MAX_LAT) {
			throw new RuntimeException("lat must between -90 and 90");
		}
	}

	private static void checkLng(double lng) {
		if (lng < MIN_LNG || lng > MAX_LNG) {
			throw new RuntimeException("lng must between -180 and 180");
		}
	}

	private static void checkLen(int len) {
		if (len < 1) {
			throw new RuntimeException("len must greater than 1");
		}
	}
}
