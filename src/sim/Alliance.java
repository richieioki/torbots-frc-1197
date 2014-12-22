package sim;

import objects.Robot;

public class Alliance {

    private String Team;
    private Robot one,two,three;
    
    public Alliance(String r, Robot one, Robot two, Robot three) {
        this.Team = r;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public String getTeam() {
        return Team;
    }

    public void setTeam(String Team) {
        this.Team = Team;
    }

    public Robot getOne() {
        return one;
    }

    public void setOne(Robot one) {
        this.one = one;
    }

    public Robot getTwo() {
        return two;
    }

    public void setTwo(Robot two) {
        this.two = two;
    }

    public Robot getThree() {
        return three;
    }

    public void setThree(Robot three) {
        this.three = three;
    }
    
}
