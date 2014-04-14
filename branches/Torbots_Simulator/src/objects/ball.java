/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objects;

/**
 *
 * @author richi_000
 */
public class ball {
    
    private String m_name;
    private int passes;
    private String m_team;
    
    public ball(String name, String team) {
        m_name = name;
        m_team = team;
    }
    
    public void passed() {
        passes++;
    }
    
    public void reset() {
        passes = 0;
    }
    
    public int returnPassed() {
        return passes;
    }
    
    public String returnTeam() {
        return m_team;
    }
}
