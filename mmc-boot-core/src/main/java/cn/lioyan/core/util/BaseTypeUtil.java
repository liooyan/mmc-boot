package cn.lioyan.core.util;


import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 基础类型转换方法
 */
public class BaseTypeUtil
{

    private static final Pattern NUMBER_WITH_TRAILING_ZEROS_PATTERN = Pattern.compile("\\.0*$");

    public static <T> T parsing(Object source, Class type)
        throws ClassCastException
    {
        if (int.class == type || type == Integer.class)
        {
            return (T)parsingInt(source);
        }
        else if (String.class == type)
        {
            return (T)parsingString(source);
        }
        else if (boolean.class == type || Boolean.class == type)
        {
            return (T)parsingBoolean(source);
        }
        else if (double.class == type || Double.class == type)
        {
            return (T)parsingDouble(source);
        }
        else if (long.class == type || Long.class == type)
        {
            return (T)parsingLong(source);
        }else {
            return (T)JacksonUtils.fromJson(source.toString(),type);
        }

    }

    private static Double parsingDouble(Object source)
    {
        if (source == null)
        {
            return null;
        }
        if (source instanceof Number)
        {
            return ((Number)source).doubleValue();
        }
        if (source instanceof String)
        {
            String strVal = source.toString();
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal))
            {
                return null;
            }
            if (strVal.indexOf(',') != -1)
            {
                strVal = strVal.replaceAll(",", "");
            }
            return Double.parseDouble(strVal);
        }

        if (source instanceof Boolean)
        {
            return (Boolean)source ? 1D : 0D;
        }
        throwClassCastException(source, "Double");
        return null;
    }

    private static Long parsingLong(Object source)
    {
        if (source == null)
        {
            return null;
        }

        if (source instanceof BigDecimal)
        {
            return longValue((BigDecimal)source);
        }

        if (source instanceof Number)
        {
            return ((Number)source).longValue();
        }

        if (source instanceof String)
        {
            String strVal = (String)source;
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal))
            {
                return null;
            }
            if (strVal.indexOf(',') != -1)
            {
                strVal = strVal.replaceAll(",", "");
            }
            try
            {
                return Long.parseLong(strVal);
            }
            catch (NumberFormatException ex)
            {
                throwClassCastException(source, "long");
            }
        }

        if (source instanceof Boolean)
        {
            return (Boolean)source ? 1L : 0L;
        }
        throwClassCastException(source, "long");
        return null;
    }

    private static Boolean parsingBoolean(Object source)
    {
        if (source == null)
        {
            return null;
        }
        if (source instanceof Boolean)
        {
            return (Boolean)source;
        }

        if (source instanceof BigDecimal)
        {
            return intValue((BigDecimal)source) == 1;
        }

        if (source instanceof Number)
        {
            return ((Number)source).intValue() == 1;
        }

        if (source instanceof String)
        {
            String strVal = (String)source;
            if (strVal.length() == 0 //
                || "null".equals(strVal) //
                || "NULL".equals(strVal))
            {
                return null;
            }
            if ("true".equalsIgnoreCase(strVal) //
                || "1".equals(strVal))
            {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal) //
                || "0".equals(strVal))
            {
                return Boolean.FALSE;
            }
            if ("Y".equalsIgnoreCase(strVal) //
                || "T".equals(strVal))
            {
                return Boolean.TRUE;
            }
            if ("F".equalsIgnoreCase(strVal) //
                || "N".equals(strVal))
            {
                return Boolean.FALSE;
            }
        }
        throwClassCastException(source, "boolean");
        return null;
    }

    private static String parsingString(Object source)
        throws ClassCastException
    {
        String value;
        if (source == null)
        {
            value = null;
        }
        else
        {
            value = source.toString();
        }
        return value;
    }

    private static Integer parsingInt(Object source)
        throws ClassCastException
    {

        try
        {
            if (source == null)
            {
                return null;
            }

            if (source instanceof Integer)
            {
                return (Integer)source;
            }

            if (source instanceof BigDecimal)
            {
                return intValue((BigDecimal)source);
            }

            if (source instanceof Number)
            {
                return ((Number)source).intValue();
            }

            if (source instanceof String)
            {
                String strVal = (String)source;
                if (strVal.length() == 0 //
                    || "null".equals(strVal) //
                    || "NULL".equals(strVal))
                {
                    return null;
                }
                if (strVal.indexOf(',') != -1)
                {
                    strVal = strVal.replaceAll(",", "");
                }

                Matcher matcher = NUMBER_WITH_TRAILING_ZEROS_PATTERN.matcher(strVal);
                if (matcher.find())
                {
                    strVal = matcher.replaceAll("");
                }
                try
                {

                    return Integer.parseInt(strVal);
                }
                catch (Exception e)
                {
                    throwClassCastException(source, "long");
                }
            }

            if (source instanceof Boolean)
            {
                return (Boolean)source ? 1 : 0;
            }
        }
        catch (Exception ex)
        {
            throwClassCastException(source, "int");
        }
        throwClassCastException(source, "int");
        return null;

    }

    private static void throwClassCastException(Object source, String type)
    {
        if (source == null)
        {
            throw new ClassCastException("can not parsing to " + type + " ,source is null");
        }
        throw new ClassCastException(
            "can not parsing to " + type + " ,source value:" + source + ", source type:"
            + source.getClass());
    }

    public static long longValue(BigDecimal decimal)
    {
        if (decimal == null)
        {
            return 0;
        }

        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100)
        {
            return decimal.longValue();
        }

        return decimal.longValueExact();
    }

    private static int intValue(BigDecimal decimal)
    {
        if (decimal == null)
        {
            return 0;
        }

        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100)
        {
            return decimal.intValue();
        }

        return decimal.intValueExact();
    }

}
