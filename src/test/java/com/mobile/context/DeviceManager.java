package com.mobile.context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DeviceManager
{
    private static final Logger logger = Logger.getLogger(DeviceManager.class);

    public DeviceManager() throws IOException, InterruptedException
    {
        List<String> getDevicesUID = getDevicesUID();

        getDevicesUID.forEach(device ->
        {
            String result = getDevicesInformation(ADBCommands.ADB_RO_BUILD_VERSION_RELEASE.getAdbCommand(), device);
            logger.info(result);
        });
    }

    private List<String> getDevicesUID() throws IOException, InterruptedException
    {
        List<String> devicesUID = new ArrayList<>();

        String command = ADBCommands.ADB_DEVICES.getAdbCommand();

        Process procGetDeviceUID = Runtime.getRuntime().exec(command);

        try (BufferedReader processOutputReader =
                     new BufferedReader(new InputStreamReader(procGetDeviceUID.getInputStream())))
        {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null)
            {
                if (!readLine.contains("List") & readLine.contains("device"))
                {
                    String result = readLine.split("device")[0].trim();

                    devicesUID.add(result);
                }
            }
            procGetDeviceUID.waitFor();
        }

        return devicesUID;
    }

    private String getDevicesInformation(String adbCommand, String devicesUid)
    {
        String result = null;

        String command = String.format(adbCommand, devicesUid);

        try
        {
            Process procGetDeviceOperator = Runtime.getRuntime().exec(command);

            try (BufferedReader processOutputReader =
                         new BufferedReader(new InputStreamReader(procGetDeviceOperator.getInputStream())))
            {
                result = processOutputReader.readLine();

                procGetDeviceOperator.waitFor();
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }

        return result;
    }

}
