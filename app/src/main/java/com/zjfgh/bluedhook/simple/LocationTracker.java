package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

public class LocationTracker {
    private final AMapHookHelper aMapHelper;
    private final String uid;
    private final ClassLoader classLoader;
    private final View fl_content;
    private int maxIterations;
    private int noIntersectionAttempts = 0;

    private volatile boolean tracking = true; // 追踪标志

    public LocationTracker(AMapHookHelper aMapHelper, String uid, ClassLoader classLoader, View fl_content) {
        this.aMapHelper = aMapHelper;
        this.uid = uid;
        this.classLoader = classLoader;
        this.fl_content = fl_content;
    }

    // 停止追踪
    public void stopTracking() {
        tracking = false;
    }

    // 开始追踪
    public void startTracking(double initialLat, double initialLng, double initialDistanceKm, int maxIterations, LocationTrackingCallback callback) {
        tracking = true; // 每次重新开始时恢复
        this.maxIterations = maxIterations;
        this.noIntersectionAttempts = 0; // Reset attempts counter

        aMapHelper.clearAllOverlays();
        aMapHelper.addMarker(initialLat, initialLng, "初始位置");
        drawCircle(initialLat, initialLng, initialDistanceKm);

        if (callback != null) {
            callback.onInitialLocation(initialLat, initialLng, initialDistanceKm);
        }

        locateTarget(initialLat, initialLng, initialDistanceKm, maxIterations, callback);
    }

    // 递归定位方法
    private void locateTarget(double centerLat, double centerLng, double radiusKm, int maxIterations, LocationTrackingCallback callback) {
        if (!tracking) return;

        if (radiusKm == 0 || maxIterations <= 0) {
            aMapHelper.addMarker(centerLat, centerLng, "最终位置");
            if (callback != null) {
                callback.onFinalLocation(centerLat, centerLng, radiusKm);
            }
            return;
        }

        double offsetLat = centerLat;
        double offsetLng = centerLng + kmToDegrees(radiusKm, centerLat);

        aMapHelper.addMarker(offsetLat, offsetLng, "探测点");

        if (callback != null) {
            callback.onProbeLocation(offsetLat, offsetLng);
        }

        getDistanceFromServer(offsetLat, offsetLng, new DistanceCallback() {
            @Override
            public void onDistanceReceived(double newDistanceKm) {
                if (!tracking) return;

                if (callback != null) {
                    callback.onProbeDistance(newDistanceKm);
                }

                drawCircle(offsetLat, offsetLng, newDistanceKm);

                List<LatLng> intersections = calculateIntersections(
                        centerLat, centerLng, radiusKm,
                        offsetLat, offsetLng, newDistanceKm
                );

                if (callback != null && !intersections.isEmpty()) {
                    for (LatLng intersection : intersections) {
                        callback.onIntersectionLocation(intersection.latitude, intersection.longitude);
                    }
                }

                if (!intersections.isEmpty()) {
                    noIntersectionAttempts = 0;
                    evaluateIntersections(intersections, 0, new ArrayList<>(), Double.MAX_VALUE, null, callback);
                } else {
                    if (noIntersectionAttempts < 5) {
                        noIntersectionAttempts++;

                        double newCenterLat = centerLat;
                        double newCenterLng = centerLng + kmToDegrees(radiusKm * 0.1, centerLat);

                        if (callback != null) {
                            callback.onNoIntersection(newCenterLat, newCenterLng, radiusKm, noIntersectionAttempts);
                        }

                        aMapHelper.addMarker(newCenterLat, newCenterLng, "新中心(尝试" + noIntersectionAttempts + ")");
                        locateTarget(newCenterLat, newCenterLng, radiusKm, maxIterations, callback);
                    } else {
                        if (callback != null) {
                            callback.onError("无法找到交点，已达到最大尝试次数");
                        }
                        aMapHelper.addMarker(centerLat, centerLng, "终止位置(无交点)");
                    }
                }
            }
        });
    }

    private void evaluateIntersections(List<LatLng> intersections, int index,
                                       List<LatLng> validPoints, double minDistance,
                                       LatLng bestPoint, LocationTrackingCallback callback) {
        if (!tracking) return;

        if (index >= intersections.size()) {
            if (bestPoint != null) {
                aMapHelper.clearAllOverlays();
                getDistanceFromServer(bestPoint.latitude, bestPoint.longitude, new DistanceCallback() {
                    @Override
                    public void onDistanceReceived(double distanceKm) {
                        if (!tracking) return;
                        if (callback != null) {
                            callback.onNewCenterLocation(bestPoint.latitude, bestPoint.longitude, distanceKm);
                        }
                        drawCircle(bestPoint.latitude, bestPoint.longitude, distanceKm);
                        locateTarget(bestPoint.latitude, bestPoint.longitude, distanceKm, maxIterations - 1, callback);
                    }
                });
            }
            return;
        }

        LatLng point = intersections.get(index);
        getDistanceFromServer(point.latitude, point.longitude, new DistanceCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDistanceReceived(double distanceKm) {
                if (!tracking) return;

                aMapHelper.addMarker(point.latitude, point.longitude,
                        String.format("交点距离:%.3fkm", distanceKm));
                aMapHelper.moveCamera(point.latitude, point.longitude, DistanceConverter.calculateZoomLevelByDistance(distanceKm + 0.01));
                if (callback != null) {
                    callback.onIntersectionDistance(point.latitude, point.longitude, distanceKm);
                }

                if (distanceKm < minDistance) {
                    validPoints.add(point);
                    evaluateIntersections(intersections, index + 1, validPoints, distanceKm, point, callback);
                } else {
                    evaluateIntersections(intersections, index + 1, validPoints, minDistance, bestPoint, callback);
                }
            }
        });
    }

    // 从服务器获取距离
    private void getDistanceFromServer(double lat, double lng, DistanceCallback callback) {
        if (!tracking) return;
        NetworkManager.getInstance().getAsync(
                NetworkManager.getBluedSetUsersLocationApi(lat, lng),
                AuthManager.auHook(false, classLoader, fl_content.getContext()),
                new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!tracking) return;
                        NetworkManager.getInstance().getAsync(
                                NetworkManager.getBluedUserBasicAPI(uid),
                                AuthManager.auHook(false, classLoader, fl_content.getContext()),
                                new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {}

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                                        if (!tracking) return;
                                        try {
                                            if (response.code() == 200) {
                                                assert response.body() != null;
                                                String jsonStr = response.body().string();
                                                JSONObject json = new JSONObject(jsonStr);
                                                JSONArray dataArray = json.getJSONArray("data");
                                                if (dataArray.length() > 0) {
                                                    JSONObject userData = dataArray.getJSONObject(0);
                                                    int isHideDistance = userData.getInt("is_hide_distance");
                                                    if (isHideDistance == 0) {
                                                        double distanceKm = userData.getDouble("distance");
                                                        callback.onDistanceReceived(distanceKm);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {}
                                    }
                                });
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                });
    }

    // 画圆辅助方法
    private void drawCircle(double lat, double lng, double radiusKm) {
        aMapHelper.addCircle(lat, lng, DistanceConverter.kmToMeters(radiusKm),
                "#003399FF", "#603399FF");
    }

    // 计算两个圆的交点
    private List<LatLng> calculateIntersections(double lat1, double lng1, double r1,
                                                double lat2, double lng2, double r2) {
        List<LatLng> intersections = new ArrayList<>();
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        Ellipsoid reference = Ellipsoid.WGS84;

        GlobalPosition p1 = new GlobalPosition(lat1, lng1, 0);
        GlobalPosition p2 = new GlobalPosition(lat2, lng2, 0);

        try {
            GeodeticMeasurement results = geoCalc.calculateGeodeticMeasurement(reference, p1, p2);
            double distance = results.getPointToPointDistance() / 1000;
            double azimuth = results.getAzimuth();

            if (distance > r1 + r2 || distance < Math.abs(r1 - r2)) {
                Log.e("BluedHook", String.format("无交点: 距离=%.2fkm, r1=%.2fkm, r2=%.2fkm", distance, r1, r2));
                return intersections;
            }

            double a = (r1 * r1 - r2 * r2 + distance * distance) / (2 * distance);
            double h = Math.sqrt(r1 * r1 - a * a);

            GlobalCoordinates midPoint = geoCalc.calculateEndingGlobalCoordinates(
                    reference, p1, azimuth, a * 1000);

            GlobalCoordinates inter1 = geoCalc.calculateEndingGlobalCoordinates(
                    reference, midPoint, azimuth + 90, h * 1000);
            intersections.add(new LatLng(inter1.getLatitude(), inter1.getLongitude()));

            GlobalCoordinates inter2 = geoCalc.calculateEndingGlobalCoordinates(
                    reference, midPoint, azimuth - 90, h * 1000);
            intersections.add(new LatLng(inter2.getLatitude(), inter2.getLongitude()));

        } catch (Exception e) {
            Log.e("BluedHook", "计算交点时出错: " + e.getMessage());
        }

        return intersections;
    }

    // 将公里转换为经度度数（近似）
    private double kmToDegrees(double km, double lat) {
        double degreesPerKm = 1 / (111.32 * Math.cos(Math.toRadians(lat)));
        return km * degreesPerKm;
    }

    public interface LocationTrackingCallback {
        void onInitialLocation(double lat, double lng, double distanceKm);

        void onProbeLocation(double lat, double lng);

        void onProbeDistance(double distanceKm);

        void onIntersectionLocation(double lat, double lng);

        void onIntersectionDistance(double lat, double lng, double distanceKm);

        void onNewCenterLocation(double lat, double lng, double distanceKm);

        void onFinalLocation(double lat, double lng, double distanceKm);

        default void onNoIntersection(double newLat, double newLng, double currentRadius, int attempt) {}
        default void onError(String message) {}
    }

    interface DistanceCallback {
        void onDistanceReceived(double distanceKm);
    }

    static class LatLng {
        double latitude;
        double longitude;

        LatLng(double lat, double lng) {
            this.latitude = lat;
            this.longitude = lng;
        }
    }
}
