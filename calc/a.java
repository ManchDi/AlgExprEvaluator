package calc;
import java.util.Scanner;
import calc.Calc;
public class a {
	static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
        if (args.length == -1) {
            System.err.println("Syntax: java -jar <jarname.jar> <expression to evaluate>");
        } else {
        	boolean on=true;
            while (on) {
                String inp = getInp();
                if ("exit".equalsIgnoreCase(inp)) {
                	System.out.println("Thank you for using our calculator, bye!");
                    on = false;
                } else {
                    try {
                    	checkBrackets(inp);
                        System.out.println(Calc.eval(inp));
                    } catch (IllegalArgumentException e) {
                        // Handle the exception and continue the loop
                        System.err.println(e.getMessage());
                    }
                    catch (ArithmeticException e) {
                        // Handle other exceptions and continue the loop
                        System.err.println(e.getMessage());
                    }
                }
            }
        
    }}
	
	public static String getInp() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a string: ");
   	 	String userInput = scanner.nextLine();
		return userInput;
	}
	public static String checkBrackets(String userInput) {
		String orgInp=userInput;
   	 	int openBr = 0, closBr = 0, crlBro=0, crlBrc=0;
   	 	for (char c : orgInp.toCharArray()) {
   	 		if (c == '(') {
   	 		openBr++;
         } else if (c == ')') {
        	 closBr++;
         }
         	else if (c == '{') {
   	 		crlBro++;
         } else if (c == '}') {
        	 crlBrc++;
         }
	}
   	 if(!(openBr-closBr==0)) {
    	 	throw new IllegalArgumentException("unmatched parenthesis");
    	 	}
   	 return orgInp;
	}
}
