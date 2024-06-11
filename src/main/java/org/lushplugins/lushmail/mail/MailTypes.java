package org.lushplugins.lushmail.mail;

import com.google.gson.JsonElement;
import org.lushplugins.lushmail.LushMail;

import java.util.HashMap;

public class MailTypes {
    private final HashMap<String, Class<? extends Mail>> types = new HashMap<>();

    public MailTypes() {
        register("command", CommandMail.class);
        register("item", ItemMail.class);
        register("parcel", Parcel.class);
        register("text", TextMail.class);
    }

    public void register(String name, Class<? extends Mail> mailClass) {
        types.put(name, mailClass);
    }

    public void unregister(String name) {
        types.remove(name);
    }

    public Mail constructMail(String type, JsonElement json) {
        return types.containsKey(type) ? LushMail.getGson().fromJson(json, types.get(type)) : null;
    }
}
