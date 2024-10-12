package vip.mokardder.smsclient.interfaces;

import vip.mokardder.smsclient.models.dacPayload;

public interface dacListener {
    void onDacReceived(dacPayload payload);
}
