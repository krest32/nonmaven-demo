package com.fico.cbs.utils;

/**
 * 格式化输入工具类
 * 
 * @author logan.chen
 * @date 2018-06-13
 * @Modified 
 * 
 */
public class JsonFormatUtil {
	
	/**
	 * 打印输入到控制台
	 * 
	 * @param jsonStr
	 * @author logan.chen
	 * @Date 2018-06-13 
	 */
	public static void printJson(String jsonStr) {
		System.out.println(formatJson(jsonStr));
	}

	/**
	 * 格式化
	 * 
	 * @param jsonStr
	 * @return
	 * @author logan.chen
	 * @Date 2018-06-13 
	 * @Modified 
	 * 
	 */
	public static String formatJson(String jsonStr) {
		if (null == jsonStr || "".equals(jsonStr.trim())) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		boolean isInQuotationMarks = false;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			switch (current) {
			case '"':
				if (last != '\\'){
				    isInQuotationMarks = !isInQuotationMarks;
                }
				sb.append(current);
				break;
			case '{':
			case '[':
				sb.append(current);
				if (!isInQuotationMarks) {
					sb.append('\n');
					indent++;
					addIndentBlank(sb, indent);
				}
				break;
			case '}':
			case ']':
				if (!isInQuotationMarks) {
					sb.append('\n');
					indent--;
					addIndentBlank(sb, indent);
				}
				sb.append(current);
				break;
			case ',':
				sb.append(current);
				if (last != '\\' && !isInQuotationMarks) {
					sb.append('\n');
					addIndentBlank(sb, indent);
				}
				break;
			default:
				sb.append(current);
			}
		}

		return sb.toString();
	}

	/**
	 * 添加space
	 * 
	 * @param sb
	 * @param indent
	 * @author logan.chen
	 * @Date 2018-06-13 
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append('\t');
		}
	}
}
