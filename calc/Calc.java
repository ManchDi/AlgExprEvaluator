package calc;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Calc {


	
	    private String gsFormula; //input
	    private Map<String, BigDecimal> varspace;

	    public static BigDecimal eval(String expr) {
	        return new Calc(expr, emptyMap()).expSum();
	    }

	    public static BigDecimal eval(String expr, Map<String, ? extends Number> varspace) {
	        return new Calc(expr, varspace).expSum();
	    }

	    private Calc(String formula, Map<String, ? extends Number> vars) {//mapping the chars to keys, something like a dict. Used for function handling.
	        Objects.nonNull(vars);
	        Objects.nonNull(formula);

	        varspace = vars.entrySet().stream()
	                .collect(toMap(Map.Entry::getKey, item ->new BigDecimal(item.getValue().toString())));

	        this.gsFormula = formula.replace(" ", "");

	    }

	    private BigDecimal expSum() { // + and - parser. -Low priority
	        String op;
	        BigDecimal result;

	        result = expProduct();
	        while (true) {
	            op = head(gsFormula);
	            if ("+".equals(op)) {
	                gsFormula = tail(gsFormula);
	                result = result.add(expProduct());
	            } else if ("-".equals(op)) {
	                gsFormula = tail(gsFormula);
	                result = result.subtract(expProduct());
	            } else {
	                break;
	            }
	        }
	        return result;
	    }

	    private BigDecimal expNumber() { // * and / -high priority
	        BigDecimal result = BigDecimal.ZERO;
	        String number = "";
	        String identifier = "";
	        BigDecimal functionArgument = BigDecimal.ZERO;

	        String c = head(gsFormula);
	        if (c.matches("[0-9\\.]")) {
	            while (true) {
	                c = head(gsFormula);
	                if (c.matches("[0-9\\.]")) {
	                    number += head(gsFormula);
	                    gsFormula = tail(gsFormula);
	                } else {
	                    result = new BigDecimal(number);
	                    break;
	                }
	            }
	        } else if (c.equals("(") || c.equals("{")) { //stepping into a bracket
	            char bracketType = c.charAt(0);
	            gsFormula = tail(gsFormula);
	            result = expSum();
	            if (gsFormula.isEmpty() || (bracketType == '(' && !")".equals(head(gsFormula))) || (bracketType == '{' && !"}".equals(head(gsFormula)))) {
	                throw new IllegalArgumentException("Unmatched  " + bracketType + " parenthesis.");
	            } else {
	                gsFormula = tail(gsFormula);
	            }
	        
	        } else if (c.matches("[a-zA-Z]")) {//still parsing data inside the brackets
	            while (true) {
	                c = head(gsFormula);
	                if (c.matches("[a-zA-Z]")) {
	                    identifier += head(gsFormula);
	                    gsFormula = tail(gsFormula);
	                } else if (c.equals("(")) { //any following opening brackets open here. 
	                    gsFormula = tail(gsFormula);
	                    functionArgument = expSum();
	                    if (!")".equals(head(gsFormula))) {
	                        throw new IllegalArgumentException("syntax error");
	                    } else {
	                        gsFormula = tail(gsFormula);
	                        result = evaluateFunction(identifier, functionArgument);
	                        break;
	                    }
	                } else {
	                    result = varspace.get(identifier);
	                    if (result == null) {
	                        throw new IllegalArgumentException("Undefined variable '" + identifier + "'");
	                    } else {
	                        break;
	                    }
	                }
	            }
	        }
	        return result;
	    }

	    private BigDecimal evaluateFunction(String functionName, BigDecimal functionArgument) {
	        if ("sqrt".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.sqrt(functionArgument.doubleValue()));
	        } else if ("sin".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.sin(functionArgument.doubleValue()));
	        } else if ("cos".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.cos(functionArgument.doubleValue()));
	        } else if ("tan".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.tan(functionArgument.doubleValue()));
	        } else if ("asin".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.asin(functionArgument.doubleValue()));
	        } else if ("acos".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.acos(functionArgument.doubleValue()));
	        } else if ("atan".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.atan(functionArgument.doubleValue()));
	        } else if ("sgn".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(functionArgument.signum());
	        } else if ("abs".equalsIgnoreCase(functionName)) {
	            return functionArgument.abs();
	        }  else if ("ln".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.log(functionArgument.doubleValue()));
	        }else if ("log".equalsIgnoreCase(functionName)) {
	            return new BigDecimal(Math.log10(functionArgument.doubleValue()));
	        }
	           else {
	            throw new IllegalArgumentException("Undefined function '" + functionName + "'");
	        }
	    }

	    private BigDecimal expProduct() {
	        String op;
	        BigDecimal result;
	        BigDecimal num = BigDecimal.ZERO;

	        result = expNumber();
	        while (true) {
	            op = head(gsFormula);
	            if ("*".equals(op)) {
	                gsFormula = tail(gsFormula);
	                result = result.multiply(expNumber());
	            } else if ("/".equals(op)) {
	                gsFormula = tail(gsFormula);
	                try {
	                    num = expNumber();
	                    result = result.divide(num);
	                } catch (ArithmeticException e) {
	                    if (e.getMessage().startsWith("Non-terminating")) {
	                        result = result.divide(num, MathContext.DECIMAL128);
	                    } else {
	                        throw e;
	                    }
	                }

	            } else if ("^".equals(op)) {  // Handle exponentiation
	                gsFormula = tail(gsFormula);
	                num = expNumber();
	                result = result.pow(num.intValue(), MathContext.DECIMAL128);
	            } else {
	                break;
	            }
	        }
	        return result;
	    }

	    private String head(String s) {
	        return (s != null && s.length() > 0) ? s.substring(0, 1) : "";
	    }

	    private String tail(String s) {
	        return (s != null && s.length() > 1) ? s.substring(1) : "";
	    }
	}


