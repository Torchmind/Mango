package com.torchmind.mango.event.utility;

/**
 * Provides an extension to the cancelable event specification which permits the direct change of
 * the cancellation state.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface MutableCancelableEvent extends CancelableEvent {

  /**
   * <p>Defines whether or not the event is to be cancelled (e.g. whether the publisher will invoke
   * the associated action).</p>
   *
   * <p>Note that changes to this state may cause subsequent listeners to be skipped due to their
   * filters or may negate the state defined by a previously executed subscription.</p>
   *
   * @param value if true marks the event cancelled, otherwise restores its state.
   */
  void setCancelled(boolean value);
}
