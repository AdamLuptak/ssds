package com.here.owc.client;

import com.here.owc.model.EmrJobMessage;

public interface EmrJobReporter {
    void sendMessage(String message);
}
