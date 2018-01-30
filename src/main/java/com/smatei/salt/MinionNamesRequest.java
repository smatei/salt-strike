package com.smatei.salt;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.modules.Grains;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;

/**
 * Get list of minions from salt-api and create a
 * vuetable json model for MinionsTable.vue component.
 *
 * @author Stefan Matei
 *
 */
public class MinionNamesRequest extends RequestWrapper
{
  @Override
  protected ColumnBuilder CreateColumnBuilder()
  {
    ColumnBuilder columnBuilder = new ColumnBuilder();
    columnBuilder.appendStringColumn("id");

    return columnBuilder;
  }

  @Override
  protected Map<String, Map<String, Object>> CallSaltAPI() throws SaltException
  {
    SaltCredentials credentials = SaltCredentials.GetInstance();
    SaltClient saltClient = new SaltClient(URI.create(credentials.GetAPIURL()));

    // TODO: USE SOMETHING LIGHTER THAN grains.items
    // SOMETHING THAT RETURNS JUST THE NAMES
    Target<String> target = new Glob("*");
    Map<String, Result<Map<String, Object>>> res = Grains.items(false).callSync(saltClient, target,
            credentials.GetAPIUser(), credentials.GetAPIPassword(), AuthModule.PAM);

    Map<String, Map<String, Object>> result = new HashMap<String, Map<String,Object>>();

    res.forEach((key, value)->{
        try
        {
            result.put(key, value.result().get());
        }
        catch(NoSuchElementException ex)
        {
            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("id", key);
            result.put(key, valueMap);
        }
    });

    return result;
  }
}
