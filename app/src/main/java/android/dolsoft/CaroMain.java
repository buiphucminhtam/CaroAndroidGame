package android.dolsoft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.dolsoft.Model.People;
import android.dolsoft.Pattern.PatternServer;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CaroMain extends Activity{
    private static final String TAG = "CaroMain";

	private MyViewForTwoPlayer myChess;
    private PatternServer patternServer;

    private Button btnJoin;
    private ListView listView;
    private EditText edtName;

    private People myInfo;
    private ArrayList<String> listPlayer;
    private ArrayList<People> listPeoples;
    private ArrayAdapter<String> adapter;

    //Dialog invite
    AlertDialog.Builder alertDialog;


	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        myChess = (MyViewForTwoPlayer) findViewById(R.id.myView1);
        myChess.setCaroViewListener(caroViewListener);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        listView = (ListView) findViewById(R.id.listViewPeople);

        edtName = (EditText) findViewById(R.id.edtName);

        listPeoples = new ArrayList<People>();

        listView.setAdapter(adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listPlayer = new ArrayList<String>()));

        patternServer = new PatternServer();
        patternServer.setUiListenerFromServer(uiListenerFromServer);


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtName.getText().toString().equals(""))
                  patternServer.sendJoin(edtName.getText().toString());
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                invitePlayer(i);
            }
        });
    }

    private MyViewForTwoPlayer.CaroViewListener caroViewListener = new MyViewForTwoPlayer.CaroViewListener() {
        @Override
        public void onTouchListener(int x, int y) {
            patternServer.sendMove(myInfo,x,y);
        }
    };
    private PatternServer.UIListenerFromServer uiListenerFromServer = new PatternServer.UIListenerFromServer() {
        @Override
        public void updateListPeople(List<People> listPeople) {
            Log.d(TAG, listPeople.toString());

            for (People people : listPeople) {
                listPlayer.add(people.getName());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateListPlayer();
                }
            });

        }

        @Override
        public void updateUIInfo(People info) {
            myInfo = info;
        }

        @Override
        public void updateUIInvited(People thatInfo) {
            showDialogInvite(thatInfo);
        }

        @Override
        public void updateUIAccepted(People thatInfo) {
            Toast.makeText(CaroMain.this, thatInfo.getName()+" accepted", Toast.LENGTH_SHORT).show();

            myChess.setVisibility(View.VISIBLE);
            btnJoin.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            edtName.setVisibility(View.GONE);

            patternServer.sendReady(myInfo);
        }

        @Override
        public void updateUIDeclined(People thatInfo) {
            Toast.makeText(CaroMain.this, thatInfo.getName()+" declined", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateUIReady(People thatInfo) {

        }

        @Override
        public void updateMattrix(People thatInfo, int x, int y) {
            myChess.player2Move(x,y);
        }

        @Override
        public void updateUIQuit(People thatInfo) {

        }
    };


    private void initDialogInvite(final People thatInfo) {
        alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(thatInfo.getName() + " send you invite");
        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                acceptPlayer(thatInfo);
            }
        });

        alertDialog.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                declinePlayer();
            }
        });
    }

    private void showDialogInvite(People thatInfo) {
        if(alertDialog==null) initDialogInvite(thatInfo);

        alertDialog.create().show();
    }

    private void updateListPlayer() {
        adapter.notifyDataSetChanged();
    }

    private void invitePlayer(int position) {
        if(myInfo == null) return;
        if(listPeoples.get(position) == null) return;
        patternServer.sendInvite(myInfo,listPeoples.get(position));
    }

    private void acceptPlayer(People thatInfo) {
        if(myInfo == null) return;
        patternServer.sendAccept(myInfo,thatInfo);
    }

    private void declinePlayer() {
        if(myInfo==null) return;
        patternServer.sendDecline(myInfo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        patternServer.sendDisconnect();
    }
}