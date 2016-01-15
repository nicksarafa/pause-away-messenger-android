// Code generated by dagger-compiler.  Do not edit.
package com.pauselabs.pause.controllers;


import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binder<UpgradeViewController>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 * 
 * Owning the dependency links between {@code UpgradeViewController} and its
 * dependencies.
 * 
 * Being a {@code Provider<UpgradeViewController>} and handling creation and
 * preparation of object instances.
 * 
 * Being a {@code MembersInjector<UpgradeViewController>} and handling injection
 * of annotated fields.
 */
public final class UpgradeViewController$$InjectAdapter extends Binding<UpgradeViewController>
    implements Provider<UpgradeViewController>, MembersInjector<UpgradeViewController> {
  private Binding<android.view.LayoutInflater> inflater;

  public UpgradeViewController$$InjectAdapter() {
    super("com.pauselabs.pause.controllers.UpgradeViewController", "members/com.pauselabs.pause.controllers.UpgradeViewController", NOT_SINGLETON, UpgradeViewController.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    inflater = (Binding<android.view.LayoutInflater>) linker.requestBinding("android.view.LayoutInflater", UpgradeViewController.class);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(inflater);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<UpgradeViewController>}.
   */
  @Override
  public UpgradeViewController get() {
    UpgradeViewController result = new UpgradeViewController();
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<UpgradeViewController>}.
   */
  @Override
  public void injectMembers(UpgradeViewController object) {
    object.inflater = inflater.get();
  }
}
