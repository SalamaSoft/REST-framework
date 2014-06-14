package com.salama.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public final class StringUtil {
	private StringUtil() {
	}

	/**
	 * Format string such like "Input params {0} could be {1} "
	 * @param stringFormat
	 * @param args
	 * @return
	 */
	public static String formatString(String stringFormat, String... args) {
		if(args.length == 0) {
			return stringFormat;
		} else {
			List<String> listArgs = new ArrayList<String>();

			for (String arg : args) {
				listArgs.add(arg);
			}
			
			StringBuilder sb = new StringBuilder(stringFormat);
			int beginIndex = 0;
			int endIndex = 0;
			int argIndex = 0;
			
			for(int index = 0; index < sb.length();) {
				beginIndex = sb.indexOf("{", index);
				if(beginIndex < 0) {
					break;
				}
				endIndex = sb.indexOf("}", beginIndex);
				if(endIndex < 0) {
					break;
				}
				
				argIndex = Integer.parseInt(sb.substring(beginIndex + 1, endIndex));
				
				sb.replace(beginIndex, endIndex + 1, listArgs.get(argIndex));
				
				System.out.println(sb.toString());
				
				index = beginIndex + listArgs.get(argIndex).length();
			}
			
			return sb.toString();
		}
	}
}
