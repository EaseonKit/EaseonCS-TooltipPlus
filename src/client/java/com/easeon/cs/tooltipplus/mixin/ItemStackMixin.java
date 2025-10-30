package com.easeon.cs.tooltipplus.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void addCustomTooltip(Item.TooltipContext context, PlayerEntity player,
                                  TooltipType tooltipType,
                                  CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        List<Text> tooltip = cir.getReturnValue();

        long window = MinecraftClient.getInstance().getWindow().getHandle();
        boolean isShiftPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        if (stack.isDamageable()) {
            int max = stack.getMaxDamage();
            int current = stack.getDamage();
            int durability = max - current;
            double percent = (double) durability / max * 100.0;

            Formatting color =
                    percent >= 75 ? Formatting.GREEN :
                            percent >= 50 ? Formatting.YELLOW :
                                    percent >= 25 ? Formatting.GOLD : Formatting.RED;

            tooltip.add(Text.translatable("item.durability", durability, max)
                    .formatted(color)
                    .append(Text.literal(" (" + String.format("%.1f", percent) + "%)").formatted(Formatting.DARK_GRAY)));
        }

        if (isShiftPressed) {
            tooltip.add(Text.literal(""));

            String id = Registries.ITEM.getId(stack.getItem()).toString().replace("minecraft:", "");
            tooltip.add(Text.literal("ID: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(id).formatted(Formatting.AQUA)));

            String modNamespace = Registries.ITEM.getId(stack.getItem()).getNamespace();
            tooltip.add(Text.literal("From: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(modNamespace).formatted(Formatting.LIGHT_PURPLE)));

        }
    }
}