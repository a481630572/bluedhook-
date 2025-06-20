package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Objects;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BluedHook implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) {
        if (param.packageName.equals("com.soft.blued")) {
            XposedHelpers.findAndHookMethod("com.soft.blued.StubWrapperProxyApplication", param.classLoader, "initProxyApplication", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context bluedContext = (Context) param.args[0];
                    AppContainer.getInstance().setBluedContext(bluedContext);
                    AppContainer.getInstance().setClassLoader(bluedContext.getClassLoader());
                    Toast.makeText(bluedContext, "外挂成功！", Toast.LENGTH_LONG).show();

                    // 模块加载时确保设置项已初始化
                    new SettingsViewCreator(bluedContext);

                    // 初始化个人主页Hook（原逻辑）
                    UserInfoFragmentNewHook.getInstance(bluedContext, AppContainer.getInstance().getModuleRes());

                    // 自动初始化主播开播提醒Hook
                    initializeAnchorMonitorHook(bluedContext);
                }
            });
        }
    }

    /**
     * 初始化主播开播提醒Hook（ID=1）
     */
    private void initializeAnchorMonitorHook(Context context) {
        try {
            SQLiteManagement dbManager = SQLiteManagement.getInstance();
            // 读取设置项（此时已保证初始化）
            SettingItem setting = dbManager.getSettingByFunctionId(SettingsViewCreator.ANCHOR_MONITOR_LIVE_HOOK);
            if (setting != null && setting.isSwitchOn()) {
                Log.d("BluedHook", "自动加载主播开播提醒Hook（ID=1）");
                // AnchorMonitorHook.getInstance().startMonitor(); // 如无实现请注释掉
            }
        } catch (Exception e) {
            Log.e("BluedHook", "初始化主播开播提醒Hook失败", e);
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resParam) {
        if (resParam.packageName.equals("com.soft.blued")) {
            String modulePath = AppContainer.getInstance().getModulePath();
            XModuleResources moduleRes = XModuleResources.createInstance(modulePath, resParam.res);
            AppContainer.getInstance().setModuleRes(moduleRes);
            resParam.res.hookLayout("com.soft.blued", "layout", "fragment_settings", new XC_LayoutInflated() {
                @SuppressLint({"ResourceType", "SetTextI18n"})
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liParam) {
                    LayoutInflater inflater = (LayoutInflater) liParam.view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    Context bluedContext = AppContainer.getInstance().getBluedContext();
                    int scrollView1ID = bluedContext.getResources().getIdentifier("scrollView1", "id", bluedContext.getPackageName());
                    ScrollView scrollView = liParam.view.findViewById(scrollView1ID);
                    LinearLayout scrollLinearLayout = (LinearLayout) scrollView.getChildAt(0);

                    // 保留设置界面布局
                    LinearLayout mySettingsLayoutAu = (LinearLayout) inflater.inflate(moduleRes.getLayout(R.layout.module_settings_layout), null, false);
                    TextView auCopyTitleTv = mySettingsLayoutAu.findViewById(R.id.settings_name);
                    auCopyTitleTv.setText("复制授权信息(请勿随意泄漏)");
                    mySettingsLayoutAu.setOnClickListener(v -> AuthManager.auHook(true, AppContainer.getInstance().getClassLoader(), bluedContext));

                    LinearLayout moduleSettingsLayout = (LinearLayout) inflater.inflate(moduleRes.getLayout(R.layout.module_settings_layout), null, false);
                    TextView moduleSettingsTitleTv = moduleSettingsLayout.findViewById(R.id.settings_name);
                    moduleSettingsTitleTv.setText("外挂模块设置");
                    moduleSettingsLayout.setOnClickListener(view -> {
                        AlertDialog dialog = getAlertDialog(liParam);
                        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
                        dialog.getWindow().setLayout(100, 300);
                        dialog.setOnShowListener(dialogInterface -> {
                            View parentView = dialog.getWindow().getDecorView();
                            parentView.setBackgroundColor(Color.parseColor("#F7F6F7"));
                        });
                        dialog.show();
                    });

                    scrollLinearLayout.addView(mySettingsLayoutAu, 0);
                    scrollLinearLayout.addView(moduleSettingsLayout, 1);
                }

                private AlertDialog getAlertDialog(LayoutInflatedParam liParam) {
                    // 保留设置界面创建逻辑
                    SettingsViewCreator creator = new SettingsViewCreator(liParam.view.getContext());
                    View settingsView = creator.createSettingsView();
                    creator.setOnSwitchCheckedChangeListener((functionId, isChecked) -> {
                        // 空实现（原逻辑）
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(liParam.view.getContext());
                    builder.setView(settingsView);
                    return builder.create();
                }
            });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        AppContainer.getInstance().setModulePath(startupParam.modulePath);
    }
}
