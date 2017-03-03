package com.smatei.salt;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    module.addProperty("name", "Cmd");
    module.addProperty("default", "run");
    functions = new JsonArray();
    function = new JsonObject();
    function.addProperty("name", "exec_code_all");
    functions.add(function);
    function = new JsonObject();
    function.addProperty("name", "run");
    functions.add(function);
    module.add("functions", functions);

    modules.add("Cmd", module);

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
}
