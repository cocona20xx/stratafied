package cocona20xx.stratafied;

import cocona20xx.stratafied.api.StrataManager;
import net.minecraft.block.Blocks;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StratafiedMod implements ModInitializer {
	public static final String MOD_ID = "stratafied";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final StrataManager MANAGER = StrataManager.get();
	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("doing tests...");
		MANAGER.removeVanillaDeepslateStrata();
		MANAGER.addStrata("glass", 100, 50, Blocks.GLASS.getDefaultState(), 5);
		MANAGER.addStrata("new_deepslate", 10, -64, Blocks.DEEPSLATE.getDefaultState(), 4);
		MANAGER.addStrata("intersect_test", -10, -20, Blocks.RED_STAINED_GLASS.getDefaultState(), 2);
		MANAGER.addStratasToWorldgen();
	}
}
