package com.myprojects.grideye;

import gnu.io.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ReadDataFromUSB implements SerialPortEventListener {

    public ReadDataFromUSB() {
        super();
    }

//    void connect(String portName) throws Exception {
//        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
//        if (portIdentifier.isCurrentlyOwned()) {
//            System.out.println("Error: Port is currently in use");
//        } else {
//            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
//
//            if (commPort instanceof SerialPort) {
//                SerialPort serialPort = (SerialPort) commPort;
//                serialPort.setOutputBufferSize(135);
//                serialPort.setSerialPortParams(57600 , SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//
//                InputStream in = serialPort.getInputStream();
//                OutputStream out = serialPort.getOutputStream();
//
//                (new Thread(new SerialReader(in))).start();
//                (new Thread(new SerialWriter(out))).start();
//
//            } else {
//                System.out.println("Error: Only serial ports are handled by this example.");
//            }
//        }
//    }
//
//    /** */
//    public static class SerialReader implements Runnable {
//        InputStream in;
//
//        public SerialReader(InputStream in) {
//            this.in = in;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[270];
//            try {
//                GridEyeData obj = new GridEyeData();
//                while (this.in.read(buffer) > -1) {
//                    obj.saveToBuffer(buffer);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /** */
//    public static class SerialWriter implements Runnable {
//        OutputStream out;
//
//        public SerialWriter(OutputStream out) {
//            this.out = out;
//        }
//
//        public void run() {
//            try {
//                int c = 0;
//                while ((c = System.in.read()) > -1) {
//                    this.out.write(c);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    ///////////////////////////////////////////////////////////////////

//    private BufferedReader input;
    private BufferedReader input;

    void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);

            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = null;
                GridEyeData obj = new GridEyeData();
                if (input.ready()) {
                    inputLine = input.readLine();
                    obj.saveToBuffer(inputLine.getBytes(Charset.forName("UTF-8")));
                }

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }
}
