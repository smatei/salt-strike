package com.smatei.salt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Create composite column for vuetable 'data' json array model
 * (example: http://vuetable.ratiw.net/api/users), using columns from
 * salt api result map.
 *
 * @author Stefan Matei
 *
 */
public class CompositeColumn implements IColumn<String>
{
  List<SimpleColumn<String>> columnList;

  public CompositeColumn(String... columnNames)
  {
    columnList = new ArrayList<>();

    for (String columnName : columnNames)
    {
      columnList.add(new SimpleColumn<String>(columnName)
      {
        @Override
        public void CopyColumnToJson(JsonObject json, Map<String, Object> map)
        {
          json.addProperty(GetSaltAPIName(), GetValue(map));
        }
      });
    }
  }

  @Override
  public String GetSaltAPIName()
  {
    StringBuilder sb = new StringBuilder();
    columnList.stream().forEach((SimpleColumn<String> column) ->
    {
      if (sb.length() > 0)
      {
        sb.append("/");
      }
      sb.append(column.GetSaltAPIName());
    });
    return sb.toString();
  }

  /**
   * vuetable column name with format 'String/os/osmajorrelease'
   * (type/column1/column2)
   *
   * @return vuetable column name
   */
  @Override
  public String GetVuetableName()
  {
    StringBuilder sb = new StringBuilder();
    columnList.stream().forEach((SimpleColumn<String> column) ->
    {
      if (sb.length() > 0)
      {
        sb.append("/");
      }
      sb.append(column.GetSaltAPIName());
    });
    return "String/" + sb.toString();
  }

  @Override
  public String GetValue(Map<String, Object> map)
  {
    StringBuilder sb = new StringBuilder();
    columnList.stream().forEach((SimpleColumn<String> column) ->
    {
      if (sb.length() > 0)
      {
        sb.append("|");
      }
      sb.append(map.get(column.GetSaltAPIName()) == null ? "" : map.get(column.GetSaltAPIName()));
    });
    return sb.toString();
  }

  @Override
  public void CopyColumnToJson(JsonObject json, Map<String, Object> map)
  {
    json.addProperty(GetVuetableName(), GetValue(map));
  }
}
