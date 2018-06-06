package com.myprojects.grideye;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class GridEyeData {
    private final Sender sender;
    private byte[] buffer;
    private int dataCounter;


    public GridEyeData(Sender sender) {
        this.buffer = new byte[2048];
        this.dataCounter = 0;
        this.sender = sender;
    }

    public void saveToBuffer(byte[] data) {

        //System.out.println("Data : " + data.length);
        for (int i = 0; i < data.length; i++) {
            buffer[dataCounter] = data[i];
            dataCounter++;
        }

        int foundStart = -1;
        int foundStop = -1;
        for (int i = 0; i < dataCounter - 135; i++) {
            if (buffer[i] == 0x2A && buffer[i + 1] == 0x2A && buffer[i + 2] == 0x2A
                    && buffer[i + 133] == 0x0D && buffer[i + 134] == 0x0A) {
                foundStart = i;
                foundStop = i + 135;
            }

            if (foundStart >= 0 && foundStop >= 0) {
//                System.out.println("Start: " + foundStart + "  Stop: " + foundStop);
                byte[] slicedData = Arrays.copyOfRange(buffer, foundStart, foundStop);
                byte[] endData = Arrays.copyOfRange(buffer, foundStop, buffer.length);

                sender.temperaturePackage(DataToJSON(slicedData).toString());
                try {
//                    System.arraycopy(endData, foundStop, buffer, 0, endData.length - foundStop);
                    System.arraycopy(endData, 0, buffer, 0, endData.length);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                dataCounter = 0;
                break;
            }
            if (dataCounter >= buffer.length - 3 * 150) {
                dataCounter = 0;
                buffer = new byte[2048];
                System.out.println("Clear");
            }
        }
    }

    public JSONObject DataToJSON(byte[] data) {
        JSONObject jsonObj = new JSONObject();
        if (data.length == 135 && data[0] == 0x2A && data[1] == 0x2A && data[2] == 0x2A) {
            double[] temperatures = new double[64];

            for (int i = 5, j = 0; i < 133; i += 2, j++) {
                temperatures[j] = 0.25 * (short) (((data[i + 1] & 0x7 << 8)) | ((data[i] & 0xff)));
                if ((data[i + 1] & 0x8) == 8) {
                    temperatures[j] *= -1;
                }
                try {
                    jsonObj.accumulate("temperature", Double.toString(temperatures[j]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObj;
    }
}

