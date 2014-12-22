/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim;

import game.gameConstants;

/**
 *
 * @author gaming
 */
public class RobotSimulator {
    public static void main(String args[]) {
        
        Simulator sim = new Simulator(gameConstants.numberOfRuns);
        
        sim.run(); //should run one instance for now.
        
        //print out average score of our run
        int totalRed = 0;
        int totalBlue = 0;
        int torbotAVG = 0, torbots = 0, torbotTot = 0;
        int avgLow = 0, avgMid = 0, avgElite = 0;
        int totLow = 0, totMid = 0, totElite = 0;
        int lows = 0, mids = 0, elite = 0; //number of elite, mid and lows
        for(Data data: sim.results) {
            totalBlue += data.getBlueTotal();
            totalRed += data.getRedTotal();
            
            for(int i = 0; i < data.auto.length; i++) {
                switch(data.ranks[i]) {
                    case ELITE:
                        elite++;
                        totElite += data.total[i];
                        break;
                    case MID:
                        mids++;
                        totMid += data.total[i];
                        break;
                    case LOW:
                        lows++;
                        totLow += data.total[i];
                        break;
                    case TORBOT:
                        torbots++;
                        torbotTot += data.total[i];
                        break;
                }
            }
            if(elite == 0) {
                elite++;
            }
            if(mids == 0) {
                mids++;
            } 
            if(lows == 0) {
                lows++;
            } 
            if(torbots == 0) {
                torbots++;
            }
            torbotAVG = torbotTot/torbots;
            avgLow = totLow/lows;
            avgMid = totMid/mids;
            avgElite = totElite/elite;
        }
        int avgBlue = totalBlue/gameConstants.numberOfRuns;
        int avgRed = totalRed/gameConstants.numberOfRuns;
        System.out.println("\n************Data********************");
        System.out.println("The average score of a match for red was " + avgRed + " and the blue average was " + avgBlue);
        System.out.println("The averages for the different types of robots is ");
        System.out.println("Elite : " + avgElite);
        System.out.println("Mids : " + avgMid);
        System.out.println("Lows : " + avgLow);
        System.out.println("Torbots " + torbotAVG);
        System.out.println("Torbots info Total points " + torbotTot + " number of torbots " + torbots );
    }
}
