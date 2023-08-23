using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class DeviceMenu : MonoBehaviour
{


    public static DeviceMenu instance;

    [SerializeField] private Image connectionImage;

    [SerializeField] private Text deviceName, deviceMac;

    public IBle Ble;

    private BluetoothDevice m_currentDevice;

    [SerializeField] private CanvasGroup canvasGroup;


    private void Awake()
    {
        if (instance != null) Destroy(instance);
        instance = this;
    }

    private IEnumerator Start()
    {

        yield return null;
        yield return null;
        yield return null;
        yield return null;
        yield return null;
        yield return null;
        Ble = Test.instance.Ble;

    }




    public void Show(BluetoothDevice device)
    {
        m_currentDevice = device;

        if (m_currentDevice.name != null)
        {
            deviceName.text = m_currentDevice.name;
        }
        else
        {
            deviceName.text = "";
        }
        if (m_currentDevice.address != null)
        {
            deviceMac.text = m_currentDevice.address;
        }
        else
        {
            deviceMac.text = "";
        }

        canvasGroup.alpha = 1;
        canvasGroup.interactable = true;
        canvasGroup.blocksRaycasts = true;

    }


    public void Back()
    {
        canvasGroup.alpha = 0;
        canvasGroup.interactable = false;
        canvasGroup.blocksRaycasts = false;
    }

    public void Connect()
    {
        Ble.ConnectTo(m_currentDevice, (msg) =>
        {

            
            //Debug.Log($"Connection = {msg}");
            //if (msg == true)
            //{
            //    connectButton.image.color = Color.green;
            //}
            //else
            //{
            //    connectButton.image.color = Color.red;
            //}
        });
    }

    public void CheckConnection()
    {
        Ble.CheckConnection(m_currentDevice, (msg) =>
        {

            if(msg == 2)
            {
                // Connected
                connectionImage.color = Color.green;
            } else if(msg == 1)
            {
                // Connecting
                connectionImage.color = Color.yellow;
            } else if(msg == 0)
            {
                // Disconnected
                connectionImage.color = Color.white;
            } else
            {
                // Undefined
                connectionImage.color = Color.red;
            }

        });
    }



    [SerializeField] private InputField writeValueField;
    [SerializeField] private InputField readValueField;


    public void Write()
    {

        string value = writeValueField.text;

        if (value == "") return;

        Ble.WriteCharacteristic(m_currentDevice, value, (msg) =>
        {



        });


    }

    public void WriteRead()
    {

        string value = writeValueField.text;

        if (value == "") return;

        Ble.WriteReadCharacteristic(m_currentDevice, value, (msg) =>
        {

            readValueField.text = msg.values;

        });


    }


    public void SetNotifications()
    {
        Ble.SetNotification(m_currentDevice, (msg) =>
        {

        });
    }

    public void TestBackground()
    {
        Ble.BackgoundTest(m_currentDevice, "0000INFO");
    }




}
