package edu.fgu.dclab;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

/*public class NowString {
    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
    }
}*/
public class Servant implements Runnable {
    private ObjectOutputStream out = null;
    private String source = null;

    private Socket socket = null;

    private ChatRoom room = null;
/*
    public String TIME() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String str=df.format(new Date());// new Date()为获取当前系统时间
        return str;
    }
*/
    public Servant(Socket socket, ChatRoom room) {
        this.room = room;
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream(
                this.socket.getOutputStream()
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        greet();
    }

    public void process(Message message) {



        switch (message.getType()) {
            case Message.ROOM_STATE:
                this.write(message);

                break;

            case Message.CHAT:
                this.write(message);
                break;

            case Message.LOGIN:
                if (this.source == null) {
                    this.source = ((LoginMessage) message).ID;
                    this.room.multicast(new ChatMessage(
                        "MurMur",
                        MessageFormat.format("{0} 進入了聊天室。", this.source)
                    ));
               /* if (this.source.equals("TIME?")){
                    //this.source = ((LoginMessage)message).TIME;
                    //this.room.multicast(new ChatMessage(MessageFormat.format()));
                    System.out.println(df.format(str));
                }
*/
                    this.room.multicast(new RoomMessage(
                        room.getRoomNumber(),
                        room.getNumberOfGuests()
                    ));
                }

                break;

            default:
        }
    }

    private void write(Message message) {
        try {
            this.out.writeObject(message);
            this.out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void greet() {
        String[] greetings = {
            "歡迎來到 MurMur 聊天室",
            "請問你的【暱稱】?"
        };

        for (String msg : greetings) {
            write(new ChatMessage("MurMur", msg));
        }
    }
    /*
    public void timemessage(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String str=df.format(new Date());// new Date()为获取当前系统时间
    }
    */

    @Override
    public void run() {
        Message message;
/*
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String str=df.format(new Date());// new Date()为获取当前系统时间
       // System.out.println(str);
        String T="time?";
        Message MSG = new ChatMessage(T,"");
*/
        try (
            ObjectInputStream in = new ObjectInputStream(
                this.socket.getInputStream()
            )
        ) {
            this.process((Message)in.readObject());

            while ((message = (Message) in.readObject()) != null) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String str=df.format(new Date());// new Date()为获取当前系统时间
                //System.out.println(str);
                //String T="time?";
                //ChatMessage MSG = new ChatMessage("MurMur",T);
                // Message MSG = ((ChatMessage)message).MESSAGE;


                if (((ChatMessage)message).MESSAGE.equals("time?")){
                    //write(new ChatMessage("", str));
                    write(new ChatMessage("MurMur",str));
                    //System.out.println(str);
                }
                else{ this.room.multicast(message);}

            }



            this.out.close();
        }
        catch (IOException e) {
            System.out.println("Servant: I/O Exc eption");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// Servant.java