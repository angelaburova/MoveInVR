package angelaburova.com.moveinvr;

/**
 * Created by angelaburova on 28.10.2017.
 */

public class Point {

    private double x,y,z;
    public Point(double x,double y,double z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public double getX()
    {
        return this.x;
    }
    public double getY()
    {
        return this.y;
    }
    public double getZ()
    {
        return this.z;
    }
    public void writeAddX(double x)
    {
        this.x+=x;
    }
    public void writeAddY(double y)
    {
        this.y+=y;
    }
    public void writeAddZ(double z)
    {
        this.z+=z;
    }

    public void writeAdd(double x,double y,double z)
    {
        this.x+=x;
        this.y+=y;
        this.z+=z;
    }

    public void setX(double x)
    {
        this.x=x;
    }
    public void setY(double y)
    {
        this.y=y;
    }
    public void setZ(double z)
    {
        this.z=z;
    }
    public void set(double x,double y, double z)
    {
        setX(x);
        setY(y);
        setZ(z);
    }
    public double[] getArray()
    {
        return new double[]{x,y,z};
    }

}
