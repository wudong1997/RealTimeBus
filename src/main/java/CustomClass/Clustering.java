package CustomClass;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jw.android.realtime_bus.MainActivity;
import com.jw.android.realtime_bus.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/3 0003.
 */

public class Clustering {

        static AMap aMap = MainActivity.mapView.getMap();
        /*执行Timer进度*/
        public final static int LOAD_PROGRESS =0;
        /*关闭TImer进度*/
        public final static int CLOSE_PROGRESS =1;
        /*timer对象*/
        public static Timer mTimer = null;
        /*TimerTask对象*/
        public static TimerTask mTimerTask = null;
        /*记录TimerID*/
        static int mTimerID =0;

    TextView mTextView = null;

    private int TIME = 3000;//定时器执行周期1s
    static double get_lat;
    static double get_lng;
    static double bus_lat;
    static double bus_lng;
    public static double[][] latlng_get;
    //存放未聚类之前的点
    public static double[][] buslatlng11 = new double[2][50];
    public static double[][] buslatlng22 = new double[2][50];
    public static double[][] buslatlng33 = new double[2][50];
    //存放剔除粗差后未聚类之前的点
    public static double[][] buslatlng1 = new double[2][50];
    public static double[][] buslatlng2 = new double[2][50];
    public static double[][] buslatlng3 = new double[2][50];

    //剔除粗差后点的个数
    public static int a1=0;
    public static int a2=0;
    public static int a3=0;
    //查询到点的个数
    public static int a11=0;
    public static int a22=0;
    public static int a33=0;


    public static LatLng shibiedian2;
    public static LatLng shibiedian1;
    public  static LatLng locationupposition=null;

    public static ArrayList list1 = new ArrayList();
    public static ArrayList list2 = new ArrayList();
    public static ArrayList list3 = new ArrayList();

    public static Marker bus_marker1=null;
    public static Marker bus_marker2=null;
    public static Marker bus_marker3=null;
    public static Marker locationup1=null;

    public static int  busnumber =0;
    public int msgKey1=1;


    private static Handler handler = new Handler(){
            public void handleMessage(final Message msg){
                switch(msg.what){
                    case LOAD_PROGRESS:

                        //测试查询
                       /*Date star_time = MainActivity.test_time;//测试时间，取一段时间内的数据，此段时间的开始时间
                        long time=star_time.getTime()+5000;//结束时间，时间段为5秒
                        final Date end_time=new Date(Long.parseLong(String.valueOf(time)));//结束时间的date格式

                        final AVQuery<AVObject> startDateQuery = new AVQuery<>("admin_point");
                        startDateQuery.whereGreaterThanOrEqualTo("createdAt", star_time);//查询条件1：上传时间大于等于开始时间

                        final AVQuery<AVObject> endDateQuery = new AVQuery<>("admin_point");
                        endDateQuery.whereLessThan("createdAt", end_time);//查询条件2：上传时间小于结束时间*/

                       //实时查询
                        Date end_time =new Date(System.currentTimeMillis());//测试时间，取一段时间内的数据，此段时间的开始时间
                        long time=end_time.getTime()-5000;//结束时间，时间段为5秒
                        final Date star_time=new Date(Long.parseLong(String.valueOf(time)));//结束时间的date格式

                        final AVQuery<AVObject> startDateQuery = new AVQuery<>("Bus_location");
                        startDateQuery.whereGreaterThanOrEqualTo("createdAt", star_time);//查询条件1：上传时间大于等于开始时间


                        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Bus_location");
                        endDateQuery.whereLessThan("createdAt", end_time);//查询条件2：上传时间小于结束时间
                        //*/


                        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));//联合查询
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if(e == null){
                                    //读取成功执行事件
                                    if(!list.isEmpty()) {//读取有共享数据
                                        get_lat = list.get(0).getDouble("lat");
                                        get_lng = list.get(0).getDouble("lng");
                                        shibiedian1 = new LatLng(get_lat, get_lng);//获取的数据第一个数据作为第一个识别点

                                        buslatlng11[0][0]=get_lat;//此数据写入第一个数组
                                        buslatlng11[1][0]=get_lng;
                                        a11++;//第一组数据个数
                                        busnumber=1;

//                                        latlng_get=new double[list.size()][2];
                                        if (list.size()==1){
                                            busnumber = 1;//只有一个数据，只有一辆校车
                                        }else {
                                            for (int i = 1; i < list.size(); i++) {
                                                LatLng latLng2 = new LatLng(list.get(i).getDouble("lat"), list.get(i).getDouble("lng"));//需要判断的点
                                                float distance = AMapUtils.calculateLineDistance(shibiedian1, latLng2);//计算两两之间的距离
                                                //先和第一个识别点比较
                                                //分组
                                                if (distance <= 30) {//小于阈值，计入第一个数组
                                                    buslatlng11[0][a11]=list.get(i).getDouble("lat");
                                                    buslatlng11[1][a11]=list.get(i).getDouble("lng");
                                                    a11++;

                                                } else {
                                                    if (a22==0){//即第一次出现距离大于阈值的情况，需要设置第二个识别点，并且将此数据计入第二个数组
                                                        //将此数据写入第二个数组，作为第二组第一个数据，也是第二个识别点
                                                        buslatlng22[0][0]=list.get(i).getDouble("lat");
                                                        buslatlng22[1][0]=list.get(i).getDouble("lng");
                                                        //第二个识别点
                                                        shibiedian2 = new LatLng(buslatlng22[0][0],buslatlng22[1][0]);
                                                        busnumber=2;
                                                        a22++;
                                                    }else {
                                                        float distance2 = AMapUtils.calculateLineDistance(shibiedian2, latLng2);
                                                        if (distance2<=30){//距离小于阈值，写入第二组
                                                            buslatlng22[0][a22]=list.get(i).getDouble("lat");
                                                            buslatlng22[1][a22]=list.get(i).getDouble("lng");
                                                            a22++;
                                                        }else {
                                                            buslatlng33[0][a33]=list.get(i).getDouble("lat");
                                                            buslatlng33[1][a33]=list.get(i).getDouble("lng");
                                                            busnumber=3;
                                                            a33++;
                                                        }//距离大于第二个识别点的函数结尾

                                                    }//

                                                }//这里是阈值大于第一个识别点的情况的函数结尾

                                            }//这里是for循环、即对数据循环处理的函数的结尾
                                        }//这里是有多个数据的情况下 的处理函数的结尾
                                        Clustering();//校车位置显示
                                        shibiedian1=null;
                                        shibiedian2=null;

                                    }//这里是读取到共享数据的函数结尾
                                    else {
                                        String show = "未查询到共享数据！";

                                    }

                                }//这里是数据库访问成功的函数结尾
                            }
                        });//查询回调函数结尾

                        int c = a11+a22+a33;
                        MainActivity.test_time = end_time;//为下一个进程准备
                        MainActivity.mTextView.setText("已执行次数"+mTimerID+"查询数据一共："+c);
                        a11=a22=a33=0;//执行完一次查询后清零
                        a1=a2=a3=0;
                        break;
                    case CLOSE_PROGRESS:
                        MainActivity.mTextView.setText("当前Timer已经关闭请重新启动");
                        if (bus_marker1!=null){
                            bus_marker1.remove();
                        }
                        if (bus_marker2!=null){
                            bus_marker2.remove();
                        }
                        if (bus_marker3!=null){
                            bus_marker3.remove();
                        }
                        break;
                }
                super.handleMessage(msg);
            }
    };


    public static void ShowUnClustering(double[][] buslatlng,int a){
        for (int i=0;i<a;i++){
            locationupposition=new LatLng(buslatlng[0][i],buslatlng[1][i]);
            locationup1 = aMap.addMarker(new MarkerOptions()
                    .position(locationupposition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.locationup))
                    .draggable(true));// 设置远小近大效果,2.1.0版本新增

        }
    }
    //求点到直线距离
    public static double PointToLinedistance(double x1,double y1,double x2,double y2,double x0,double y0){
        double distancepointtoline;
        double A=y2-y1;
        double B=x1-x2;
        double C=x2*y1-y2*x1;
        double C1=B*x0+A*y0;
        if(x1==x2&&y1==y2)
        {
            LatLng latLng1 = new LatLng(x1,y1);
            LatLng latLng2 = new LatLng(x0,y0);
            distancepointtoline=AMapUtils.calculateLineDistance(latLng1,latLng2);
        }
        else if (x1==x2)
        {

            LatLng latLng1 = new LatLng(x1,y0);
            LatLng latLng2 = new LatLng(x0,y0);
            distancepointtoline=AMapUtils.calculateLineDistance(latLng1,latLng2);
        }
        else if(y1==y2)
        {
            LatLng latLng1 = new LatLng(x0,y1);
            LatLng latLng2 = new LatLng(x0,y0);
            distancepointtoline=AMapUtils.calculateLineDistance(latLng1,latLng2);
        }
        else
        {
            double K1=(y1-y2)/(x1-x2);
            double K2=-1/K1;
            double B1=y1-(y1-y2)*x1/(x1-x2);
            double B2=y0+x0/K1;
            double x3=(B1-B2)/(K1-K2);
            double y3=K1*x3+B1;
            LatLng latLng1 = new LatLng(x3,y3);
            LatLng latLng2 = new LatLng(x0,y0);
            distancepointtoline=AMapUtils.calculateLineDistance(latLng1,latLng2);

        }
        return distancepointtoline;
    }
    //Ransac算法
    public static int Ransac(double[][] point0,double[][] point1,int n) {
        double[][] point = new double[2][50];//暂时存放局内点
        double[][] pointend = new double[2][50];//存放局内点
        int pointnumber = 0;//暂时存放局内点个数
        int pointnumberend = 0;//存放局内点个数
        double[] distance = new double[50];//存放点到直线距离
        double alldistance = 0;
        double NiHeDu = 99999;//用于比较直线的拟合度哪条比较好
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {//任意取两点
                pointnumber = 0;
                alldistance = 0;
                for (int k = 0; k < n; k++) {
                    if (PointToLinedistance(point0[0][i], point0[1][i], point0[0][j], point0[1][j], point0[0][k], point0[1][k]) < 10)
                    //如果点到直线的距离小于10m,则将改点写入point数组中
                    {
                        point[0][k] = point0[0][k];
                        point[1][k] = point0[1][k];
                        pointnumber++;
                    }
                }

                if (pointnumber / n >= 0.75) {//写入到point数组中的数要大于原数组的75%
                    for (int k0 = 0; k0 < pointnumber; k0++) {
                        distance[k0] = PointToLinedistance(point0[0][i], point0[1][i], point0[0][j], point0[1][j], point[0][k0], point[1][k0]);
                        alldistance = alldistance + distance[k0];
                    }
                    if (alldistance / pointnumber < NiHeDu)
                    //如果新的拟合度比原来的好
                    {
                        NiHeDu = alldistance / pointnumber;
                        pointend = point;
                        pointnumberend = pointnumber;
                    }
                }
            }
        }
        if (pointnumberend!=0) {
            for (int i = 0; i < pointnumberend; i++) {
                point1[0][i] = pointend[0][i];
                point1[1][i] = pointend[1][i];
            }

        }
        if (pointnumberend==0) {
            for (int i = 0; i < n; i++) {
                point1[0][i] = point0[0][i];
                point1[1][i] = point0[1][i];
            }
            pointnumberend=n;
        }
        return pointnumberend;
    }

    public static void Clustering(){
        a1=Ransac(buslatlng11,buslatlng1,a11);
        a2=Ransac(buslatlng22,buslatlng2,a22);
        a3=Ransac(buslatlng33,buslatlng3,a33);



        bus_lng=0;
        bus_lat=0;
        if (bus_marker1!=null){
            bus_marker1.remove();
        }
        if (bus_marker2!=null){
            bus_marker2.remove();
        }
        if (bus_marker3!=null){
            bus_marker3.remove();
        }
        switch (busnumber){
            case 1:
                for (int i=0;i<a1;i++){
                    bus_lat += buslatlng1[0][i];
                    bus_lng += buslatlng1[1][i];

                }
                //ShowUnClustering(buslatlng11,a11);
                //显示第一辆车未聚类的点
                double a = bus_lat/a1;
                double n = bus_lng/a1;
                bus_marker1 = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(a,n))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cheliang))
                        .draggable(true));// 设置远小近大效果,2.1.0版本新增
                bus_lng=bus_lat=0;


                break;
            case 2:
                for (int i=0;i<a1;i++){
                    bus_lat += buslatlng1[0][i];
                    bus_lng += buslatlng1[1][i];

                }
                //ShowUnClustering(buslatlng11,a11);
                //显示第一辆车未聚类的点
                bus_marker1 = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bus_lat/a1,bus_lng/a1))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cheliang))
                        .draggable(true));// 设置远小近大效果,2.1.0版本新增
                bus_lng=bus_lat=0;

                for (int i=0;i<a2;i++){
                    bus_lat += buslatlng2[0][i];
                    bus_lng += buslatlng2[1][i];

                }
                //ShowUnClustering(buslatlng22,a22);
                //显示第二辆车未聚类的点
                bus_marker2 = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bus_lat/a2,bus_lng/a2))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cheliang))
                        .draggable(true));// 设置远小近大效果,2.1.0版本新增
                bus_lng=bus_lat=0;

                break;
            case 3:
                for (int i=0;i<a1;i++){
                    bus_lat += buslatlng1[0][i];
                    bus_lng += buslatlng1[1][i];

                }
                //ShowUnClustering(buslatlng11,a11);
                //显示第一辆车未聚类的点
                bus_marker1 = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bus_lat/a1,bus_lng/a1))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cheliang))
                        .draggable(true));// 设置远小近大效果,2.1.0版本新增
                bus_lng=bus_lat=0;

                for (int i=0;i<a2;i++){
                    bus_lat += buslatlng2[0][i];
                    bus_lng += buslatlng2[1][i];

                }
                //ShowUnClustering(buslatlng22,a22);
                //显示第二辆车未聚类的点
                bus_marker2 = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bus_lat/a2,bus_lng/a2))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cheliang))
                        .draggable(true));// 设置远小近大效果,2.1.0版本新增
                bus_lng=bus_lat=0;

                for (int i=0;i<a3;i++){
                    bus_lat += buslatlng3[0][i];
                    bus_lng += buslatlng3[1][i];

                }
                //ShowUnClustering(buslatlng33,a33);
                //显示第三辆车未聚类的点
                bus_marker3 = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bus_lat/a3,bus_lng/a3))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cheliang))
                        .draggable(true));// 设置远小近大效果,2.1.0版本新增
                bus_lng=bus_lat=0;



                break;
            default:
                break;
        }


    }

    public static void Start(){
        if(mTimer == null){
            mTimerTask = new TimerTask(){
                @Override
                public void run() {
                    mTimerID ++;//访问次数，也是计时器执行次数
                    Message msg = new Message();
                    msg.what = LOAD_PROGRESS;
                    msg.arg1 =(int) (mTimerID);
                    handler.sendMessage(msg);
                }
            };

            mTimer = new Timer();
            //第一个参数为执行的mTimerTask
            //第二个参数为延迟得事件，这里写1000得意思是 mTimerTask将延迟1秒执行
            //第三个参数为多久执行一次，这里写2000 表示每2秒执行一次mTimerTask的Run方法
            mTimer.schedule(mTimerTask, 1000,3000);
        }
    }

    public static void Close(){
        if(mTimer !=null){
            mTimer.cancel();
            mTimer = null;
        }
        if(mTimerTask!= null){
            mTimerTask = null;
        }
        mTimerID =0;
        handler.sendEmptyMessage(CLOSE_PROGRESS);
    }

}


