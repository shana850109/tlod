package edu.fgu.dclab;

import java.sql.Time;

public class LoginMessage extends AbstractMessage {
    public final String ID;
    public final String PASSWORD;
   // public final String TIME;

    public LoginMessage(String id, String password/*,String time*/) {
        this.ID = id;
        this.PASSWORD = password;
       // this.TIME = time;
    }

    public int getType() {
        return Message.LOGIN;
    }
}
