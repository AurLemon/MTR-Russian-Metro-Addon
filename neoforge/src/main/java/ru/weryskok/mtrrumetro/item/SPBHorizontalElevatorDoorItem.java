package ru.weryskok.mtrrumetro.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.mtr.block.IBlock;
import org.mtr.item.ItemPSDAPGBase;
import ru.weryskok.mtrrumetro.registry.RMRBlocks;

public class SPBHorizontalElevatorDoorItem extends Item implements IBlock {
    private final boolean odd;

    public SPBHorizontalElevatorDoorItem(boolean odd, Properties properties) {
        super(properties);
        this.odd = odd;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final int horizontalBlocks = odd ? 1 : 2;
        final Block doorBlock = (odd ? RMRBlocks.SPB_HORIZONTAL_ELEVATOR_DOOR_ODD : RMRBlocks.SPB_HORIZONTAL_ELEVATOR_DOOR).get();
        if (ItemPSDAPGBase.blocksNotReplaceable(context, horizontalBlocks, 2, doorBlock)) {
            return InteractionResult.FAIL;
        }

        final Level level = context.getLevel();
        final Direction playerFacing = context.getHorizontalDirection();
        final BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        for (int x = 0; x < horizontalBlocks; x++) {
            final BlockPos newPos = pos.relative(playerFacing.getClockWise(), x);

            for (int y = 0; y < 2; y++) {
                BlockState state = doorBlock.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, playerFacing)
                        .setValue(HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
                if (!odd) {
                    state = state.setValue(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT);
                }
                level.setBlockAndUpdate(newPos.above(y), state);
            }
        }

        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
