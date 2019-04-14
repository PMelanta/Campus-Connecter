package cc.util;

public class NumberToWords {
	

	static final String[] tensNames = {"", "", "Twenty", "Thirty", "Forty",
		"Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
	static final String[] onesNames = {"", "One", "Two", "Three", "Four",
		"Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
		"Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
	
	public NumberToWords () {
    }

	public static String evaluate(String text) {
		try {
			long number = Long.parseLong(text);
			return evaluate(number);
		} catch (NumberFormatException ex) {
			return "";
		}
	}
	
	private static String evaluate(long number) {
		long temp = number;
		long crores = temp/10000000;
		temp %= 10000000;
		long lakhs = temp/100000;
		temp %= 100000;
		long thousands = temp/1000;
		temp %= 1000;
		long hundreds = temp/100;
		temp %= 100;
		
		StringBuffer result = new StringBuffer(30);
		if (crores > 0) result.append(evaluate(crores)+" Crore ");
		if (lakhs > 0) result.append(evaluate(lakhs)+" Lakh ");
		if (thousands > 0) result.append(evaluate(thousands)+" Thousand ");
		if (hundreds > 0) result.append(evaluate(hundreds)+" Hundred ");
		if (temp != 0) {
			if (number >= 100) result.append("and ");
			if (0 < temp && temp <= 19) result.append(onesNames[(int)temp]);
			else {
				long tens = temp/10;
				long ones = temp%10;
				result.append(tensNames[(int)tens]+" ");
				result.append(onesNames[(int)ones]);
			}
		//}else{
                    
                //    result.append("Zero");
                }
		return result.toString();
	}
			
}
