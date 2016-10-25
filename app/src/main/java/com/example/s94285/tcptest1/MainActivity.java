package com.example.s94285.tcptest1;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusLocator;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;

public class MainActivity extends AppCompatActivity {
    //XML field
    private EditText input_IP, input_port,input_offset,input_bit, input_decimal;
    private Button button_connect,button_disconnect;
    private ToggleButton toggleButton_bit0,toggleButton_bit1,toggleButton_bit2,toggleButton_bit3;
    private ToggleButton toggleButton_bit4,toggleButton_bit5,toggleButton_bit6,toggleButton_bit7;
    private RadioButton radioButton_readMB,radioButton_writeMB;
    private Spinner spinner_dataType;
    private ListView listView_posttime;
    private ArrayAdapter spinnerAdapter_dataType,listView_adapter;
    private TextView textView_result;
    //XML field

    private ModbusMaster modbusMaster;
    private ModbusLocator myLocation;
    private IpParameters ipPhone;
    private ModbusFactory modbusFactory;
    private String IP;
    static boolean isInitted = false;
    private Object modbusValues;
    private Object[] valuesInArray;
    Handler mainHandler;
    private boolean refresh_on = false;
    private ModbusRW mbrw;

    private AlertDialog AD;
    private int refreshDelay;

    private static final String[] DATA_TYPE_SELECTIONS = {"Bit","Byte","Word","INT","DINT","REAL","LREAL"};
    private static final String[] POSTTIME_SELECTIONS = {"100 ms","200 ms","500 ms","1 second","2 seconds","5 seconds","10 seconds"};
    private static final String DEFAULT_IP = "192.168.2.155";  //TODO: use preference page to change default
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        //findViewById(R.id.content_main).requestFocus();

        IP = DEFAULT_IP;

        init();

        createAlertDialog();



        ipPhone = new IpParameters();
        modbusFactory = new ModbusFactory();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { //TODO: Focusing **********************
                AD.show();
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ipPhone.setHost((input_IP.length()!=0)?input_IP.toString():IP);
                ipPhone.setPort((input_port.length()!=0)?Integer.parseInt(input_port.toString()):502);

                refresh_on = !refresh_on;

                mainHandler = new Handler();
                mainHandler.postDelayed(refresh,refreshDelay);  //TODO: Finish this later

                Snackbar.make(view, (refresh_on)?"Refresh":"Stop", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private Runnable refresh = new Runnable() { //TODO: Correct the name right
        @Override
        public void run() {

            Thread thread = new Thread(multiThread);
            thread.start();
        }
    };

    private Runnable multiThread = new Runnable() {
        @Override
        public void run() {
            try{
                modbusMaster = modbusFactory.createTcpMaster(ipPhone,true);
                modbusMaster.setTimeout(500);
                modbusMaster.setRetries(1);
                modbusMaster.init();
                ModbusRW modbusRW = new ModbusRW(modbusMaster);

                final String result = modbusRW.mbReadINTtoInteger(0).toString();
                Log.d("Value",result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView_result.setText(result);
                    }
                });
                modbusRW.mbWriteBoolArrayToByteWord(0,readToggleButtonStatus());
            }catch (ModbusInitException e){
                Log.e("ModbusInitException","Init Failed"+e.toString());
            }catch (Exception e){
                Log.e("ModbusRW Exception",e.toString());
            }
            Log.d("Refresh","Refreshing");
            if(refresh_on)mainHandler.postDelayed(refresh,refreshDelay);
        }
    };

    private Boolean[] readToggleButtonStatus(){
        Boolean[] booleans = new Boolean[8];
        int[] ID = {R.id.toggleButton_bit0,R.id.toggleButton_bit1,R.id.toggleButton_bit2,R.id.toggleButton_bit3,R.id.toggleButton_bit4,R.id.toggleButton_bit5,R.id.toggleButton_bit6,R.id.toggleButton_bit7};
        for(int i = 0;i <8; i++){
            ToggleButton bit = (ToggleButton)findViewById(ID[i]);
            booleans[i] = bit.isChecked();
        }
        return booleans;
    }





    /***   //TODO: Clear them all when finished


    private Runnable multiThread = new Runnable() {
        @Override
        public void run() {
            modbusValues = null;
            try {
                modbusMaster = modbusFactory.createTcpMaster(ipPhone, true);
                modbusMaster.setTimeout(500);
                modbusMaster.setRetries(1);
                modbusMaster.init();
            }catch (ModbusInitException e){
                Log.d("initE",e.toString());
            }
            if(modbusMaster.isInitialized()){
                Log.d("isInit","Success");
                byte byteRead = 7;
                mbrw = new ModbusRW(modbusMaster);
                try {
                    //List <Integer> result = modbusMaster.scanForSlaveNodes();
                    //Log.d("ReadNode", result.toString());
                    ModbusLocator modbusLocator = new ModbusLocator(1,RegisterRange.HOLDING_REGISTER,0,byteRead);
                    modbusValues = modbusMaster.getValue(modbusLocator);
                    //modbusMaster.getValue(1,RegisterRange.HOLDING_REGISTER,4001,byteRead);
                    Log.d("ReadBit", modbusValues.toString());
                    Log.d("ReadWord",new ModbusRW(modbusMaster).mbReadINTtoInteger(0).toString());
                    if(modbusValues.toString().equals("true")){modbusMaster.setValue(modbusLocator,false);Log.d("TF","true");}
                    if(modbusValues.toString().equals("false")){modbusMaster.setValue(modbusLocator,true);Log.d("TF","false");}
                }catch(ModbusTransportException e1){
                    Log.d("ModTransportE", e1.toString());
                }catch(ErrorResponseException e2){
                    Log.d("ErrResponseE", e2.toString());
                }catch(Exception e){
                    Log.d("Exception",e.toString());
                }
            }else{
                Log.d("isInit","Failed");
            }
            if(refresh_on)mainHandler.postDelayed(refresh,2000);
        }

    };
     */

    private void init(){     //TODO: DO THIS FIRST
        input_IP = (EditText)findViewById(R.id.input_IP);
        input_port = (EditText)findViewById(R.id.input_port);
        input_offset = (EditText)findViewById(R.id.input_offset);
        input_bit = (EditText)findViewById(R.id.input_bit);
        input_decimal = (EditText)findViewById(R.id.input_decimal);
        button_connect = (Button)findViewById(R.id.button_connect);
        button_disconnect = (Button)findViewById(R.id.button_disconnect);
        toggleButton_bit0 = (ToggleButton)findViewById(R.id.toggleButton_bit0);
        toggleButton_bit1 = (ToggleButton)findViewById(R.id.toggleButton_bit1);
        toggleButton_bit2 = (ToggleButton)findViewById(R.id.toggleButton_bit2);
        toggleButton_bit3 = (ToggleButton)findViewById(R.id.toggleButton_bit3);
        toggleButton_bit4 = (ToggleButton)findViewById(R.id.toggleButton_bit4);
        toggleButton_bit5 = (ToggleButton)findViewById(R.id.toggleButton_bit5);
        toggleButton_bit6 = (ToggleButton)findViewById(R.id.toggleButton_bit6);
        toggleButton_bit7 = (ToggleButton)findViewById(R.id.toggleButton_bit7);
        radioButton_readMB = (RadioButton)findViewById(R.id.radio_readMB);
        radioButton_writeMB = (RadioButton)findViewById(R.id.radio_writeMB);
        spinner_dataType = (Spinner)findViewById(R.id.spinner_dataType);
        textView_result = (TextView)findViewById(R.id.text_result);

        spinnerAdapter_dataType = new ArrayAdapter(MainActivity.this,android.R.layout.select_dialog_item,DATA_TYPE_SELECTIONS);
        spinner_dataType.setAdapter(spinnerAdapter_dataType);




    }

    private void createAlertDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        final View alertDialogForPosttimeChosing = li.inflate(R.layout.alert_dialog_for_posttime_chosing,null);
        AlertDialog.Builder AB = new AlertDialog.Builder(this);
        listView_posttime = (ListView)alertDialogForPosttimeChosing.findViewById(R.id.listView_posttime);
        listView_adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,POSTTIME_SELECTIONS);
        listView_posttime.setAdapter(listView_adapter); //TODO: CHECK IT OUT
        final AdapterView.OnItemClickListener listView_postTime_listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        refreshDelay = 50;
                        break;
                    case 1:
                        refreshDelay = 100;
                        break;
                    case 2:
                        refreshDelay = 250;
                        break;
                    case 3:
                        refreshDelay = 500;
                        break;
                    case 4:
                        refreshDelay = 1000;
                        break;
                    case 5:
                        refreshDelay = 2500;
                        break;
                    case 6:
                        refreshDelay = 5000;
                        break;
                }
            }
        };
        listView_posttime.setOnItemClickListener(listView_postTime_listener);
        AB.setView(alertDialogForPosttimeChosing);
        AB.setTitle("Select refreshing period");
        AB.setCancelable(true);
        AB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AB.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(MainActivity.this,"Refresh every "+refreshDelay*2 + " ms",Toast.LENGTH_SHORT).show();
            }
        });
        AD = AB.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    
}
