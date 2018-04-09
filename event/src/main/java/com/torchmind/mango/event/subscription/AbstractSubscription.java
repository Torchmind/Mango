package com.torchmind.mango.event.subscription;

import com.torchmind.mango.event.subscription.filter.EventFilter;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides an abstract subscription implementation which delegates all of its decision making to an
 * arbitrarily defined event filter.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractSubscription implements Subscription {

  private final EventFilter eventFilter;
  private final int priority;

  public AbstractSubscription(@NonNull EventFilter eventFilter, int priority) {
    this.eventFilter = eventFilter;
    this.priority = priority;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPriority() {
    return this.priority;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Class<?> type) {
    return this.eventFilter.accepts(type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Object event) {
    return this.eventFilter.accepts(event);
  }
}
