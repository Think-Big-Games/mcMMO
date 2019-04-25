package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Archery {
    private static List<TrackedEntity> trackedEntities;

    private static double skillShotDamageCap;

    private static double dazeBonusDamage;

    private static double distanceXpMultiplier;

    private static Archery archery;

    public Archery() {
        List<TrackedEntity> trackedEntities = new ArrayList<>();

        skillShotDamageCap = AdvancedConfig.getInstance().getSkillShotDamageMax();

        dazeBonusDamage = AdvancedConfig.getInstance().getDazeBonusDamage();

        distanceXpMultiplier = mcMMO.getConfigManager().getConfigExperience().getDistanceMultiplier();
    }

    public static Archery getInstance() {
        if (archery == null)
            archery = new Archery();

        return archery;
    }

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        for (TrackedEntity trackedEntity : trackedEntities) {
            if (trackedEntity.getLivingEntity().getEntityId() == livingEntity.getEntityId()) {
                trackedEntity.incrementArrowCount();
                return;
            }
        }

        addToTracker(livingEntity); // If the entity isn't tracked yet
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        TrackedEntity trackedEntity = new TrackedEntity(livingEntity);

        trackedEntity.incrementArrowCount();
        trackedEntities.add(trackedEntity);
    }

    protected static void removeFromTracker(TrackedEntity trackedEntity) {
        trackedEntities.remove(trackedEntity);
    }

    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(LivingEntity livingEntity) {
        for (Iterator<TrackedEntity> entityIterator = trackedEntities.iterator(); entityIterator.hasNext(); ) {
            TrackedEntity trackedEntity = entityIterator.next();

            if (trackedEntity.getID() == livingEntity.getUniqueId()) {
                Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), trackedEntity.getArrowCount());
                entityIterator.remove();
                return;
            }
        }
    }

    public static double getSkillShotBonusDamage(Player player, double oldDamage) {
        double damageBonusPercent = getDamageBonusPercent(player);
        double newDamage = oldDamage + (oldDamage * damageBonusPercent);
        return Math.min(newDamage, Archery.skillShotDamageCap);
    }

    public static double getDamageBonusPercent(Player player) {
        return ((RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)) * AdvancedConfig.getInstance().getSkillShotRankDamageMultiplier()) / 100.0D;
    }

    public List<TrackedEntity> getTrackedEntities() {
        return trackedEntities;
    }

    public double getSkillShotDamageCap() {
        return skillShotDamageCap;
    }

    public double getDazeBonusDamage() {
        return dazeBonusDamage;
    }

    public double getDistanceXpMultiplier() {
        return distanceXpMultiplier;
    }
}
