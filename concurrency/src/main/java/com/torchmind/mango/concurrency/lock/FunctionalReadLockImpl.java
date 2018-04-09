/*
 * Copyright 2016 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.torchmind.mango.concurrency.lock;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Provides a delegating smart read lock implementation.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class FunctionalReadLockImpl extends DelegatingFunctionalLock implements FunctionalReadLock {

  FunctionalReadLockImpl(@NonNull Lock lock) {
    super(lock);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <R> R get(@NonNull Supplier<R> supplier) {
    this.lock();
    return this.unlock(supplier);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <R> R getInterruptibly(@NonNull Supplier<R> supplier) throws InterruptedException {
    this.lockInterruptibly();
    return this.unlock(supplier);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public <R> Optional<R> tryGet(@NonNull Supplier<R> supplier) {
    if (!this.tryLock()) {
      return Optional.empty();
    }

    return Optional.of(this.unlock(supplier));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public <R> Optional<R> tryGet(long time, @NonNull TimeUnit timeUnit,
      @NonNull Supplier<R> supplier) throws InterruptedException {
    if (!this.tryLock(time, timeUnit)) {
      return Optional.empty();
    }

    return Optional.of(this.unlock(supplier));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <R> R unlock(@Nullable R value) {
    this.unlock();
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <R> R unlock(@NonNull Supplier<R> supplier) {
    try {
      return supplier.get();
    } finally {
      this.unlock();
    }
  }
}
