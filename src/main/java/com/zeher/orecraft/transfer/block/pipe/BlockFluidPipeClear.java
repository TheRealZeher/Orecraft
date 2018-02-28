package com.zeher.orecraft.transfer.block.pipe;

import java.util.List;

import javax.annotation.Nullable;

import com.zeher.orecraft.OreCraft;
import com.zeher.orecraft.core.blockstate.StringProperty;
import com.zeher.orecraft.storage.util.StorageUtil;
import com.zeher.orecraft.transfer.tileentity.pipe.TileEntityFluidPipeClear;
import com.zeher.trzlib.api.TRZUtil;
import com.zeher.trzlib.api.connect.TRZPipeConnectionsList;
import com.zeher.trzlib.storage.TRZFluidUtil;
import com.zeher.trzlib.transfer.TRZBlockFluidPipe;
import com.zeher.trzlib.transfer.TRZTileEntityFluidPipe;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidPipeClear extends TRZBlockFluidPipe {

	protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);

	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 1.0D);
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.0D, 0.7D, 0.7D, 0.7D);

	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 1.0D, 0.7D, 0.7D);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);

	protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 1.0D, 0.7D);
	protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3D, 0.0D, 0.3D, 0.7D, 0.7D, 0.7D);

	public static final IUnlistedProperty<String> FluidName = new StringProperty("fluidName");
	public static final IUnlistedProperty<Boolean> CullFluidTop = new Properties.PropertyAdapter<Boolean>(
			PropertyBool.create("cullFluidTop"));

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");

	public BlockFluidPipeClear(String name, Material material, String tool, int harvest, int hardness, int resistance, CreativeTabs tab) {
		super(tool, material, name, harvest, hardness, resistance, tab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false)).withProperty(UP, Boolean.valueOf(false))
				.withProperty(DOWN, Boolean.valueOf(false)));
		this.setHarvestLevel("pickaxe", 2);
		this.setCreativeTab(OreCraft.tab_orecraft_energy);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityFluidPipeClear();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack stack = player.inventory.getCurrentItem();
		TileEntityFluidPipeClear tile = (TileEntityFluidPipeClear) world.getTileEntity(pos);
		if ((tile instanceof TileEntityFluidPipeClear) && tile != null) {
			if ((TRZUtil.isHoldingHammer(player)) && (player.isSneaking())) {
				player.swingArm(EnumHand.MAIN_HAND);
				if (!world.isRemote) {
					EntityItem fluidDropped = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(),
							new ItemStack(Item.getItemFromBlock(this)));

					world.spawnEntity(fluidDropped);
					world.setBlockToAir(pos);
				}
			}
			
			if ((TRZUtil.isHoldingHammer(player)) && (!player.isSneaking())) {
				player.swingArm(EnumHand.MAIN_HAND);
				if (side.equals(EnumFacing.UP)) {
					int i = tile.getSide("up");
					i = i + 1;
					if (i > 2) {
						i = 0;
					}
					tile.setSide("up", i);
				}
				if (side.equals(EnumFacing.DOWN)) {
					int i = tile.getSide("down");
					i = i + 1;
					if (i > 2) {
						i = 0;
					}
					tile.setSide("down", i);
				}
				if (side.equals(EnumFacing.NORTH)) {
					int i = tile.getSide("north");
					i = i + 1;
					if (i > 2) {
						i = 0;
					}
					tile.setSide("north", i);
				}
				if (side.equals(EnumFacing.SOUTH)) {
					int i = tile.getSide("south");
					i = i + 1;
					if (i > 2) {
						i = 0;
					}
					tile.setSide("south", i);
				}
				if (side.equals(EnumFacing.EAST)) {
					int i = tile.getSide("east");
					i = i + 1;
					if (i > 2) {
						i = 0;
					}
					tile.setSide("east", i);
				}
				if (side.equals(EnumFacing.WEST)) {
					int i = tile.getSide("west");
					i = i + 1;
					if (i > 2) {
						i = 0;
					}
					tile.setSide("west", i);
				}
			}
		}
		
		if (stack.isEmpty()) {
			if (world.isRemote) {
				TileEntityFluidPipeClear tank = (TileEntityFluidPipeClear) world.getTileEntity(pos);
				if (tank.getCurrentStoredAmount() > 0) {
					Fluid fluid = tank.getCurrentStoredFluid();
					FluidStack fluidStack = new FluidStack(fluid, 0);
					String fluidName = tank.getCurrentStoredFluid().getLocalizedName(fluidStack);
					player.sendMessage(
							new TextComponentString(fluidName + " / " + tank.getCurrentStoredAmount() + " mB"));
				} else {
					player.sendMessage(new TextComponentString("Empty" + " / " + 0 + " mB"));
				}
			}
		}
		
		if (stack != null) {
			FluidStack liquid = FluidUtil.getFluidContained(stack);
			TileEntityFluidPipeClear tank = (TileEntityFluidPipeClear) world.getTileEntity(pos);
			if (liquid != null) {
				int amount = tank.fill(liquid, false);
				if (amount == liquid.amount) {
					tank.fill(liquid, true);
					if (!player.capabilities.isCreativeMode)
						player.inventory.setInventorySlotContents(player.inventory.currentItem,
								TRZFluidUtil.useItemSafely(stack));
					if (tank.tank.getFluid().getFluid().getBlock() != null) {
						if(tank.tank.getFluid().getFluid().getBlock().getLightValue(state) > world.getLight(pos)){
							//world.setLightFor(EnumSkyBlock.SKY, tank.getPos(), tank.tank.getFluid().getFluid().getBlock().getLightValue(state));
							StorageUtil.syncBlockAndRerender(world, pos);
						}
					}
					StorageUtil.syncBlockAndRerender(world, pos);
					return true;
				} else
					StorageUtil.syncBlockAndRerender(world, pos);
				return true;
			} else if (stack.getItem() instanceof ItemBucket) {
				FluidTankInfo[] tanks = tank.getTankInfo(side);
				if (tanks[0] != null) {
					FluidStack fillFluid = tanks[0].fluid;
					ItemStack fillStack = FluidUtil.tryFillContainer(stack, tank, 1000, player, false).result;
					if (fillStack != null) {
						if (tank.isEmpty()) {
							return true;
						} else {
							tank.drain(FluidUtil.getFluidContained(fillStack).amount, true);
							//world.setLightFor(EnumSkyBlock.SKY, pos, world.getLight(pos) - 5);
							//world.markBlockRangeForRenderUpdate(pos, pos);
							if (!player.capabilities.isCreativeMode) {
								if (stack.getCount() == 1) {
									player.inventory.setInventorySlotContents(player.inventory.currentItem, fillStack);
								} else {
									player.inventory.setInventorySlotContents(player.inventory.currentItem,
											TRZFluidUtil.useItemSafely(stack));
									if (!player.inventory.addItemStackToInventory(fillStack))
										player.dropItem(fillStack, false);
								}
							} else {
								StorageUtil.syncBlockAndRerender(world, pos);
								return true;
							}
							StorageUtil.syncBlockAndRerender(world, pos);
							return true;
						}
					} else
						StorageUtil.syncBlockAndRerender(world, pos);
					return true;
				}
			}
			StorageUtil.syncBlockAndRerender(world, pos);
		}
		StorageUtil.syncBlockAndRerender(world, pos);
		return false;
	}

	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		if (!p_185477_7_) {
			state = state.getActualState(worldIn, pos);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);

		if (((Boolean) state.getValue(NORTH)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
		}

		if (((Boolean) state.getValue(EAST)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
		}

		if (((Boolean) state.getValue(SOUTH)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		}

		if (((Boolean) state.getValue(WEST)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		}
		if (((Boolean) state.getValue(UP)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_AABB);
		}
		if (((Boolean) state.getValue(DOWN)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_AABB);
		}
	}

	protected BlockStateContainer createBlockState() {
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { FluidName, CullFluidTop };
		IProperty[] listedProperties = new IProperty[] { NORTH, EAST, WEST, SOUTH, UP, DOWN };
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		TileEntityFluidPipeClear tankEntity = (TileEntityFluidPipeClear) world.getTileEntity(pos);

		if (tankEntity != null) {
			Fluid tankFluid = tankEntity.getCurrentStoredFluid();
			int fluidLevel = 0;

			return extendedState.withProperty(FluidName, (tankFluid != null) ? tankFluid.getName() : "")
					.withProperty(CullFluidTop, false);
		}

		return extendedState.withProperty(FluidName, "").withProperty(CullFluidTop, false);
	}

	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return (layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	@Override
	public boolean requiresUpdates() {
		return true;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		return TRZUtil.BOUNDING_BOXES_FAT[getBoundingBoxIdx(state)];
	}

	private static int getBoundingBoxIdx(IBlockState state) {
		int i = 0;

		if (((Boolean) state.getValue(NORTH)).booleanValue()) {
			i |= 1 << 2;
		}

		if (((Boolean) state.getValue(EAST)).booleanValue()) {
			i |= 1 << 5;
		}

		if (((Boolean) state.getValue(SOUTH)).booleanValue()) {
			i |= 1 << 3;
		}

		if (((Boolean) state.getValue(WEST)).booleanValue()) {
			i |= 1 << 4;
		}
		if (((Boolean) state.getValue(UP)).booleanValue()) {
			i |= 1 << 1;
		}
		if (((Boolean) state.getValue(DOWN)).booleanValue()) {
			i |= 1 << 0;
		}
		// System.out.println(i);
		return i;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		int northInt = 0;
		int eastInt = 0;
		int southInt = 0;
		int westInt = 0;
		int downInt = 0;
		int upInt = 0;
		if (tile != null && tile instanceof TileEntityFluidPipeClear) {
			northInt = ((TileEntityFluidPipeClear) tile).getSide("north");
			eastInt = ((TileEntityFluidPipeClear) tile).getSide("east");
			southInt = ((TileEntityFluidPipeClear) tile).getSide("south");
			westInt = ((TileEntityFluidPipeClear) tile).getSide("west");
			downInt = ((TileEntityFluidPipeClear) tile).getSide("down");
			upInt = ((TileEntityFluidPipeClear) tile).getSide("up");
		} else {

		}

		boolean upBool = false;
		boolean downBool = false;
		boolean northBool = false;
		boolean southBool = false;
		boolean eastBool = false;
		boolean westBool = false;
		if (upInt == 1) {
			upBool = false;
		} else if (upInt == 2) {
			upBool = true;
		} else {
			upBool = canFenceConnectTo(worldIn, pos, EnumFacing.UP);
		}

		if (downInt == 1) {
			downBool = false;
		} else if (downInt == 2) {
			downBool = true;
		} else {
			downBool = canFenceConnectTo(worldIn, pos, EnumFacing.DOWN);
		}

		if (northInt == 1) {
			northBool = false;
		} else if (northInt == 2) {
			northBool = true;
		} else {
			northBool = canFenceConnectTo(worldIn, pos, EnumFacing.NORTH);
		}

		if (southInt == 1) {
			southBool = false;
		} else if (southInt == 2) {
			southBool = true;
		} else {
			southBool = canFenceConnectTo(worldIn, pos, EnumFacing.SOUTH);
		}

		if (eastInt == 1) {
			eastBool = false;
		} else if (eastInt == 2) {
			eastBool = true;
		} else {
			eastBool = canFenceConnectTo(worldIn, pos, EnumFacing.EAST);
		}

		if (westInt == 1) {
			westBool = false;
		} else if (westInt == 2) {
			westBool = true;
		} else {
			westBool = canFenceConnectTo(worldIn, pos, EnumFacing.WEST);
		}

		return state.withProperty(NORTH, northBool).withProperty(EAST, eastBool).withProperty(SOUTH, southBool)
				.withProperty(WEST, westBool).withProperty(UP, upBool).withProperty(DOWN, downBool);
	}

	private boolean canFenceConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		Block block = world.getBlockState(pos.offset(facing)).getBlock();
		TileEntity tile = world.getTileEntity(pos.offset(facing));
		TileEntity tileThis = world.getTileEntity(pos);
		if (tile != null && tileThis != null) {
			if (tile instanceof TRZTileEntityFluidPipe) {
				if (facing.equals(EnumFacing.UP)) {
					if (((TRZTileEntityFluidPipe) tile).getSide("down") == 1) {
						return false;
					}
					if(((TRZTileEntityFluidPipe)tileThis).getTank().getFluidAmount() == 0 && ((TRZTileEntityFluidPipe)tile).getTank().getFluid() != null){
						return false;
					}
					else if(((TRZTileEntityFluidPipe)tileThis).getTank().getFluid() != null){
						if(((TRZTileEntityFluidPipe) tile).getTank().getFluid() != null){
							if(((TRZTileEntityFluidPipe) tile).getTank().getFluid().getFluid().getName().equals(((TRZTileEntityFluidPipe)tileThis).getTank().getFluid().getFluid().getName())){
								return true;
							} else {
								return false;
							}
						}
						return false;
					} else {
						return true;
					}
				}
				if (facing.equals(EnumFacing.DOWN)) {
					if (((TRZTileEntityFluidPipe) tile).getSide("up") == 1) {
						return false;
					}
					if(((TRZTileEntityFluidPipe)tileThis).getTank().getFluidAmount() == 0 && ((TRZTileEntityFluidPipe)tile).getTank().getFluid() != null){
						return false;
					}
					else if(((TRZTileEntityFluidPipe)tileThis).getTank().getFluid() != null){
						if(((TRZTileEntityFluidPipe) tile).getTank().getFluid() != null){
							if(((TRZTileEntityFluidPipe) tile).getTank().getFluid().getFluid().getName().equals(((TRZTileEntityFluidPipe)tileThis).getTank().getFluid().getFluid().getName())){
								return true;
							} else {
								return false;
							}
						}
						return false;
					} else {
						return true;
					}
				}
				if (facing.equals(EnumFacing.NORTH)) {
					if (((TRZTileEntityFluidPipe) tile).getSide("south") == 1) {
						return false;
					} else {
						return true;
					}
				}
				if (facing.equals(EnumFacing.SOUTH)) {
					if (((TRZTileEntityFluidPipe) tile).getSide("north") == 1) {
						return false;
					} else {
						return true;
					}
				}
				if (facing.equals(EnumFacing.EAST)) {
					if (((TRZTileEntityFluidPipe) tile).getSide("west") == 1) {
						return false;
					} else {
						return true;
					}
				}
				if (facing.equals(EnumFacing.WEST)) {
					if (((TRZTileEntityFluidPipe) tile).getSide("east") == 1) {
						return false;
					} else {
						return true;
					}
				}
			}
		}
		return block.canBeConnectedTo(world, pos.offset(facing), facing.getOpposite())
				|| canConnectTo(world, pos.offset(facing), facing);
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return TRZPipeConnectionsList.getFluidBlockConnections(worldIn, pos, side);
	}

}
