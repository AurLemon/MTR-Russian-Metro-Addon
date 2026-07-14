package ru.weryskok.mtrrumetro.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.block.IBlock;

public class MoscowOldTicketBarrierSideCoverBlock extends FacingBlock {
    public MoscowOldTicketBarrierSideCoverBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(10.9, 0, -2, 16, 24.1, 19.5, state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(10.9, 0, -2, 16, 24.1, 19.5, state.getValue(FACING));
    }
}
