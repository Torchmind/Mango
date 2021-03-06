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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Extends the lock specification to provide more modern approaches for executing operations inside
 * of locks.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface FunctionalLock extends Lock {

  /**
   * Creates a smart lock instance by wrapping an existing lock instance.
   *
   * @param lock a lock.
   * @return a wrapped smart lock.
   */
  @NonNull
  static FunctionalLock wrap(@NonNull Lock lock) {
    return new DelegatingFunctionalLock(lock);
  }

  /**
   * Executes an arbitrary runnable within the protection of this lock.
   *
   * @param runnable a runnable declaring the action to be taken within the safety of this lock.
   * @see #lock()
   */
  default void runProtected(@NonNull Runnable runnable) {
    this.lock();

    try {
      runnable.run();
    } finally {
      this.unlock();
    }
  }

  /**
   * Executes a supplier within the protection of this lock.
   *
   * @param supplier a supplier.
   * @param <R> a return type.
   * @return a return value.
   */
  default <R> R runProtected(@NonNull Supplier<R> supplier) {
    this.lock();

    try {
      return supplier.get();
    } finally {
      this.unlock();
    }
  }

  /**
   * Attempts to execute an arbitrary runnable within the protection of this lock.
   *
   * @param runnable a runnable declaring the action to be taken within the safety of this lock.
   * @return true if the lock was acquired, false if another thread is currently holding this lock
   * and no action was taken.
   * @see #tryLock()
   */
  default boolean tryRunProtected(@NonNull Runnable runnable) {
    if (!this.tryLock()) {
      return false;
    }

    try {
      runnable.run();
      return true;
    } finally {
      this.unlock();
    }
  }

  /**
   * Attempts to execute an arbitrary runnable within the protection of this lock.
   *
   * @param time an amount of time to wait for the lock holder to release this lock.
   * @param timeUnit a unit of time.
   * @param runnable a runnable declaring the action to be taking within the safety of this lock.
   * @return true if the lock was acquired, false if another thread is currently holding this lock
   * and no action was taken.
   * @throws InterruptedException when the specified amount of time is exceeded.
   * @see #tryLock(long, TimeUnit)
   */
  default boolean tryRunProtected(long time, @NonNull TimeUnit timeUnit, @NonNull Runnable runnable)
      throws InterruptedException {
    if (!this.tryLock(time, timeUnit)) {
      return false;
    }

    try {
      runnable.run();
      return true;
    } finally {
      this.unlock();
    }
  }
}
