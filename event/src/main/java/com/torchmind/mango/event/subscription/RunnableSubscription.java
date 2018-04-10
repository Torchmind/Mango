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
package com.torchmind.mango.event.subscription;

import com.torchmind.mango.event.subscription.filter.EventFilter;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a subscription implementation which invokes an arbitrarily defined runnable whenever a
 * matching event is received.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RunnableSubscription extends AbstractSubscription {

  private final Runnable runnable;

  public RunnableSubscription(
      @NonNull Runnable runnable,
      @NonNull EventFilter eventFilter,
      int priority) {
    super(eventFilter, priority);
    this.runnable = runnable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(@NonNull Object event) {
    this.runnable.run();
  }
}
