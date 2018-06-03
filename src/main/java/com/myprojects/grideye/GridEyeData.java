package com.myprojects.grideye;

import java.util.Arrays;

public class GridEyeData {
    private byte[] buffer;
    private int dataCounter;

    public GridEyeData() {
        this.buffer = new byte[1024];
        this.dataCounter = 0;
    }

    public void saveToBuffer(byte[] data) {

        for (int i = 0; i < data.length; i++) {
            buffer[dataCounter] = data[i];
            dataCounter++;
        }

        int foundStart = -1;
        int foundStop = -1;
        for (int i = 0; i < buffer.length - 135; i++) {
//            if (buffer[i] == 0x2A && buffer[i + 1] == 0x2A && (buffer[i + 2] == 0x2A || buffer[i + 2] == 0xBD)) {
//            if (buffer[i] == 0x2A && buffer[i + 1] == 0x2A && buffer[i + 2] == 0x2A) {
            if (buffer[i] == 0x2A && buffer[i + 1] == 0x2A) {
                foundStart = i;
            }

            if (buffer[i + 133] == 0x0D && buffer[i + 134] == 0x0A) {
                foundStop = i + 135;
            } else if (buffer[i + 132] == 0x0D && buffer[i + 133] == 0x0A) {
                foundStop = i + 134;
            }

            if (foundStart >= 0 && foundStop >= 0) {
                byte[] slicedData = Arrays.copyOfRange(buffer, foundStart, foundStop);
                byte[] endData = Arrays.copyOfRange(buffer, foundStop, buffer.length);
                DataToJSON(slicedData);
                System.arraycopy(endData, foundStop, buffer, 0, endData.length - foundStop);
                dataCounter = 0;
                break;
            }
        }
    }

    public void DataToJSON(byte[] data) {
        if (data.length == 135 && data[0] == 0x2A && data[1] == 0x2A && data[2] == 0x2A) {
            data.toString();
            System.out.println("Ok");
        } else if (data.length == 134 && data[0] == 0x2A && data[1] == 0x2A) {
            System.out.println("Ok2");
        } else {
            System.out.println("Error");
        }

    }
}
