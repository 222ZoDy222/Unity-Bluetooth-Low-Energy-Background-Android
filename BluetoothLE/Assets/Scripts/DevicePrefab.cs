using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


public class DevicePrefab : MonoBehaviour
{

    [SerializeField] private Text m_macText;

    [SerializeField] private Button connectButton;

    private BluetoothDevice m_currentDevice;

    public void Init(BluetoothDevice bluetoothDevice)
    {

        m_currentDevice = bluetoothDevice;

        if(m_currentDevice.name != null)
        {
            m_macText.text = m_currentDevice.name;
        }
        else
        {
            m_macText.text = m_currentDevice.address;
        }
       

        connectButton.onClick.AddListener(Connect);

    }


    private void Connect()
    {

        DeviceMenu.instance.Show(m_currentDevice);


       
    }


}
