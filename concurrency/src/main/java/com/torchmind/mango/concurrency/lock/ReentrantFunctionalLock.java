package com.torchmind.mango.concurrency.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides a variation of the reentrant lock implementation which implements methods from the
 * functional lock specification.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ReentrantFunctionalLock extends ReentrantLock implements FunctionalLock {

  public ReentrantFunctionalLock() {
  }

  public ReentrantFunctionalLock(boolean fair) {
    super(fair);
  }
}
