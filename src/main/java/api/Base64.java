package api;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Base64 {
    public Base64() {
    }

    public static String encode(String var0) throws RuntimeException {
        byte[] var1 = var0.getBytes();
        byte[] var2 = encode(var1);

        try {
            return new String(var2, "ASCII");
        } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException("ASCII is not supported!", var4);
        }
    }

    public static String encode(String var0, String var1) throws RuntimeException {
        byte[] var2;
        try {
            var2 = var0.getBytes(var1);
        } catch (UnsupportedEncodingException var6) {
            throw new RuntimeException("Unsupported charset: " + var1, var6);
        }

        byte[] var3 = encode(var2);

        try {
            return new String(var3, "ASCII");
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException("ASCII is not supported!", var5);
        }
    }

    public static String decode(String var0) throws RuntimeException {
        byte[] var1;
        try {
            var1 = var0.getBytes("ASCII");
        } catch (UnsupportedEncodingException var3) {
            throw new RuntimeException("ASCII is not supported!", var3);
        }

        byte[] var2 = decode(var1);
        return new String(var2);
    }

    public static String decode(String var0, String var1) throws RuntimeException {
        byte[] var2;
        try {
            var2 = var0.getBytes("ASCII");
        } catch (UnsupportedEncodingException var6) {
            throw new RuntimeException("ASCII is not supported!", var6);
        }

        byte[] var3 = decode(var2);

        try {
            return new String(var3, var1);
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException("Unsupported charset: " + var1, var5);
        }
    }

    public static byte[] encode(byte[] var0) throws RuntimeException {
        return encode(var0, 0);
    }

    public static byte[] encode(byte[] var0, int var1) throws RuntimeException {
        ByteArrayInputStream var2 = new ByteArrayInputStream(var0);
        ByteArrayOutputStream var3 = new ByteArrayOutputStream();

        try {
            encode((InputStream)var2, (OutputStream)var3, var1);
        } catch (IOException var15) {
            throw new RuntimeException("Unexpected I/O error", var15);
        } finally {
            try {
                var2.close();
            } catch (Throwable var14) {
                ;
            }

            try {
                var3.close();
            } catch (Throwable var13) {
                ;
            }

        }

        return var3.toByteArray();
    }

    public static byte[] decode(byte[] var0) throws RuntimeException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(var0);
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();

        try {
            decode((InputStream)var1, (OutputStream)var2);
        } catch (IOException var14) {
            throw new RuntimeException("Unexpected I/O error", var14);
        } finally {
            try {
                var1.close();
            } catch (Throwable var13) {
                ;
            }

            try {
                var2.close();
            } catch (Throwable var12) {
                ;
            }

        }

        return var2.toByteArray();
    }

    public static void encode(InputStream var0, OutputStream var1) throws IOException {
        encode((InputStream)var0, (OutputStream)var1, 0);
    }

    public static void encode(InputStream var0, OutputStream var1, int var2) throws IOException {
        Base64OutputStream var3 = new Base64OutputStream(var1, var2);
        copy(var0, var3);
        var3.commit();
    }

    public static void decode(InputStream var0, OutputStream var1) throws IOException {
        copy(new Base64InputStream(var0), var1);
    }

    public static void encode(File var0, File var1, int var2) throws IOException {
        FileInputStream var3 = null;
        FileOutputStream var4 = null;

        try {
            var3 = new FileInputStream(var0);
            var4 = new FileOutputStream(var1);
            encode((InputStream)var3, (OutputStream)var4, var2);
        } finally {
            if (var4 != null) {
                try {
                    var4.close();
                } catch (Throwable var14) {
                    ;
                }
            }

            if (var3 != null) {
                try {
                    var3.close();
                } catch (Throwable var13) {
                    ;
                }
            }

        }

    }

    public static void encode(File var0, File var1) throws IOException {
        FileInputStream var2 = null;
        FileOutputStream var3 = null;

        try {
            var2 = new FileInputStream(var0);
            var3 = new FileOutputStream(var1);
            encode((InputStream)var2, (OutputStream)var3);
        } finally {
            if (var3 != null) {
                try {
                    var3.close();
                } catch (Throwable var13) {
                    ;
                }
            }

            if (var2 != null) {
                try {
                    var2.close();
                } catch (Throwable var12) {
                    ;
                }
            }

        }

    }

    public static void decode(File var0, File var1) throws IOException {
        FileInputStream var2 = null;
        FileOutputStream var3 = null;

        try {
            var2 = new FileInputStream(var0);
            var3 = new FileOutputStream(var1);
            decode((InputStream)var2, (OutputStream)var3);
        } finally {
            if (var3 != null) {
                try {
                    var3.close();
                } catch (Throwable var13) {
                    ;
                }
            }

            if (var2 != null) {
                try {
                    var2.close();
                } catch (Throwable var12) {
                    ;
                }
            }

        }

    }

    private static void copy(InputStream var0, OutputStream var1) throws IOException {
        byte[] var2 = new byte[1024];

        int var3;
        while((var3 = var0.read(var2)) != -1) {
            var1.write(var2, 0, var3);
        }

    }
}
