// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx.unused;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;

import nl.jvdploeg.exception.Cause;
import nl.jvdploeg.exception.Checks;
import nl.jvdploeg.flat.Change;
import nl.jvdploeg.flat.ChangeType;
import nl.jvdploeg.flat.Version;

public final class ChangeLogger implements Subscriber<Change> {

  private final Logger logger;
  private final String source;
  private Subscription subscription;

  public ChangeLogger(final Logger logger, final String source) {
    this.source = source;
    this.logger = logger;
  }

  @Override
  public void onComplete() {
    logger.debug("{} onComplete", source);
  }

  @Override
  public void onError(final Throwable t) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("%s onError %s", source, Cause.getCause(t)));
    }
  }

  @Override
  public void onNext(final Change change) {

    final ChangeType action = change.getAction();
    final String path = change.getPath().toString();
    final Version version = change.getVersion();
    final String value = change.getValue();

    switch (action) {
      case SET:
        if (version == null) {
          logger.debug("{} onNext set {} {}", source, path, value);
        } else {
          logger.debug("{} onNext set {} {} {}", source, path, version, value);
        }
        break;
      case ADD:
        logger.debug("{} onNext add {}", source, path);
        break;
      case REMOVE:
        logger.debug("{} onNext remove {}", source, path);
        break;
      default:
        Checks.ARGUMENT.invalid(action, "change");
    }

    subscription.request(1);
  }

  @Override
  public void onSubscribe(final Subscription newSubscription) {
    logger.debug("{} onSubscribe", source);
    subscription = newSubscription;
    subscription.request(1);
  }
}
