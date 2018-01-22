package tpoffline;

/**
 * Created by Cesar on 7/3/2017.
 */

public class TimeDelta {

    private long t1=-1;
    private long t2=-1;

    public void markStartTime() {
        t1 = System.currentTimeMillis();

    }

    public void markEndTime() {

        t2 = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        if(t1 ==-1) {
            return super.toString() + " tiempo inicial invalido " + t1;
        }
        if(t2 ==-1) {
            return super.toString() + " tiempo final invalido " + t2;
        }
        return  super.toString() + " tiempo delta:= " +  (t2-t1)/1000.0 + " segs = " +  ((t2-t1)/1000.0) / 60 + " mins.";
    }

}

