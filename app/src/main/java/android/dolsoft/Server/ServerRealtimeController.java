package android.dolsoft.Server;
import android.dolsoft.Model.People;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tam on 9/15/2017.
 */

public class ServerRealtimeController {
    public static final String EVENT_UPDATE_LIST = "update-peoples";
    public static final String EVENT_GET_INFO = "info";
    public static final String EVENT_MAIN = "event";
    public static final String EVENT_JOIN = "join";

    public static final String FLAG_INVITED = "invite";
    public static final String FLAG_ACCEPT = "accept";
    public static final String FLAG_DECLINE = "decline";
    public static final String FLAG_MOVE = "move";
    public static final String FLAG_READY = "ready";
    public static final String FLAG_QUIT = "quit";

    private onEventListener onEventListener;


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.5.38:3000");
        } catch (URISyntaxException e) {}
    }

    public ServerRealtimeController() {
        super();

        mSocket.on(EVENT_UPDATE_LIST, onUpdateListPeople);
        mSocket.on(EVENT_GET_INFO, onInfo);
        mSocket.on(EVENT_MAIN, onEvent);
        mSocket.connect();
    }

    public void setOnEventListener(ServerRealtimeController.onEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public Socket getmSocket() {
        return mSocket;
    }

    private Emitter.Listener onUpdateListPeople = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onEventListener == null) return;

            JSONObject object = (JSONObject) args[0];

            List<People> listPeople = new ArrayList<People>();

            try {
                listPeople.addAll((Collection<? extends People>) new Gson().fromJson(object.getString("peoples"), new TypeToken<List<People>>(){}.getType()));
                onEventListener.updateListPeople(listPeople);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onInfo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onEventListener == null) return;

            JSONObject object = (JSONObject) args[0];

            try {
                People myInfo = new Gson().fromJson(object.getString("info"),People.class);

                onEventListener.infoJoinedServer(myInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onEventListener == null) return;

        }
    };

    private Emitter.Listener onEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onEventListener == null) return;

            JSONObject object = (JSONObject) args[0];

            try {
                String flag = object.getString("flag");

                if (flag.equals(FLAG_MOVE)) {
                    onEventListener.move(new Gson().fromJson(object.getString("p1"),People.class),object.getInt("x"),object.getInt("y"));
                } else if (flag.equals(FLAG_ACCEPT)) {
                    onEventListener.accepted(new Gson().fromJson(object.getString("p1"),People.class));
                } else if (flag.equals(FLAG_READY)) {
                    onEventListener.ready(new Gson().fromJson(object.getString("p1"),People.class));
                } else if(flag.equals(FLAG_INVITED)){
                    onEventListener.invited(new Gson().fromJson(object.getString("p2"),People.class),new Gson().fromJson(object.getString("p1"),People.class));
                } else if (flag.equals(FLAG_DECLINE)) {
                    onEventListener.decline(new Gson().fromJson(object.getString("p1"),People.class));
                } else {
                    onEventListener.quit(new Gson().fromJson(object.getString("p1"),People.class));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    public interface onEventListener{
        void updateListPeople(List<People> listPeople);
        void infoJoinedServer(People myInfo);
        void invited(People myInfo, People thatInfo);
        void accepted(People thatInfo);
        void decline(People thatInfo);
        void ready(People thatInfo);
        void move(People thatInfo, int x, int y);
        void quit(People thatInfo);
    }

}
