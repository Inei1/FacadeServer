/*
 * Copyright 2018 MovingBlocks
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

public enum AdminPermissions {
    CONSOLE_CHEAT(new ResourcePath("console")),
    CONSOLE_USER_MANAGEMENT(new ResourcePath("console")),
    CONSOLE_SERVER_MANAGEMENT(new ResourcePath("console")),
    CONSOLE_DEBUG(new ResourcePath("console")),
    INSTALL_MODULES(new ResourcePath("modules", "installer")),
    CREATE_BACKUP_RENAME_GAMES(new ResourcePath("games")),
    DELETE_GAMES(new ResourcePath("games")),
    STOP_GAMES(new ResourcePath("engineState")),
    CHANGE_SETTINGS(new ResourcePath("resources", "config")),
    USER_MANAGEMENT(new ResourcePath("console"));

    AdminPermissions(ResourcePath path) {

    }
}