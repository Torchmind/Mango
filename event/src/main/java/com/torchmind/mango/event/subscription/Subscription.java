package com.torchmind.mango.event.subscription;

import com.torchmind.mango.event.error.EventException;
import com.torchmind.mango.event.subscription.filter.EventFilter;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a subscription to an arbitrary set of event types.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Subscription extends EventFilter {

  /**
   * Defines the highest possible priority which theoretically causes a subscription to be executed
   * before any other registration.
   */
  int HIGHEST_PRIORITY = Integer.MIN_VALUE;

  /**
   * Defines the lowest possible priority which theoretically causes a subscription to be executed
   * after any other registration.
   */
  int LOWEST_PRIORITY = Integer.MAX_VALUE;

  /**
   * <p>Retrieves the relative order in which this subscription is to be executed when multiple
   * registrations match the same event.</p>
   *
   * <p>When multiple matching events indicate the same relative priority, the order of execution is
   * undefined (e.g. either of the subscriptions may gain precedence depending on the
   * implementation).</p>
   *
   * @return a priority.
   */
  int getPriority();

  /**
   * <p>Handles an event which the subscription has previously expressed interest in.</p>
   *
   * <p>This method is only invoked if both {@link #accepts(Class)} and {@link #accepts(Object)}
   * return true in response to the event type and object respectively. If any of these methods
   * returns false, the handling will be skipped.</p>
   *
   * @param event an event.
   * @throws EventException when the handler invocation fails.
   */
  void handle(@NonNull Object event);
}
