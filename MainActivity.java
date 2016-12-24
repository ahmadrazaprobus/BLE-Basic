package com.example.ahmadraza.bleconnect;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



public class MainActivity extends AppCompatActivity implements
        BluetoothAdapter.LeScanCallback, BluetoothProfile {


    ListView lv;
    BluetoothGatt gattOutside;
    ArrayAdapter<String> myarrayAdapter;
    ArrayList<String> listItems = new ArrayList<String>();
    public BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothDevice devicesss;
    private BluetoothGatt mConnectedGatt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		/* Request permission asked for api 23 on runtime*/
        this.requestPermissions(
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        myarrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems);
        lv.setAdapter(myarrayAdapter);
        lv.setTextFilterEnabled(true);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mDevices = new SparseArray<BluetoothDevice>();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                devicesss = mDevices.valueAt(arg2);
                String itemName = ((TextView) arg1).getText().toString();
                updateText("Connecting to " + itemName);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectToDevice(arg2);
            }
        });
    }

    Handler h=new Handler();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");
            TextView myTextView = (TextView) findViewById(R.id.textView);
            myTextView.setText(string);

        }
    };

	/* to update on textView on xml */
    private void updateText(String msgToSend) {

        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("myKey", msgToSend);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public void connectToDevice(int i) {
        BluetoothDevice device = mDevices.valueAt(i);
        mConnectedGatt = device.connectGatt(this, true, mGattCallback);
		devicesss=device;
    }


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {

            super.onCharacteristicWrite(gatt, characteristic, status);

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {
				/*
				 * Once successfully connected, we must next discover all the
				 * services on the device before we can read and write their
				 * characteristics.
				 */
                gattOutside = gatt;
                gatt.discoverServices();
                if (gatt.discoverServices()) {
                    updateText("Discovering Services");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    updateText("Failed to Discover Services");

                }

            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
				/*
				 * If at any point we disconnect, send a message to clear the
				 * weather values out of the UI
				 */
                updateText("Disconnected on 1");
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
				/*
				 * If there is a failure at any stage, simply disconnect
				 */
                gatt.disconnect();
                gatt.close();
                updateText("Disconnected on 2");
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            btnUpdateClick();
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            updateText("Connected");
			/* all services are discovered here 
			apply for loop to get all discovered 
			services and there corresponding 
			characterstics from gatt 
			*/

        }
    };

	/* res xml file button' onClick event*/
	
    public void startBleScan(View view) {
        myarrayAdapter.notifyDataSetChanged();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(mStartRunnable);
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    public void disconnect(View view){
        gattOutside.disconnect();
        gattOutside.close();
    }

    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            updateText("Scan Started");
            mDevices.clear();
            startScan();
        }
    };

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
            updateText("Press Button To Scan");
            handler.post(mListPopulate);
        }
    };

    private void startScan() {
        mBluetoothAdapter.startLeScan(this);
        setProgressBarIndeterminateVisibility(true);
        handler.postDelayed(mStopRunnable, 2500);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);
    }

    private Runnable mListPopulate = new Runnable() {
        @Override
        public void run() {
            listItems.clear();
            for (int i = 0; i < mDevices.size(); i++) {
                devicesss = mDevices.valueAt(i);
                listItems.add(devicesss.getName()+"\n"+devicesss.getAddress());
            }
            myarrayAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        mDevices.put(device.hashCode(), device);
    }

    @Override
    public List<BluetoothDevice> getConnectedDevices() {
        return null;
    }

    @Override
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] ints) {
        return null;
    }

    @Override
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        return 0;
    }
}
