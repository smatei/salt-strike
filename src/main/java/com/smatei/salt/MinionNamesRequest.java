package com.smatei.salt;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.exception.SaltException;

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

    Token token = saltClient.login(credentials.GetAPIUser(), credentials.GetAPIPassword(), AuthModule.PAM);
    ClientConfig config = saltClient.getConfig();
    config.put(ClientConfig.TOKEN, token.getToken());
    config.put(ClientConfig.SOCKET_TIMEOUT, 20000);

    Map<String, Object> map = saltClient.getMinions();
    Map<String, Map<String, Object>> result = new HashMap<String, Map<String,Object>>();
    map.entrySet().forEach(entry -> {
        String key = entry.getKey();
        Object value = entry.getValue();

        // offline minions are returned as boolean
        // {"return": [{"minion": false}]}
        if (value instanceof Boolean) {
            HashMap<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("id", key);
            result.put(key, mapValue);
        } else {
            result.put(key, (Map<String, Object>) value);
        }
    });

    return result;
  }
}
