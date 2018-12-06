package cn.jade.Tools;

public class Time {

    public double totaltime;
    public double minutes;
    public double seconds;
    public String time;


    public Time(double t){
        this.totaltime = t;
        minutes = totaltime/60;
        seconds = totaltime%60;
    }
    public Time(String t){
        this.time = t;
    }
    public void setTime(){
        minutes = totaltime/60;
        seconds = totaltime%60;

    }
    //转化成00:00格式
    public String getTime(){
        int number1 = (int)minutes;
        String str1 = String.format("%02d", number1);
        int number2 = (int)seconds;
        String str2 = String.format("%02d",number2);
        return str1+":"+str2;


    }
    //转换成毫秒
    public double getSeconds(){
        double s = 0;
        int index1=time.indexOf(":");
        int index2=time.indexOf(".",index1+1);
        s = Integer.parseInt(time.substring(0,index1))*60;//分钟
        s+=Integer.parseInt(time.substring(index1+1,index2));//秒钟
        s+= Double.parseDouble(time.substring(index2+1))/1000;//豪秒
        return s;

    }
//    public static void main(String []args){
//        String s = "03:31.370";
//        Time t = new Time(s);
//        System.out.println(t.getSeconds());
//    }
}
