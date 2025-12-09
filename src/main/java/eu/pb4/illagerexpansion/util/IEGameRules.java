package eu.pb4.illagerexpansion.util;


import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

public class IEGameRules {
    public static final GameRule<Double> XP_COST_BOOK_MULTIPLIER = GameRuleBuilder.forDouble(1/ 3f)
                    .minValue(0d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.of("illagerexp:xp_cost_book_multiplier"));

    public static final GameRule<Double> XP_COST_ITEM_MULTIPLIER = GameRuleBuilder.forDouble(1/ 5f)
            .minValue(0d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.of("illagerexp:xp_cost_item_multiplier"));

    public static final GameRule<Double> XP_COST_BOOK_MIN = GameRuleBuilder.forDouble(8)
            .minValue(0d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.of("illagerexp:xp_minimal_cost_book"));

    public static final GameRule<Double> XP_COST_ITEM_MIN = GameRuleBuilder.forDouble(4)
            .minValue(0d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.of("illagerexp:xp_minimal_cost_item"));

    public static final GameRule<Integer> XP_COST_MAX = GameRuleBuilder.forInteger(35)
            .minValue(0).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.of("illagerexp:xp_cost_max"));
    public static void register() {}
}
