package com.zody.bleplugin.Container;

public class Response
{

    /**
     * -2 - Error (in SOFTWARE)
     * -1 - Timeout (in SOFTWARE)
     * 0 - unComplete
     * 1 - OK
     * 2 - Error
     *
     */
    public int status = 0;

    public String values;


    /**
     * 0 - no errors
     * 1 - Error in OnConnectionStateChange
     * 2 - undefined device
     * 3 - Services Discovered Error
     * 4 - Characteristic Error
     * 5 - write Characteristic Error
     */
    public int error;
    public String errorMessage;

    /**
     * 0 - undefined
     *
     * 1 - Response on connection
     * 2 - Response on connection callback
     *
     * 3 - Response on Notification
     * 4 - Response on Notification callback
     *
     * 5 - Response on Write
     * 6 - Response on Write callback
     *
     * 7 - Response on Read
     * 8 - Response on Read callback
     */
    public int responseID;
}
