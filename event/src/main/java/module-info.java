/**
 * Provides an event bus implementation which permits the broadcasting of user defined events within
 * complex applications.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
module com.torchmind.mango.event {
  exports com.torchmind.mango.event;
  exports com.torchmind.mango.event.annotation;
  exports com.torchmind.mango.event.error;
  exports com.torchmind.mango.event.subscription;
  exports com.torchmind.mango.event.subscription.filter;
  exports com.torchmind.mango.event.utility;

  requires static com.github.spotbugs.annotations;
  requires com.torchmind.mango.concurrency;
  requires com.torchmind.reflect;
}
