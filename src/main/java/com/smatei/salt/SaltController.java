package com.smatei.salt;

import java.net.URI;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
  public String index(Model model)
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

      model.addAttribute("minions", minions);
    }
    catch (SaltException e)
    {
      // TODO: DISPLAY ERROR AND LOG EXCEPTION
      e.printStackTrace();
    }
    return "index";
  }
}
