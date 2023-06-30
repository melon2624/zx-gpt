package com.zhangxin.gpt.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author zhangxin
 * @date 2023-06-02 9:47
 */
public class WxUtil {


    private static Logger logger = LoggerFactory.getLogger(WxUtil.class);

    public static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    public static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }

    /**
     * 判断是是否是openId
     *
     * @param input
     * @return
     */
    public static boolean validateWeChatOpenID(String input) {
        String pattern = "^[a-zA-Z0-9]{28}$";
        return Pattern.matches(pattern, input);
    }


    public static String createInvitePicture(String imagePath, String ticket, String accessToken, String openId) {

        String orlImagePath = imagePath + "模板图片.jpg";
        String outputPath = imagePath + openId + ".jpg"; // 输出图片路径
        InputStream inputStream = qrCodeDownloader(accessToken, ticket);
        try {
            // 加载原始图片和二维码图片
            BufferedImage image = ImageIO.read(new File(orlImagePath));
            BufferedImage qrCode = ImageIO.read(inputStream);
            // 调整二维码图片大小
            int targetWidth = image.getWidth() / 4; // 调整为原始图片宽度的1/4
            int targetHeight = targetWidth * qrCode.getHeight() / qrCode.getWidth();
            Image scaledQRCode = qrCode.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

            // 创建合成后的图片
            int resultWidth = Math.max(image.getWidth(), targetWidth);
            int resultHeight = Math.max(image.getHeight(), targetHeight);
            BufferedImage result = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = result.createGraphics();
            // 绘制原始图片
            int imageX = 0; // x坐标：距离左侧0像素
            int imageY = resultHeight - image.getHeight(null); // y坐标：距离底部与合成图片高度一致
            graphics.drawImage(image, imageX, imageY, null);
            // 计算二维码的位置
            int qrCodeX = 0; // x坐标：距离左侧0像素
            int qrCodeY = resultHeight - scaledQRCode.getHeight(null); // y坐标：距离底部与合成图片高度一致

            // 绘制二维码图片
            graphics.drawImage(scaledQRCode, qrCodeX, qrCodeY, null);
            // 保存合成后的图片
            ImageIO.write(result, "png", new File(outputPath));
            logger.info("-----openId:" + openId + "----------合成图片成功!");
            System.out.println("合成图片成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outputPath;
    }

    //生成推广二维码,上传到微信服务器
    public static JSONObject materialUploader(String accessToken, String type, String imagePath, String openId) {
        // String accessToken = "YOUR_ACCESS_TOKEN"; // 替换为实际的访问令牌
        //String type = "TYPE"; // 替换为实际的素材类型，如"image"、"video"等
        //String filePath = "PATH_TO_FILE"; // 替换为实际的文件路径
        // String accessToken = "YOUR_ACCESS_TOKEN";
        //生成分享二维码
        JSONObject qrCodeJson = getQrCode(accessToken, openId);
        // 解析响应数据，获取永久二维码的ticket
        String ticket = qrCodeJson.getString("ticket");

        String tuiGuangQrCode = WxUtil.createInvitePicture(imagePath, ticket, accessToken, openId);

        String tuiguangPictureName = openId + ".jpg";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("media", tuiguangPictureName,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(tuiGuangQrCode)))
                .build();
        String url = String.format("https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=%s&type=%s", accessToken, type);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 解析响应
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            // 提取需要的信息
            String mediaId = jsonResponse.getString("media_id");
            String returnUrl = jsonResponse.getString("url");
            logger.info("上传图片返回信息:" + jsonResponse.toJSONString());
            System.out.println(jsonResponse.toJSONString());
            // Close the OkHttpClient instance to release all resources
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            return jsonResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    /**
     * 分享二维码
     *
     * @param accessToken
     * @param opnenId
     * @return
     */
    public static JSONObject getQrCode(String accessToken, String opnenId) {

        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;

        CloseableHttpClient httpClient = null;

        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // 构建请求参数
            JSONObject params = new JSONObject();
            params.put("action_name", "QR_LIMIT_STR_SCENE"); // 永久二维码场景
            JSONObject scene = new JSONObject();
            scene.put("scene_str", opnenId); // 替换为你的场景值
            JSONObject actionInfo = new JSONObject();
            actionInfo.put("scene", scene);
            params.put("action_info", actionInfo);

            // 设置请求体
            StringEntity requestEntity = new StringEntity(params.toString(), "UTF-8");
            httpPost.setEntity(requestEntity);
            httpPost.setHeader("Content-type", "application/json");

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                JSONObject jsonResponse = JSONObject.parseObject(responseString);

                // 解析响应数据，获取永久二维码的ticket
                String ticket = jsonResponse.getString("ticket");
                String qrcodeUrl = jsonResponse.getString("url");
                logger.info("-----openId:" + opnenId + "----------永久二维码ticket: " + ticket);
                logger.info("-----openId:" + opnenId + "----------永久二维码URL: " + qrcodeUrl);

                return jsonResponse;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static InputStream qrCodeDownloader(String accessToken, String ticket) {


        String qrCodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;

        try {
            URL url = new URL(qrCodeUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            logger.info("-----ticket:" + ticket + "----------二维码文件下载完成。");
            return connection.getInputStream();
       /*     FileOutputStream fileOutputStream = new FileOutputStream(qrCodePath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.close();
            inputStream.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws FileNotFoundException {

//        //  String accessToken = "YOUR_ACCESS_TOKEN";
//        String accessToken = "69_prAtqxpgzloKofXQgcNFWCHACSX3-lF1KOEx_XCAkDRPinBCjham8wzxk81-ds8ec9fcIqxnot1SdLOyRBfqoAvgbjUn_qwjG1yDwV4oM0HD-nRHTTFp9nX4ZGYJPLjAFACIE";
//        String type = "image";
//        //  String imagePath = "C:\\Users\\zhangxin\\Desktop\\0327\\微信图片_20230428101439.jpg";
//        String imagePath = "C:\\Users\\zhangxin\\Desktop\\0327\\";
//        String qqqqq = "C:\\Users\\zhangxin\\Desktop\\0327\\zx\\微信图片_20230602184308.jpg";
//
//        String qqq = "C:\\Users\\zhangxin\\Desktop\\0327\\574954aa6ed7c2b11cb76e092700c7c.jpg";
//
//        String qrCode = "C:\\Users\\zhangxin\\Desktop\\0327\\";
//
//        String aa = qrCode + "123456789" + ".jpg";
//
//        String ticket = "gQGu8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyVm1WeFFiVlBlVkUxMDAwMHcwNzAAAgTN5H1kAwQAAAAA";
//        String qrCodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
//
//        // createInvitePicture(imagePath, ticket, accessToken, null);
//        //生成分享二维码
//        logger.info("二维码文件下载完成。");
//        // JSONObject jsonResponse = WxUtil.materialUploader(accessToken, "image", imagePath, "1415458002");

        String orlImagePath = "C:\\Users\\zhangxin\\Desktop\\0327\\" + "615.png";
        String outputPath = "C:\\Users\\zhangxin\\Desktop\\0327\\" + "openId" + ".jpg"; // 输出图片路径
        //  InputStream inputStream = qrCodeDownloader(accessToken, ticket);

        String filePath = "C:\\Users\\zhangxin\\Desktop\\0327\\123456789.jpg";
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        // 现在你可以使用该InputStream进行进一步的操作，如上传到服务器或处理图像数据等

        // 记得在使用完InputStream后关闭它

        try {
            // 加载原始图片和二维码图片
            BufferedImage image = ImageIO.read(new File(orlImagePath));
            BufferedImage qrCode = ImageIO.read(inputStream);
            // 调整二维码图片大小
            int targetWidth = image.getWidth() / 4; // 调整为原始图片宽度的1/4
            int targetHeight = targetWidth * qrCode.getHeight() / qrCode.getWidth();
            Image scaledQRCode = qrCode.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

            // 创建合成后的图片
            int resultWidth = Math.max(image.getWidth(), targetWidth);
            int resultHeight = Math.max(image.getHeight(), targetHeight);
            BufferedImage result = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = result.createGraphics();
            // 绘制原始图片
            int imageX = 0; // x坐标：距离左侧0像素
            int imageY = resultHeight - image.getHeight(null); // y坐标：距离底部与合成图片高度一致
            graphics.drawImage(image, imageX, imageY, null);
            // 计算二维码的位置
            int qrCodeX = 0; // x坐标：距离左侧0像素
            int qrCodeY = resultHeight - scaledQRCode.getHeight(null); // y坐标：距离底部与合成图片高度一致

            // 绘制二维码图片
            graphics.drawImage(scaledQRCode, qrCodeX, qrCodeY, null);
            // 保存合成后的图片
            ImageIO.write(result, "png", new File(outputPath));
            logger.info("-----openId:" + 123456 + "----------合成图片成功!");
            System.out.println("合成图片成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
