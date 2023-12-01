package cocona20xx.stratafied.api;

import cocona20xx.stratafied.impl.StrataDefinitionImpl;
import cocona20xx.stratafied.impl.StrataManagerImpl;
import net.minecraft.block.BlockState;

public interface StrataManager {

	static StrataManager get() {
		return StrataManagerImpl.getInstance();
	}
	boolean addStrata(StrataDefinition strataDefinition);
	default boolean addStrata(String name, int upperLimit, int lowerLimit, BlockState baseBlockState, int gradientThickness) {
		return addStrata(new StrataDefinitionImpl(name, upperLimit, lowerLimit, baseBlockState, gradientThickness));
	}
	boolean removeStrata(String name);
	void removeVanillaDeepslateStrata();
	boolean isVanillaDeepslateStrataPresent();

	void addStratasToWorldgen();
}
