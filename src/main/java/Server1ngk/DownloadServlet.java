package Server1ngk;

import api.RSAUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pojo.fileInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/downLoadThisFile")
public class DownloadServlet extends HttpServlet{
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCWLyJIwwcxMgcKbnX2DKQsdWwfvk1YrdxXDtZNxBaI2nJND92f+bwl2jpOwjYQD6MXWUWu/WI3ucJfk" +
            "od2zj8GgieM+PrBGdHkgqyEIJR2Io2T0bt6SdmHABilfQe16Dn83czHBnEvY4C6v02UtbelM0B8+FB16VmuNSkFXvbBwIDAQAB";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doPost方法调用了");

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("SID"+req.getHeader("SID"));
        System.out.println("Signature"+req.getHeader("Signature"));
        try {
            //验签成功
            if(RSAUtils.verify(req.getHeader("SID"),publicKey,req.getHeader("Signature"))){
                String uuid = req.getParameter("uuid");
                System.out.println(uuid);
                fileInfo fi = new fileInfo();

                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");//加载驱动
                Connection conn = DriverManager.getConnection("jdbc:derby:filedb;create=true");//连接数据库
                String sql = "SELECT file_size,file_type,create_time,save_path,org_name,mataData,uuid FROM fileTable WHERE uuid='"+uuid+"'";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();

                while(rs.next()){
                    fi.setUuid(rs.getString("uuid"));
                    fi.setCreateTime(rs.getString("create_time"));
                    fi.setFileSize(rs.getLong("file_size"));
                    fi.setFileType(rs.getString("file_type"));
                    fi.setMataData(rs.getString("mataData"));
                    fi.setSavePath(rs.getString("save_path"));
                    fi.setOrgName(rs.getString("org_name"));

                }
//                fi.setSavePath("D:\\bisheTest\\TestFile\\ServerFiles\\20200331\\01c0e337-2810-426f-a9f5-65db32632ddd");
//                fi.setMataData("XKhkIm1/e7/K+D6/w90xLVNHX9GRxNUmevtFN2Bnwz40nx9kLhdJIQA32QNEnh06sTLpFddRk9vzE+UaFEiHQBNzadDG3MiGjOjO2/8qQm+xqKj1gwQVLuliclDEjfuHFFTd6Q+/Ua0KhHKThOJ4GNbLCO2M9oCW1Pto9JGb4qA=");
//                fi.setOrgName("a.txt");
                //获取要下载文件再服务端的保存路径
                String path = fi.getSavePath();
                String matadata = fi.getMataData();
                String orgName = fi.getOrgName();

                //发送到客户端
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = null;
                String res = null;
                try{
//                    MultipartFormDataContent content = new MultipartFormDataContent();
                    Map<String,String>  sendMap = new HashMap<String, String>();
                    sendMap.put("mataData",matadata);
                    sendMap.put("orgName",orgName);
                    StringBody sb = new StringBody(JSON.toJSONString(sendMap));
                    FileBody fb = new FileBody(new File(path));
                    //发送文件
                    HttpPost httpPost = new HttpPost("http://localhost:10010/download/getDownloadFile");
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
                    //把用户上传的文件和元数据塞进去(addpart)
                    multipartEntityBuilder.addPart("file",fb);
                    multipartEntityBuilder.addPart("data",sb);


                    //发出请求
                    httpPost.setEntity(multipartEntityBuilder.build());
                    response = httpClient.execute(httpPost);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK){
                        HttpEntity httpEntity = response.getEntity();
                        res = EntityUtils.toString(httpEntity);

                        EntityUtils.consume(httpEntity);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    HttpClientUtils.closeQuietly(httpClient);
                    HttpClientUtils.closeQuietly(response);
                }





            }else {
                resp.sendError(403);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
