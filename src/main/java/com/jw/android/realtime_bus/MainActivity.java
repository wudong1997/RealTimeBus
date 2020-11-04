package com.jw.android.realtime_bus;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import CustomClass.Circle_Image;
import CustomClass.Clustering;
import CustomClass.Function;

//import CustomClass.Function;

public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {

    public static MapView mapView;//地图空间
    public AMap aMap;//地图

    public static boolean isLocation=false;//是否定位成功
    public static double longitude;//经度，定位服务获取的本机位置信息
    public static double latitude;//纬度，定位服务获取的本机位置信息
    public static AVGeoPoint point=null;
    double[] latlng =new double[2];

    double get_lat;//从云端数据库获取的纬度
    double get_lng;//从云端数据库获取的经度




    /**执行Timer进度**/
    public final static int LOAD_PROGRESS =0;
    /**关闭TImer进度**/
    public final static int CLOSE_PROGRESS =1;
    /**timer对象**/
     Timer mTimer = null;
    /**TimerTask对象**/
    TimerTask mTimerTask = null;

    /**记录TimerID**/
    int mTimerID =0;
    int id=0;//用来获取试验点数据表中数据的ID

    Marker marker_Bus=null;//校车marker
    Marker marker_station = null;//车站
    List<Marker> duogedian=null;

    public static TextView mTextView = null;//上传次数文本框控件
    public static FloatingActionButton floatingActionButton=null;//悬浮按钮控件
    RelativeLayout mine;//“我的”界面
    RelativeLayout xinxi;// 信息界面
    Circle_Image touxiang=null;//头像控件
    Button download_dongtai= null;//上传按钮
    Button download_dange = null;//下载按钮
    Button zhandian = null;//站点
    Button shangchuan=null;
    Button xiazai = null;
    Button dange = null;
    Button tuichu = null;

    Button shikebiao= null;//全部时刻表按钮
    Switch didian = null;//校区选择按钮
    EditText d1;//时刻1
    EditText d2;//时刻2
    EditText d3;//时刻3

    String get_pass = "admin";
    public static Date test_time;//测试用时间，自定义为20170510-20:30:00
    public static Date adddate;

    private int requestCode;//跳转界面标签，1：登录界面
    private int TIME = 3000;//定时器执行周期1s
    int season;//周几
    int type;//始发站

    private static boolean isLogin=false;//是否已经登录？
    private boolean isUpload = false;//是否开始上传数据
    private boolean isTwinkle = false;//上传按钮是否闪烁
    private boolean isFirstLoc =true;//是否第一次定位
    private boolean isCheck = false;
    private boolean isClustering = false;//是否开始聚类分析
    private boolean isChezhan = false;
    //定位功能
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器
    private AMapLocationClient mlocationClient=null;//定位发起端
    private AMapLocationClientOption mLocationOption=null;//定位参数

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton  = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mapView = (MapView)findViewById(R.id.map);
         mine = (RelativeLayout)findViewById(R.id.mine);
        xinxi = (RelativeLayout)findViewById(R.id.xinxi);
        touxiang=(Circle_Image)findViewById(R.id.touxiang);
        mTextView=(TextView)findViewById(R.id.mTextView);
        shangchuan=(Button)findViewById(R.id.shangchuan);
        xiazai = (Button)findViewById(R.id.xiazai);

        tuichu = (Button)findViewById(R.id.tuichudenglu);
        shikebiao = (Button)findViewById(R.id.shikebiao);
        zhandian = (Button)findViewById(R.id.zhandian);
        didian = (Switch)findViewById(R.id.switch_didian);
        d1=(EditText) findViewById(R.id.date1);
        d2=(EditText) findViewById(R.id.date2);
        d3=(EditText) findViewById(R.id.date3);

        floatingActionButton.setOnClickListener(new Upload());//悬浮按钮启动动态上传数据，每秒上传一次位置细信息信息
        touxiang.setOnClickListener(new Goto());
        shangchuan.setOnClickListener(new Upload());
        xiazai.setOnClickListener(new Dongtai());//动态获取点，调试获取校车位置功能
        zhandian.setOnClickListener(new Zhandain());
        shikebiao.setOnClickListener(new shike());
        didian.setOnCheckedChangeListener(new check());
        tuichu.setOnClickListener(new Tuichu());

        xinxi.setVisibility(View.GONE);

        //加载地图
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        /*//修改地图的中心点位置
        CameraPosition cp = aMap.getCameraPosition();
        CameraPosition cpNew = CameraPosition.fromLatLngZoom(new LatLng(36.1,119.5), 16);//34.26, 117.20
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
        aMap.moveCamera(cu);*/

        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);

        //定位的小图标 默认是蓝点 这里自定义一团火，其实就是一张图片
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
        //启动定位
        initLocationService();

        // 连接leancloud数据库
        AVOSCloud.initialize(this,"BkVM7W9WqwbQlhJGIr0gTAJt-gzGzoHsz","1rAh1evpPcs5mw4vPqNhpgtH");
        AVOSCloud.setDebugLogEnabled(true);

        //        底部菜单栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    //    底部菜单栏点击事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home://点击地图按钮

                    mapView.setVisibility(View.VISIBLE);//加载地图界面
                    mine.setVisibility(View.GONE);//我的界面不加载
                    xinxi.setVisibility(View.GONE);
                    return true;

                case R.id.navigation_dashboard:
                    mine.setVisibility(View.VISIBLE);//不加载我的界面
                    xinxi.setVisibility(View.VISIBLE);//加载信息界面
                    mapView.setVisibility(View.GONE);//不加载地图界面

                    if (type==2){
                        didian.setChecked(true);
                    }
                    xiabanche();
                    return true;

                case R.id.navigation_notifications://点击我的按钮
                    mine.setVisibility(View.VISIBLE);//加载我的界面
                    mapView.setVisibility(View.GONE);//不加载地图界面
                    xinxi.setVisibility(View.GONE);//不加载信息界面
                    return true;
            }
            return false;
        }

    };

    //switch控件的监听函数
    class check implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                type=2;//文昌
                xiabanche();
            }else {
                type = 1;//南湖
                xiabanche();
            }
        }
    }

    //时刻表函数
    private void xiabanche() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");//可以方便地修改日期格式

        String zhouji = dateFormat.format(now);
        switch (zhouji){
            case "星期一":
            case "星期二":
            case "星期三":
            case "星期四":
            case "星期五":
            { season=1;//工作日
                break; }
            case "星期六":
            case "星期日": {
                season = 2;//周末
                 break;}
        }

        final Calendar c = Calendar.getInstance();//可以对每个时间域单独修改

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        final long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        final long nh = 1000 * 60 * 60;// 一小时的毫秒数
        final long nm = 1000 * 60;// 一分钟的毫秒数
        final long ns = 1000;// 一秒钟的毫秒数

        Date search_time = null;
        try {
            search_time = Zidingyishijian(2017, 06, 03, hour, minute, second);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AVQuery<AVObject> startDateQuery = null;
        AVQuery<AVObject> Query1 = null;
        AVQuery<AVObject> Query2 = null;

        startDateQuery = new AVQuery<>("Bus_Time");
        startDateQuery.whereGreaterThanOrEqualTo("Departure_time", search_time);//查询条件1：上传时间大于等于开始时间

        Query1 = new AVQuery<>("Bus_Time");
        Query1.whereEqualTo("Season", 1);//查询条件1：工作日

        Query2 = new AVQuery<>("Bus_Time");
        Query2.whereEqualTo("Type", type);//查询条件1：始发站

        final Date finalSearch_time = search_time;
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(startDateQuery, Query1,Query2));
        query.orderByAscending("Departure_time");

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (!list.isEmpty()) {
                    if (list.size()>=3) {
                        for (int i = 0; i < 3; i++) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");//可以方便地修改日期格式
                            String date = dateFormat.format(list.get(i).getDate("Departure_time"));
                            switch (i) {
                                case 0:
                                    d1.setText(date);
                                    break;
                                case 1:
                                    d2.setText(date);
                                    break;
                                case 2:
                                    d3.setText(date);
                                    break;
                            }
                        }
                    }else {
                        for (int i = 0; i < list.size(); i++) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");//可以方便地修改日期格式
                            String date = dateFormat.format(list.get(i).getDate("Departure_time"));
                            switch (list.size()) {
                                case 1:
                                    d1.setText(date);
                                    d2.setText(null);
                                    d3.setText(null);
                                    break;
                                case 2:
                                    switch (i) {
                                        case 0:
                                            d1.setText(date);
                                            break;
                                        case 1:
                                            d2.setText(date);
                                            d3.setText(null);
                                            break;
                                    }
                            }
                        }
                    }
                    Date shijian = list.get(0).getDate("Departure_time");
                    long shicha = shijian.getTime() - finalSearch_time.getTime();

                    long day = shicha / nd;// 计算差多少天
                    long hour = shicha % nd / nh + day * 24;// 计算差多少小时
                    long min = shicha % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
                    long sec = shicha % nd % nh % nm / ns;// 计算差多少秒

                    String str = "距下班车还有：" + hour + ":" + min + ":" + sec;
                    TextView text = (TextView) findViewById(R.id.shijian);
                    text.setText(str);

                } else {
                    String str = "校车已停运~";
                    TextView text = (TextView) findViewById(R.id.shijian);
                    text.setText(str);
                    d1.setText(null);
                    d2.setText(null);
                    d3.setText(null);
//                    e.printStackTrace();
                }

            }
        });
    }

    //    跳转到登录界面
    class Goto implements View.OnClickListener{
        public void onClick(View v){
            requestCode = 1;//跳转到登录界面
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,LoginActivity.class);
            MainActivity.this.startActivityForResult(intent,requestCode);
        }
    }

    //跳转时刻表界面
    class shike implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,Shikebiao.class);
            MainActivity.this.startActivity(intent);
        }
    }

    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求吗来区别
        switch (resultCode) {
            case 1://从登录界面返回的数据
                String usename_get = data.getStringExtra("name");
                TextView textView=(TextView)findViewById(R.id.username_get);
                textView.setText(usename_get);
                isLogin =true;
                touxiang.setImageResource(R.drawable.touxiang);
                tuichu.setText("退出登录");
                break;

            default:
                break;
        }
    }

    //启动动态获取数据计时器，用于调试获取校车位置功能
    class Dongtai implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                test_time = Zidingyishijian(2017,05,10,20,32,55);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//
            if (!isClustering){
                Clustering.Start();
                isClustering=true;
            }else if(isClustering){
                Clustering.Close();
                isClustering=false;
            }

        }
    }

    //每秒上传一次位置信息执行函数
    class Upload implements View.OnClickListener{

        public void onClick(View v){
            if(!isLocation){
                AlertDialog login_info= new  AlertDialog.Builder(MainActivity.this)
                        .setTitle("定位提示" )
                        .setMessage("未能成功定位" )
                        .setPositiveButton("确定" ,  null )
                        .show();
            }else if(isLocation){
                if(!isUpload){//如果没有开始上传数据，则启动上传
                    Function.StartUpload();//启动上传共享位置数据信息
                    isUpload = true;
                     //上传定位信息

                }else if(isUpload){
                     Function.CloseUpload();//关闭上传
                     isUpload = false;
                }
            }

        }
    }

    //获取站点的函数
    class Zhandain implements View.OnClickListener{

        public void onClick(View v){

            if (!isChezhan) {
                AVQuery<AVObject> avQuery = new AVQuery<>("Bus_station");
                avQuery.whereGreaterThan("id", 0);
                avQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (!list.isEmpty()) {
                            for (int i = 0; i < list.size(); i++) {
                                get_lat = list.get(i).getDouble("lat");
                                get_lng = list.get(i).getDouble("lng");
                                LatLng latLng = new LatLng(get_lat, get_lng);
                                String name = list.get(i).getString("name");

                                marker_station = aMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.chezhan))
                                        .draggable(false));// 设置远小近大效果,2.1.0版本新增

                            }
                            //这里写具体操作
                        }

                    }
                });
                isChezhan=true;
            }else if (isChezhan){
                aMap.clear();
                isChezhan=false;
            }
        }
    }

    //退出事件
    class Tuichu implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (isLogin) {
                String usename_tuichu = "点击头像登录";
                TextView textView = (TextView) findViewById(R.id.username_get);
                textView.setText(usename_tuichu);
                isLogin = false;
                touxiang.setImageResource(R.drawable.touxiang_weidenglu);
            }else if (!isLogin){
                String string = "请点击头像登录！";
                Toast.makeText(getApplicationContext(), string,Toast.LENGTH_LONG).show();
            }
        }
    }
    //自定义时间
    private Date Zidingyishijian (int year, int month, int day, int hour, int min, int sec) throws ParseException {
        Date date = new Date(year,month,day,hour,min,sec);
        Date date2 = new Date(2017-05-10-20-30-00);
        String str = "2017-05-10 20:30:00";
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date3 = format.parse(str);

        String time  = year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
        SimpleDateFormat format1 =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date31 = format1.parse(time);
        return date31;
    }


    //定位
    private void initLocationService(){
        //初始化定位
        mlocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mlocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption=new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }

    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                latitude= amapLocation.getLatitude();//获取纬度
                longitude= amapLocation.getLongitude();//获取经度

                if (longitude<117.16){
                    type=1;
                }else {
                    type=2;
                }

                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
//                    aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    //Toast显示地理位置信息
                    isFirstLoc = false;
                    isLocation=true;
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        //位置
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        options.snippet("当前位置");
        //设置多少帧刷新一次图片资源
        options.period(60);
        return options;

    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
