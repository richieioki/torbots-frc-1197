package sim;

import events.Cycle;
import events.CycleType;
import objects.robotRank;

/**
 * Container class for data collected in the match, mostly points for now
 *
 * @author Richie Ioki
 */
class Data {

    //variables to record one match
//    public int r1Tele, r1Auto, r2Tele, r2Auto, r3Tele, r3Auto,
//            b1Tele, b1Auto, b2Tele, b2Auto, b3Tele, b3Auto;
//    public robotRank r1, r2, r3, b1, b2, b3;

    private int matchNumber;
    
    //Robot specific information
    //red is always first and blue is second
    public int[] tele = new int[6];
    public int[] auto = new int[6];
    public int[] total = new int[6];
    public Cycle[] cycles = new Cycle[6];
    public robotRank[] ranks = new robotRank[6];
    public Cycle redCycle, blueCycle;
    public int[] trips = new int[6];

    public Data(int matchNumber) {
        this.matchNumber = matchNumber;
        for(int i = 0; i < tele.length; i++) {
            tele[i] = 0;
        }
        for(int j = 0; j < auto.length; j++) {
            auto[j] = 0;
        }
        
    }

    public int getRedTotal() {
        total[0] = tele[0] + auto[0];
        total[1] = tele[1] + auto[1];
        total[2] = tele[2] + auto[2];
        
        return tele[0] + auto[0] + tele[1] + auto[1] + tele[2] + auto[2];
    }

    public int getBlueTotal() {
        total[3] = tele[3] + auto[3];
        total[4] = tele[4] + auto[4];
        total[5] = tele[5] + auto[5];
        
        return tele[3] + auto[3] + tele[4] + auto[4] + tele[5] + auto[5];
    }

    public int getMatchNumber() {
        return matchNumber;
    }
}
