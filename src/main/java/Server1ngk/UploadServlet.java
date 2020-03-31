package Server1ngk;

import api.AESUtils;
import api.GetAESKey;
import api.HttpClientUtil;
import api.RSAUtils;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;


@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
    //    Logger logger = LoggerFactory.getLogger(UploadServlet.class);
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCWLyJIwwcxMgcKbnX2DKQsdWwfvk1YrdxXDtZNxBaI2nJND92f+bwl2jpOwjYQD6MXWUWu/WI3ucJfk" +
            "od2zj8GgieM+PrBGdHkgqyEIJR2Io2T0bt6SdmHABilfQe16Dn83czHBnEvY4C6v02UtbelM0B8+FB16VmuNSkFXvbBwIDAQAB";


    private static final long serialVersionUID = 1L;
    private static final long FILE_MAX_SIZE = 1024 * 1024 * 200;


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doGet方法调用了");

    }

    /**
     * @Description 接受上传文件的请求
     * @Param
     * @Return
     * @Author 1ng
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String AESKey = null;
        long fileSize = 0L;
        Date createTime = null;
        String orgName = null;
        String fileType = null;


        //生成uuid
        String uuid = UUID.randomUUID().toString();

        //上传的都是二进制数据，所以在Servlet中就不能直接用request.getParameter();
        // 方法进行数据的获取，需要借助第三方jar包对上传的二进制文件进行解析
        RequestContext requestContext = new ServletRequestContext(req);
        if (FileUpload.isMultipartContent(requestContext)) {
            //创建一个磁盘文件工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            //设置上传文件的大小
            fileUpload.setFileSizeMax(FILE_MAX_SIZE);
            //设置编码
            fileUpload.setHeaderEncoding("utf-8");
            List<FileItem> items = new ArrayList<FileItem>();
            try {
                items = fileUpload.parseRequest(req);
            } catch (FileUploadException f) {
                f.printStackTrace();
            }
            System.out.println("hello");
            Iterator<FileItem> it = items.iterator();
            while (it.hasNext()) {
                FileItem fileItem = (FileItem) it.next();

                //如果是表单
                if (fileItem.isFormField()) {
                    //先不处理
                } else {
//                        logger.info(fileItem.getFieldName()+"  "+fileItem.getName()+"  "
//                        +fileItem.getContentType()+"  "+fileItem.getSize());
                    System.out.println(fileItem.getFieldName() + "  " + fileItem.getName() + "  "
                            + fileItem.getContentType() + "  " + fileItem.getSize());
                    //原文件名
                    orgName = fileItem.getName();
                    //拿到对称密钥
                    try {
                        AESKey = AESUtils.getSecretKey();
                        System.out.println(AESKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (fileItem.getName() != null && fileItem.getSize() != 0) {
                        File oldFile = new File(fileItem.getName());


                        String newFlieRoute = createDirByCurTime(GetDirName());
                        //中间过渡文件，加密完后会删除
                        File midFile = new File("mid" + oldFile.getName());
//                        File newFile = new File(newFlieRoute+getFileName(oldFile));
                        //文件保存目录地址
                        String encryptFileRoute = newFlieRoute + getFileName(oldFile, uuid);


                        try {
                            fileItem.write(midFile);
                            //加密文件并写入目标文件
                            AESUtils.encryptFile(AESKey, midFile, encryptFileRoute);

                            File encryptFileOpen = new File(encryptFileRoute);
                            //文件大小
                            fileSize = encryptFileOpen.length();
                            String[] strArray = encryptFileOpen.getName().split("\\.");
                            //文件类型
                            fileType = strArray[strArray.length - 1];
                            //文件加密元数据
                            String mataSaveKey = getKeysAndEncrypt(AESKey);
                            //创建日期
                            String curTime = getCurFormatDate();


                            //发送uuid,原文件名给客户端
                            sendInfoToClient(uuid,orgName);
                            
                            //写入derby数据库
                            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");//加载驱动
                            Connection conn = DriverManager.getConnection("jdbc:derby:filedb;create=true");//连接数据库
                            Statement st = conn.createStatement();
                            String sql ="INSERT INTO fileTable(file_size,file_type,create_time,save_path,org_name,mataData,uuid) VALUES(" +fileSize+",'"+fileType+"','"+createTime+"','"+encryptFileRoute+"','"+orgName+"','"+mataSaveKey+"','"+uuid+"')";
                            System.out.println(sql);
                            st.execute(sql);




                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            midFile.delete();
                        }




                    } else {
                        System.out.println("没有该文件或者该文件为空!");
                    }
                }
            }
        }
        System.out.println("hello1");



    }

    private static String getAESKey() throws Exception {
        String res = AESUtils.getSecretKey();
        return res;
    }


    //获得当前日期并格式化
    private static String GetDirName() {
        Date curDate = new Date();
        //格式化当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String resDate = sdf.format(curDate);
        return resDate;
    }

    //获取当前时间并格式化往数据库里存
    public static String getCurFormatDate(){
        Date curDate = new Date();
        //格式化当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String resDate = sdf.format(curDate);
        return resDate;
    }

    //根据日期创建目录
    private static String createDirByCurTime(String curTime) {
        File dir = new File("D:\\bisheTest\\TestFile\\ServerFiles");
        if (!dir.exists()) {
            dir.mkdir();
//            logger.info("创建目录成功   "+dir.getName());
        } else {
//            logger.info("当前日期已存在"+dir.getName());
        }
        return "D:\\bisheTest\\TestFile\\ServerFiles\\" + curTime + "\\";
    }

    //通过uuid生成文件名
    private String getFileName(File oldFile, String uuid) {
        String newFileName = uuid;

        return newFileName;
    }



    //生成私钥公钥并对对称密钥加密
    private String  getKeysAndEncrypt(String AESKey) throws Exception {
        RSAUtils rsaUtils = new RSAUtils();
        rsaUtils.genKeyPair();
//        privateKey = rsaUtils.keyMap.get(1);
//        publicKey = rsaUtils.keyMap.get(0);

        String res = rsaUtils.encrypt(AESKey,publicKey);
        return res;
    }

    //发送uuid,原文件名给客户端
    public boolean sendInfoToClient(String uuid,String orgName){
        Map<String,String> resMap = new HashMap<String,String>();
        resMap.put("uuid",uuid);
        resMap.put("orgName",orgName);

        String url="http://127.0.0.1:10010/getInfoFromServer";
        String res = HttpClientUtil.doGet(url,resMap);
        System.out.println(res);
        return true;
    }
}
