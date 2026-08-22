package io.github.nitsuya.aa.display.xposed;

import android.os.Bundle;

interface IShellManager {
    boolean createVirtualDisplayBefore();
    Bundle getConfig();
    boolean destroyVirtualDisplayAfter();
}