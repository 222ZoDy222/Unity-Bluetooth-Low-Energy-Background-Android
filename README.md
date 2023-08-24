
# BLE (Bluetooth Low Energy) in Unity on Background

This Repository will be useful to those who want or who need to create an application on Unity that interact with BLE devices on foreground (To be honest, it’s not really [foreground services](https://developer.android.com/guide/components/foreground-services), the app create new Thread which continues to work in the background).

If you really want/need to understand this topic and create your own Ble interactive Application, this is really cool Repositories that helped me:



## Cool Repositores

 - [Java BLE Terminal](https://github.com/Amir-yazdanmanesh/Bluetooth-Low-Energy-Terminal-Android-BLE-Library)
 - [Unity JNI facilities](https://github.com/elmirjagudin/UnityBluetooth/tree/master)
 - [Scanning in main Thread](https://github.com/Velorexe/Unity-Android-Bluetooth-Low-Energy)
 


## What you need to Search

- How to create Java plugin in Unity
- Java (to be able to look for basic things in the google like arrays, methods, callbacks)
- Andoid studio ( I dunno how to compile java in other compilers )
- [BLE Android API](https://developer.android.com/reference/android/bluetooth/package-summary)
- [AndroidJavaClass](https://docs.unity3d.com/ScriptReference/AndroidJavaClass.html) and [AndroidJavaObject](https://docs.unity3d.com/ScriptReference/AndroidJavaObject.html)

## Must read all parts of [this article](https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02)


## Start

- Download or Clone repository

in the path "..BluetoothLE\JavaPlugin\blePlugin\build\outputs\aar" u can find builded plugin with the expansion ".aar".
this file u must copy to your unity project in "..\Assets\Plugins\Android\"

- open project in Unity editor and your code editor

- before start interacting with plugin u must initialize UnityThread

```
UnityThread.initUnityThread();
Ble = InterfaceReciever.RecieveIConfForm(gameObject);

```

### Using Thread to interact with plugin

How to use interface callbacks and create Threads u can see in the example app

In each method must be inserted this line of code:
```
AndroidJNI.AttachCurrentThread();

```
[there](https://docs.unity3d.com/ScriptReference/AndroidJNI.AttachCurrentThread.html) is a little information about this method in Unity docs, but without of this line of code your application will crash.

- Firstly init plugin that using singleton

```
    private AndroidJavaClass unityClass;
    private AndroidJavaObject unityActivity;
    private AndroidJavaClass customClass;
    private AndroidJavaObject ble_instance;

    private const string PackageName = "com.zody.bleplugin.BlePlugin";
    private const string UnityDefaultJavaClassName = "com.unity3d.player.UnityPlayer";


    public void Init()
    {

        AndroidJNI.AttachCurrentThread();


        unityClass = new AndroidJavaClass(UnityDefaultJavaClassName);
        unityActivity = unityClass.GetStatic<AndroidJavaObject>("currentActivity");
        customClass = new AndroidJavaClass(PackageName);
        customClass.CallStatic("InitInstance", unityActivity);
        ble_instance = customClass.GetStatic<AndroidJavaObject>("instance");

        string plugin_name_version = ble_instance.Get<string>("PLUGIN_NAME");
        UnityEngine.Debug.Log(plugin_name_version);

        

    }

```

AndroidJavaObject ble_instance - variable that hold plugin instance and all plugin methods should be called through it
#### example
```
ble_instance.Call("ScanDevices");

```

### How to get answers from Android BluetoothLE API

We can't use Unity methods for getting bluetooth answers because thay are got from callbacks in abstract class.

But u can rewrite plugin and add your own interface that will have delegates that react to gattCallback and  catch callback from unity using [Android Java Runnable](https://docs.unity3d.com/ScriptReference/AndroidJavaRunnable.html) 

#### But i went the other way
I save data in plugin and getting it from standart methods in cycle (I'll rewrite it mb later)

## Scanning

Before start scanning u can change [settings](https://developer.android.com/reference/android/bluetooth/le/ScanSettings) and set [filters](https://developer.android.com/reference/android/bluetooth/le/ScanFilter) of scan.

#### C#
```
AndroidJNI.AttachCurrentThread();

adapter.SetScanSettings(2, 1, 1, 3);
adapter.SetScanFilters(new string[] { "SMCLO-4B7BDF5D-1030" });

```

#### Java
```
/**
     * SCAN SETTINGS PARAMETERS
     * https://developer.android.com/reference/android/bluetooth/le/ScanSettings
     *
     * SCANMODE:
     *          SCAN_MODE_BALANCED : 1
     *          SCAN_MODE_LOW_LATENCY : 2
     *          SCAN_MODE_LOW_POWER : 0
     *          SCAN_MODE_OPPORTUNISTIC : -1
     *
     * Callback Type
     *          CALLBACK_TYPE_ALL_MATCHES : 1
     *          CALLBACK_TYPE_FIRST_MATCH : 2
     *          CALLBACK_TYPE_MATCH_LOST : 4
     *
     * MatchMode
     *          MATCH_MODE_AGGRESSIVE : 1
     *          MATCH_MODE_STICKY : 2
     *
     * MatchNum
     *          MATCH_NUM_FEW_ADVERTISEMENT : 2
     *          MATCH_NUM_MAX_ADVERTISEMENT : 3
     *          MATCH_NUM_ONE_ADVERTISEMENT : 1
     */
    public void SetScanSettings(int SCANMODE, int CALLBACKTYPE, int MATCHMODE, int MATCHNUM)
    {
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(SCANMODE)
                .setCallbackType(CALLBACKTYPE)
                .setMatchMode(MATCHMODE)
                .setNumOfMatches(MATCHNUM)
                .setReportDelay(0L)
                .build();
        m_scanSettings = scanSettings;
    }
```
#### it is desirable to put settings and filters together or don't use it all.

#### Note: If u don't use filters - applications will not scan in background.

## Permissions

```
  <uses-permission android:name="android.permission.BLUETOOTH"/>
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
  

  <!-- Bluetooth permissions for BLE scanning -->
  <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
  <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
  <uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />

  <!-- Location permissions for BLE scanning -->
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />


  <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
  <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION"/>
  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
  <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
  <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

```
I'm using this permissions, perhaps some are not needed.

#### Some of this permissions is [Dangerious](https://developer.android.com/guide/topics/permissions/overview) and u should call it in runtime.

```
@RequiresApi(api = Build.VERSION_CODES.M)
    public static void checkPermissions(Context context, Activity activity) {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
    }
```

## Connect

After u've found the right device - connect with it.

Connect requires Application Context and we already save it in initialization.

Bluetooth stack set result of connection in ```onConnectionStateChange``` callback after using ```connectGatt()```

### After connecting 
U should set notification in order to read Characteristics.

```
m_currentBluetoothGatt.setCharacteristicNotification(characteristic,true);
```
Characteristics must have description with UUID equals ```00002902-0000-1000-8000-00805f9b34fb```

Most often this is the UUID used to read and write Characteristics.

## Write Characteristics

```
    - Type of writeCharacteristic method
        1 - WRITE_TYPE_NO_RESPONSE
        2 - WRITE_TYPE_DEFAULT
        4 - WRITE_TYPE_SIGNED

```

```
        characteristic.setValue(bytes);
        characteristic.setWriteType(WRITE_TYPE_DEFAULT );

        if (!m_currentBluetoothGatt.writeCharacteristic(characteristic)) {
            // Write characteristic error
            
        } else {
            // Write Characteristic success
        }

```

Then u will get the result from callback
```
@Override
public void onCharacteristicWrite (BluetoothGatt gatt,
                                    BluetoothGattCharacteristic characteristic,
                                    int status)
```

```onCharacteristicChanged``` will callback result from BLE device
(if u've set notification)

```
@Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            final BluetoothGattCharacteristic characteristic) 
        {
            
            byte[] data = characteristic.getValue();
            String dataString = characteristic.getStringValue(0);
            
            ReadValue = dataString;
            
        }
```

