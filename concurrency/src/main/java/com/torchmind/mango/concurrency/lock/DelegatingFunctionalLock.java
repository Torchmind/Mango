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
 * Provides a wrapper which delegates all of its lock calls to an arbitrary lock implementation in
 * order to provide support for the functional lock specification.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DelegatingFunctionalLock implements FunctionalLock {

  private final Lock lock;

  DelegatingFunctionalLock(@NonNull Lock lock) {
    this.lock = lock;
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
  public boolean tryLock() {
    return this.lock.tryLock();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryLock(long l, @NonNull TimeUnit timeUnit) throws InterruptedException {
    return this.lock.tryLock(l, timeUnit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unlock() {
    this.lock.unlock();
  }
}
