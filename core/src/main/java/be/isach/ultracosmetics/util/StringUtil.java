package be.isach.ultracosmetics.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
	
	/**
	 * Converts all color and format codes
	 * @param string
	 * @return
	 */
	public static String convertCodes(String string){
		if(string == null)return string;
		int current = 0;
		char[] cs = string.toCharArray();
		StringBuilder sb = new StringBuilder();
		
		for(char c : cs){
			if(current <= cs.length && c == '&'){
				char tc = Character.toLowerCase(cs[current+1]);
				if(tc == '1' || tc == '2' || tc == '3' || tc == '4' || tc == '5' || tc == '6' || tc == '7' ||
						tc == '8' || tc == '9' || tc == '0' || tc == 'a' || tc == 'b' || tc == 'c' || tc == 'd' ||
						tc == 'e' || tc == 'f' || tc == 'k' || tc == 'l' || tc == 'm' || tc == 'n' || tc == 'o'){
					sb.append("§");
				}else{
					sb.append(c);
				}
			}else{
				sb.append(c);
			}
			current ++;
		}
		
		return sb.toString();
	}
	
	/**
	 * Checks whether the given string is a confirming word
	 * @param string
	 * @return
	 */
	public static boolean isConfirming(String string){
		return match(string, new String[]{
			"on",
			"true",
			"yes",
			"allow",
			"positive",
			"enable",
			"enabled",
			"confirm",
			"confirmed"
		});
	}
	/**
	 * Checks whether the given string is a rejecting word
	 * @param string
	 * @return
	 */
	public static boolean isRejecting(String string){
		return match(string, new String[]{
			"off",
			"false",
			"no",
			"deny",
			"negative",
			"disable",
			"disabled",
			"reject",
			"rejected"
		});
	}
	
	/**
	 * Counts the amount of times a certain
	 * character is part of a string
	 * @param string
	 * @return
	 */
	public static int countMatches(String string, char c){
		int n = 0;
		for(char tc : string.toCharArray()){
			if(c == tc)n++;
		}
		return n;
	}
	
	/**
	 * Checks if any of the strings from the given
	 * array matches the given string
	 * @param s
	 * @param sa
	 * @return
	 */
	public static boolean match(String s, String...sa){
		for(String st : sa){
			if(st.equalsIgnoreCase(s))return true;
		}
		return false;
	}
	public static boolean match(String s, List<String> list){
		String[] sa = new String[list.size()];
		for(int n = 0; n < sa.length; n ++){
			sa[n]=list.get(n);
		}
		return match(s, sa);
	}
	
	/**
	 * Splits the string every time the given
	 * character has been found
	 * @param string
	 * @return
	 */
	public static String[] split(String string, char c){
		if(countMatches(string, c) <= 0)return new String[]{string};
		
		String s = string + c;
		String[] sa = new String[countMatches(string, c)+1];
		StringBuilder sb = new StringBuilder();
		char[] ca = s.toCharArray();
		int n = 0, x;
		
		for(x = 0; x < ca.length; x ++){
			if(ca[x] == c){
				sa[n] = sb.toString();
				sb = new StringBuilder();
				n++;
			}else{
				sb.append(ca[x]);
			}
		}
		
		return sa;
	}
	
	/**
	 * Changes a group of words into
	 * a formatted list
	 * @param strings
	 * @param startAt
	 * @param betweenWords
	 * @return
	 */	
	public static String toFormattedList(String[] strings, int startAt, String betweenWords){
		return toFormattedList(Arrays.asList(strings), startAt, betweenWords);
	}
	public static String toFormattedList(List<String> strings, int startAt, String betweenWords){
		if(strings==null||startAt >= strings.size())return "";
		
		StringBuilder sb = new StringBuilder();
		
		for(int n = startAt; n < strings.size(); n ++){
			if(sb.length() > 0)sb.append(betweenWords);
			sb.append(strings.get(n));
		}
		
		return sb.toString();		
	}
	
	/**
	 * Changes the string if bigger then
	 * the given limit
	 * @param string
	 * @param startAt
	 * @param limit
	 * @return
	 */
	public static String limit(String string, int startAt, int limit){
		try{
			return string.substring(startAt, startAt+limit);
		}catch(Exception e){
			return string;
		}
	}
}
