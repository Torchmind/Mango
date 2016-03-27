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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Provides a delegating smart read write lock implementation.
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
class SmartReadWriteLockImpl implements SmartReadWriteLock {
        private final SmartReadLock readLock;
        private final SmartLock writeLock;

        SmartReadWriteLockImpl(@Nonnull ReadWriteLock lock) {
                this.readLock = SmartReadLock.wrap(lock.readLock());
                this.writeLock = SmartLock.wrap(lock.writeLock());
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public SmartReadLock readLock() {
                return this.readLock;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public SmartLock writeLock() {
                return this.writeLock;
        }
}
