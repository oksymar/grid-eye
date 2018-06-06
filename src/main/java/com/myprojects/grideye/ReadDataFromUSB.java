package com.myprojects.grideye;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class ReadDataFromUSB {

    @Autowired
    Sender sender;

    @PostConstruct
    void connect() throws Exception {
        String portName = "/dev/ttyACM0";
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            SerialPort serialPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (serialPort != null) {
                serialPort.setInputBufferSize(135);
                serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialReader(in, sender))).start();
                (new Thread(new SerialWriter(out))).start();

            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable {
        private final InputStream in;
        private final Sender sender;

        public SerialReader(InputStream in, Sender sender) {
            this.in = in;
            this.sender = sender;
        }

        public void run() {
            byte[] buffer = new byte[135];
            GridEyeData obj = new GridEyeData(sender);
            try {
                while (this.in.read(buffer) > -1) {
                    obj.saveToBuffer(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** */
    public static class SerialWriter implements Runnable {
        OutputStream out;

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void run() {
            try {
                int c;
                while ((c = System.in.read()) > -1) {
                    this.out.write(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
