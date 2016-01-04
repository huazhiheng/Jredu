package com.example.heng.jredu.util;

/**
 * Created by heng on 2015/10/19.
 */
public class UrlUtil {
    public static String BASE_URL = "http://192.168.191.1:8080/VedioManager/";
    // public static String BASE_URL = "http://192.168.8.126:8080/VedioManager/";

    public static String VEDIO_URL = BASE_URL + "servlet/VedioCtrl";
    public static String CLASSPROJ_URL = BASE_URL + "servlet/ClassProjCtrl";
    public static String TOP_URL = BASE_URL + "servlet/VedioCtrl?top=1";
    public static String UserLogin = BASE_URL + "servlet/UserLogin";
    public static String COLLECT_URL = BASE_URL + "servlet/UserCollectCtrl";//收藏
    public static String VERSION_URL = BASE_URL + "servlet/ApkVersionCtrl?top=1";//新版本


}
