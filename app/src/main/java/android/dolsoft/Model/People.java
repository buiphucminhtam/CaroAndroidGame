package android.dolsoft.Model;

import com.google.gson.Gson;

/**
 * Created by Tam on 9/15/2017.
 */

public class People {
    private String name,id,roomID,status,ready;

    public People() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReady() {
        return ready;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
