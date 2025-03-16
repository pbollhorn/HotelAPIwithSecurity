package app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import app.exceptions.ConfigPropertyNotFoundException;

public class Utils
{

    public static String getPropertyValue(String propName, String resourceName)
    {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName))
        {
            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null)
            {
                return value.trim();  // Trim whitespace
            } else
            {
                throw new ConfigPropertyNotFoundException(String.format("Property %s not found in %s", propName, resourceName));
            }
        }
        catch (IOException ex)
        {
            throw new ConfigPropertyNotFoundException(String.format("Could not read property %s.", propName));
        }
    }
}