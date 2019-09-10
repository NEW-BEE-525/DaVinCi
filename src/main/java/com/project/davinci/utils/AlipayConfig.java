package com.project.davinci.utils;

import java.io.FileWriter;
import java.io.IOException;


public class AlipayConfig {

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101300676214";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3CcYoBuz57SptZRg7c5ZjggXrMxXdCCf0ox4UnhTwZt9c4/1s6PqvFtfmwnzedDhXFz+CplHp1OpRw3HNXs3mqfvZ2JKuNKR9unYJCwOxOU9zKE3WUxKNTUXfoo9boBOLo1zu0XiRVwaLah3eVow+JzARaRwel5ahvHlcY7fNitTxq6xXI6cC4omGVEHL1GAPt1BIwrLNP1dNl62GtP0kkptxEoy2ZpaiPGOaMlaoHfLuzIurIjDcVzWQ1LnTWody6Bpgc89a7FjhLAtyaj0Cq+ykR2MI3KUmJrhrrenj4Idi6J1JpGkmIDRSb21+d0QLShvBtjzrqtYQzmsyjTDZAgMBAAECggEBAIYTbMmJOTFh3sA/W17BLpo5dihxRUShYtM6YSBtI51tXZRnlQ2gI3D55LCuKlWxAEwbRXRobHxCcFnwNn5fQagDP+61S8y71voaCbhaDxxUhLQwA8RJGb/TqKlGJIjTWz3gLoTpf83G145r7vEcqKImhnlaazSlibtzmdppWQv/s6r2XcBUCHDo9+557JTBhSQInERth21CFYnZmnH42D6om38sfgkNGWkKDCiW1JTln6VcoUDkY/V5ICX7CUxbiO026N93HZNaRGjm3H0IYS0BKuUHSIvFv/9Ls8EZqf1+uOgPt7TmU+XP8bWzl3eSPQRZhLfrwZCMGJhJoYYtomECgYEA+rEqxq/jcJ+ldkVofk3RclvJ2GFNqmyOGWokwR84yzzAJthXLPQp6DIMNGflAS5aDgI8tZWL2aBMlLoTqpIEsdP32u9VvRevLgKwJN9uSzowvPQ8w7/ZiBVqAfxJ0v6VGMudZKe1KVLRws4YMoa7Hfaf+uT3jPrjOBhEvW/ECsUCgYEAuunmm8rMrnP0TRg7xPMjOUTkY/yFtBMgQMH/EHoGeEcv3JL5A2ODHOCEth7HfQYvE/Pzkz9KZifz8fQZTDiz3SWGcn6sVKbgFOng4iCVSh3gbs3W6IS3EijqxUbokYJDY2Cm6Y51oNYnp/ndo6nAaWhn6YtQAVN6guO3xnvwvwUCgYAqBwef1Z7wo6MOp+YzvoINhPvazb1Cg/5ynkvZII3Z7K5eFMjQnYJlDfbj1kMbJpUsYg2hd5fBfVE5YdSoNOCam2mjB5q9H7pboVG8bMTF0dSX6O5C9ufWG5jxCkWqHMoMJR6W4FLLwbmuyfRejB3UkSMS4c3qkakEH3VuuM0J7QKBgC1xvQ3XytgHfThZUf7alo/P/i+JMADOPaNBB4C8CmKn/C0G1uBKC5CzzdBxv1LYdA3H3ErKso4P3LHRBW2Qw7pa60tfHQsf3492JaUVAvcYOH74MO4rhk81/46jZNwr7bJ1x181sce/Cbk/ePHXzd5S8iDILFWGNF0qfH4XmDr5AoGAJUZ/KWp1+y52Nj/KTnFscKCQgyrHbPQOtcst6TE1WayIneiTuIJxDLR9/016c+9jcLWS1QpVkSeMppjkF5AQV+Sn6qEO9q/+Rrd1BrLW3SdgObtbcWSFqjVI8S5mLL/ThE5D3bCoSos4Bg6ELo+NzTuaFxrbqwHJJwd75ysRqL8=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8eQIfXAPI8mN9+FXEL1MrW/+tVUpOIe6x1eeYgi4XljLHOSSkYzQt6s+VrrUWND69CXrwj34JQyBjOclvqHlGynDFQeYX6Q7XYsToptdPKPcc+XhpKQojFPS47ip8KvkkDiB4GK509Egb0QgY5rjoM6MYEI5eFpqSP/BTcF5qzKWER9v61skPN54yTdKVHInJvlGUmKHtIAhbgX+r/LB1F1af9E0qOz9HbYFo9zPd9czC3STo1E5IniEz+pBadSMrauYjM1IMkxifmNLjt8i8aRkwrykTq/sWBTQuJcxXXSggE7Uxj9NfRjDo1aYil56yW++xbTldYWhDUXD+LknrwIDAQAB";
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://zw9etp.natappfree.cc/notify";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://zw9etp.natappfree.cc/is_trade_success";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";
    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
