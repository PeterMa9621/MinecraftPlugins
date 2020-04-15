package dps.rewardBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class RewardTable {
    private String dungeonName;
    private ArrayList<Reward> rewards;
    private ArrayList<Double> probs;
    private Double bonusRewardProb = 0.005;
    private int minExp;
    private int maxExp;

    public RewardTable(String dungeonName, ArrayList<Reward> rewards, int minExp, int maxExp) {
        this.dungeonName = dungeonName;
        this.rewards = rewards;
        this.minExp = minExp;
        this.maxExp = maxExp;
        convertToProbList();
    }

    private void convertToProbList() {
        probs = new ArrayList<>();
        probs.add(0d);

        int i = 1;
        for(Reward reward:rewards){
            double chance = reward.getChance();
            // If the probability of the reward is zero, then ignore it
            if(chance == 0.0)
                continue;
            double prob = chance + probs.get(i-1);
            if(prob > 1.0){
                prob = 1.0;
                probs.add(prob);
                break;
            }
            probs.add(prob);
            i++;
        }
    }

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> rewards) {
        this.rewards = rewards;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public Double getBonusRewardProb() {
        return bonusRewardProb;
    }

    public void setBonusRewardProb(Double bonusRewardProb) {
        this.bonusRewardProb = bonusRewardProb;
    }

    public Reward getRandomReward() {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        final double randomNum = random.nextDouble();
        // Here I use the size of rewards because probs has 0 in the list as default, thus probs will have one more element
        // than rewards which is not what I want
        for(int i=0; i<rewards.size(); i++){
            if(randomNum >= probs.get(i) && randomNum < probs.get(i+1)){
                return rewards.get(i);
            }
        }
        return rewards.get(0);
    }

    public int getRandomExp() {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        return random.nextInt(maxExp - minExp) + minExp;
    }
}
