package com.pauselabs.pause.models;

/**
 * Model for Access Point Name Object. APN is the name of a gateway between GPRS, 3G, or 4G mobile network
 * This is required for sending MMS messages
 */
public class APN {

    public String MMSCenterUrl = "";
    public String MMSPort = "";
    public String MMSProxy = "";

    public APN(String MMSCenterUrl, String MMSPort, String MMSProxy)
    {
        this.MMSCenterUrl = MMSCenterUrl;
        this.MMSPort = MMSPort;
        this.MMSProxy = MMSProxy;
    }

    public APN()
    {

    }
}
