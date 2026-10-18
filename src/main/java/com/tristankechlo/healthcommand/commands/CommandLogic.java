package com.tristankechlo.healthcommand.commands;

import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.function.Supplier;

public class CommandLogic {

    private static final Supplier<String> SUPPLIER = () -> "healthcommand:" + HealthCommand.class.getSimpleName();

    protected static boolean addHealthSingle(LivingEntity livingEntity, float amount, boolean increaseMaxHealth) {
        final ModifiableAttributeInstance attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) {
            // should never happen, as every living entity SHOULD have the max health attribute
            return false;
        }

        final boolean fireHealEvent = amount > 0 && HealthCommandConfig.get().fireLivingHealEvent();
        final float newHealth = livingEntity.getHealth() + amount;

        if (newHealth <= 0) {
            livingEntity.setHealth(0);
            return true;
        }

        // no need for new maximum health here
        if (newHealth <= livingEntity.getMaxHealth()) {
            if (fireHealEvent) {
                livingEntity.heal(amount); // rejects negative values
            } else {
                livingEntity.setHealth(newHealth);
            }
            return true;
        }

        // requested health is greater than the maximum health of the entity
        if (increaseMaxHealth) {
            attribute.removeModifier(HealthCommand.UUID);
            final double amountToAdd = newHealth - attribute.getBaseValue();
            attribute.addPermanentModifier(new AttributeModifier(HealthCommand.UUID, SUPPLIER, amountToAdd, AttributeModifier.Operation.ADDITION));
            if (fireHealEvent) {
                livingEntity.heal(amount);
            } else {
                livingEntity.setHealth(newHealth);
            }
        } else {
            // only increase health to the maximum health
            if (fireHealEvent) {
                livingEntity.heal(livingEntity.getMaxHealth() - livingEntity.getHealth());
            } else {
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }
        }
        return true;
    }

    protected static boolean setHealthSingle(LivingEntity livingEntity, float newHealth, boolean updateMaxHealth) {
        ModifiableAttributeInstance attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) {
            // should never happen, as every living entity SHOULD have the max health attribute
            return false;
        }

        if (newHealth <= 0) {
            livingEntity.setHealth(0);
            return true;
        }
        if (updateMaxHealth) {
            attribute.removeModifier(HealthCommand.UUID);
            final double amountToAdd = newHealth - attribute.getBaseValue();
            attribute.addPermanentModifier(new AttributeModifier(HealthCommand.UUID, SUPPLIER, amountToAdd, AttributeModifier.Operation.ADDITION));
        }
        livingEntity.setHealth(newHealth);
        return true;
    }

}
