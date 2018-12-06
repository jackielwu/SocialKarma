package cs407.socialkarmaapp.Models;

import cs407.socialkarmaapp.User;
import java.io.Serializable;

public class Chat implements Serializable {
    private String chatId;
    private long lastTimestamp;
    private String lastMessage;
    private boolean readReceipt;
    private String lastSentUser;
    private String partnerId;
    private User partner;

    public Chat() {
    }

    public String getChatId() {
        return chatId;
    }
    public long getLastTimestamp() {
        return lastTimestamp;
    }
    public String getLastMessage() {
        return lastMessage;
    }
    public boolean isReadReceipt() {
        return readReceipt;
    }
    public String getLastSentUser() {
        return lastSentUser;
    }
    public String getPartnerId() {
        return partnerId;
    }
    public User getPartner() {
        return partner;
    }

    public void setReadReceipt(boolean readReceipt) {
        this.readReceipt = readReceipt;
    }
    public void setLastSentUser(String partnerId) {
        this.partnerId = partnerId;
    }
    public void setPartner(User partner) {
        this.partner = partner;
    }
}
