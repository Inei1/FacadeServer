/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.web.authentication;

import org.terasology.web.io.gsonUtils.InvalidClientMessageException;
import org.terasology.web.io.gsonUtils.Validable;

/**
 * Represents a message sent from the client to the server where the client is requesting authentication.
 */
public class ClientAuthenticationMessage implements Validable {

    private HandshakeHello clientHello;
    private byte[] signature;

    public ClientAuthenticationMessage(HandshakeHello clientHello, byte[] signature) {
        this.clientHello = clientHello;
        this.signature = signature;
    }

    public HandshakeHello getClientHello() {
        return clientHello;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public void validate() throws InvalidClientMessageException {
        if (clientHello == null || signature == null) {
            throw new InvalidClientMessageException("clientHello and signature fields must be specified");
        }
        clientHello.validate();
    }
}
