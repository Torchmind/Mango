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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Provides a few extensions to Java's lock interface to improve code quality when working with blocking structures.
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface SmartLock extends Lock {

        /**
         * Creates a smart lock instance by wrapping an existing lock instance.
         * @param lock a lock.
         * @return a wrapped smart lock.
         */
        @Nonnull
        static SmartLock wrap(@Nonnull Lock lock) {
                return new SmartLockImpl(lock);
        }

        /**
         * Acquires the lock, executes an action and releases the lock.
         * @param runnable a runnable declaring the action to be taken within the safety of this lock.
         * @see #lock() for a more specific documentation on the locking process.
         * @see #unlock() for a more specific documentation on the unlocking process.
         */
        void execute(@Nonnull Runnable runnable);

        /**
         * Acquires the lock interruptibly, executes an action and releases the lock.
         * @param runnable a runnable declaring teh action to be taken within the safety of this lock.
         * @throws InterruptedException when the locking process is interrupted.
         * @see #lockInterruptibly() for a more specific documentation on the locking process.
         * @see #unlock() for a more specific documentation on the unlocking process.
         */
        void executeInterruptibly(@Nonnull Runnable runnable) throws InterruptedException;

        /**
         * Attempts to acquire the lock, execute and action and release the lock.
         * @param runnable a runnable declaring the action to be taken within the safety of this lock.
         * @return true if the lock was acquired, false if another thread is currently holding this lock and no action was taken.
         * @see #tryLock() for a more specific documentation on the locking process.
         * @see #unlock() for a more specific documentation on the unlocking process.
         */
        boolean tryExecute(@Nonnull Runnable runnable);

        /**
         * Attempts to acquire the lock, execute and action and release the lock while limiting the process of lock
         * acquisition to the specified amount of time.
         * @param time an amount of time to wait for the lock holder to release this lock.
         * @param timeUnit a unit of time.
         * @param runnable a runnable declaring the action to be taking within the safety of this lock.
         * @return true if the lock was acquired, false if another thread is currently holding this lock and no action was taken.
         * @see #tryLock(long, TimeUnit) for a more specific documentation on the locking process.
         * @see #unlock() for a more specific documentation on the unlocking process.
         * @throws InterruptedException when the specified amount of time is exceeded.
         */
        boolean tryExecute(@Nonnegative long time, @Nonnull TimeUnit timeUnit, @Nonnull Runnable runnable) throws InterruptedException;
}
