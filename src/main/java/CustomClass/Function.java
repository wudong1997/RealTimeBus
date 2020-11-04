package CustomClass;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.avos.avoscloud.AVException;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jw.android.realtime_bus.MainActivity;
import com.jw.android.realtime_bus.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

 //Created by Administrator on 2017/5/13 0013.

public class Function {

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

    private static boolean isUpload = false;//是否开始上传数据
    private static boolean isTwinkle = false;//上传按钮是否闪烁



    protected static Handler handler = new Handler(){
        public void handleMessage(final Message msg){
            switch(msg.what){
                case LOAD_PROGRESS:
                    MainActivity.point=new AVGeoPoint(MainActivity.latitude,MainActivity.longitude);
                    AVObject Bus_location = new AVObject("Bus_location");

                    Bus_location.put("lat",MainActivity.latitude);
                    Bus_location.put("lng",MainActivity.longitude);
                    Bus_location.put("point",MainActivity.point);

                    Bus_location.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null){
                               //上传成功执行事件
                            }
                        }
                    });

                    if(MainActivity.floatingActionButton!=null){

                        if(!isTwinkle){//如果上传按钮没有闪烁
                            MainActivity.floatingActionButton.setImageResource(R.drawable.shangchuant);
                            isTwinkle=true;

                        }else if(isTwinkle){
                            MainActivity.floatingActionButton.setImageResource(R.drawable.shangchuanf);
                            isTwinkle=false;
                        }
                        MainActivity.mTextView.setText("上传成功次数："+msg.arg1);

                    }

                    break;
                case CLOSE_PROGRESS:
                    MainActivity.mTextView.setText("当前Timer已经关闭请重新启动");
                    MainActivity.floatingActionButton.setImageResource(R.drawable.shangchuanf);
                    isTwinkle=false;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public static void StartUpload(){
        if(mTimer == null){
            mTimerTask = new TimerTask(){
                @Override
                public void run() {
                    mTimerID ++;//上传次数，也是计时器执行次数
                    Message msg = new Message();
                    msg.what = LOAD_PROGRESS;
                    msg.arg1 =(int) (mTimerID);
                    handler.sendMessage(msg);
                }
            };



            mTimer = new Timer();
            //第一个参数为执行的mTimerTask
            //第二个参数为延迟得事件，这里写1000得意思是 mTimerTask将延迟1秒执行
            //第三个参数为多久执行一次，这里写1000 表示没1秒执行一次mTimerTask的Run方法
            mTimer.schedule(mTimerTask, 1000,2000);
        }
    }

    public static void CloseUpload(){
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
