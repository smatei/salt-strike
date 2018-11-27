package com.smatei.salt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * List of columns that will be copied from the salt-api result map to the
 * vuetable json model.
 *
 * @author Stefan Matei
 *
 */
public class ColumnBuilder
{
  List<IColumn<?>> columns;

  public ColumnBuilder()
  {
    columns = new ArrayList<>();
  }

  public void appendIntegerColumn(String columnName)
  {
    columns.add(new SimpleColumn<Integer>(columnName)
    {
      @Override
      public void CopyColumnToJson(JsonObject json, Map<String, Object> map)
      {
        json.addProperty(GetVuetableName(), GetValue(map));
      }
    });
  }

  public void appendDoubleColumn(String columnName)
  {
    columns.add(new SimpleColumn<Double>(columnName)
    {
      @Override
      public void CopyColumnToJson(JsonObject json, Map<String, Object> map)
      {
        json.addProperty(GetVuetableName(), GetValue(map));
      }
    });
  }

  public void appendStringColumn(String columnName)
  {
    columns.add(new SimpleColumn<String>(columnName)
    {
      @Override
      public void CopyColumnToJson(JsonObject json, Map<String, Object> map)
      {
        json.addProperty(GetVuetableName(), GetValue(map));
      }

      @Override
      public boolean MatchesFilter(JsonElement value, String filter)
      {
        if (filter == null)
        {
          return true;
        }

        return value.toString().toLowerCase().contains(filter);
      }
    });
  }

  public void appendCompositeColumn(String... columnNames)
  {
    columns.add(new CompositeColumn(columnNames));
  }

  public JsonObject GetJson(Map<String, Object> saltAPIResultEntry, String filter)
  {
    JsonObject json = new JsonObject();

    columns.stream().forEach((IColumn<?> column) ->
    {
      Object object = column.GetValue(saltAPIResultEntry);
      if (object != null)
      {
        column.CopyColumnToJson(json, saltAPIResultEntry);
      }
    });

    if (filter == null)
    {
      return json;
    }

    boolean result = columns.stream()
        .anyMatch(column -> column.MatchesFilter(json.get(column.GetVuetableName()), filter));
    return result ? json : null;
  }
}
