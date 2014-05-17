
package weibo4j.util;


public class WeiboConfig {

  public WeiboConfig() {}

  // private static Properties props = new Properties();
  static {
    /*
     * try { //
     * props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream
     * ("config.properties")); props.put("client_ID", ""); } catch
     * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e)
     * { e.printStackTrace(); }
     */
  }

  public static String getValue(String key) {
    // return props.getProperty(key);
    if (key.equals("client_ID")) return "3556572523";
    if (key.equals("client_SERCRET")) return "123b9f40dd2ff1271b62c8473ca5437c";
    if (key.equals("redirect_URI")) return "http://127.0.0.1/uSNS/sources/sinaweibo_callback";
    if (key.equals("baseURL")) return "https://api.weibo.com/2/";
    if (key.equals("accessTokenURL")) return "https://api.weibo.com/oauth2/access_token";
    if (key.equals("authorizeURL")) return "https://api.weibo.com/oauth2/authorize";
    if (key.equals("rmURL")) return "https://rm.api.weibo.com/2/";
    return null;
  }

  // public static void updateProperties(String key, String value) {
  // props.setProperty(key, value);
  // }
}
