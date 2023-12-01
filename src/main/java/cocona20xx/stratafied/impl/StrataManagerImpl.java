package cocona20xx.stratafied.impl;

import cocona20xx.stratafied.api.StrataDefinition;
import cocona20xx.stratafied.api.StrataManager;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.worldgen.surface_rule.api.SurfaceRuleEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class StrataManagerImpl implements StrataManager {
	private static final Map<String, StrataDefinition> BASE_STRATA_MAP = new HashMap<>();
	private static final ArrayList<StrataDefinition> CALCULATED_STRATA_LIST = new ArrayList<>();
	private static boolean KEEP_VANILLA_DEEPSLATE = true;
	private static final StrataManagerImpl INSTANCE = new StrataManagerImpl();

	public static StrataManagerImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean addStrata(StrataDefinition strataDefinition) {
		if (BASE_STRATA_MAP.containsKey(strataDefinition.getName())) return false;
		else {
			BASE_STRATA_MAP.put(strataDefinition.getName(), strataDefinition);
			calculate();
			return true;
		}
	}

	@Override
	public boolean removeStrata(String name) {
		return false;
	}

	@Override
	public void removeVanillaDeepslateStrata() {
		StrataManagerImpl.KEEP_VANILLA_DEEPSLATE = false;
	}

	@Override
	public boolean isVanillaDeepslateStrataPresent() {
		return StrataManagerImpl.KEEP_VANILLA_DEEPSLATE;
	}

	@Override
	public void addStratasToWorldgen() {
		//setup
		SurfaceRules.MaterialCondition deepslateCondition = SurfaceRules.verticalGradient("deepslate", YOffset.fixed(0), YOffset.fixed(8));
		calculate();
		SurfaceRuleEvents.MODIFY_OVERWORLD.register(SurfaceRuleEvents.REMOVE_PHASE, context -> {
			List<SurfaceRules.MaterialRule> allRules = context.materialRules();
			for (SurfaceRules.MaterialRule materialRule : allRules) {
				if (materialRule instanceof SurfaceRules.SequenceMaterialRule sequenceRule) {
					List<SurfaceRules.MaterialRule> rulesInSequence = new ArrayList<>(sequenceRule.sequence());
					if (!isVanillaDeepslateStrataPresent()) {
						//remove deepslate rule
						List<SurfaceRules.MaterialRule> rulesInSequenceCopy = new ArrayList<>(sequenceRule.sequence()); //exists to prevent ConcurrentModificationExceptions
						for (SurfaceRules.MaterialRule sequencedRule : rulesInSequence) {
							if (sequencedRule instanceof SurfaceRules.ConditionMaterialRule cRule && cRule.ifTrue().equals(deepslateCondition)) {
								rulesInSequenceCopy.remove(sequencedRule);
							}
						}
						rulesInSequence = rulesInSequenceCopy;
					}
					for (StrataDefinition definition : CALCULATED_STRATA_LIST) {
						rulesInSequence.add(convert(definition));
					}
					SurfaceRules.SequenceMaterialRule newSequenceRule = new SurfaceRules.SequenceMaterialRule(rulesInSequence);
					context.materialRules().remove(sequenceRule);
					context.materialRules().add(newSequenceRule);
				}
			}
		});
	}

	private void calculate() {
		CALCULATED_STRATA_LIST.clear();
		CALCULATED_STRATA_LIST.addAll(BASE_STRATA_MAP.values());
		IntArraySet toRemove = new IntArraySet();
		Set<StrataDefinition> toAdd = new HashSet<>();
		//sort by upper bound into ascending order - array is worked through from the last index up as a result
		Collections.sort(CALCULATED_STRATA_LIST);
		for (int uIter = CALCULATED_STRATA_LIST.size() - 1; uIter >= 1; uIter--) {
			int bIter = uIter--;
			StrataDefinition upper = CALCULATED_STRATA_LIST.get(uIter);
			StrataDefinition lower = CALCULATED_STRATA_LIST.get(bIter);
			int uT = upper.getUpperLimit();
			int uB = upper.getLowerLimit();
			int lT = lower.getUpperLimit();
			int lB = lower.getLowerLimit();
			if (uT <= lT) throw new RuntimeException("uT <= lT: " + uT + " <= " + lT + " (This should be impossible!)"); //failsafe
			else {
				if (lB > uB) {
					//lower is fully contained within upper, upper needs to be bisected as a result
					StrataDefinition upperTop = StrataDefinition.makeNew(
						upper.getName() + "_top", uT, lT, upper.getBlockState(), upper.getGradientSize());
					StrataDefinition upperBottom = StrataDefinition.makeNew(
						upper.getName() + "_bottom", lB, uB, upper.getBlockState(), upper.getGradientSize());
					toRemove.add(uIter);
					toAdd.add(upperTop);
					toAdd.add(upperBottom);
				} else if (lT > uB) {
					//lower partially intersects the 'bottom' of upper
					StrataDefinition newUpper = StrataDefinition.makeNew(upper.getName(), uT, lT, upper.getBlockState(), upper.getGradientSize());
					toRemove.add(uIter);
					toAdd.add(newUpper);
				}
			}
		}
		for (int rem : toRemove) {
			CALCULATED_STRATA_LIST.remove(rem);
		}
		CALCULATED_STRATA_LIST.addAll(toAdd);
		Collections.sort(CALCULATED_STRATA_LIST);
	}

	private SurfaceRules.MaterialRule convert(StrataDefinition definition) {
		SurfaceRules.MaterialRule blockRule = SurfaceRules.block(definition.getBlockState());
		SurfaceRules.MaterialCondition gradientUpper = SurfaceRules.verticalGradient(
			definition.getName(), YOffset.fixed(definition.getUpperLimit()), YOffset.fixed(definition.getUpperLimit() + definition.getGradientSize()));
		SurfaceRules.MaterialCondition lower = SurfaceRules.aboveY(YOffset.fixed(definition.getLowerLimit()), 0);
		return SurfaceRules.condition(lower, SurfaceRules.condition(gradientUpper, blockRule));
	}

}
