package com.smatei.salt;

import java.net.URI;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.exception.SaltException;

/**
 * Handle web requests for this application.
 *
 * @author Stefan Matei
 *
 */
@Controller
public class SaltController
{
  /**
   * List minions using salt-api credentials.
   *
   * @param model
   * @return
   */
  @RequestMapping("/")
  public String index()
  {
    return "index";
  }

  /**
   * API for vuetable. It needs a json like this
   *
   * http://vuetable.ratiw.net/api/users
   *
   * @return
   */
  @RequestMapping("/minions")
  @ResponseBody
  public String minions()
  {
    SaltCredentials credentials = SaltCredentials.GetInstance();
    SaltClient saltClient = new SaltClient(URI.create(credentials.GetAPIURL()));
    try
    {
      Token token = saltClient.login(credentials.GetAPIUser(), credentials.GetAPIPassword(), AuthModule.PAM);
      ClientConfig config = saltClient.getConfig();
      config.put(ClientConfig.TOKEN, token.getToken());
      config.put(ClientConfig.SOCKET_TIMEOUT, 20000);

      Map<String, Map<String, Object>> minions = saltClient.getMinions();

      // get the result from salt-api and translate it
      // into the vuetable json.
      JsonObject result = new JsonObject();

      result.addProperty("total", minions.size());
      JsonArray data = new JsonArray();
      result.add("data", data);

      minions.entrySet().forEach((Map.Entry<String, Map<String, Object>> entry)->{
        JsonObject jsonEntry = new JsonObject();
        jsonEntry.addProperty("minion", entry.getKey());
        jsonEntry.addProperty("nodename", (String) entry.getValue().get("nodename"));
        jsonEntry.addProperty("kernel", (String) entry.getValue().get("kernel"));
        jsonEntry.addProperty("os", (String) entry.getValue().get("os"));
        jsonEntry.addProperty("osmajorrelease", (String) entry.getValue().get("osmajorrelease"));
        jsonEntry.addProperty("osarch", (String) entry.getValue().get("osarch"));
        jsonEntry.addProperty("cpuarch", (String) entry.getValue().get("cpuarch"));
        jsonEntry.addProperty("mem_total", (Double) entry.getValue().get("mem_total"));

        data.add(jsonEntry);
      });

      return result.toString();
    }
    catch (SaltException e)
    {
      // TODO: DISPLAY ERROR AND LOG EXCEPTION
      e.printStackTrace();
      return null;
    }
  }
}
