package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.UUID;

public class Session
{
    private final String username;
    private final String playerID;
    private final String token;
    private final Session.Type sessionType;

    public PropertyMap getProperties() {
        return properties;
    }

    public void setProperties(PropertyMap properties) {
        this.properties = properties;
    }

    private PropertyMap properties;

    public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn)
    {
        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Session.Type.setSessionType(sessionTypeIn);
    }

    public String getSessionID()
    {
        return "token:" + this.token + ":" + this.playerID;
    }

    public String getPlayerID()
    {
        return this.playerID;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getToken()
    {
        return this.token;
    }

    public GameProfile getProfile()
    {
        try
        {
            UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
            GameProfile ret = new GameProfile(uuid, this.getUsername());    //Forge: Adds cached GameProfile properties to returned GameProfile.
            if (properties != null) {                                       // Helps to cut down on calls to the session service,
                ret.getProperties().putAll(properties);                     // which helps to fix MC-52974.
            }
            return ret;
        }
        catch (IllegalArgumentException var2)
        {
            //return new GameProfile(null, this.getUsername());
            return new GameProfile(EntityPlayer.getUUID(new GameProfile(null, this.getUsername())), this.getUsername());
        }
    }

    /**
     * Returns either 'legacy' or 'mojang' whether the account is migrated or not
     */
    public Session.Type getSessionType()
    {
        return this.sessionType;
    }

    public enum Type {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, Session.Type> SESSION_TYPES = Maps.<String, Session.Type>newHashMap();
        private final String sessionType;

        Type(String sessionTypeIn) {
            this.sessionType = sessionTypeIn;
        }

        public static Session.Type setSessionType(String sessionTypeIn)
        {
            return SESSION_TYPES.get(sessionTypeIn.toLowerCase());
        }

        static {
            for (Session.Type session$type : values())
            {
                SESSION_TYPES.put(session$type.sessionType, session$type);
            }
        }
    }
}
