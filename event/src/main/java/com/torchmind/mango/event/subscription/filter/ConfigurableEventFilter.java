package com.torchmind.mango.event.subscription.filter;

import com.torchmind.mango.event.utility.CancelableEvent;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Set;

/**
 * Provides an event filter implementation which is exclusively used when dynamically creating
 * filters using the builder API.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class ConfigurableEventFilter implements EventFilter {

  private Set<Class<?>> eventTypes;

  private boolean acceptingCancelled;
  private boolean acceptingHeirs;

  public ConfigurableEventFilter(
      @NonNull Set<Class<?>> eventTypes,
      boolean acceptingCancelled,
      boolean acceptingHeirs) {
    this.eventTypes = eventTypes;
    this.acceptingCancelled = acceptingCancelled;
    this.acceptingHeirs = acceptingHeirs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Class<?> type) {
    if (!this.acceptingHeirs) {
      return this.eventTypes.contains(type);
    }

    return this.eventTypes.stream().anyMatch((clazz) -> clazz.isAssignableFrom(type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Object event) {
    return !(event instanceof CancelableEvent) || this.acceptingCancelled
        || !((CancelableEvent) event).isCancelled();
  }
}
