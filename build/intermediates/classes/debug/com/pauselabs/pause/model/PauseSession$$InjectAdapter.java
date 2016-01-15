// Code generated by dagger-compiler.  Do not edit.
package com.pauselabs.pause.model;


import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;

/**
 * A {@code Binder<PauseSession>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 * 
 * Owning the dependency links between {@code PauseSession} and its
 * dependencies.
 * 
 * Being a {@code Provider<PauseSession>} and handling creation and
 * preparation of object instances.
 * 
 * Being a {@code MembersInjector<PauseSession>} and handling injection
 * of annotated fields.
 */
public final class PauseSession$$InjectAdapter extends Binding<PauseSession>
    implements MembersInjector<PauseSession> {
  private Binding<android.content.SharedPreferences> mPrefs;

  public PauseSession$$InjectAdapter() {
    super(null, "members/com.pauselabs.pause.model.PauseSession", NOT_SINGLETON, PauseSession.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    mPrefs = (Binding<android.content.SharedPreferences>) linker.requestBinding("android.content.SharedPreferences", PauseSession.class);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(mPrefs);
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<PauseSession>}.
   */
  @Override
  public void injectMembers(PauseSession object) {
    object.mPrefs = mPrefs.get();
  }
}
