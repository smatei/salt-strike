package com.smatei.salt;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * Create simple column for vuetable 'data' json array model
 * (example: http://vuetable.ratiw.net/api/users), using some column from
 * salt api result map.
 *
 * @author Stefan Matei
 *
 */
public abstract class SimpleColumn<Type> implements IColumn<Type>
{
  private String name;

  public SimpleColumn(String columnName)
  {
    name = columnName;
  }

  @Override
  public String GetSaltAPIName()
  {
    return name;
  }

  /**
   * vuetable column name with format 'String/nodename'
   * (type/column)
   *
   * @return vuetable column name
   */
  @Override
  public String GetVuetableName()
  {
    return GetColumnType() + "/" + name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Type GetValue(Map<String, Object> map)
  {
    return (Type) map.get(GetSaltAPIName());
  }

  /**
   * Get generic type. It will be displayed in the vuetable model.
   * It will be used to sort the vuetable json objects.
   *
   * @return
   */
  private String GetColumnType()
  {
    java.lang.reflect.Type genericSuperclass = this.getClass().getGenericSuperclass();
    if (genericSuperclass instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) genericSuperclass;
      java.lang.reflect.Type type = pt.getActualTypeArguments()[0];

      String className = type.getTypeName();
      return className.substring(className.lastIndexOf(".") + 1);
    }

    return "unknown";
  }
}
