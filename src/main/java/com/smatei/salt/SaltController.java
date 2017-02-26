package com.smatei.salt;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
