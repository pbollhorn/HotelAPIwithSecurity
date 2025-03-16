package app.exceptions;

public class ConfigPropertyNotFoundException extends RuntimeException
{
    public ConfigPropertyNotFoundException(String message)
    {
        super(message);
    }
}
