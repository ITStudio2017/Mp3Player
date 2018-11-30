package cn.jade.Tools;

public class Time {

    public double totaltime;
    public double minutes;
    public double seconds;
    public Time(double t){
        this.totaltime = t;
        minutes = totaltime/60;
        seconds = totaltime%60;
    }
    public void setTime(){
        minutes = totaltime/60;
        seconds = totaltime%60;

    }
    public String getTime(){
        int number1 = (int)minutes;
        String str1 = String.format("%02d", number1);
        int number2 = (int)seconds;
        String str2 = String.format("%02d",number2);
        return str1+":"+str2;


    }
}
