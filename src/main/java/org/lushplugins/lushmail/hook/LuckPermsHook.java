package org.lushplugins.lushmail.hook;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.lushplugins.lushlib.hook.Hook;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsHook extends Hook {

    public LuckPermsHook() {
        super("LuckPerms");
    }

    public CompletableFuture<Boolean> isInGroup(UUID uuid, String groupName) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid)
            .thenApplyAsync(user -> {
                Collection<Group> groups = user.getInheritedGroups(user.getQueryOptions());
                return groups.stream().anyMatch(group -> group.getName().equals(groupName));
            });
    }
}
