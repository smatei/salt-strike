package com.smatei.salt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handle web requests for this application.
 *
 * @author Stefan Matei
 *
 */
@Controller
public class SaltController
{

  @ResponseBody
  @RequestMapping("/")
  public String index()
  {
    return "index";
  }
}
