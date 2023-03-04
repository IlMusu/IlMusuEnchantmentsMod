package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.PlayerDropInventoryCallback;
import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import com.ilmusu.musuen.networking.messages.PocketsLevelMessage;
import com.ilmusu.musuen.networking.messages.PocketsToggleMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerWithPockets implements _IPlayerPockets
{
    @Shadow @Final public PlayerScreenHandler playerScreenHandler;

    // The inventory containing the items in the pockets
    private final SimpleInventory pockets = new SimpleInventory(20);
    private int firstSlotId = -1;
    // If the pockets are currently open
    private boolean pocketsOpen = false;
    // The current level of the pockets, which changes the amount of available slots
    private int pocketsLevel = 0;

    @Override
    public Inventory getPockets()
    {
        return this.pockets;
    }

    @Override
    public void setFirstSlotId(int id)
    {
        if(this.firstSlotId != -1)
            return;
        this.firstSlotId = id;
    }

    @Override
    public boolean arePocketsOpen()
    {
        return this.pocketsOpen;
    }

    @Override
    public int getPocketLevel()
    {
        return this.pocketsLevel;
    }

    @Override
    public void setPocketsOpen(boolean open)
    {
        this.pocketsOpen = open;

        // Setting the correct state for the pockets
        for(int i=0; i<this.pocketsLevel*4; ++i)
        {
            PocketSlot slot = (PocketSlot)this.playerScreenHandler.slots.get(this.firstSlotId+i);
            slot.arePocketsOpen = this.pocketsOpen;
        }

        PlayerEntity player = (PlayerEntity)(Object)this;
        if(player.world.isClient)
            new PocketsToggleMessage(player).sendToServer();
    }

    @Override
    public void setPocketLevel(World world, int pocketLevel)
    {
        this.pocketsLevel = pocketLevel;

        // Enabling all the slots up to the correct one
        for(int i=0; i<pocketLevel*4; ++i)
        {
            PocketSlot slot = (PocketSlot)this.playerScreenHandler.slots.get(this.firstSlotId+i);
            slot.enabled = true;
        }

        // Disabling all the other slots
        for(int i=pocketLevel*4; i<20; ++i)
        {
            PocketSlot slot = (PocketSlot)this.playerScreenHandler.slots.get(this.firstSlotId+i);
            slot.enabled = false;

            if(!world.isClient)
            {
                // Dropping the stacks in the pockets if the player decreases the number of slots
                ItemStack stack = this.pockets.removeStack(i);
                if(!stack.isEmpty())
                {
                    ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
                    player.dropItem(stack, false, true);
                }
            }
        }

        // Syncing the client with the current level of the pockets
        if(!world.isClient)
        {
            ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
            new PocketsLevelMessage(player).sendToClient(player);
        }
    }

    @Override
    public void clone(_IPlayerPockets other)
    {
        for (int i = 0; i < this.pockets.size(); ++i)
            this.pockets.setStack(i, other.getPockets().getStack(i));
        this.pocketsLevel = other.getPocketLevel();
        this.pocketsOpen = other.arePocketsOpen();
    }

    static
    {
        PlayerDropInventoryCallback.AFTER.register(player ->
        {
            _IPlayerPockets pockets = (_IPlayerPockets)player;
            for(int i=0; i<pockets.getPockets().size(); ++i)
            {
                ItemStack stack = pockets.getPockets().removeStack(i);
                if(!stack.isEmpty())
                    player.dropItem(stack, false, true);
            }
        });
    }

    @Inject(method = "tickMovement", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerInventory;updateItems()V"
    ))
    public void updatePocketItems(CallbackInfo ci)
    {
        for(int i=0; i<this.pockets.size(); ++i)
        {
            if (this.pockets.getStack(i).isEmpty())
                continue;

            PlayerEntity player = (PlayerEntity)(Object)this;
            this.pockets.getStack(i).inventoryTick(player.world, player, this.firstSlotId+i, false);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfo ci)
    {
        NbtCompound pockets = new NbtCompound();
        // Writing items
        NbtList list = new NbtList();
        for(int slot=0; slot<this.pockets.size(); ++slot)
        {
            ItemStack stack = this.pockets.getStack(slot);
            if (stack.isEmpty())
                continue;

            NbtCompound compound = new NbtCompound();
            compound.putByte("slot", (byte)slot);
            list.add(stack.writeNbt(compound));
        }
        pockets.put("items", list);
        // Writing level
        pockets.putInt("level", this.pocketsLevel);
        // Writing state
        pockets.putBoolean("open", this.pocketsOpen);

        nbt.put(Resources.MOD_ID+".pockets", pockets);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci)
    {
        NbtCompound pockets = nbt.getCompound(Resources.MOD_ID+".pockets");
        if (pockets != null)
        {
            // Reading items
            NbtList list = pockets.getList("items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); ++i)
            {
                NbtCompound nbtCompound = list.getCompound(i);
                int slot = nbtCompound.getByte("slot") & 0xFF;
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
                if (itemStack.isEmpty())
                    continue;

                this.pockets.setStack(slot, itemStack);
            }
            // Reading level
            this.pocketsLevel = pockets.getInt("level");
            // Reading state
            this.pocketsOpen = pockets.getBoolean("open");
        }
    }

    @Mixin(PlayerScreenHandler.class)
    public abstract static class AddPocketSlotsToPlayerInventory
    {
        @Inject(method = "<init>", at = @At("TAIL"))
        public void addPocketSlotsToInventory(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci)
        {
            AccessorScreenHandler handler = (AccessorScreenHandler) this;
            _IPlayerPockets player = ((_IPlayerPockets)owner);
            Inventory pockets = player.getPockets();

            // When the slots are added, the actual id in the handler is assigned
            // from the index of the list of slots, therefore, this is the id
            player.setFirstSlotId(((PlayerScreenHandler)(Object)this).slots.size());

            int id = 0;
            for(int y=0; y<5; ++y)
            {
                // Adding pocket on the right
                for(int x=0; x<2; ++x)
                    handler.addSlotAccess(new PocketSlot(pockets, id++, -46+18*x, 8+18*y));
                // Adding pocket on the left
                for(int x=0; x<2; ++x)
                    handler.addSlotAccess(new PocketSlot(pockets, id++, 189+18*x, 8+18*y));
            }
        }
    }

    @Mixin(AbstractInventoryScreen.class)
    public abstract static class RemoveStatusEffectsRenderingWhenPocketsOpen
    {
        @Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
        public void removeStatusEffectsRendering(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci)
        {
            _IPlayerPockets pockets = ((_IPlayerPockets)MinecraftClient.getInstance().player);
            if(pockets.arePocketsOpen())
                ci.cancel();
        }
    }

    @Mixin(InventoryScreen.class)
    public abstract static class RenderPocketsInPlayerInventory
    {
        @Shadow @Final private RecipeBookWidget recipeBook;

        private TexturedButtonWidget pocketsButton;

        @Inject(method = "init", at = @At(value = "RETURN", ordinal = 1))
        public void initPocketsButton(CallbackInfo ci)
        {
            _IPlayerPockets pockets = ((_IPlayerPockets)MinecraftClient.getInstance().player);

            this.pocketsButton = new TexturedButtonWidget(0, 0, 20, 18, 0, 0, 19, Resources.POCKETS_BUTTON_TEXTURE, button ->
                pockets.setPocketsOpen(!pockets.arePocketsOpen())
            );
            this.updatePocketsButtonPos();

            ((AccessorScreen)this).getDrawables().add(this.pocketsButton);
            ((AccessorScreen)this).getChildren().add(this.pocketsButton);
            ((AccessorScreen)this).getSelectables().add(this.pocketsButton);
        }

        @Inject(method = "drawBackground", at = @At("TAIL"))
        public void renderPocketSlots(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Disabling the pockets button if the pockets are not open
            _IPlayerPockets pockets = ((_IPlayerPockets)MinecraftClient.getInstance().player);
            this.pocketsButton.visible = pockets.getPocketLevel() > 0;
            this.updatePocketsButtonPos();

            // Do not render pockets if recipe book is open
            if(this.recipeBook.isOpen())
                return;

            if(pockets.getPocketLevel() == 0 || !pockets.arePocketsOpen())
                return;

            RenderSystem.setShaderTexture(0, Resources.POCKETS_TEXTURE);
            renderPockets(matrices, pockets.getPocketLevel(), true);
            renderPockets(matrices, pockets.getPocketLevel(), false);
        }

        @SuppressWarnings("unchecked")
        public void updatePocketsButtonPos()
        {
            int x = ((AccessorHandledScreen<PlayerScreenHandler>)this).getX();
            int height = ((AccessorScreen)this).getHeight();
            this.pocketsButton.setPos(x+126, height/2-22);
        }

        @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
        public void addPocketsOutsideInventoryBounds(double mouseX, double mouseY, int left, int top, int button,
            CallbackInfoReturnable<Boolean> cir)
        {
            int level = ((_IPlayerPockets)MinecraftClient.getInstance().player).getPocketLevel();
            if(level == 0)
                return;

            // Mouse click is on the left or right pocket
            boolean isLeftX = mouseX>left-55 && mouseX<(left-55+51);
            boolean isRightX = mouseX>left+176+4 && mouseX<(left+176+4+51);
            if(mouseY>top && mouseY<(top+32+18*level) && (isLeftX || isRightX))
                cir.setReturnValue(false);
        }

        @Inject(method = "method_19891", at = @At("TAIL"))
        public void disablePocketsWhenRecipeBookOpen(ButtonWidget button, CallbackInfo ci)
        {
            ((AccessorHandledScreen<?>)this).getHandler().slots.forEach((slot -> {
                if(slot instanceof PocketSlot pocketSlot)
                    pocketSlot.isRecipeBookOpen = this.recipeBook.isOpen();
            }));
        }

        private void renderPockets(MatrixStack matrices, int level, boolean left)
        {
            InventoryScreen self = (InventoryScreen)(Object)this;
            AccessorHandledScreen<?> accessor = (AccessorHandledScreen<?>)this;

            int x = accessor.getX() + (left ? -55 : accessor.getBackgroundWidth()+4);
            int y = accessor.getY();
            int u = 51*(level-1);
            int v = 0;
            int sizeX = 51;
            int sizeY = 32+18*(level-1);
            self.drawTexture(matrices, x, y, u, v, sizeX, sizeY);
        }
    }

    @Mixin(CreativeInventoryScreen.class)
    public abstract static class DisablePocketsInCreativeInventory
    {
        private static boolean isPocket;

        @ModifyArgs(method = "setSelectedTab", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen$CreativeSlot;<init>(Lnet/minecraft/screen/slot/Slot;III)V"
        ))
        public void disablePocketsInCreativeInventoryHook(Args args)
        {
            DisablePocketsInCreativeInventory.isPocket = args.get(0) instanceof PocketSlot;
        }

        @Redirect(method = "setSelectedTab", at = @At(
            value = "INVOKE",
            ordinal = 2,
            target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z"
        ))
        public boolean disablePocketsInCreativeInventory(DefaultedList<Slot> instance, Object o)
        {
            if(DisablePocketsInCreativeInventory.isPocket)
                return false;
            return instance.add((Slot)o);
        }
    }

    static
    {
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->
        {
            new PocketsLevelMessage(handler.player).sendToClient(handler.player);
            new PocketsToggleMessage(handler.player).sendToClient(handler.player);
        }));

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) ->
        {
            new PocketsLevelMessage(player).sendToClient(player);
            new PocketsToggleMessage(player).sendToClient(player);
        }));

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, isEndTeleport) ->
        {
            if(!isEndTeleport)
                return;

            // Copying the pockets from the old player to the new player
            // Only if this is not death (end portal teleport)
            ((_IPlayerPockets)newPlayer).clone((_IPlayerPockets)oldPlayer);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, isEndTeleport) ->
        {
            new PocketsLevelMessage(newPlayer).sendToClient(newPlayer);
            new PocketsToggleMessage(newPlayer).sendToClient(newPlayer);
        }));
    }
}
