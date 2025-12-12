package com.payscoopplugin;

import com.facebook.react.BaseReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;

import java.util.HashMap;
import java.util.Map;

public class PayscoopPluginPackage extends BaseReactPackage {
  
  @Override
  public NativeModule getModule(String name, ReactApplicationContext reactContext) {
    if (name.equals(PayscoopPluginModule.NAME)) {
      return new PayscoopPluginModule(reactContext);
    } else {
      return null;
    }
  }

  @Override
  public ReactModuleInfoProvider getReactModuleInfoProvider() {
    return new ReactModuleInfoProvider() {
      @Override
      public Map<String, ReactModuleInfo> getReactModuleInfos() {
        Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
        moduleInfos.put(
          PayscoopPluginModule.NAME,
          new ReactModuleInfo(
            PayscoopPluginModule.NAME,
            PayscoopPluginModule.NAME,
            false,  // canOverrideExistingModule
            false,  // needsEagerInit
            false,  // isCxxModule
            true    // isTurboModule
          )
        );
        return moduleInfos;
      }
    };
  }
}