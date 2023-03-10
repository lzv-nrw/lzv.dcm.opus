package opus;

/**
 *  Two methods that split a text and remove the front or back part.
 * 
 * @author ogan
 *
 */
public class StringCutter {
	
	// The method cut a given String returns a partial string starting from a certain position of a separator sign.
	public static String cutFront(String text, String sign, int number) {
        for (int i = 0; i < number; i++) {
            text = text.substring(text.indexOf(sign) + 1, text.length());
        }
        return text;
    }
	
	// The method cut a given String returns a partial string starting from a certain position of a separator sign.
    public static String cutBack(String text, String sign, int number) {
        for (int i = 0; i < number; i++) {
        	text = text.substring(0, text.lastIndexOf(sign));
        }
        return text;
    }
}
