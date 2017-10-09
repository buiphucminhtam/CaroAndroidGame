package android.dolsoft.Pattern;

import android.dolsoft.Model.People;
import android.dolsoft.Server.EmitEventToServer;
import android.dolsoft.Server.ServerRealtimeController;

import java.util.List;

/**
 * Created by Tam on 9/18/2017.
 */

public class PatternServer {
    private ServerRealtimeController serverRealtimeController;
    private EmitEventToServer emitEventToServer;
    private UIListenerFromServer uiListenerFromServer;

    public PatternServer() {
        super();

        serverRealtimeController = new ServerRealtimeController();
        emitEventToServer = new EmitEventToServer(serverRealtimeController.getmSocket());


        serverRealtimeController.setOnEventListener(new ServerRealtimeController.onEventListener() {
            @Override
            public void updateListPeople(List<People> listPeople) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateListPeople(listPeople);
            }

            @Override
            public void infoJoinedServer(People myInfo) {
                if(uiListenerFromServer==null) return;
                if(myInfo == null) return;
                uiListenerFromServer.updateUIInfo(myInfo);
            }

            @Override
            public void invited(People myInfo, People thatInfo) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateUIInvited(thatInfo);
            }

            @Override
            public void accepted(People thatInfo) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateUIAccepted(thatInfo);
            }

            @Override
            public void decline(People people) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateUIDeclined(people);
            }

            @Override
            public void ready(People thatInfo) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateUIReady(thatInfo);
            }

            @Override
            public void move(People thatInfo, int x,int y) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateMattrix(thatInfo,x,y);
            }

            @Override
            public void quit(People thatInfo) {
                if(uiListenerFromServer==null) return;
                uiListenerFromServer.updateUIQuit(thatInfo);
            }
        });
    }

    public void sendJoin(String name) {
        if(emitEventToServer == null) return;

        emitEventToServer.emitJoin(name);
    }

    public void sendGetListPeople() {
        if(emitEventToServer==null) return;

        emitEventToServer.emitGetListPeople();
    }

    public void sendInvite(People p1, People p2) {
        if(emitEventToServer==null) return;
        if(p1==null) return;
        if(p2==null) return;

        emitEventToServer.emiteInvite(p1,p2);
    }

    public void sendAccept(People p1, People p2) {
        if(emitEventToServer==null) return;
        if(p1==null) return;
        if(p2==null) return;

        emitEventToServer.emitAccept(p1,p2);
    }

    public void sendDecline(People p1) {
        if(emitEventToServer==null) return;
        if(p1==null) return;

        emitEventToServer.emitDecline(p1);
    }

    public void sendReady(People p1) {
        if(emitEventToServer==null) return;
        if(p1==null) return;

        emitEventToServer.emitReady(p1);
    }

    public void sendMove(People p1, int x, int y) {
        if(emitEventToServer==null) return;
        if(p1==null) return;

        emitEventToServer.emitMove(p1,x,y);
    }

    public void sendDisconnect() {
        if(emitEventToServer==null) return;
        emitEventToServer.disconnect();
    }

    public void setUiListenerFromServer(UIListenerFromServer uiListenerFromServer) {
        this.uiListenerFromServer = uiListenerFromServer;
    }

    public interface UIListenerFromServer{
        void updateListPeople(List<People> listPeople);
        void updateUIInfo(People info);
        void updateUIInvited(People thatInfo);
        void updateUIAccepted(People thatInfo);
        void updateUIDeclined(People thatInfo);
        void updateUIReady(People thatInfo);
        void updateMattrix(People thatInfo, int x, int y);
        void updateUIQuit(People thatInfo);
    }
}
