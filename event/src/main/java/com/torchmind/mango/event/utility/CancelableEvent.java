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
