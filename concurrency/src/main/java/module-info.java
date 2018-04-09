/**
 * Provides utilities which simplify the interaction with concurrency related APIs.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
module com.torchmind.mango.concurrency {
  exports com.torchmind.mango.concurrency.lock;

  requires static com.github.spotbugs.annotations;
}
