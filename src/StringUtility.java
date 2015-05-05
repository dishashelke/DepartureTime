
public class StringUtility {

	//minimum edit distance
	public int minDistance(String stopFromGoogle, String stopFrom511) {
		int len1 = stopFromGoogle.length();
		int len2 = stopFrom511.length();
		
		//preprocessing strings
		stopFromGoogle = stopFromGoogle.toLowerCase();
		stopFrom511 = stopFrom511.toLowerCase();
		stopFromGoogle = stopFromGoogle.replaceAll("&", "and");
		stopFrom511 = stopFrom511.replaceAll("&", "and");
		
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = stopFromGoogle.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = stopFrom511.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
}
