// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import java.io.Closeable;
import java.io.IOException;

import org.reactivestreams.Publisher;

import nl.jvdploeg.flat.Change;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.Version;
import nl.jvdploeg.flat.impl.DefaultChange;

@SuppressWarnings("rawtypes")
public final class RxModel implements Model, Closeable {

  private final Model<?> model;
  private final ModelPublisher publisher;

  /**
   * Create a model.
   *
   * @param model
   *          The backing model.
   */
  public RxModel(final Model<?> model) {
    this.model = model;
    publisher = new ModelPublisher(this);
  }

  @Override
  public void add(final Path path) {
    model.add(path);
    publisher.publishNext(DefaultChange.add(path));
  }

  @Override
  public void close() throws IOException {
    publisher.publishComplete();
  }

  @Override
  public Node createChild(final Path path) {
    final Node<?> child = model.createChild(path);
    publisher.publishNext(DefaultChange.add(path));
    return child;
  }

  @Override
  public Node findNode(final Path path) {
    final Node<?> node = model.findNode(path);
    return node;
  }

  @Override
  public String getName() {
    final String name = model.getName();
    return name;
  }

  @Override
  public Node getNode(final Path path) {
    final Node<?> node = model.getNode(path);
    return node;
  }

  public Publisher<Change> getPublisher() {
    return publisher;
  }

  @Override
  public Node getRoot() {
    final Node<?> root = model.getRoot();
    return root;
  }

  @Override
  public String getValue(final Path path) {
    final String value = model.getValue(path);
    return value;
  }

  @Override
  public Version getVersion(final Path path) {
    final Version version = model.getVersion(path);
    return version;
  }

  @Override
  public void remove(final Path path) {
    model.remove(path);
    publisher.publishNext(DefaultChange.remove(path));
  }

  @Override
  public String setValue(final Path path, final String newValue) {
    final String oldValue = model.setValue(path, newValue);
    final Version oldVersion = model.getVersion(path);
    publisher.publishNext(DefaultChange.set(path, oldVersion, newValue));
    return oldValue;
  }

  @Override
  public Version setVersion(final Path path, final Version newVersion) {
    throw new UnsupportedOperationException();
  }
}
