package pro_calculator;

public class CalculatorFactory {

    public static ExpressionCalculator getCalculator(){

        return new Calculator();

    }
}
