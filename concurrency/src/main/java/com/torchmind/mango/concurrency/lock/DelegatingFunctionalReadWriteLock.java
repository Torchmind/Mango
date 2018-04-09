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
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Provides a wrapper which delegates all of its lock calls to an arbitrary read/write lock
 * implementation in order to provide support for the functional lock specification.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DelegatingFunctionalReadWriteLock implements FunctionalReadWriteLock {

  private final FunctionalLock readLock;
  private final FunctionalLock writeLock;

  public DelegatingFunctionalReadWriteLock(@NonNull ReadWriteLock lock) {
    this.readLock = FunctionalLock.wrap(lock.readLock());
    this.writeLock = FunctionalLock.wrap(lock.writeLock());
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public FunctionalLock readLock() {
    return this.readLock;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public FunctionalLock writeLock() {
    return this.writeLock;
  }
}
