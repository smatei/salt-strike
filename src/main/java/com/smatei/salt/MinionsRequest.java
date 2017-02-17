package com.smatei.salt;

import java.net.URI;
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
public class MinionsRequest extends RequestWrapper
{
  @Override
  protected ColumnBuilder CreateColumnBuilder()
  {
    ColumnBuilder columnBuilder = new ColumnBuilder();

    columnBuilder.appendStringColumn("id");
    columnBuilder.appendStringColumn("nodename");
    columnBuilder.appendStringColumn("kernel");
    columnBuilder.appendCompositeColumn("os", "osmajorrelease");
    columnBuilder.appendStringColumn("osarch");
    columnBuilder.appendDoubleColumn("mem_total");

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

    return saltClient.getMinions();
  }
}
