package org.lushplugins.lushmail.mail;

import com.mysql.cj.util.LRUCache;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushmail.LushMail;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MailPreviewCache {
    private final Map<String, DisplayItemStack> mailPreviewCache = Collections.synchronizedMap(new LRUCache<>(108));

    public CompletableFuture<DisplayItemStack> getPreview(String mailId) {
        if (mailPreviewCache.containsKey(mailId)) {
            return CompletableFuture.completedFuture(mailPreviewCache.get(mailId));
        }

        CompletableFuture<DisplayItemStack> future = LushMail.getInstance().getStorageManager().loadMailPreviewItem(mailId);
        future.thenAccept(item -> mailPreviewCache.put(mailId, item));

        return future;
    }
}
