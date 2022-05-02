package com.ruchij.services.messages;

import com.ruchij.services.messages.models.OneToOne;

public interface MessageHandler {
    void handle(OneToOne oneToOne);
}
