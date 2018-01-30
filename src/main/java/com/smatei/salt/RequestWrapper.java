package com.smatei.salt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suse.salt.netapi.exception.SaltException;

/**
 * Adapter from salt-api result map to vuetable json model.
 * It will read columns from a salt-api result map and add colums to the vuetable
 * 'data' json array model (example: http://vuetable.ratiw.net/api/users)
 *
 * @author Stefan Matei
 *
 */
public abstract class RequestWrapper
{
  private ColumnBuilder columnBuilder;

  public RequestWrapper()
  {
    columnBuilder = CreateColumnBuilder();
  }

  protected abstract ColumnBuilder CreateColumnBuilder();

  protected abstract Map<String, Map<String, Object>> CallSaltAPI() throws SaltException;

  /**
   * Make the salt-api call, then create the vuetable json model
   * (example: http://vuetable.ratiw.net/api/users).
   *
   * @param sortCriteria
   * @param sortOrder
   * @param sortType
   * @return
   * @throws SaltException
   */
  public String GetJson(final String sortCriteria, final String sortOrder, final String sortType, String filter)
      throws SaltException
  {
    Map<String, Map<String, Object>> apiCallResult = CallSaltAPI();

    JsonObject result = new JsonObject();
    result.addProperty("total", apiCallResult.size());

    JsonArray data = new JsonArray();
    result.add("data", data);
    List<JsonObject> dataList = new ArrayList<>();

    apiCallResult.entrySet().forEach((Map.Entry<String, Map<String, Object>> entry) ->
    {
      JsonObject jsonEntry = columnBuilder.GetJson(entry.getValue(), filter);

      if (jsonEntry != null)
      {
        dataList.add(jsonEntry);
      }
    });

    if (sortCriteria != null && sortOrder != null)
    {
      Collections.sort(dataList, new Comparator<JsonObject>()
      {
        @Override
        public int compare(JsonObject o1, JsonObject o2)
        {
          int compare = 0;

          switch (sortType)
          {
          case "String":
            String str1 = o1.get(sortCriteria) == null? "": o1.get(sortCriteria).getAsString();
            String str2 = o2.get(sortCriteria) == null? "": o2.get(sortCriteria).getAsString();

            compare = str1.compareTo(str2);
            break;
          case "Double":
            Double d1 = o1.get(sortCriteria) == null? 0: o1.get(sortCriteria).getAsDouble();
            Double d2 = o2.get(sortCriteria) == null? 0: o2.get(sortCriteria).getAsDouble();
            compare = d1.compareTo(d2);
            break;
          case "Integer":
            Integer i1 = o1.get(sortCriteria) == null? 0: o1.get(sortCriteria).getAsInt();
            Integer i2 = o2.get(sortCriteria) == null? 0: o2.get(sortCriteria).getAsInt();
            compare = i1 - i2;
            break;
          }

          return compare * ("asc".equals(sortOrder) ? 1 : -1);
        }
      });
    }

    dataList.stream().forEach((JsonObject entry) ->
    {
      data.add(entry);
    });

    return result.toString();
  }
}
