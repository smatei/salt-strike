package com.smatei.salt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 *
 * Read Salt-API credentials from scr/main/resources/salt-api.properties.
 *
 * @author Stefan Matei
 *
 */
public final class SaltCredentials
{
  private final Properties apiProperties;

  public SaltCredentials()
  {
    apiProperties = new Properties();

    URL fileURL = getClass().getClassLoader().getResource("salt-api.properties");

    try(InputStream inputStream = fileURL.openStream())
    {
      apiProperties.load(inputStream);
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
    }
  }

  public final String GetAPIURL()
  {
    return apiProperties.getProperty("salt.api.url");
  }

  public final String GetAPIUser()
  {
    return apiProperties.getProperty("salt.api.user");
  }

  public final String GetAPIPassword()
  {
    return apiProperties.getProperty("salt.api.password");
  }
}
