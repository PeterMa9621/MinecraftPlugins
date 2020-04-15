package levelSystem.model;

import java.util.ArrayList;
import java.util.List;

public class ExpFormula {
    private List<Double> coefficients;
    private List<Double> powers;
    public ExpFormula(List<Double> coefficients, List<Double> powers) {
        this.coefficients = coefficients;
        this.powers = powers;
    }
    // y = 10*x^2 + 10*x^1.5 + 80*x
    public Integer getExp(int level) {
        double exp = 0;
        for(int i=0; i<this.coefficients.size(); i++) {
            double coefficient = this.coefficients.get(i);
            double power = this.powers.get(i);
            exp += coefficient * Math.pow(level,power);
        }
        return (int) Math.round(exp);
    }
}
