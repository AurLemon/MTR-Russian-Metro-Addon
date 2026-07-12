package ru.weryskok.mtrrumetro.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.mtr.block.BlockAPGDoor;
import org.mtr.block.BlockPSDAPGDoorBase;
import org.mtr.block.IBlock;
import ru.weryskok.mtrrumetro.registry.RMRBlockEntities;

public class SPBHorizontalElevatorDoorBlock extends BlockAPGDoor {
    private final boolean odd;

    public SPBHorizontalElevatorDoorBlock(BlockBehaviour.Properties properties, boolean odd) {
        super(properties);
        this.odd = odd;
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(HALF, IBlock.DoubleBlockHalf.LOWER)
                .setValue(SIDE, IBlock.EnumSide.LEFT)
                .setValue(END, false)
                .setValue(UNLOCKED, false));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (updated.isAir()) {
            return updated;
        }

        if (odd) {
            boolean upper = state.getValue(HALF) == IBlock.DoubleBlockHalf.UPPER;
            return (upper && direction == Direction.DOWN || !upper && direction == Direction.UP) && !neighborState.is(this)
                    ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                    : updated;
        }

        Direction sideDirection = IBlock.getSideDirection(state);
        if (sideDirection == direction && !neighborState.is(this)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }

        boolean end = level.getBlockState(pos.relative(sideDirection.getOpposite())).getBlock() instanceof org.mtr.block.BlockPSDAPGGlassEndBase;
        return updated.setValue(END, end);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntity(pos, state, odd);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public static final class BlockEntity extends BlockPSDAPGDoorBase.BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state, boolean odd) {
            super((odd ? RMRBlockEntities.SPB_HORIZONTAL_ELEVATOR_DOOR_ODD : RMRBlockEntities.SPB_HORIZONTAL_ELEVATOR_DOOR).get(), pos, state);
        }
    }
}
