package com.smatei.salt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.datatypes.target.NodeGroup;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;

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
   * Run commands.
   *
   * @param model
   * @return
   */
  @RequestMapping("/run.html")
  public String run(Model model)
  {
    JsonObject modules = new JsonObject();

    // TODO: build module object using reflection
    // from com.suse.salt.netapi.calls JsonObject
    JsonObject module = new JsonObject();
    module.addProperty("name", "Test");
    module.addProperty("default", "ping");
    JsonArray functions = new JsonArray();
    JsonObject function = new JsonObject();
    function.addProperty("name", "echo");
    functions.add(function);
    function = new JsonObject();
    function.addProperty("name", "ping");
    functions.add(function);
    module.add("functions", functions);

    modules.add("Test", module);

    module = new JsonObject();
    module.addProperty("name", "Status");
    module.addProperty("default", "meminfo");
    functions = new JsonArray();
    function = new JsonObject();
    function.addProperty("name", "meminfo");
    functions.add(function);
    function = new JsonObject();
    function.addProperty("name", "uptime");
    functions.add(function);
    module.add("functions", functions);

    modules.add("Status", module);

    model.addAttribute("modules", modules.toString());

    return "run";
  }

  /**
   * Example.
   *
   * @param model
   * @return
   */
  @RequestMapping("/example.html")
  public String example(Model model)
  {
    // pass data from java to html
    model.addAttribute("javaParam", "javaParamValue");
    return "example";
  }

  /**
   * API for vuetable. It needs a json like this
   *
   * http://vuetable.ratiw.net/api/users
   *
   * @return
   */
  @RequestMapping(value = "/minions", method = RequestMethod.GET)
  @ResponseBody
  public String minions(@RequestParam("sort") String sort,
      @RequestParam(value = "filter", required = false) String filter)
  {
    // the requests come in this format
    // minions?sort=String%2Fos%2Fosmajorrelease%7Casc&page=1&per_page=10
    // so we need to parse sort criteria, sort type and sort order
    // String/os/osmajorrelease|asc
    String sortCriteria = null;
    String sortOrder = null;
    String sortType = null;
    if (sort.length() > 0)
    {
      String[] split = sort.split("[|]");
      sortCriteria = split[0];
      sortOrder = split[1];

      sortType = sortCriteria.substring(0, sortCriteria.indexOf("/"));
    }

    try
    {
      MinionsRequest request = new MinionsRequest();

      return request.GetJson(sortCriteria, sortOrder, sortType, filter);
    }
    catch (SaltException e)
    {
      // TODO: DISPLAY ERROR AND LOG EXCEPTION
      e.printStackTrace();
      return null;
    }
  }

  /**
   * API for vue-select. List of minions.
   *
   * @return
   */
  @RequestMapping(value = "/minionlist", method = RequestMethod.POST)
  @ResponseBody
  public String minionList()
  {
    try
    {
      MinionNamesRequest request = new MinionNamesRequest();

      return request.GetJson(null, null, null, null);
    }
    catch (SaltException e)
    {
      // TODO: DISPLAY ERROR AND LOG EXCEPTION
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Run command using salt-api.
   *
   * @param module Salt module
   * @param function Salt module function
   *
   * @return json with result
   */
  @RequestMapping(value = "/run", method = RequestMethod.POST)
  @ResponseBody
  public String runCommand(@RequestParam("module") String module,
      @RequestParam("function") String function,
      @RequestParam("target_type") String targetType,
      @RequestParam("targets") String targets)
  {
    String packageName = "com.suse.salt.netapi.calls.modules.";
    String classFullName = packageName + module;

    JsonObject result = new JsonObject();

    Class<?> clazz;
    try
    {
      clazz = Class.forName(classFullName);

      Method method = clazz.getMethod(function, null);
      LocalCall call = (LocalCall) method.invoke(null, null);

      SaltCredentials credentials = SaltCredentials.GetInstance();
      SaltClient saltClient = new SaltClient(URI.create(credentials.GetAPIURL()));
      ClientConfig config = saltClient.getConfig();
      config.put(ClientConfig.SOCKET_TIMEOUT, 20000);

      Target<?> saltTarget = null;

      switch(targetType)
      {
      case "All":
        saltTarget = new Glob("*");
        break;
      case "Group":
        saltTarget = new NodeGroup(targets);
        break;
      case "Hosts":
        JsonParser parser = new JsonParser();
        JsonArray hosts = parser.parse(targets).getAsJsonArray();
        List<String> array = new ArrayList<String>();
        hosts.forEach(element->{
          array.add(element.getAsString());
        });

        saltTarget = new MinionList(array);
        break;
      }

      Map<String, Result<?>> res = call.callSync(saltClient, saltTarget, credentials.GetAPIUser(),
          credentials.GetAPIPassword(), AuthModule.PAM);

      JsonArray data = new JsonArray();
      result.add("return", data);

      Gson gson = new Gson();

      // parse salt library result and
      // rebuild the original JSON
      // TODO: maybe extract this to a separate function
      res.entrySet().stream().forEach((Map.Entry<String, Result<?>> entry) ->
      {
        Result<?> val = entry.getValue();
        JsonObject object = null;
        try
        {
          JsonParser parser = new JsonParser();

          if (val.result().get() instanceof com.google.gson.internal.LinkedTreeMap)
          {
            object = gson.toJsonTree(val.result().get()).getAsJsonObject();
          }
          else
          {
            object = parser.parse(val.result().get().toString()).getAsJsonObject();
          }
        }
        catch(IllegalStateException | JsonSyntaxException ex)
        {
          object = new JsonObject();
          object.addProperty("result", val.result().get().toString());
        }

        JsonObject item = new JsonObject();
        item.add(entry.getKey(), object);
        data.add(item);
      });
    }
    catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException
        | InvocationTargetException | SaltException e)
    {
      // TODO log exception here
      e.printStackTrace();

      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      String exceptionAsString = sw.toString();

      result.addProperty("error_message", e.getMessage());
      result.addProperty("error_description", exceptionAsString);
    }

    return result.toString();
  }
}
