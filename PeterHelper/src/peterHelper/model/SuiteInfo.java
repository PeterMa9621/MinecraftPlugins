package peterHelper.model;

public class SuiteInfo {
    private String suiteName;
    private int particleId;
    private double damage;
    private double armor;
    private double maxHealth;

    public SuiteInfo(String suiteName, int particleId, double damage, double armor, double maxHealth) {
        this.suiteName = suiteName;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public double getArmor() {
        return armor;
    }

    public double getDamage() {
        return damage;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public int getParticleId() {
        return particleId;
    }
}
