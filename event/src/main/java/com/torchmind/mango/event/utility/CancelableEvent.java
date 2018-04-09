package com.torchmind.mango.event.utility;

/**
 * <p>Provides a specification for events which may be cancelled (either by their publisher or one
 * of its subscriptions).</p>
 *
 * <p>Typically event publishers will invoke events of this type <strong>before</strong> actually
 * performing the associated action. The cancellation state is then checked before the action is
 * executed to decide based on the event cancellation state.</p>
 *
 * <p>Note that this specification does not define how the cancellation state of an event is
 * updated. It purely defines how this value is to be expressed in an implementation agnostic
 * manner.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface CancelableEvent {

  /**
   * Evaluates whether this event has been cancelled by the publisher or another subscription with a
   * higher priority.
   *
   * @return true if cancelled, false otherwise.
   */
  boolean isCancelled();
}
