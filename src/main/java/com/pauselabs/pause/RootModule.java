package com.pauselabs.pause;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module(
        includes = {
                AndroidModule.class,
                PauseModule.class
        }
)
public class RootModule {
}
