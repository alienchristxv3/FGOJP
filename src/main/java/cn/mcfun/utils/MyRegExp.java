package cn.mcfun.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MyRegExp extends PlainDocument {
    private Pattern pattern;
    private Matcher m;

    public MyRegExp(String pat) {
        this.pattern = Pattern.compile(pat);
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str != null) {
            String tmp = this.getText(0, offset).concat(str);
            this.m = this.pattern.matcher(tmp);
            if (this.m.matches()) {
                super.insertString(offset, str, attr);
            }

        }
    }
}
