package com.smatei.salt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * Entry point for the application.
 *
 * @author Stefan Matei
 *
 */
@SpringBootApplication
@ResponseBody
public class SaltStrike
{

  public static void main(String[] args)
  {
    SpringApplication.run(SaltStrike.class, args);
  }
}
