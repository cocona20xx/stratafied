package cocona20xx.stratafied.impl;

import cocona20xx.stratafied.api.StrataDefinition;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class StrataDefinitionImpl extends StrataDefinition {
	private final String name;
	private final int upperLimit;
	private final int lowerLimit;
	private final BlockState blockState;
	private final int gradientSize;


	public StrataDefinitionImpl(String name, int upperLimit, int lowerLimit, BlockState blockState, int gradientSize) throws IndexOutOfBoundsException {
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		this.blockState = blockState;
		this.name = name;
		this.gradientSize = gradientSize;
		if (lowerLimit >= upperLimit) {
			throw new IndexOutOfBoundsException("Invalid limits for specified strata with name" + name + ": lower limit "
				+ lowerLimit + " is greater than or equal to upper limit " + upperLimit);
		}
	}
	@Override
	public int getUpperLimit() {
		return upperLimit;
	}

	@Override
	public int getLowerLimit() {
		return lowerLimit;
	}

	@Override
	public BlockState getBlockState() {
		return blockState;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getGradientSize() {
		return gradientSize;
	}

	@Override
	public int compareTo(@NotNull StrataDefinition to) {
		//StrataDefinitions are ordered based on their upper bounds (as stratas in-game are constructed from the top of the world downwards)
		return Integer.compare(to.getUpperLimit(), this.upperLimit);
	}
}
