package angelaburova.com.walkinvr;

/**
 * Created by angelaburova on 28.10.2017.
 */

public class Point<T extends Number> {

    private T x,y,z;
    public Point(T x,T y,T z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public T getX()
    {
        return this.x;
    }
    public T getY()
    {
        return this.y;
    }
    public T getZ()
    {
        return this.z;
    }
    public void writeAddX(T x)
    {
        if(x instanceof Integer)
              this.x=(T)Integer.valueOf(this.x.intValue()+x.intValue());
        else if(x instanceof Double)
             this.x=(T)(Double.valueOf(this.x.doubleValue() + x.doubleValue()));
        else if(x instanceof Float)
            this.x= (T) (Float.valueOf(this.x.floatValue() + x.floatValue()));

    }
    public void writeAddY(T y)
    {
        if(y instanceof Integer)
            this.y=(T)Integer.valueOf(this.y.intValue()+y.intValue());
        else if(y instanceof Double)
            this.y=(T)(Double.valueOf(this.y.doubleValue() + y.doubleValue()));
        else if(y instanceof Float)
            this.y= (T) (Float.valueOf(this.y.floatValue() + y.floatValue()));
    }
    public void writeAddZ(T z)
    {
        if(z instanceof Integer)
            this.z=(T)Integer.valueOf(this.z.intValue()+z.intValue());
        else if(z instanceof Double)
            this.z=(T)(Double.valueOf(this.z.doubleValue() + z.doubleValue()));
        else if(z instanceof Float)
            this.z= (T) (Float.valueOf(this.z.floatValue() + z.floatValue()));
    }

    public void writeAdd(T x,T y,T z)
    {
        writeAddX(x);
        writeAddY(y);
        writeAddZ(z);
    }

    public void setX(T x)
    {
        this.x=x;
    }
    public void setY(T y)
    {
        this.y=y;
    }
    public void setZ(T z)
    {
        this.z=z;
    }
    public void set(T x,T y, T z)
    {
        setX(x);
        setY(y);
        setZ(z);
    }


}
