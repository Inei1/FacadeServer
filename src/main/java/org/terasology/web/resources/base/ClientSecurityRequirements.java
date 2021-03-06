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
package org.terasology.web.resources.base;

import org.terasology.web.client.ClientSecurityInfo;
import org.terasology.web.serverAdminManagement.PermissionType;

/**
 * This class contains information on the security requirements of a resource, and checks if the client can access it.
 */
public final class ClientSecurityRequirements {

    public static final ClientSecurityRequirements PUBLIC = new ClientSecurityRequirements(false, false);
    public static final ClientSecurityRequirements REQUIRE_AUTH = new ClientSecurityRequirements(true, false);
    public static final ClientSecurityRequirements REQUIRE_ADMIN = new ClientSecurityRequirements(false, true);
    public static final ClientSecurityRequirements REQUIRE_AUTH_ADMIN = new ClientSecurityRequirements(true, true);

    private boolean requireAuthentication;
    private boolean requireAdminPermission;
    private PermissionType requiredPermission;

    private ClientSecurityRequirements(boolean requireAuthentication, boolean requireAdminPermission) {
        this.requireAuthentication = requireAuthentication;
        this.requireAdminPermission = requireAdminPermission;
    }

    private ClientSecurityRequirements(boolean requireAuthentication, boolean requireAdminPermission, PermissionType requiredPermission) {
        this.requireAuthentication = requireAuthentication;
        this.requireAdminPermission = requireAdminPermission;
        this.requiredPermission = requiredPermission;
    }

    public static ClientSecurityRequirements requireAdminPermission(PermissionType permissionType) {
        return new ClientSecurityRequirements(false, true, permissionType);
    }

    public boolean clientIsAllowed(ClientSecurityInfo client) {
        return !((requireAuthentication && !client.isAuthenticated())
                || (requireAdminPermission && !client.isAdmin())
                || (requiredPermission != null && !client.ownsPermission(requiredPermission)));
    }
}
