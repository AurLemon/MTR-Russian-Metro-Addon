package ru.weryskok.mtrrumetro.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;

public class ThreeHighFacingBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<IBlock.EnumThird> THIRD = IBlock.THIRD;

    public ThreeHighFacingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(THIRD, IBlock.EnumThird.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, THIRD);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return IBlock.isReplaceable(context, Direction.UP, 3)
                ? defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(THIRD, IBlock.EnumThird.LOWER)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos.above(), state.setValue(THIRD, IBlock.EnumThird.MIDDLE));
            level.setBlockAndUpdate(pos.above(2), state.setValue(THIRD, IBlock.EnumThird.UPPER));
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        IBlock.EnumThird third = state.getValue(THIRD);
        boolean invalid =
                (direction == Direction.UP && third != IBlock.EnumThird.UPPER || direction == Direction.DOWN && third != IBlock.EnumThird.LOWER)
                && !neighborState.is(this);
        return invalid ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
        if (!level.isClientSide && player.isCreative()) {
            switch (state.getValue(THIRD)) {
                case MIDDLE -> IBlock.playerWillDestroyCreative(level, player, pos.below());
                case UPPER -> IBlock.playerWillDestroyCreative(level, player, pos.below(2));
                default -> {
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        double height = state.getValue(THIRD) == IBlock.EnumThird.UPPER ? 9.5 : 16;
        return IBlock.getVoxelShapeByDirection(2, 0, 6.7, 14, height, 9.3, state.getValue(FACING));
    }
}
