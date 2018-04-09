package com.torchmind.mango.event.error;

/**
 * Informs the caller about a failed event invocation (when one of the subscriptions throws an
 * exception, for instance).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class EventException extends RuntimeException {

  public EventException() {
  }

  public EventException(String message) {
    super(message);
  }

  public EventException(String message, Throwable cause) {
    super(message, cause);
  }

  public EventException(Throwable cause) {
    super(cause);
  }
}
