package eu.pb4.illagerexpansion.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules;

public class IEGameRules {
    public static final GameRules.Key<DoubleRule> XP_COST_BOOK_MULTIPLIER = GameRuleRegistry.register("illagerexp:xp_cost_book_multiplier",
            GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(1 / 3f, 0));

    public static final GameRules.Key<DoubleRule> XP_COST_ITEM_MULTIPLIER = GameRuleRegistry.register("illagerexp:xp_cost_item_multiplier",
            GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(1 / 5f, 0));

    public static final GameRules.Key<DoubleRule> XP_COST_BOOK_MIN = GameRuleRegistry.register("illagerexp:xp_minimal_cost_book",
            GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(8, 0));

    public static final GameRules.Key<DoubleRule> XP_COST_ITEM_MIN = GameRuleRegistry.register("illagerexp:xp_minimal_cost_item",
            GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(4, 0));

    public static final GameRules.Key<GameRules.IntRule> XP_COST_MAX = GameRuleRegistry.register("illagerexp:xp_cost_max",
            GameRules.Category.MISC,
            GameRuleFactory.createIntRule(35, 0));
    public static void register() {}
}
