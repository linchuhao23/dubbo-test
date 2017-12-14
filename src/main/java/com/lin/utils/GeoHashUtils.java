package com.lin.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static final char[] CHARS = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', //
					'8', '9', 'b', 'c', 'd', 'e', 'f', 'g', //
					'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', //
					's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private static final Map<Integer, Integer> HASHLEN_2_LATLEN = new HashMap<>();

	private static final Map<Integer, Integer> HASHLEN_2_LNGLEN = new HashMap<>();

	static {
		HASHLEN_2_LATLEN.put(1, 2);
		HASHLEN_2_LATLEN.put(2, 5);
		HASHLEN_2_LATLEN.put(3, 7);
		HASHLEN_2_LATLEN.put(4, 10);
		HASHLEN_2_LATLEN.put(5, 12);
		HASHLEN_2_LATLEN.put(6, 15);
		HASHLEN_2_LATLEN.put(7, 17);
		HASHLEN_2_LATLEN.put(8, 20);
		HASHLEN_2_LATLEN.put(9, 22);
		HASHLEN_2_LATLEN.put(10, 25);
		HASHLEN_2_LATLEN.put(11, 27);
		HASHLEN_2_LATLEN.put(12, 30);

		HASHLEN_2_LNGLEN.put(1, 3);
		HASHLEN_2_LNGLEN.put(2, 5);
		HASHLEN_2_LNGLEN.put(3, 8);
		HASHLEN_2_LNGLEN.put(4, 10);
		HASHLEN_2_LNGLEN.put(5, 13);
		HASHLEN_2_LNGLEN.put(6, 15);
		HASHLEN_2_LNGLEN.put(7, 18);
		HASHLEN_2_LNGLEN.put(8, 20);
		HASHLEN_2_LNGLEN.put(9, 23);
		HASHLEN_2_LNGLEN.put(10, 25);
		HASHLEN_2_LNGLEN.put(11, 28);
		HASHLEN_2_LNGLEN.put(12, 30);
	}

	public static void main(String[] args) {
//		System.out.println(getGeoHashBase32(39.92324, 116.3906, 8));
		listGeoHashBase32(39.92324, 116.3906, 12).forEach(e -> {
			System.out.println(e);
		});
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
		byte[] latbytes = getHashArray(lat, MIN_LAT, MAX_LAT, HASHLEN_2_LATLEN.get(hashLen));
		byte[] lngbytes = getHashArray(lng, MIN_LNG, MAX_LNG, HASHLEN_2_LNGLEN.get(hashLen));
		byte[] rt = new byte[latbytes.length + lngbytes.length];
		for (int i = 0, l = latbytes.length; i < l; ++i) {
			rt[i * 2 + 1] = latbytes[i];
		}
		for (int i = 0, l = lngbytes.length; i < l; ++i) {
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

	private static double initMinLat(int len) {
		double minLat = MAX_LAT - MIN_LAT;
		for (int i = 0, l = HASHLEN_2_LATLEN.get(len); i < l; i++) {
			minLat /= 2.0;
		}
		return minLat;
	}

	private static double initMinLng(int len) {
		double minLng = MAX_LNG - MIN_LNG;
		for (int i = 0, l = HASHLEN_2_LNGLEN.get(len); i < l; i++) {
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
		if (len < 1 || len > 12) {
			throw new RuntimeException("len must between 1 and 12");
		}
	}
}
