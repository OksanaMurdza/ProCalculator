package pro_calculator;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Calculator implements ExpressionCalculator{

    public boolean isExpressionValid(String inpStr) {
        //Check length
        if (inpStr.length() <= 0 ){
            return false;
        }

        //Check is ok bracket number
        if (!bracket(inpStr)){
            return false;
        }

        ArrayList<Character> elements = check_str(inpStr);
        StringBuilder str = new StringBuilder();
        for (Character c : elements) {
            str.append(c.toString());
        }

        String digit = "[0-9]+";
        final String dot = "\\.";
        final String literal = "(" + digit + "|" + digit + dot + digit + ")";
        final String unar_opr = "[+-]";
        final String binar_opr = "[*/+-]";
        final String bracket = "[()]";
        final String math_literal = "(" + unar_opr + literal + "|" + literal + ")";
        final String math_move = "(" + literal + "|" + bracket + ")";
        final String math_expr = math_literal + "|(" + math_literal + "(" + binar_opr + math_move + ")+)";

        Pattern compile = Pattern.compile(math_expr);
        Function<String, Boolean> literalChecker = (input) -> compile.matcher(input).matches();

        return literalChecker.apply(str.toString());
    }

    private boolean bracket(String inpStr){
        int i = 0;
        int num = 0;
        while (i<inpStr.length()){
            if (inpStr.charAt(i) == '(') {num++;}
            if (inpStr.charAt(i) == ')') {
                if (num > 0) {
                    num--;
                } else {
                    return false;
                }
            }
            i++;
        }
        return num == 0;
    }

    private ArrayList<Character> check_str(String inpStr){
        ArrayList<Character> str = new ArrayList();
        int pos_inpStr = 0;
        int pos_newStr = 0;
        while (pos_inpStr<inpStr.length()){
            if (pos_inpStr == 0 && inpStr.charAt(pos_inpStr) == '('){
                pos_inpStr++;
            }
            if (pos_inpStr == (inpStr.length() - 1) && inpStr.charAt(pos_inpStr) == ')'){
                return str;
            }
            if (inpStr.charAt(pos_inpStr) == '(' || inpStr.charAt(pos_inpStr) == ')') {
                if ((str.get(pos_newStr - 1) == '*' || str.get(pos_newStr - 1) == '/' || str.get(pos_newStr - 1) == '+' ||
                        str.get(pos_newStr - 1) == '-' ) && (inpStr.charAt(pos_inpStr+1) == '*' || inpStr.charAt(pos_inpStr+1) == '/' || inpStr.charAt(pos_inpStr+1) == '+' ||
                            inpStr.charAt(pos_inpStr+1) == '-' )) {
                    str.add(inpStr.charAt(pos_inpStr));
                    pos_inpStr++;
                    pos_newStr++;
                } else {
                    pos_inpStr++;
                }
            } else {
                str.add(inpStr.charAt(pos_inpStr));
                pos_inpStr++;
                pos_newStr++;
            }
        }
        return str;
    }

    private int findBarder(String inpStr, int positon) {
        int tmpPosition = positon;
        tmpPosition++;
        int num = 0;

        while (tmpPosition < inpStr.length()) {
            if (inpStr.charAt(tmpPosition) == ')') {
                if (inpStr.charAt(tmpPosition) == ')' && num == 0) {
                    break;
                }
                num--;
            }
            if (inpStr.charAt(tmpPosition) == '(') {
                num++;
            }
            tmpPosition++;
        }
        return tmpPosition;
    }

    private int findDigit(String inpStr, int position) {
        while (position < inpStr.length()) {
            if (Character.isDigit(inpStr.charAt(position)) || inpStr.charAt(position) == '.') {
                position++;
            } else {
                break;
            }
        }
        return position;
    }

    @Override
    public double calculateExpression(String inpStr)  throws IllegalArgumentException {

        if (!isExpressionValid(inpStr)) {
            throw new IllegalArgumentException(inpStr);
        }

        ArrayList<Character> operators = new ArrayList();
        ArrayList<Double> elements = new ArrayList();

        int elements_lenght = 0;
        double result;
        boolean unar_opr = false;
        int position = 0;

        while (position < inpStr.length()) {
            int tmpPosition;

            if (inpStr.charAt(0) == '+' || inpStr.charAt(0) == '-') {
                unar_opr = true;
            }
            if (inpStr.charAt(position) == '(') {
                tmpPosition = findBarder(inpStr, position);
                elements.add(calculateExpression(inpStr.substring(position + 1, tmpPosition)));
                position = tmpPosition;
                position++;
                elements_lenght++;
                if (position == inpStr.length()){
                    break;
                }
            } else {
                if (Character.isDigit(inpStr.charAt(position))) {
                    tmpPosition = findDigit(inpStr, position);
                    elements.add(Double.parseDouble(inpStr.substring(position, tmpPosition)));
                    position = tmpPosition;
                    elements_lenght++;
                    if (position == inpStr.length()){
                        break;
                    }
                } else {
                    if (inpStr.charAt(position) == '*' || inpStr.charAt(position) == '+' ||
                            inpStr.charAt(position) == '/' || inpStr.charAt(position) == '-') {
                        operators.add(inpStr.charAt(position));
                        position++;
                    }
                    if (position == inpStr.length()){
                        break;
                    }
                }
            }
        }

        double tmp_res = 0;
        int i;
        if (unar_opr) {
            if (operators.get(0) == Character.valueOf('+')) {
                operators.remove(0);
            } else {
                tmp_res = -elements.get(0);
                //elements.remove(0);
                elements.set(0, tmp_res);
                operators.remove(0);
            }
        }
        if (elements_lenght == 1){
            tmp_res = elements.get(0);
        }
        for ( i = 0; i < elements_lenght-1; i++) {
            if (operators.get(i) == Character.valueOf('-')) {
                tmp_res = -elements.get(i + 1);
                //operators.remove(i);
                operators.set(i, '+');
                //elements.remove(i + 1);
                elements.set(i + 1, tmp_res);
            }
        }
        i=0;
        while ( i < elements_lenght-1) {
            if (operators.get(i) == Character.valueOf('*') || operators.get(i) == Character.valueOf('/')) {
                if (operators.get(i) == Character.valueOf('*')) {
                    tmp_res = elements.get(i) * elements.get(i + 1);
                } else {
                    tmp_res = elements.get(i) / elements.get(i + 1);
                }
                elements.remove(i + 1);
                //elements.remove(i);
                elements.set(i, tmp_res);
                operators.remove(i);
                elements_lenght--;
            } else {
                i++;
            }
        }
        i=0;
        while ( i < elements_lenght-1) {
            tmp_res = elements.get(i) + elements.get(i + 1);
            elements.remove(i + 1);
//            elements.remove(i);
            elements.set(i, tmp_res);
            operators.remove(i);
            elements_lenght--;
        }

        /*if ( i == 1 && elements_lenght == 1 ){
            if (operators.get(i) == Character.valueOf('-')) {
                result = elements.get(i) - elements.get(i + 1);
            }
            if (operators.get(i) == Character.valueOf('+')) {
                result = elements.get(i) + elements.get(i + 1);
            }
            if (operators.get(i) == Character.valueOf('*')) {
                result = elements.get(i) * elements.get(i + 1);
            }
            if (operators.get(i) == Character.valueOf('/')) {
                result = elements.get(i) / elements.get(i + 1);
            }
        }*/
        
        result = tmp_res;


        //write correct result
        //write test to project
        return result;
    }

}
