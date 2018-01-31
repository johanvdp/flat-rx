// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import nl.jvdploeg.flat.Change;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.util.ModelUtils;

public final class ModelSubscriber implements Subscriber<Change> {

  private Subscription subscription;
  private final Model<?> model;

  public ModelSubscriber(final Model<?> model) {
    this.model = model;
  }

  @Override
  public void onComplete() {
    subscription = null;
  }

  @Override
  public void onError(final Throwable t) {
    subscription = null;
  }

  @Override
  public void onNext(final Change change) {
    ModelUtils.applyChange(model, change);
    subscription.request(1);
  }

  @Override
  public void onSubscribe(final Subscription newSubscription) {
    subscription = newSubscription;
    subscription.request(1);
  }
}
