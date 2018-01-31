// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import java.util.Map.Entry;
import java.util.Set;

import nl.jvdploeg.flat.Change;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.Version;
import nl.jvdploeg.flat.impl.DefaultChange;
import nl.jvdploeg.rx.DefaultPublisher;
import nl.jvdploeg.rx.DefaultSubscription;

public final class ModelPublisher extends DefaultPublisher<Change> {

  private final Model<?> model;
  private boolean finished;

  public ModelPublisher(final Model<?> model) {
    this.model = model;
  }

  /**
   * Publish complete.
   */
  @Override
  public void publishComplete() {
    if (!finished) {
      finished = true;
      super.publishComplete();
    }
  }

  /**
   * Publish error.
   *
   * @param t
   *          The throwable.
   */
  @Override
  public void publishError(final Throwable t) {
    if (!finished) {
      finished = true;
      super.publishError(t);
    }
  }

  /**
   * Publish next.
   *
   * @param change
   *          The change.
   */
  @Override
  public void publishNext(final Change change) {
    if (!finished) {
      super.publishNext(change);
    }
  }

  /**
   * Recursive publish the current model state as changes.
   */
  @SuppressWarnings("rawtypes")
  private void publish(final DefaultSubscription<Change> subscription, final Path path, final Node node) {
    if (path.getLength() > 0) {
      subscription.fireNext(DefaultChange.add(path));
      final Version version = node.getVersion();
      final String value = node.getValue();
      subscription.fireNext(DefaultChange.set(path, version, value));
    }
    final Set<Entry<String, Node>> children = node.getChildren().entrySet();
    for (final Entry<String, Node> child : children) {
      final String childName = child.getKey();
      final Node childNode = child.getValue();
      final Path childPath = path.createChildPath(childName);
      publish(subscription, childPath, childNode);
    }
  }

  @Override
  protected void onSubscribe(final DefaultSubscription<Change> subscription) {
    if (!finished) {
      super.onSubscribe(subscription);
      publish(subscription, new Path(), model.getRoot());
    }
  }
}
