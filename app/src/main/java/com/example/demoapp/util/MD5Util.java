package com.example.demoapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	/**
	 * 16进制字符�?
	 */
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

	/**
	 * 指定算法为MD5的MessageDigest
	 */
	private static MessageDigest messageDigest = null;

	/**
	 * 初始化MessageDigest的加密算法为MD5
	 */
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * MD5加密字符�?
	 * 
	 * @param 目标字符
	 *            �?
	 * @return 加密后的字符�?
	 */
	public static String getMD5String(String str) {
		return getMD5String(str.getBytes());
	}

	/**
	 * MD5加密以byte数组表示的字符串
	 * 
	 * @param 目标byte数组
	 * @return 加密后的字符�?
	 */
	public static String getMD5String(byte[] bytes) {
		messageDigest.update(bytes);
		return bytesToHex(messageDigest.digest());
	}

	/**
	 * 校验密码与器MD5是否�?��
	 * 
	 * @param pwd
	 * @param md5
	 * @return
	 */
	public static boolean checkPassword(String pwd, String md5) {
		return getMD5String(pwd).equalsIgnoreCase(md5);
	}

	/**
	 * 校验密码与器MD5是否�?��
	 * 
	 * @param pwd
	 * @param md5
	 * @return
	 */
	public static boolean checkPassword(char[] pwd, String md5) {
		return checkPassword(new String(pwd), md5);
	}

	/**
	 * 将字节数组转换成16进制字符�?
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte bytes[]) {
		return bytesToHex(bytes, 0, bytes.length);
	}

	/**
	 * 将字节数组中指定区间的子数组转换�?6进制字符�?
	 * 
	 * @param bytes
	 * @param start
	 * @param end
	 * @return
	 */
	public static String bytesToHex(byte bytes[], int start, int end) {
		StringBuilder sbBuilder = new StringBuilder();
		for (int i = start; i < start + end; i++) {
			sbBuilder.append(bytesToHex(bytes[i]));
		}
		return sbBuilder.toString();
	}

	/**
	 * 将单个字节转换成16进制字符�?
	 * 
	 * @param bt
	 * @return
	 */
	public static String bytesToHex(byte bt) {
		return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf];
	}

	/**
	 * MD5 32位加�?
	 * 
	 * @param strs
	 * @return
	 * @throws Exception
	 */
	public static String getMD5Entry(String strs) throws Exception {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(strs.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
			// System.out.println("result32: " + buf.toString());// 32位的加密
			// System.out.println("result16: " + buf.toString().substring(8,
			// 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;

	}

	public static void main(String[] a) {
		try {
			System.out.println(MD5Util.convertSHA1(new MD5Util()
					.getMD5Entry("123jjj")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 哈希加密
	 * 
	 * @param plainText
	 * @return
	 */
	public static String convertSHA1(String plainText) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
			// System.out.println("result32: " + buf.toString());// 32位的加密
			// System.out.println("result16: " + buf.toString().substring(8,
			// 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;
	}
}
