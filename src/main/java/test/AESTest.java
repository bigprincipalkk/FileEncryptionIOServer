package test;

import api.AESUtils;
import api.GetAESKey;

import java.io.File;
import java.sql.*;

public class AESTest {
    public static void main(String[] args) throws Exception {
//        File file = new File("D:\\bisheTest\\TestFile\\ServerFiles\\20200330\\a70bac58-d5e0-47a6-949a-51aecb86108c");
//        File file1 = new File("D:\\bisheTest\\TestFile\\ServerFiles\\20200330\\IOAccessCardRule.coderule");
//        AESUtils.encryptFile("oDBgPlUOL4veN8/oqabKeg==",file,"D:\\bisheTest\\TestFile\\ServerFiles\\ServerFiles4be5b40e-99e1-4ae8-a1bf-dba5d366fbe1index.html");
  //      AESUtils.decryptFile("dNwhFCVd+XQLa9auexKCWw==","D:\\bisheTest\\TestFile\\ServerFiles\\20200330\\a70bac58-d5e0-47a6-949a-51aecb86108c","D:\\bisheTest\\TestFile\\ServerFiles\\20200330\\IOAccessCardRule.coderule");
//        System.out.println(AESUtils.getSecretKey());
//        GetAESKey getAESKey = new GetAESKey();
//        Thread thread = new Thread(getAESKey);
//        thread.start();
//        thread.run();

//        System.out.println(getAESKey.AESkey);

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");//加载驱动
        Connection conn = DriverManager.getConnection("jdbc:derby:filedb;create=true");//连接数据库
        Statement st = conn.createStatement();
        String file_type ="html";
        String create_time ="20200321";
        String save_path ="wwdawdawd";
        String org_name ="wwdawdawd";
        String mataData ="wwdawdawd";
        String uuid ="wadawd";
        String bai = "INSERT INTO fileTable(file_size,file_type,create_time,save_path,org_name,mataData,uuid) VALUES(3680,'cbf92717-543d-4d3b-9404-b3414bf2db9e','null','D:\\bisheTest\\TestFile\\ServerFiles\\20200331\\cbf92717-543d-4d3b-9404-b3414bf2db9e','激活码.txt','FPjXYLT/dO9rDRhf9b+gVJ/VRCY3FGtKouC1X1SNxn7H36/AyBAqDdxbz/IdmYAWxT1/XE17GQHWNtfdHlq3/4uYafTqk/4iJw987yuEnk9NxbRPkYX784sBLHf6ZZz/5a3c/OMwPTo7iAmezUD78M+2KdsXDsdh3/S6hDbVoCI=','cbf92717-543d-4d3b-9404-b3414bf2db9e')";
        st.execute(bai);
//        String sql = "INSERT INTO fileTable(file_size,file_type,create_time,save_path,org_name,mataData,uuid) VALUES(" +
//                "10000,'"+file_type+"','"+create_time+"','"+save_path+"','"+org_name+"','"+mataData+"','"+uuid+"')";
//        System.out.println(sql);
//        st.execute(sql);
//        st.executeUpdate("insert into USER_INFO(ID,NAME) values (1,'hermit')");//插入数据
        ResultSet rs = st.executeQuery("select uuid from fileTable");//读取刚插入的数据
        while(rs.next()){
            String uuids = rs.getString("uuid");
//            String orgName = rs.getString("org_name");
            System.out.println("id:"+uuids+" 姓名：");
        }

//        DriverManager.getConnection("jdbc:derby:;shutdown=true");//关闭数据库
    }
}
