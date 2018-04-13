package org.apache.cordova.plugin;

/**
 * Exception that is thrown to show error to end user.
 */
public class Wl_UserException extends Exception
{
  /**
   * Error code.
   */
  private String s_error;

  /**
   * Error message.
   */
  private String s_message;

  /**
   * Initializes a new user exception.
   *
   * @param s_error Error code.
   * @param s_message Error message.
   */
  Wl_UserException(String s_error, String s_message)
  {
    this.s_error=s_error;
    this.s_message=s_message;
  }

  /**
   * Returns error code.
   *
   * @return Error code.
   */
  public String errorGet()
  {
    return this.s_error;
  }

  /**
   * Returns error message.
   *
   * @return Error message.
   */
  public String messageGet()
  {
    return this.s_message;
  }
}
