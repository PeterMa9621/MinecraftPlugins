package levelSystem.manager;

import levelSystem.model.ExpFormula;

public class ExpManager {
    private static ExpFormula expFormula;

    public static void setExpFormula(ExpFormula expFormula) {
        ExpManager.expFormula = expFormula;
    }

    public static Integer getExp(int level) {
        if(expFormula==null)
            throw new NullPointerException("ExpFormula is null");
        return expFormula.getExp(level);
    }
}
