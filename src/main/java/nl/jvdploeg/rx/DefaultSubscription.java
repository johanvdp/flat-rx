// The author disclaims copyright to this source code.
package nl.jvdploeg.rx;

import java.util.LinkedList;
import java.util.Queue;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.jvdploeg.exception.Cause;

public final class DefaultSubscription<T> {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSubscription.class);

  private final Queue<T> queue = new LinkedList<>();
  private boolean finished;
  private final Subscriber<? super T> subscriber;
  private long n;

  public DefaultSubscription(final Subscriber<? super T> subscriber) {
    this.subscriber = subscriber;
  }

  /**
   * Fire complete.
   */
  public synchronized void fireComplete() {
    LOG.debug("fireComplete");
    if (!finished) {
      finished = true;
      subscriber.onComplete();
    }
  }

  /**
   * Fire error.
   *
   * @param t
   *          The throwable.
   */
  public synchronized void fireError(final Throwable t) {
    LOG.debug("fireError {}", Cause.getCause(t));
    if (!finished) {
      finished = true;
      subscriber.onError(t);
    }
  }

  /**
   * Fire next.
   *
   * @param observation
   *          The observation.
   */
  public synchronized void fireNext(final T observation) {
    LOG.debug("fireNext");
    if (!finished) {
      fireNextToQueue(observation);
      fireNextFromQueue();
    }
  }

  /**
   * Fire subscribe.
   */
  public void fireSubscribe() {
    subscriber.onSubscribe(new Subscription() {

      @Override
      public void cancel() {
        DefaultSubscription.this.cancel();
      }

      @Override
      public void request(final long more) {
        DefaultSubscription.this.request(more);
      }
    });
  }

  private synchronized void cancel() {
    LOG.debug("cancel");
    this.n = 0;
    queue.clear();
    fireComplete();
  }

  private void fireNextFromQueue() {
    // check request count
    while (n > 0) {
      // check queue
      final T next = queue.poll();
      if (next == null) {
        return;
      }
      // update request count if not indicating unlimited
      if (n < Long.MAX_VALUE) {
        n--;
      }
      // round-trip should be fine
      // fireNext -> fireNextFromQueue -> onNext -> request ->
      // fireNextFromQueue
      subscriber.onNext(next);
    }
  }

  private void fireNextToQueue(final T observation) {
    queue.offer(observation);
  }

  private synchronized void request(final long more) {
    LOG.debug("request {}", Long.valueOf(more));
    this.n += more;
    fireNextFromQueue();
  }
}
