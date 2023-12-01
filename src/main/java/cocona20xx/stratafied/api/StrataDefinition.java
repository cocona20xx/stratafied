package cocona20xx.stratafied.api;

import cocona20xx.stratafied.impl.StrataDefinitionImpl;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public abstract class StrataDefinition implements Comparable<StrataDefinition> {

	public static StrataDefinition makeNew(
		String name, int upperLimit, int lowerLimit, BlockState blockState, int gradientSize) throws IndexOutOfBoundsException {
		return new StrataDefinitionImpl(name, upperLimit, lowerLimit, blockState, gradientSize);
	}

	public abstract int getUpperLimit();
	public abstract int getLowerLimit();
	public abstract BlockState getBlockState();
	public abstract String getName();

	public abstract int getGradientSize();

	@Override
	public String toString() {
		Identifier blockId = Registries.BLOCK.getId(getBlockState().getBlock());
		return "StrataDefinition - name: " + getName()
			+ ", upperLimit: " + getUpperLimit()
			+ ", lowerLimit: " + getLowerLimit()
			+ ", blockState (as block id): " + blockId
			+ ", gradientSize: " + getGradientSize()
			+ ", Object toString() info: " + super.toString();
	}
}
