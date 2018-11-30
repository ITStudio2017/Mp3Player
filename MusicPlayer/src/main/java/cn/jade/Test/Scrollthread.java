package cn.jade.Test;

import cn.jade.Tools.Time;

import javax.swing.*;

public class Scrollthread extends Thread {
    public JSlider musicslider = null;
    public JLabel progressnow = null;
    public double stoptime = 0; // 记录现在停止时的时间
    public double pinjun = 1;

    Scrollthread(JSlider jSlider,JLabel progress,double p){
        this.musicslider = jSlider;
        this.progressnow = progress;
        this.pinjun = p;

    }
    public void run(){
//                System.out.println();
        try{
            int now =(int)(stoptime/pinjun);
            System.out.println("now"+now);

            for(int i = now;i < 100; i++){

                if(this.interrupted()){
                    System.out.println("线程已经终止， for循环不再执行");
                    throw new InterruptedException();
                }
                System.out.println("i="+(i+1));



                Thread.sleep((long) pinjun*1000);
                musicslider.setValue(i);
                int temp = i;
                Time t = new Time(i);
                progressnow.setText(t.getTime());
            }

            //假如播完了怎么办?现在正常停止了并且应该重新开始,那么就应该根据播放模式进行切换




        }catch (InterruptedException e){
            System.out.println("本次拖动条线程已暂停");
//                            e.printStackTrace();
        }






    }
}
