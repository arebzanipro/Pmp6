package fr.pmp6.message.pmp6;

import java.util.Date;

/**
 * Created by amir on 15/09/2017.
 */

public class ChatMessage {
    private String messageText;
    private String messageUSer;
    private long messageTime;
    public ChatMessage (String messageText, String messageUSer){
        this.messageText = messageText;
        this.messageUSer = messageUSer;

        this.messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUSer() {
        return messageUSer;
    }

    public void setMessageUSer(String messageUSer) {
        this.messageUSer = messageUSer;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
