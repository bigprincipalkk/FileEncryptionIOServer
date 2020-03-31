package api;

import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends OutputStream {
    private OutputStream outputStream;
    private int buffer;
    private int bytecounter;
    private int linecounter;
    private int linelength;

    public Base64OutputStream(OutputStream var1) {
        this(var1, 76);
    }

    public Base64OutputStream(OutputStream var1, int var2) {
        this.outputStream = null;
        this.buffer = 0;
        this.bytecounter = 0;
        this.linecounter = 0;
        this.linelength = 0;
        this.outputStream = var1;
        this.linelength = var2;
    }

    public void write(int var1) throws IOException {
        int var2 = (var1 & 255) << 16 - this.bytecounter * 8;
        this.buffer |= var2;
        ++this.bytecounter;
        if (this.bytecounter == 3) {
            this.commit();
        }

    }

    public void close() throws IOException {
        this.commit();
        this.outputStream.close();
    }

    protected void commit() throws IOException {
        if (this.bytecounter > 0) {
            if (this.linelength > 0 && this.linecounter == this.linelength) {
                this.outputStream.write("\r\n".getBytes());
                this.linecounter = 0;
            }

            char var1 = Shared.chars.charAt(this.buffer << 8 >>> 26);
            char var2 = Shared.chars.charAt(this.buffer << 14 >>> 26);
            char var3 = this.bytecounter < 2 ? Shared.pad : Shared.chars.charAt(this.buffer << 20 >>> 26);
            char var4 = this.bytecounter < 3 ? Shared.pad : Shared.chars.charAt(this.buffer << 26 >>> 26);
            this.outputStream.write(var1);
            this.outputStream.write(var2);
            this.outputStream.write(var3);
            this.outputStream.write(var4);
            this.linecounter += 4;
            this.bytecounter = 0;
            this.buffer = 0;
        }

    }
}
