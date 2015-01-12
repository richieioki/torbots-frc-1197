package sim;

import events.CycleType;
import game.gameConstants;

public class RobotSimulator {
    public static void main(String args[]) {
        
        Simulator sim = new Simulator(gameConstants.numberOfRuns);
        
        sim.run(); //should run one instance for now.
        
        int type1 = 0, type2 = 0, type3 = 0, type4 = 0, torbots1 = 0, torbots2 = 0;
        int t1score = 0, t2score = 0, t3score = 0, t4score = 0, tor1score = 0, tor2score = 0;
        
        int t1trips = 0, t2trips = 0, t3trips = 0, t4trips = 0, tor1trips = 0, tor2trips = 0;
        
        int eliteTrips = 0, midTrips = 0, lowTrips = 0, torbotTrips = 0;
        
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
                        eliteTrips += data.trips[i];
                        break;
                    case MID:
                        mids++;
                        totMid += data.total[i];
                        midTrips += data.trips[i];
                        break;
                    case LOW:
                        lows++;
                        totLow += data.total[i];
                        lowTrips += data.trips[i];
                        break;
                    case TORBOT:
                        torbots++;
                        torbotTot += data.total[i];
                        torbotTrips += data.trips[i];
                        break;
                }
                
                switch(data.cycles[i].m_type) {
                    case TYPE1:
                        t1trips += data.trips[i];
                        type1++;
                        t1score+=data.tele[i];
                        break;
                    case TYPE2:
                        type2++;
                        t2trips += data.trips[i];
                        t2score+=data.tele[i];
                        break;
                    case TYPE3:
                        type3++;
                        t3trips += data.trips[i];
                        t3score+=data.tele[i];
                        break;
                    case TYPE4:
                        type4++;
                        t4trips += data.trips[i];
                        t4score+=data.tele[i];
                        break;
                    case TORBOT1:
                        torbots1++;
                        tor1trips += data.trips[i];
                        tor1score+=data.tele[i];
                        break;
                    case TORBOT2:
                        torbots2++;
                        tor2trips += data.trips[i];
                        tor2score+=data.tele[i];
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
        int avgRobot = (totalRed + totalBlue)/ (gameConstants.numberOfRuns * 6); //total points/number of runs * 6 robots per match
        System.out.println("Average Robot Score " + avgRobot);
        
        System.out.println("\n\n\n\n");
        System.out.println("Type1 = " + type1);
        System.out.println("Type2 = " + type2);
        System.out.println("Type3 = " + type3);
        System.out.println("Type4 = " + type4);
        System.out.println("Torbots1 = " + torbots1);
        System.out.println("Torbots2 = " + torbots2);
        System.out.println("TYPE 1 total points = " + t1score + " average score : " + t1score/type1); 
        System.out.println("TYPE 2 total points = " + t2score + " average score : " + t2score/type2); 
        System.out.println("TYPE 3 total points = " + t3score + " average score : " + t3score/type3); 
        System.out.println("TYPE 4 total points = " + t4score + " average score : " + t4score/type4); 
        System.out.println("TORBOTS1 total points = " + tor1score + " average score : " + tor1score/torbots1); 
        System.out.println("TORBOTS2 total points = " + tor2score + " average score : " + tor2score/torbots2); 
        
        System.out.println("\n\n Trip data");
        float averageTrip = eliteTrips/elite;
        System.out.println("Elite trips = " + averageTrip);
        averageTrip = midTrips/mids;
        System.out.println("Mid trips = " + averageTrip);
        averageTrip = lowTrips/lows;
        System.out.println("Low trips = " + averageTrip);
        averageTrip = torbotTrips/torbots;
        System.out.println("Torbot trips = " + averageTrip);
        
        System.out.println("\n\n Trip data by cycle");
        averageTrip = t1trips/type1;
        System.out.println("Type1 trips = " + averageTrip);
        averageTrip = t2trips/type2;
        System.out.println("Type2 trips = " + averageTrip);
        averageTrip = t3trips/type3;
        System.out.println("Type3 trips = " + averageTrip);
        averageTrip = t4trips/type4;
        System.out.println("Type1 trips = " + averageTrip);
        averageTrip = tor1trips/torbots1;
        System.out.println("Type1 trips = " + averageTrip);
        averageTrip = tor2trips/torbots2;
        System.out.println("Type1 trips = " + averageTrip);
        
    }
}
