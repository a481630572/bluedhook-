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
                // 已删除插入自定义设置项的代码
            }
        });
    }
}

    @Override
    public void initZygote(StartupParam startupParam) {
        AppContainer.getInstance().setModulePath(startupParam.modulePath);
    }
}
