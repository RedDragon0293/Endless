package cn.asone.endless.script.test;

import cn.asone.endless.event.EventHook;
import cn.asone.endless.event.UpdateEvent;
import cn.asone.endless.script.AbstractScriptClass;
import cn.asone.endless.utils.ClientUtils;
import cn.asone.endless.utils.MovementUtils;
import cn.asone.endless.value.AbstractValue;
import cn.asone.endless.value.BoolValue;
import kotlin.collections.CollectionsKt;
import net.minecraft.potion.Potion;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Test extends AbstractScriptClass {
    private final BoolValue allDirectionsValue = new BoolValue("AllDirections", false);
    private final BoolValue blindnessValue = new BoolValue("Blindness", true);
    private final BoolValue foodValue = new BoolValue("Food", true);
    private final BoolValue checkServerSide = new BoolValue("CheckServerSide", false);
    private final BoolValue checkServerSideGround = new BoolValue("CheckServerSideOnlyGround", false);


    public Test() {
        super("ScriptTestModule", "", 5);
        checkServerSide.getSubValue().add(checkServerSideGround);
    }

    @NotNull
    @Override
    public String getScriptName() {
        return "ScriptTest";
    }

    @NotNull
    @Override
    public String getScriptVersion() {
        return "0.1.0";
    }

    @NotNull
    @Override
    public String getScriptAuthor() {
        return "RedDragon0293";
    }

    @Override
    public void onEnable() {
        ClientUtils.displayChatMessage("This is a message from a script.");
    }

    @NotNull
    @Override
    public ArrayList<AbstractValue<?>> getValues() {
        return CollectionsKt.arrayListOf(
                allDirectionsValue,
                blindnessValue,
                checkServerSide,
                foodValue
        );
    }

    @NotNull
    @Override
    public ArrayList<EventHook> getHandledEvents() {
        return CollectionsKt.arrayListOf(
                new EventHook(UpdateEvent.class, 0)
        );
    }

    @Override
    public void onUpdate() {
        if (allDirectionsValue.get()) {
            mc.thePlayer.setSprinting(true);
            return;
        }
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking()
                || (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness))
                || (foodValue.get() && !(mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying))
        ) {
            mc.thePlayer.setSprinting(false);
            return;
        }

        if (mc.thePlayer.movementInput.moveForward >= 0.8F) mc.thePlayer.setSprinting(true);
    }
}
