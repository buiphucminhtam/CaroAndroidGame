package android.dolsoft.Server;

import android.dolsoft.Model.People;
import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tam on 9/18/2017.
 */

public class EmitEventToServer{
    private final String TAG = "EmitEventToServer";
    private Socket mSocket;

    public EmitEventToServer(Socket socket) {
        super();
        mSocket = socket;
    }

    public void emitJoin(String name) {
        if(mSocket==null) return;
        mSocket.emit(ServerRealtimeController.EVENT_JOIN,name);
    }

    public void emitGetListPeople() {
        if(mSocket==null) return;
        mSocket.emit(ServerRealtimeController.EVENT_UPDATE_LIST);
    }

    public void emiteInvite(People p1, People p2) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("flag", ServerRealtimeController.FLAG_INVITED);
            jsonObject.put("p1", p1.toJSON());
            jsonObject.put("p2", p2.toJSON());

            Log.d(TAG, jsonObject.toString());
            mSocket.emit(ServerRealtimeController.EVENT_MAIN, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emitAccept(People p1, People p2) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("flag", ServerRealtimeController.FLAG_ACCEPT);
            jsonObject.put("p1", p1.toJSON());
            jsonObject.put("p2", p2.toJSON());

            Log.d(TAG, jsonObject.toString());
            mSocket.emit(ServerRealtimeController.EVENT_MAIN, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emitDecline(People p1) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("flag", ServerRealtimeController.FLAG_DECLINE);
            jsonObject.put("p1", p1.toJSON());

            Log.d(TAG, jsonObject.toString());
            mSocket.emit(ServerRealtimeController.EVENT_MAIN, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emitMove(People p1, int x, int y) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("flag", ServerRealtimeController.FLAG_MOVE);
            jsonObject.put("p1", p1.toJSON());
            jsonObject.put("x", x);
            jsonObject.put("y", y);

            Log.d(TAG, jsonObject.toString());
            mSocket.emit(ServerRealtimeController.EVENT_MAIN, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emitReady(People p1) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("flag", ServerRealtimeController.FLAG_READY);
            jsonObject.put("p1", p1.toJSON());

            Log.d(TAG, jsonObject.toString());
            mSocket.emit(ServerRealtimeController.EVENT_MAIN, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emitQuit(People p1) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("flag", ServerRealtimeController.FLAG_MOVE);
            jsonObject.put("p1", p1.toJSON());

            Log.d(TAG, jsonObject.toString());
            mSocket.emit(ServerRealtimeController.EVENT_MAIN, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if(mSocket==null) return;
        mSocket.disconnect();
    }

}