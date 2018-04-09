package com.torchmind.mango.event.subscription;

import com.torchmind.mango.event.EventBus;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * <p>Represents a subscription registration within an arbitrary event bus.</p>
 *
 * <p>Handles provide an implementation agnostic method for removing and re-registering generated
 * subscriptions (e.g. subscriptions which have been created for a method or complete object).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface SubscriptionHandle {

  /**
   * Retrieves the event bus in which this subscription has been created.
   *
   * @return an event bus.
   */
  @NonNull
  EventBus getEventBus();

  /**
   * Evaluates whether this subscription is currently active (e.g. registered with its event bus).
   *
   * @return true if active, false otherwise.
   * @see EventBus#isRegistered(Subscription)
   */
  boolean isActive();

  /**
   * Removes the subscription from the event bus.
   *
   * @throws IllegalStateException when the subscription is not currently active.
   * @see EventBus#unregister(Subscription)
   */
  void remove();

  /**
   * Restores the subscription within the event bus.
   *
   * @throws IllegalStateException when the subscription is already active.
   * @see EventBus#register(Subscription)
   */
  void restore();
}
