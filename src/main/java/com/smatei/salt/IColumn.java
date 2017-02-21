package com.smatei.salt;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * Adapter interface from salt-api colum (or columns) to vuetable column.
 * It will read columns from a salt-api result map and add colums to the vuetable
 * 'data' json array model (example: http://vuetable.ratiw.net/api/users)
 *
 * @author Stefan Matei
 *
 */
public interface IColumn<Type>
{
  /**
   * vuetable column name with format 'String/os/osmajorrelease'
   * (type/column1/column2) for composite columns
   * and 'String/nodename'
   * (type/column) for simple column
   *
   * @return vuetable column name
   */
  public String GetVuetableName();

  /**
   * @return salt api response column name
   */
  public String GetSaltAPIName();

  /**
   * Use the GetSaltAPIName method to get the original salt api
   * values from the original map.
   *
   * @param saltAPIMap salt api result map
   * @return
   */
  public Type GetValue(Map<String, Object> saltAPIMap);

  /**
   * Get original salt api values from the saltAPIMap using GetValue method and GetSaltAPIName
   * and add them to the vuetable json using GetVuetableName
   *
   * @param vuetableJSON json for vuetable model (example: http://vuetable.ratiw.net/api/users)
   * @param saltAPIMap salt api result map
   */
  public void CopyColumnToJson(JsonObject vuetableJSON, Map<String, Object> saltAPIMap);

  /**
   * Filter the vuetable json object using a text filter.
   * By default the columns do not have filter enabled (return false).
   * If a column wants to implement filter, it has to override.
   */
  public default boolean MatchesFilter(JsonElement value, String filter)
  {
    return false;
  }
}
