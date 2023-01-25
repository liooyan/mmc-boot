package cn.lioyan.core.util;


import java.util.Collection;


public class NullUtil
{
    public static boolean isNull(Object obj)
    {
        if (obj == null)
        {
            return true;
        }
        if (obj instanceof String)
        {
            return obj.equals("");
        }
        if (obj instanceof Collection)
        {
            return ((Collection<?>)obj).isEmpty();
        }
        return false;

    }

    public static boolean notNull(Object obj)
    {
        return !isNull(obj);
    }

}
