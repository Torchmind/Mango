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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Provides a delegating smart lock implementation.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class FunctionalLockImpl implements FunctionalLock {

  private final Lock lock;

  FunctionalLockImpl(@NonNull Lock lock) {
    this.lock = lock;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void runProtected(@NonNull Runnable runnable) {
    this.lock();

    try {
      runnable.run();
    } finally {
      this.unlock();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void lock() {
    this.lock.lock();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void lockInterruptibly() throws InterruptedException {
    this.lock.lockInterruptibly();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Condition newCondition() {
    return this.lock.newCondition();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryRunProtected(@NonNull Runnable runnable) {
    if (!this.tryLock()) {
      return false;
    }

    try {
      runnable.run();
    } finally {
      this.unlock();
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryRunProtected(long time, @NonNull TimeUnit timeUnit, @NonNull Runnable runnable)
      throws InterruptedException {
    if (!this.tryLock(time, timeUnit)) {
      return false;
    }

    try {
      runnable.run();
    } finally {
      this.unlock();
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryLock() {
    return this.lock.tryLock();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return this.lock.tryLock(time, unit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unlock() {
    this.lock.unlock();
  }
}
