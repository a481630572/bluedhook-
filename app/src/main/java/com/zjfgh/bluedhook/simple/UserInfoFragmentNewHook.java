package com.zjfgh.bluedhook.simple;

import java.util.Optional;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

// ====== 动态生成 user_info_extra_amap.xml 的等价UI ======
class UserInfoExtraAmapLayout {
    public LinearLayout root;
    public LinearLayout ll_aMap;
    public LinearLayout ll_location_root;
    public TextView tv_username;
    public ImageButton iv_clean_icon;
    public android.widget.ImageView iv_gps_icon;
    public LinearLayout ll_location_data;
    public TextView tv_longitude;
    public TextView tv_latitude;
    public TextView tv_location;
    public TextView tv_user_with_self_distance;
    public TextView tv_auto_location;

    public UserInfoExtraAmapLayout(Context context) {
        root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        ll_aMap = new LinearLayout(context);
        ll_aMap.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams llAMapParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        ll_aMap.setLayoutParams(llAMapParams);
        ll_aMap.setId(View.generateViewId());

        ll_location_root = new LinearLayout(context);
        ll_location_root.setOrientation(LinearLayout.VERTICAL);
        int locRootPadding = dp2px(context, 10);
        ll_location_root.setPadding(locRootPadding, locRootPadding, locRootPadding, locRootPadding);
        FrameLayout.LayoutParams llLocRootParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        llLocRootParams.setMargins(dp2px(context, 10), dp2px(context, 10), dp2px(context, 10), dp2px(context, 10));
        ll_location_root.setLayoutParams(llLocRootParams);
        ll_location_root.setId(View.generateViewId());

        // 第一行 横向
        LinearLayout row1 = new LinearLayout(context);
        row1.setGravity(android.view.Gravity.CENTER);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        tv_username = new TextView(context);
        tv_username.setTextSize(16f);
        tv_username.setTextColor(Color.parseColor("#FF00F0FF"));
        tv_username.setShadowLayer(10.0f, 0f, 0f, Color.parseColor("#8000F9FF"));
        tv_username.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        tv_username.setText("你好我有一个帽衫");
        tv_username.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_username.setId(View.generateViewId());

        // "收藏"按钮（⭐图标）
        iv_clean_icon = new ImageButton(context);
        LinearLayout.LayoutParams ivCleanParams = new LinearLayout.LayoutParams(
            dp2px(context, 40),
            dp2px(context, 40)
        );
        iv_clean_icon.setLayoutParams(ivCleanParams);
        iv_clean_icon.setId(View.generateViewId());
        iv_clean_icon.setContentDescription("收藏");
        iv_clean_icon.setBackgroundColor(Color.TRANSPARENT);
        iv_clean_icon.setScaleType(android.widget.ImageView.ScaleType.CENTER);

        // 用 Emoji "⭐" 作为图标
        iv_clean_icon.setImageDrawable(null);
        iv_clean_icon.setImageBitmap(textAsBitmap("⭐", 40, Color.parseColor("#FFD700")));

        row1.addView(tv_username);
        row1.addView(iv_clean_icon);

        // 第二行 横向
        LinearLayout row2 = new LinearLayout(context);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams row2Params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        row2Params.topMargin = dp2px(context, 10);
        row2.setLayoutParams(row2Params);

        iv_gps_icon = new android.widget.ImageView(context);
        LinearLayout.LayoutParams ivGpsParams = new LinearLayout.LayoutParams(
                dp2px(context, 100),
                dp2px(context, 100)
        );
        ivGpsParams.setMarginEnd(dp2px(context, 10));
        iv_gps_icon.setLayoutParams(ivGpsParams);
        iv_gps_icon.setId(View.generateViewId());
        iv_gps_icon.setContentDescription("TODO");

        ll_location_data = new LinearLayout(context);
        ll_location_data.setOrientation(LinearLayout.VERTICAL);
        ll_location_data.setPadding(dp2px(context, 2), dp2px(context, 2), dp2px(context, 2), dp2px(context, 2));
        LinearLayout.LayoutParams locDataParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        locDataParams.bottomMargin = dp2px(context, 8);
        ll_location_data.setLayoutParams(locDataParams);
        ll_location_data.setId(View.generateViewId());

        tv_longitude = new TextView(context);
        tv_longitude.setTextSize(16f);
        tv_longitude.setTextColor(Color.parseColor("#FF00FFA3"));
        tv_longitude.setText("经度：0.00000000");
        tv_longitude.setPadding(dp2px(context, 6), dp2px(context, 2), dp2px(context, 6), dp2px(context, 2));
        tv_longitude.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_longitude.setId(View.generateViewId());

        tv_latitude = new TextView(context);
        tv_latitude.setTextSize(16f);
        tv_latitude.setTextColor(Color.parseColor("#FF00FFA3"));
        tv_latitude.setText("纬度：0.00000000");
        tv_latitude.setPadding(dp2px(context, 6), dp2px(context, 2), dp2px(context, 6), dp2px(context, 2));
        tv_latitude.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_latitude.setId(View.generateViewId());

        tv_location = new TextView(context);
        tv_location.setTextSize(16f);
        tv_location.setTextColor(Color.parseColor("#FF00FFA3"));
        tv_location.setText("地理位置：中国北京");
        tv_location.setPadding(dp2px(context, 6), dp2px(context, 2), dp2px(context, 6), dp2px(context, 2));
        tv_location.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_location.setId(View.generateViewId());

        tv_user_with_self_distance = new TextView(context);
        tv_user_with_self_distance.setTextSize(16f);
        tv_user_with_self_distance.setTextColor(Color.parseColor("#FF00FFA3"));
        tv_user_with_self_distance.setText("当前和该用户的距离：0km");
        tv_user_with_self_distance.setPadding(dp2px(context, 6), dp2px(context, 2), dp2px(context, 6), dp2px(context, 2));
        tv_user_with_self_distance.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_user_with_self_distance.setId(View.generateViewId());

        ll_location_data.addView(tv_longitude);
        ll_location_data.addView(tv_latitude);
        ll_location_data.addView(tv_location);
        ll_location_data.addView(tv_user_with_self_distance);

        row2.addView(iv_gps_icon);
        row2.addView(ll_location_data);

        tv_auto_location = new TextView(context);
        tv_auto_location.setTextSize(16f);
        tv_auto_location.setTextColor(Color.parseColor("#FFFF0000"));
        tv_auto_location.setText("点我追踪位置");
        tv_auto_location.setPadding(dp2px(context, 6), dp2px(context, 2), dp2px(context, 6), dp2px(context, 2));

        LinearLayout.LayoutParams tvAutoLocParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvAutoLocParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;

        tv_auto_location.setLayoutParams(tvAutoLocParams);
        tv_auto_location.setGravity(android.view.Gravity.CENTER);
        tv_auto_location.setId(View.generateViewId());

        ll_location_root.addView(row1);
        ll_location_root.addView(row2);
        ll_location_root.addView(tv_auto_location);

        frameLayout.addView(ll_aMap);
        frameLayout.addView(ll_location_root);

        root.addView(frameLayout);
    }

    private int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}

// ====== 动态生成 user_info_fragment_new_extra.xml 的等价UI ======
class UserInfoFragmentNewExtraLayout {
    public LinearLayout root;
    public TextView tvUserRegTime;
    public Button userLocateBt;
    public TextView tvLastOperateAnchor; // 新增上线时间

    public UserInfoFragmentNewExtraLayout(Context context) {
        root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 第一行：注册时间
        LinearLayout row1 = new LinearLayout(context);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        int paddingPx = dp2px(context, 10);
        row1.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        row1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row1.setId(View.generateViewId());

        tvUserRegTime = new TextView(context);
        tvUserRegTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvUserRegTime.setTypeface(tvUserRegTime.getTypeface(), android.graphics.Typeface.BOLD);
        tvUserRegTime.setTextColor(Color.parseColor("#FF00FFA3"));
        tvUserRegTime.setPadding(0, 0, dp2px(context, 10), 0);
        tvUserRegTime.setMinWidth(0);
        tvUserRegTime.setMinHeight(0);
        tvUserRegTime.setIncludeFontPadding(false);
        tvUserRegTime.setText("注册：0000-00-00 00:00:00");
        tvUserRegTime.setLetterSpacing(0.05f);
        tvUserRegTime.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD));
        tvUserRegTime.setId(View.generateViewId());
        tvUserRegTime.setSingleLine(true);
        tvUserRegTime.setEllipsize(TextUtils.TruncateAt.END);

        // 占位用权重
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2f
        );
        tvUserRegTime.setLayoutParams(tvParams);

        userLocateBt = new Button(context);
        userLocateBt.setBackgroundColor(Color.TRANSPARENT);
        userLocateBt.setTextSize(14f);
        userLocateBt.setTypeface(userLocateBt.getTypeface(), android.graphics.Typeface.BOLD);
        userLocateBt.setTextColor(Color.parseColor("#FF00FFA3"));
        userLocateBt.setPadding(dp2px(context, 10), 0, dp2px(context, 10), 0);
        userLocateBt.setSingleLine(true);
        userLocateBt.setEllipsize(TextUtils.TruncateAt.END);
        userLocateBt.setMaxWidth(dp2px(context, 100));
        userLocateBt.setMinWidth(dp2px(context, 50));
        userLocateBt.setMaxLines(1);
        userLocateBt.setIncludeFontPadding(false);
        userLocateBt.setText("定位追踪");
        userLocateBt.setLetterSpacing(0.05f);
        userLocateBt.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD));
        userLocateBt.setId(View.generateViewId());

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        );
        userLocateBt.setLayoutParams(btnParams);

        row1.addView(tvUserRegTime);
        row1.addView(userLocateBt);

        // 第二行：上线时间
        LinearLayout row2 = new LinearLayout(context);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setPadding(paddingPx, 0, paddingPx, paddingPx);
        row2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row2.setId(View.generateViewId());

        tvLastOperateAnchor = new TextView(context);
        tvLastOperateAnchor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvLastOperateAnchor.setTypeface(tvLastOperateAnchor.getTypeface(), android.graphics.Typeface.BOLD);
        tvLastOperateAnchor.setTextColor(Color.parseColor("#FF00FFA3"));
        tvLastOperateAnchor.setPadding(0, 0, 0, 0);
        tvLastOperateAnchor.setMinWidth(0);
        tvLastOperateAnchor.setMinHeight(0);
        tvLastOperateAnchor.setIncludeFontPadding(false);
        tvLastOperateAnchor.setText(""); // 默认不显示
        tvLastOperateAnchor.setLetterSpacing(0.05f);
        tvLastOperateAnchor.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD));
        tvLastOperateAnchor.setId(View.generateViewId());
        tvLastOperateAnchor.setSingleLine(false); // 允许自动换行
        tvLastOperateAnchor.setEllipsize(null);

        LinearLayout.LayoutParams onlineParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvLastOperateAnchor.setLayoutParams(onlineParams);

        row2.addView(tvLastOperateAnchor);

        root.addView(row1);
        root.addView(row2);
    }

    private int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}


public class UserInfoFragmentNewHook {
    private static final String USER_INFO_ENTITY_CLASS = "com.soft.blued.ui.user.model.UserInfoEntity";
    private static final String TARGET_CLASS = "com.soft.blued.ui.user.fragment.UserInfoFragmentNew";
    private static final String TARGET_METHOD = "c";
    private static final double initialLat = 39.909088605597;
    private static final double initialLng = 116.39745423747772;
    private static final String LOCATION_HISTORY_FILE = "locations.txt";
    private static final String MODULE_PACKAGE_NAME = "com.zjfgh.bluedhook.simple";
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

    // 写入定位到目标程序 files/locations.txt，格式：纬度,经度,昵称（或备注）⭐yyyy年M月d号⭐，去重，每条单独一行
    private void writeLocationToCurrentFile(Context context, double latitude, double longitude, String nickname) {
        try {
            File file = new File(context.getFilesDir(), LOCATION_HISTORY_FILE);
            String dateStr = new SimpleDateFormat("yyyy年M月d号", Locale.getDefault()).format(new Date());
            String content = latitude + "," + longitude + "," + nickname + "⭐" + dateStr + "⭐" + "\n";
            // 去重
            boolean alreadyExists = false;
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(content.trim())) {
                        alreadyExists = true;
                        break;
                    }
                }
                reader.close();
            }
            if (!alreadyExists) {
                FileWriter writer = new FileWriter(file, true);
                writer.write(content); // 每次都带\n
                writer.close();
            }
        } catch (Exception e) {
            Log.e("UserInfoFragmentNewHook", "写入locations.txt异常: " + e);
        }
    }

    public void hookAnchorMonitorAddButton() {
        XposedHelpers.findAndHookMethod(TARGET_CLASS, classLoader, TARGET_METHOD,
                XposedHelpers.findClass(USER_INFO_ENTITY_CLASS, classLoader), new XC_MethodHook() {
                    private View lastView = null;

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
                            int fl_contentID = getSafeContext().getResources().getIdentifier("fl_content", "id", getSafeContext().getPackageName());
                            LinearLayout fl_content = flFeedFragmentContainer.findViewById(fl_contentID);

                            // ========== 动态生成 info_extra 区域 ==========
                            UserInfoFragmentNewExtraLayout userInfoFragmentNewExtra = new UserInfoFragmentNewExtraLayout(fl_content.getContext());

                            // 动态挂到parent
                            try {
                                int v_userinfo_card_bgID = getSafeContext().getResources().getIdentifier("v_userinfo_card_bg", "id", getSafeContext().getPackageName());
                                View v_userinfo_card_bg = flFeedFragmentContainer.findViewById(v_userinfo_card_bgID);
                                ViewGroup viewGroup = (ViewGroup) v_userinfo_card_bg.getParent();
                                ViewGroup user_info_profile_card = (ViewGroup) viewGroup.getParent();
                                user_info_profile_card.addView(userInfoFragmentNewExtra.root);
                            } catch (Throwable ignored) {}

                            // padding修正
                            try {
                                int cl_user_info_card_rootID = getSafeContext().getResources().getIdentifier("cl_user_info_card_root", "id", getSafeContext().getPackageName());
                                int ll_all_basic_infoID = getSafeContext().getResources().getIdentifier("ll_all_basic_info", "id", getSafeContext().getPackageName());
                                ViewGroup cl_user_info_card_root = flFeedFragmentContainer.findViewById(cl_user_info_card_rootID);
                                ViewGroup ll_all_basic_info = flFeedFragmentContainer.findViewById(ll_all_basic_infoID);
                                ll_all_basic_info.post(() -> {
                                    int extraHeight = userInfoFragmentNewExtra.root.getMeasuredHeight();
                                    cl_user_info_card_root.setPadding(0, extraHeight, 0, 0);
                                    ll_all_basic_info.invalidate();
                                    ll_all_basic_info.requestLayout();
                                });
                            } catch (Throwable ignored) {}

                            if (isHideLastDistance == 1) {
                                userInfoFragmentNewExtra.userLocateBt.setVisibility(View.GONE);
                            }

                            userInfoFragmentNewExtra.userLocateBt.setOnClickListener(v -> {
                                UserInfoExtraAmapLayout amapLayout = new UserInfoExtraAmapLayout(fl_content.getContext());
                                AMapHookHelper aMapHelper = new AMapHookHelper(fl_content.getContext(), fl_content.getContext().getClassLoader());
                                View aMapView = aMapHelper.createMapView();
                                amapLayout.ll_aMap.addView(aMapView);

                                amapLayout.tv_username.setText(name);

                                // 收藏按钮
                                ibvClean = amapLayout.iv_clean_icon;
                                ibvClean.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String latStr = amapLayout.tv_latitude.getText().toString();
                                        String lngStr = amapLayout.tv_longitude.getText().toString();
                                        double lat = 0, lng = 0;
                                        try {
                                            if (latStr.contains("："))
                                                lat = Double.parseDouble(latStr.substring(latStr.indexOf("：") + 1).trim());
                                            if (lngStr.contains("："))
                                                lng = Double.parseDouble(lngStr.substring(lngStr.indexOf("：") + 1).trim());
                                        } catch (Throwable ignore) {}
                                        String nickname = amapLayout.tv_username.getText().toString();
                                        writeLocationToCurrentFile(fl_content.getContext(), lat, lng, nickname);
                                        Toast.makeText(fl_content.getContext(), "已收藏坐标", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                String location = (String) XposedHelpers.getObjectField(userInfoEntity, "location");
                                amapLayout.tv_location.setText("真实位置(距离)：" + location);
                                amapLayout.tv_user_with_self_distance.setVisibility(View.GONE);

                                aMapHelper.onCreate(null);
                                aMapHelper.onResume();
                                aMapHelper.moveCamera(initialLat, initialLng, 5f);
                                aMapHelper.addMarker(initialLat, initialLng, "天安门");

                                amapLayout.tv_auto_location.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (amapLayout.tv_auto_location.getText().equals("自动追踪中...")) {
                                            return;
                                        } else {
                                            amapLayout.tv_auto_location.setText("自动追踪中...");
                                        }
                                        NetworkManager.getInstance().getAsync(NetworkManager.getBluedSetUsersLocationApi(initialLat, initialLng),
                                                AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                                    @Override
                                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                                                    @Override
                                                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                        NetworkManager.getInstance().getAsync(NetworkManager.getBluedUserBasicAPI(uid),
                                                                AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                                                                    @Override
                                                                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                                        try {
                                                                            if (response.code() == 200 && response.body() != null && !response.body().toString().isEmpty()) {
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
                                                                                        amapLayout.tv_user_with_self_distance.post(() -> {
                                                                                            amapLayout.tv_user_with_self_distance.setText("当前虚拟距离：" + DistanceConverter.formatDistance(distanceKm));
                                                                                            amapLayout.tv_user_with_self_distance.setVisibility(View.VISIBLE);
                                                                                        });
                                                                                        LocationTracker tracker = new LocationTracker(aMapHelper, uid, classLoader, fl_content);
                                                                                        tracker.startTracking(initialLat, initialLng, distanceKm, 15, new LocationTracker.LocationTrackingCallback() {
                                                                                            @Override public void onInitialLocation(double lat, double lng, double dKm) {}
                                                                                            @Override public void onProbeLocation(double lat, double lng) {}
                                                                                            @Override public void onProbeDistance(double dKm) {}
                                                                                            @Override public void onIntersectionLocation(double lat, double lng) {}
                                                                                            @Override public void onIntersectionDistance(double lat, double lng, double dKm) {}
                                                                                            @Override
                                                                                            public void onNewCenterLocation(double lat, double lng, double dKm) {
                                                                                                amapLayout.tv_latitude.post(() -> {
                                                                                                    amapLayout.tv_latitude.setText("纬度：" + lat);
                                                                                                    amapLayout.tv_longitude.setText("经度：" + lng);
                                                                                                    amapLayout.tv_user_with_self_distance.setText("当前虚拟距离：" + DistanceConverter.formatDistance(dKm));
                                                                                                });
                                                                                            }
                                                                                            @Override
                                                                                            public void onFinalLocation(double lat, double lng, double dKm) {
                                                                                                amapLayout.tv_latitude.post(() -> {
                                                                                                    amapLayout.tv_latitude.setText("经度：" + lat);
                                                                                                    amapLayout.tv_longitude.setText("纬度：" + lng);
                                                                                                    amapLayout.tv_auto_location.setText("追踪完成");
                                                                                                });
                                                                                            }
                                                                                            @NonNull
                                                                                            private CoordinateTransform getCoordinateTransform() {
                                                                                                CRSFactory crsFactory = new CRSFactory();
                                                                                                CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
                                                                                                CoordinateReferenceSystem gcj02 = crsFactory.createFromParameters("GCJ02", "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext +no_defs");
                                                                                                return new BasicCoordinateTransform(wgs84, gcj02);
                                                                                            }
                                                                                            @Override public void onError(String message) {
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
                                    aMapHelper.addMarker(lat, lng, "纬度：" + lat + "\n经度：" + lng);
                                    amapLayout.tv_latitude.setText("纬度：" + lat);
                                    amapLayout.tv_longitude.setText("经度：" + lng);
                                    NetworkManager.getInstance().getAsync(NetworkManager.getBluedSetUsersLocationApi(lat, lng),
                                            AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                                @Override
                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                                                @Override
                                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                    NetworkManager.getInstance().getAsync(NetworkManager.getBluedUserBasicAPI(uid),
                                                            AuthManager.auHook(false, classLoader, fl_content.getContext()), new Callback() {
                                                                @Override
                                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                                                                @Override
                                                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                                    try {
                                                                        if (response.code() == 200 && response.body() != null && !response.body().toString().isEmpty()) {
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
                                                                                    amapLayout.tv_user_with_self_distance.post(() -> {
                                                                                        amapLayout.tv_user_with_self_distance.setText("当前虚拟距离：" + DistanceConverter.formatDistance(distanceKm));
                                                                                        amapLayout.tv_user_with_self_distance.setVisibility(View.VISIBLE);
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

                                CustomPopupWindow aMapPopupWindow = new CustomPopupWindow((Activity) fl_content.getContext(), amapLayout.root, Color.parseColor("#FF0A121F"));
                                aMapPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
                                aMapPopupWindow.showAtCenter();
                                aMapPopupWindow.setOnDismissListener(() -> {
                                    aMapHelper.onPause();
                                    aMapHelper.onDestroy();
                                });
                            });

                            // 注册时间
                            String registrationTimeEncrypt = (String) XposedHelpers.getObjectField(userInfoEntity, "registration_time_encrypt");
                            String registrationTime = ModuleTools.AesDecrypt(registrationTimeEncrypt);
                            if (!registrationTime.isEmpty()) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String formattedDate = sdf.format(new Date(Long.parseLong(registrationTime) * 1000L));
                                userInfoFragmentNewExtra.tvUserRegTime.setText("注册：" + formattedDate);
                                userInfoFragmentNewExtra.tvUserRegTime.setOnClickListener(ev -> ModuleTools.copyToClipboard(contextRef.get(), "注册" + formattedDate, formattedDate));
                                userInfoFragmentNewExtra.tvUserRegTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                userInfoFragmentNewExtra.tvUserRegTime.setVisibility(View.VISIBLE);
                            } else {
                                userInfoFragmentNewExtra.tvUserRegTime.setVisibility(View.GONE);
                            }

                            // ====== 上线时间功能优化 ======
                            // 只为主播且隐藏上线时显示
                            if (isHideLastOperate == 1 && isAnchor == 1) {
                                userInfoFragmentNewExtra.tvLastOperateAnchor.setText(""); // 先清空
                                NetworkManager.getInstance().getAsync(
                                        NetworkManager.getBluedLiveSearchAnchorApi(name),
                                        AuthManager.auHook(false, classLoader, fl_content.getContext()),
                                        new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                // 失败时不显示任何内容
                                                userInfoFragmentNewExtra.tvLastOperateAnchor.post(() ->
                                                    userInfoFragmentNewExtra.tvLastOperateAnchor.setText("")
                                                );
                                            }
                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                                                try {
                                                    if (response.code() == 200 && response.body() != null && !response.body().toString().isEmpty()) {
                                                        JSONObject jsonResponse = new JSONObject(response.body().string());
                                                        JSONArray usersArray = jsonResponse.getJSONArray("data");
                                                        boolean found = false;
                                                        for (int i = 0; i < usersArray.length(); i++) {
                                                            JSONObject user = usersArray.getJSONObject(i);
                                                            long lastOperate = user.optLong("last_operate", 0);
                                                            int anchorUid = user.getInt("uid");
                                                            if (String.valueOf(anchorUid).equals(uid) && lastOperate > 0) {
                                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                                String date = sdf.format(new Date(lastOperate * 1000L));
                                                                userInfoFragmentNewExtra.tvLastOperateAnchor.post(() ->
                                                                    userInfoFragmentNewExtra.tvLastOperateAnchor.setText("上线：" + date)
                                                                );
                                                                found = true;
                                                                break;
                                                            }
                                                        }
                                                        if (!found) {
                                                            userInfoFragmentNewExtra.tvLastOperateAnchor.post(() ->
                                                                userInfoFragmentNewExtra.tvLastOperateAnchor.setText("")
                                                            );
                                                        }
                                                    } else {
                                                        userInfoFragmentNewExtra.tvLastOperateAnchor.post(() ->
                                                            userInfoFragmentNewExtra.tvLastOperateAnchor.setText("")
                                                        );
                                                    }
                                                } catch (JSONException | IOException e) {
                                                    userInfoFragmentNewExtra.tvLastOperateAnchor.post(() ->
                                                        userInfoFragmentNewExtra.tvLastOperateAnchor.setText("")
                                                    );
                                                    Log.e("UserInfoFragmentNewHook", "JSONException" + e);
                                                }
                                            }
                                        }
                                );
                            } else {
                                userInfoFragmentNewExtra.tvLastOperateAnchor.setText("");
                            }
                        }
                    }
                });
    }

    public Context getSafeContext() {
        Context context = contextRef.get();
        if (context == null) {
            throw new IllegalStateException("Context was garbage collected");
        }
        return context;
    }
}
