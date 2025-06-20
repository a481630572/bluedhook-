package com.zjfgh.bluedhook.simple;
import java.util.Optional;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;
import com.zjfgh.bluedhook.simple.module.UserCardResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoFragmentNewHook {
    private static final String USER_INFO_ENTITY_CLASS = "com.soft.blued.ui.user.model.UserInfoEntity";
    private static final String TARGET_CLASS = "com.soft.blued.ui.user.fragment.UserInfoFragmentNew";
    private static final String TARGET_METHOD = "c";
    private static final String COLOR_PRIMARY = "#33539E";
    private static final String COLOR_SECONDARY = "#6E89A2";
    private static final int CORNER_RADIUS = 50;
    private static final int PADDING_HORIZONTAL = 30;
    private static final int PADDING_VERTICAL = 20;
    private static final int MARGIN_HORIZONTAL = 30;
    private static final double initialLat = 39.909088605597;
    private static final double initialLng = 116.39745423747772;
    private static UserInfoFragmentNewHook instance;
    private final WeakReference<Context> contextRef;
    private final ClassLoader classLoader;
    private final XModuleResources modRes;

    public static synchronized UserInfoFragmentNewHook getInstance(Context context, XModuleResources modRes) {
        if (instance == null) {
            instance = new UserInfoFragmentNewHook(context, modRes);
        }
        return instance;
    }

    private UserInfoFragmentNewHook(Context context, XModuleResources modRes) {
        this.contextRef = new WeakReference<>(context);
        this.classLoader = context.getClassLoader();
        this.modRes = modRes;
        hookAnchorMonitorAddButton();
    }

    private ImageButton ibvClean;
    private ObjectAnimator rotateAnim;
    private final Handler handler = new Handler();

    public void hookAnchorMonitorAddButton() {
        final SQLiteManagement dbManager = SQLiteManagement.getInstance();
        XposedHelpers.findAndHookMethod(TARGET_CLASS, classLoader, TARGET_METHOD,
                XposedHelpers.findClass(USER_INFO_ENTITY_CLASS, classLoader), new XC_MethodHook() {
                    private View lastView = null;
                    String relationship;

                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }

                    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                        SettingItem settingItem = SQLiteManagement.getInstance().getSettingByFunctionId(SettingsViewCreator.ANCHOR_MONITOR_LIVE_HOOK);
    if (Optional.ofNullable(settingItem)
            .map(SettingItem::isSwitchOn)
            .orElse(false)) {
        View currentView = (View) XposedHelpers.getObjectField(param.thisObject, "U");
        if (currentView != lastView) {
            lastView = currentView;
            return;
                            }
                            Object userInfoEntity = param.args[0];
                            String uid = (String) XposedHelpers.getObjectField(userInfoEntity, "uid");
                            int isAnchor = XposedHelpers.getIntField(userInfoEntity, "anchor");
                            int isHideLastOperate = XposedHelpers.getIntField(userInfoEntity, "is_hide_last_operate");
                            int isHideLastDistance = XposedHelpers.getIntField(userInfoEntity, "is_hide_distance");
                            String name = (String) XposedHelpers.getObjectField(userInfoEntity, "name");
                            FrameLayout flFeedFragmentContainer = (FrameLayout) XposedHelpers.getObjectField(param.thisObject, "b");
                            @SuppressLint("DiscouragedApi") int fl_contentID = getSafeContext().getResources().getIdentifier("fl_content", "id", getSafeContext().getPackageName());
                            LinearLayout fl_content = flFeedFragmentContainer.findViewById(fl_contentID);
                            LayoutInflater inflater = (LayoutInflater) fl_content.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            LinearLayout userInfoFragmentNewExtra = (LinearLayout) inflater.inflate(modRes.getLayout(R.layout.user_info_fragment_new_extra), null);
                            @SuppressLint("DiscouragedApi") int v_userinfo_card_bgID = getSafeContext().getResources().getIdentifier("v_userinfo_card_bg", "id", getSafeContext().getPackageName());
                            View v_userinfo_card_bg = flFeedFragmentContainer.findViewById(v_userinfo_card_bgID);
                            ViewGroup viewGroup = (ViewGroup) v_userinfo_card_bg.getParent();
                            ViewGroup user_info_profile_card = (ViewGroup) viewGroup.getParent();
                            @SuppressLint("DiscouragedApi") int ll_all_basic_infoID = getSafeContext().getResources().getIdentifier("ll_all_basic_info", "id", getSafeContext().getPackageName());
                            ViewGroup ll_all_basic_info = flFeedFragmentContainer.findViewById(ll_all_basic_infoID);
                            user_info_profile_card.addView(userInfoFragmentNewExtra);
                            @SuppressLint("DiscouragedApi") int cl_user_info_card_rootID = getSafeContext().getResources().getIdentifier("cl_user_info_card_root", "id", getSafeContext().getPackageName());
                            ViewGroup cl_user_info_card_root = flFeedFragmentContainer.findViewById(cl_user_info_card_rootID);
                            ll_all_basic_info.post(() -> {
                                int extraHeight = userInfoFragmentNewExtra.getMeasuredHeight();
                                cl_user_info_card_root.setPadding(0, extraHeight, 0, 0);
                                ll_all_basic_info.invalidate();
                                ll_all_basic_info.requestLayout();
                            });
                            Button userInfoExtraLocate = userInfoFragmentNewExtra.findViewById(R.id.user_locate_bt);
                            if (isHideLastDistance == 1) {
                                userInfoExtraLocate.setVisibility(View.GONE);
                            }
                            userInfoExtraLocate.setBackground(modRes.getDrawable(R.drawable.bg_tech_tag, null));
                            userInfoExtraLocate.setOnClickListener(v -> {
                                LinearLayout userInfoExtraAMap = (LinearLayout) inflater.inflate(modRes.getLayout(R.layout.user_info_extra_amap), null);
                                LinearLayout llAMap = userInfoExtraAMap.findViewById(R.id.ll_aMap);
                                LinearLayout llLocationData = userInfoExtraAMap.findViewById(R.id.ll_location_data);
                                ImageView ivGpsIcon = userInfoExtraAMap.findViewById(R.id.iv_gps_icon);
                                ivGpsIcon.setImageDrawable(modRes.getDrawable(R.drawable.gps_location_icon1, null));
                                llLocationData.setBackground(modRes.getDrawable(R.drawable.bg_tech_tag, null));
                                LinearLayout llLocationRoot = userInfoExtraAMap.findViewById(R.id.ll_location_root);
                                llLocationRoot.setBackground(modRes.getDrawable(R.drawable.bg_tech_item_inner, null));
                                AMapHookHelper aMapHelper = new AMapHookHelper(fl_content.getContext(), fl_content.getContext().getClassLoader());
                                ibvClean = userInfoExtraAMap.findViewById(R.id.iv_clean_icon);
                                ibvClean.setBackground(modRes.getDrawable(R.drawable.tech_button_bg, null));
                                ibvClean.setImageDrawable(modRes.getDrawable(R.drawable.ic_refresh, null));
                                // 设置旋转动画
                                rotateAnim = ObjectAnimator.ofFloat(ibvClean, "rotation", 0f, 360f);
                                rotateAnim.setDuration(800);
                                rotateAnim.setInterpolator(new LinearInterpolator());
                                rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
                                ibvClean.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startRefreshAnimation();

                                        // 模拟3秒后刷新完成
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                aMapHelper.clearAllOverlays();
                                                stopRefreshAnimation();
                                            }
                                        }, 500);
                                    }
                                });
                                View aMapView = aMapHelper.createMapView();
                                llAMap.addView(aMapView);
                                TextView tv_username = userInfoExtraAMap.findViewById(R.id.tv_username);
                                tv_username.setText(name);
                                TextView tvAutoLocation = userInfoExtraAMap.findViewById(R.id.tv_auto_location);
                                tvAutoLocation.setBackground(modRes.getDrawable(R.drawable.bg_auto_location_button, null));
                                TextView tvLongitude = userInfoExtraAMap.findViewById(R.id.tv_longitude);
                                TextView tvLatitude = userInfoExtraAMap.findViewById(R.id.tv_latitude);
                                TextView tvLocation = userInfoExtraAMap.findViewById(R.id.tv_location);
                                String location = (String) XposedHelpers.getObjectField(userInfoEntity, "location");
                                tvLocation.setText("真实位置(距离)：" + location);
                                TextView tvUserWithSelfDistance = userInfoExtraAMap.findViewById(R.id.tv_user_with_self_distance);
                                tvUserWithSelfDistance.setVisibility(View.GONE);
                                aMapHelper.onCreate(null);
                                aMapHelper.onResume();
                                aMapHelper.moveCamera(initialLat, initialLng, 5f);
                                aMapHelper.addMarker(initialLat, initialLng, "天安门");
                                tvAutoLocation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (tvAutoLocation.getText().equals("自动追踪中...")) {
                                            return;
                                        } else {
                                            tvAutoLocation.setText("自动追踪中...");
                                        }
                                        NetworkManager.getInstance().getAsync(NetworkManager.getBluedSetUsersLocationApi(initialLat, initialLng), AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                NetworkManager.getInstance().getAsync(NetworkManager.getBluedUserBasicAPI(uid), AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                                    @Override
                                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                                    }

                                                    @Override
                                                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                        try {
                                                            if (response.code() == 200 && !response.body().toString().isEmpty()) {
                                                                String jsonStr = response.body().string();
                                                                JSONObject json = new JSONObject(jsonStr);
                                                                String message = json.getString("message");
                                                                Log.i("BluedHook", "message：" + message);
                                                                JSONArray dataArray = json.getJSONArray("data");
                                                                if (dataArray.length() > 0) {
                                                                    JSONObject userData = dataArray.getJSONObject(0);
                                                                    int isHideDistance = userData.getInt("is_hide_distance");
                                                                    double distanceKm = userData.getDouble("distance");
                                                                    if (isHideDistance == 0) {
                                                                        aMapHelper.addCircle(initialLat, initialLat, DistanceConverter.kmToMeters(distanceKm), "#003399FF", "#603399FF");
                                                                        tvUserWithSelfDistance.post(() -> {
                                                                            tvUserWithSelfDistance.setText("当前虚拟距离：" + DistanceConverter.formatDistance(distanceKm));
                                                                            tvUserWithSelfDistance.setVisibility(View.VISIBLE);
                                                                        });
                                                                        LocationTracker tracker = new LocationTracker(aMapHelper, uid, classLoader, fl_content);
                                                                        // 开始追踪并设置回调
                                                                        tracker.startTracking(initialLat, initialLng, distanceKm, 15, new LocationTracker.LocationTrackingCallback() {
                                                                            @Override
                                                                            public void onInitialLocation(double lat, double lng, double distanceKm) {
                                                                                Log.d("LocationTracker", String.format("初始位置: %.6f, %.6f, 距离: %.3fkm", lat, lng, distanceKm));
                                                                            }

                                                                            @Override
                                                                            public void onProbeLocation(double lat, double lng) {
                                                                                Log.d("LocationTracker", String.format("探测点位置: %.6f, %.6f", lat, lng));
                                                                            }

                                                                            @Override
                                                                            public void onProbeDistance(double distanceKm) {
                                                                                Log.d("LocationTracker", String.format("探测点距离: %.3fkm", distanceKm));
                                                                            }

                                                                            @Override
                                                                            public void onIntersectionLocation(double lat, double lng) {
                                                                                Log.d("LocationTracker", String.format("交点位置: %.6f, %.6f", lat, lng));
                                                                            }

                                                                            @Override
                                                                            public void onIntersectionDistance(double lat, double lng, double distanceKm) {
                                                                                Log.d("LocationTracker", String.format("交点距离: %.6f, %.6f, 距离: %.3fkm", lat, lng, distanceKm));
                                                                            }

                                                                            @Override
                                                                            public void onNewCenterLocation(double lat, double lng, double distanceKm) {
                                                                                tvLatitude.post(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        tvLatitude.setText("纬度：" + lat);
                                                                                        tvLongitude.setText("经度：" + lng);
                                                                                        tvUserWithSelfDistance.setText("当前虚拟距离：" + DistanceConverter.formatDistance(distanceKm));
                                                                                    }
                                                                                });
                                                                                Log.d("LocationTracker", String.format("新中心点: %.6f, %.6f, 距离: %.3fkm", lat, lng, distanceKm));
                                                                            }

                                                                            @Override
                                                                            public void onFinalLocation(double lat, double lng, double distanceKm) {
                                                                                Log.d("LocationTracker", String.format("最终位置: %.6f, %.6f, 距离: %.3fkm", lat, lng, distanceKm));
                                                                                tvLatitude.post(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        tvLatitude.setText("经度：" + lat);
                                                                                        tvLongitude.setText("纬度：" + lng);
                                                                                        tvAutoLocation.setText("追踪完成");
                                                                                    }
                                                                                });

                                                                            }

                                                                            @NonNull
                                                                            private CoordinateTransform getCoordinateTransform() {
                                                                                CRSFactory crsFactory = new CRSFactory();
                                                                                CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326"); // WGS84
                                                                                CoordinateReferenceSystem gcj02 = crsFactory.createFromParameters("GCJ02", "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext +no_defs");
                                                                                return new BasicCoordinateTransform(wgs84, gcj02);
                                                                            }

                                                                            @Override
                                                                            public void onError(String message) {
                                                                                Log.e("LocationTracker", "错误: " + message);
                                                                            }
                                                                        });
                                                                    } else {
                                                                        Log.i("BluedHook", "用户隐藏了距离信息");
                                                                    }
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("UserInfoFragmentNewHook", "Hook位置\nhookAnchorMonitorAddButton.获取用户距离异常：\n" + e);
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                aMapHelper.setOnMapClickListener((lat, lng) -> {
                                    aMapHelper.addMarker(lat, lng, "纬度：" + lat + "\n" +
                                            "经度：" + lng);
                                    tvLatitude.setText("纬度：" + lat);
                                    tvLongitude.setText("经度：" + lng);
                                    NetworkManager.getInstance().getAsync(NetworkManager.getBluedSetUsersLocationApi(lat, lng), AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                        @Override
                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                        }

                                        @Override
                                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                                            NetworkManager.getInstance().getAsync(NetworkManager.getBluedUserBasicAPI(uid), AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                                @Override
                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                                }

                                                @Override
                                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                    try {
                                                        if (response.code() == 200 && !response.body().toString().isEmpty()) {
                                                            String jsonStr = response.body().string();
                                                            JSONObject json = new JSONObject(jsonStr);
                                                            String message = json.getString("message");
                                                            Log.i("BluedHook", "message：" + message);
                                                            JSONArray dataArray = json.getJSONArray("data");
                                                            if (dataArray.length() > 0) {
                                                                JSONObject userData = dataArray.getJSONObject(0);
                                                                int isHideDistance = userData.getInt("is_hide_distance");
                                                                double distanceKm = userData.getDouble("distance");
                                                                if (isHideDistance == 0) {
                                                                    aMapHelper.addCircle(lat, lng, DistanceConverter.kmToMeters(distanceKm), "#003399FF", "#603399FF");
                                                                    tvUserWithSelfDistance.post(() -> {
                                                                        tvUserWithSelfDistance.setText("当前虚拟距离：" + DistanceConverter.formatDistance(distanceKm));
                                                                        tvUserWithSelfDistance.setVisibility(View.VISIBLE);
                                                                    });
                                                                } else {
                                                                    Log.i("BluedHook", "用户隐藏了距离信息");
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e("UserInfoFragmentNewHook", "Hook位置\nhookAnchorMonitorAddButton.获取用户距离异常：\n" + e);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                });
                                CustomPopupWindow aMapPopupWindow = new CustomPopupWindow((Activity) fl_content.getContext(), userInfoExtraAMap, Color.parseColor("#FF0A121F"));
                                aMapPopupWindow.setBackgroundDrawable(modRes.getDrawable(R.drawable.bg_tech_space, null));
                                aMapPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
                                aMapPopupWindow.showAtCenter();
                                aMapPopupWindow.setOnDismissListener(() -> {
                                    aMapHelper.onPause();
                                    aMapHelper.onDestroy();
                                });
                            });
                            if (isHideLastOperate == 1 && isAnchor == 1) {
                                @SuppressLint("DiscouragedApi") ViewGroup rlBasicInfoRoot = flFeedFragmentContainer.findViewById(getSafeContext().getResources().getIdentifier("rl_basic_info_root", "id", getSafeContext().getPackageName()));
                                HorizontalScrollView horizontalScrollView = (HorizontalScrollView) rlBasicInfoRoot.getChildAt(0);
                                LinearLayout linearLayout = (LinearLayout) horizontalScrollView.getChildAt(0);
                                TextView tvLastOperateAnchor = new TextView(currentView.getContext());
                                tvLastOperateAnchor.setTextColor(Color.parseColor("#00FFA3"));
                                linearLayout.addView(tvLastOperateAnchor);
                                NetworkManager.getInstance().getAsync(NetworkManager.getBluedLiveSearchAnchorApi(name), AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        try {
                                            if (response.code() == 200 && !response.body().toString().isEmpty()) {
                                                JSONObject jsonResponse = new JSONObject(response.body().string());
                                                JSONArray usersArray = jsonResponse.getJSONArray("data");
                                                for (int i = 0; i < usersArray.length(); i++) {
                                                    JSONObject user = usersArray.getJSONObject(i);
                                                    user.getInt("anchor");
                                                    user.getString("avatar");
                                                    long lastOperate = user.getLong("last_operate");
                                                    user.getString("name");
                                                    int anchorUid = user.getInt("uid");
                                                    if (String.valueOf(anchorUid).equals(uid)) {
                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                        String date = sdf.format(new Date(lastOperate * 1000L)); // 乘以1000转为毫秒
                                                        tvLastOperateAnchor.post(() -> tvLastOperateAnchor.setText("(上线时间：" + date + ")"));
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("UserInfoFragmentNewHook", "JSONException" + e);
                                        }
                                    }
                                });
                            } else {
                                Log.i("BluedHook", "非主播无法显示保密上线时间");
                            }
                            String registrationTimeEncrypt = (String) XposedHelpers.getObjectField(userInfoEntity, "registration_time_encrypt");
                            String registrationTime = ModuleTools.AesDecrypt(registrationTimeEncrypt);
                            TextView tvUserRegTime = userInfoFragmentNewExtra.findViewById(R.id.tv_user_reg_time);
                            tvUserRegTime.setBackground(modRes.getDrawable(R.drawable.bg_tech_tag, null));
                            if (!registrationTime.isEmpty()) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String formattedDate = sdf.format(new Date(Long.parseLong(registrationTime) * 1000L));
                                tvUserRegTime.setText("注册时间：" + formattedDate);
                                tvUserRegTime.setOnClickListener(v -> ModuleTools.copyToClipboard(contextRef.get(), "注册时间" + formattedDate, formattedDate));
                                tvUserRegTime.setTextSize(13f);
                                tvUserRegTime.setVisibility(View.VISIBLE);
                            } else {
                                tvUserRegTime.setVisibility(View.GONE);
                            }
                        }

                    }

                    private void startRefreshAnimation() {
                        // 启动旋转动画
                        rotateAnim.start();

                        // 按钮缩小效果
                        ibvClean.animate()
                                .scaleX(0.9f)
                                .scaleY(0.9f)
                                .setDuration(200)
                                .start();
                    }

                    @SuppressLint("UseCompatLoadingForDrawables")
                    private void stopRefreshAnimation() {
                        // 停止旋转并恢复原位
                        rotateAnim.cancel();
                        ibvClean.setRotation(0f);

                        // 恢复按钮大小
                        ibvClean.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();

                        // 添加完成效果（可选）
                        ibvClean.setImageDrawable(modRes.getDrawable(R.drawable.ic_done, null));
                        handler.postDelayed(new Runnable() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void run() {
                                ibvClean.setImageDrawable(modRes.getDrawable(R.drawable.ic_refresh, null));
                            }
                        }, 1000);
                    }
                });
    }

    // 使用时检查 Context 是否还存在
    public Context getSafeContext() {
        Context context = contextRef.get();
        if (context == null) {
            throw new IllegalStateException("Context was garbage collected");
        }
        return context;
    }
}
