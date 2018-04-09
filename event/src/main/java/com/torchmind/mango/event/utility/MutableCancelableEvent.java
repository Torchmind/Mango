/*
 * Copyright 2018 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
