package api;

public class GetAESKey implements Runnable{
    public static String AESkey =  null;

    @Override
    public void run() {
        try {
            this.AESkey = AESUtils.getSecretKey();
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
