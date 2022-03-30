package cn.mcfun.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MyPrintStream extends PrintStream {
    private JTextArea text;
    private StringBuffer sb = new StringBuffer();

    public MyPrintStream(OutputStream out, JTextArea text) {
        super(out);
        this.text = text;
    }

    public void write(byte[] buf, int off, int len) {
        final String message = new String(buf, off, len);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MyPrintStream.this.sb.append(message);
                MyPrintStream.this.text.setText(MyPrintStream.this.sb.toString());
            }
        });
    }
}
