package com.zjfgh.bluedhook.simple;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetworkManager {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // 单例实例
    private static NetworkManager instance;
    private final OkHttpClient client;
    private final Map<String, String> commonHeaders = new HashMap<>();

    // 私有构造函数
    private NetworkManager() {
        // 配置OkHttpClient
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    // 添加通用请求头
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    // 添加所有通用请求头
                    for (Map.Entry<String, String> header : commonHeaders.entrySet()) {
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }

                    Request request = requestBuilder
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    // 获取单例实例
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    // 获取金山文档认证API地址
    public static String getJinShanDocBluedAuthApi() {
        return "https://www.kdocs.cn/api/v3/ide/file/cnekSVuQCT6q/script/V2-4AAwpbpDqOyM94aQ6jBbNH/sync_task";
    }

    public static String getJinShanDocBluedUsersApi() {
        return "https://www.kdocs.cn/api/v3/ide/file/cfPwN1X4EwjS/script/V2-5yNhJpC575PN5hJNUPnhs/sync_task";
    }

    public static String getBluedUserCardAPI(String uid) {
        return "https://live.blued.cn/live/user/card?uid=" + uid + "&anchor=0";
    }

    public static String getBluedUserBasicAPI(String uid) {
        return "https://social.blued.cn/users/" + uid + "/basic";
    }

    public static String getBluedUserAPI(String uid) {
        return "https://social.blued.cn/users/" + uid;
    }

    public static String getBluedLiveUserCard(String uid) {
        return "https://live.irisgw.cn/live/user/card?uid=" + uid + "&anchor=" + uid;
    }

    public static String getBluedAnchorFansAPI(int page) {
        return "https://argo.blued.cn/live/anchor-fans/list?page=" + page;
    }

    public static String getAnchorFansFreeGoodsAPI() {
        return "https://pay.blued.cn/goods/anchor-fans/free-goods";
    }

    public static String getBuyGoodsApi() {
        return "https://pay.irisgw.cn/buy/goods";
    }

    public static String getBluedSetUsersLocationApi(double latitude, double longitude) {
        return "https://argo.blued.cn/users?latitude=" + latitude + "&column=3&start=0&sort_by=nearby&filters=%7B%7D&android_version=10&ssid=26f6b938-36ba-40b9-bcaf-818907d80b30&next_min_dist=4814.6540901941&scroll_type=0&android_model=XiaomiMI+9&extra_info=3wtqjcU7ONTbII39Kp1XcA2LlhUkHnMuy%2FyZkqBS%2BiYO4CFR4E5nR5oGa8EUBTPq%2F%2Fq4X1zNZJmxLa2fsbR5F6BD&limit=60&from=list&android_oaid=f275985c1dffab68&cid=0&longitude=" + longitude;
    }

    public static String getBluedLiveSearchAnchorApi(String content) {
        return "https://live.irisgw.cn/live/search-anchor?content=" + content;
    }

    public static String getBluedPicSaveStatusApi(String uid) {
        return "https://social.irisgw.cn/users/pic/save/status?uid=" + uid;
    }

    public static String getBluedUserConnTypeApi(int start) {
        return "https://argo.blued.cn/users?conn_type=2&country=CN&wx_sdk=5.5.8&channel=a66880&sort_by=nearby&ua=Mozilla%2F5.0+%28Linux%3B+Android+14%3B+23127PN0CC+Build%2FUKQ1.230804.001%3B+wv%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chrome%2F126.0.6478.71+Mobile+Safari%2F537.36&limit=60&model=23127PN0CC&make=Xiaomi&os=Android&column=3&start=" + start + "&filters=%7B%22filter_real_switch%22%3A%220%22%2C%22height%22%3A%220-500%22%2C%22weight%22%3A%220-1000%22%2C%22age%22%3A%220-300%22%2C%22time_span%22%3A%220-max%22%2C%22geo_reach%22%3A%220-max%22%2C%22filter_album_open%22%3A%220%22%7D";
    }

    public static String getUsersRecommendApi() {
        return "https://social.irisgw.cn/users/recommend";
    }

    // 获取配置好的OkHttpClient实例
    public OkHttpClient getClient() {
        return client;
    }

    // ========== 通用请求头管理 ==========

    // 添加通用请求头
    public void addCommonHeader(String name, String value) {
        commonHeaders.put(name, value);
    }

    // 移除通用请求头
    public void removeCommonHeader(String name) {
        commonHeaders.remove(name);
    }

    // 清除所有通用请求头
    public void clearCommonHeaders() {
        commonHeaders.clear();
    }

    // ========== 带单独请求头的请求方法 ==========

    // 同步GET请求（带单独请求头）
    public Response get(String url, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        addHeaders(requestBuilder, headers);
        return client.newCall(requestBuilder.build()).execute();
    }

    // 同步GET请求（不带单独请求头）
    public Response get(String url) throws IOException {
        return get(url, null);
    }

    // 异步GET请求（带单独请求头）
    public void getAsync(String url, Map<String, String> headers, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        addHeaders(requestBuilder, headers);
        client.newCall(requestBuilder.build()).enqueue(callback);
    }

    // 异步GET请求（不带单独请求头）
    public void getAsync(String url, Callback callback) {
        getAsync(url, null, callback);
    }

    // 同步POST请求（带单独请求头）
    public Response post(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        addHeaders(requestBuilder, headers);
        return client.newCall(requestBuilder.build()).execute();
    }

    // 同步POST请求（不带单独请求头）
    public Response post(String url, String json) throws IOException {
        return post(url, json, null);
    }

    // 异步POST请求（带单独请求头）
    public void postAsync(String url, String json, Map<String, String> headers, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        addHeaders(requestBuilder, headers);
        client.newCall(requestBuilder.build()).enqueue(callback);
    }

    // 异步POST请求（不带单独请求头）
    public void postAsync(String url, String json, Callback callback) {
        postAsync(url, json, null, callback);
    }

    // ========== 私有方法 ==========

    // 添加请求头到Request.Builder
    private void addHeaders(Request.Builder requestBuilder, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
    }
}
