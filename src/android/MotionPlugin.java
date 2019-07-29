package es.simbiosys.cordova.plugin.motion;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class MotionPlugin extends CordovaPlugin {

  private static final String TAG = "MotionPlugin";

  private CallbackContext eventsCallbackContext;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    // Initialize callback contexts
    this.eventsCallbackContext = null;
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("subscribeToNativeEvents")) {
      // Save events callback context to communicate Java with Javascript
      this.eventsCallbackContext = callbackContext;

      // Return OK result
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "subscribedOk");
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);

      return true;
    }

    return false;
  }
}