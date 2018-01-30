package com.smatei.salt;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    JsonObject modules = GetSaltModulesAndFunctions();

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
      @RequestParam("targets") String targets,
      @RequestParam(value="params", required=false) String params)
  {
    String packageName = "com.suse.salt.netapi.calls.modules.";
    String classFullName = packageName + module;

    JsonObject result = new JsonObject();

    Class<?> clazz;
    try
    {
      clazz = Class.forName(classFullName);

      Object[] parameterValues = null;
      Class[] parameterTypes = null;

      if (params != null)
      {
        JsonParser parser = new JsonParser();
        JsonObject parametersJSON = parser.parse(params).getAsJsonObject();

        Set<Entry<String, JsonElement>> entries = parametersJSON.entrySet();
        parameterValues = new Object[entries.size()];
        parameterTypes = new Class[entries.size()];

        for (Entry<String, JsonElement> entry: entries)
        {
          int index = entry.getValue().getAsJsonObject().get("index").getAsInt();
          String className = entry.getValue().getAsJsonObject().get("type").getAsString();
          if ("boolean".equals(className))
          {
              parameterTypes[index] = boolean.class;
              parameterValues[index] = entry.getValue().getAsJsonObject().get("value").getAsBoolean();
          }
          else if ("int".equals(className))
          {
              parameterTypes[index] = int.class;
              parameterValues[index] = entry.getValue().getAsJsonObject().get("value").getAsInt();
          }
          else if ("float".equals(className))
          {
              parameterTypes[index] = float.class;
              parameterValues[index] = entry.getValue().getAsJsonObject().get("value").getAsFloat();
          }
          else if ("double".equals(className))
          {
              parameterTypes[index] = double.class;
              parameterValues[index] = entry.getValue().getAsJsonObject().get("value").getAsDouble();
          }
          else if ("short".equals(className))
          {
              parameterTypes[index] = double.class;
              parameterValues[index] = entry.getValue().getAsJsonObject().get("value").getAsShort();
          }
          else if ("byte".equals(className))
          {
              parameterTypes[index] = byte.class;
              parameterValues[index] = entry.getValue().getAsJsonObject().get("value").getAsByte();
          }
          else
          {
              Class paramClass = Class.forName(className);
              parameterTypes[index] = paramClass;
              // TODO: change this to appropriate type
              Object parameterValue = entry.getValue().getAsJsonObject().get("value").getAsString();
              parameterValues[index] = parameterValue;
          }
        }
      }

      Method method = clazz.getMethod(function, parameterTypes);

      LocalCall call = (LocalCall) method.invoke(null, parameterValues);

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
        catch(NoSuchElementException ex)
        {
            object = new JsonObject();
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

  /**
   * List modules and their functions from com.suse.salt.netapi.calls.modules
   * @return
   */
  private JsonObject GetSaltModulesAndFunctions()
  {
    JsonObject modules = new JsonObject();

    try
    {
      ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
      ImmutableSet<ClassInfo> classes = cp.getTopLevelClasses("com.suse.salt.netapi.calls.modules");

      for (ClassInfo clazz: classes)
      {
        String moduleName = clazz.getSimpleName();

        Class<?> classObject = Class.forName(clazz.toString());

        Method[] methods = classObject.getMethods();

        JsonObject module = new JsonObject();

        JsonArray functions = new JsonArray();

        if (moduleName.equals("Cmd"))
        {
          System.out.println();
        }

        for (Method method: methods)
        {
          if ("com.suse.salt.netapi.calls.LocalCall".equals(method.getReturnType().getCanonicalName()))
          {
            JsonObject function = new JsonObject();
            function.addProperty("name", method.getName());

            JsonArray parameters = new JsonArray();

            Parameter[] params = method.getParameters();

            if (params.length > 0)
            {
              for (Parameter param: params)
              {
                JsonObject parameter = new JsonObject();

                parameter.addProperty("name", param.getName());
                parameter.addProperty("type", param.getType().getCanonicalName());

                parameters.add(parameter);
              }

              function.add("parameters", parameters);
            }
            functions.add(function);
          }
        }

        module.addProperty("name", moduleName);
        if ("Test".equals(moduleName))
        {
          module.addProperty("default", "ping");
        }
        module.add("functions", functions);

        modules.add(moduleName, module);
      }
    }
    catch (IOException e)
    {
      // TODO Log exception
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      // TODO Log exception
      e.printStackTrace();
    }

    return modules;
  }
}
