// The author disclaims copyright to this source code.
package nl.jvdploeg.rx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class DefaultPublisher<T> implements Publisher<T> {

  private final List<DefaultSubscription<T>> subscriptions = new ArrayList<>();

  public DefaultPublisher() {
  }

  /** Publish stream completion to all subscribers. */
  public void publishComplete() {
    final Iterator<DefaultSubscription<T>> iterator = subscriptions.iterator();
    while (iterator.hasNext()) {
      final DefaultSubscription<T> subscription = iterator.next();
      try {
        subscription.fireComplete();
      } catch (final Exception ignored) {
        // ignore
      }
      iterator.remove();
    }
  }

  /** Publish stream error to all subscribers. */
  public void publishError(final Throwable t) {
    final Iterator<DefaultSubscription<T>> iterator = subscriptions.iterator();
    while (iterator.hasNext()) {
      final DefaultSubscription<T> subscription = iterator.next();
      try {
        subscription.fireError(t);
      } catch (final Exception ignored) {
        // ignore
      }
      iterator.remove();
    }
  }

  /** Publish next item to all subscribers. */
  public void publishNext(final T object) {
    final Iterator<DefaultSubscription<T>> iterator = subscriptions.iterator();
    while (iterator.hasNext()) {
      final DefaultSubscription<T> subscription = iterator.next();
      try {
        subscription.fireNext(object);
      } catch (final Exception e) {
        // end misbehaving client
        try {
          subscription.fireError(e);
        } catch (final Exception ignored) {
          // ignore
        }
        iterator.remove();
      }
    }
  }

  @Override
  public final void subscribe(final Subscriber<? super T> subscriber) {
    final DefaultSubscription<T> subscription = new DefaultSubscription<>(subscriber);
    subscriptions.add(subscription);
    onSubscribe(subscription);
  }

  /** Allow sub-class to hook into subscriber subscribe. */
  protected void onSubscribe(final DefaultSubscription<T> subscription) {
    subscription.fireSubscribe();
  }
}
