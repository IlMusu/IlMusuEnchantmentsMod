package com.ilmusu.musuen.mixins.mixin;

import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.callbacks.PlayerDropInventoryCallback;
import com.ilmusu.musuen.mixins.interfaces._IPlayerPockets;
import com.ilmusu.musuen.networking.messages.PocketsLevelMessage;
import com.ilmusu.musuen.networking.messages.PocketsToggleMessage;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerWithPockets implements _IPlayerPockets
{
    // The inventory containing the items in the pockets
    @Unique private final SimpleInventory pockets = new SimpleInventory(20);
    // The slots in the player inventory, they need to be stored because the indexes might change
    @Unique private final List<PocketSlot> slots = new ArrayList<>(20);

    // The current level of pockets
    @Unique private int pocketsLevel = 0;
    // If the pockets are currently open
    @Unique private boolean arePocketsOpen = false;

    @Override
    public Inventory getPockets()
    {
        return this.pockets;
    }

    @Override
    public boolean arePocketsOpen()
    {
        return this.arePocketsOpen;
    }

    @Override
    public int getPocketLevel()
    {
        return this.pocketsLevel;
    }

    @Override
    public void updatePocketSlot(PocketSlot slot)
    {
        this.slots.add(slot.getIndex(), slot);
    }

    @Override
    public void setPocketsOpen(boolean open)
    {
        // Storing the current state of the pockets
        this.arePocketsOpen = open;

        // Setting the correct state for the pockets
        for(PocketSlot slot : this.slots)
            slot.arePocketsOpen = this.arePocketsOpen;

        PlayerEntity player = (PlayerEntity)(Object)this;
        if(player.getWorld().isClient)
            new PocketsToggleMessage(player).sendToServer();
    }

    @Override
    public void updatePocketsLevel(World world, int pocketsLevel)
    {
        // Storing the current level of the pockets
        this.pocketsLevel = pocketsLevel;

        // Enabling all the slots up to the correct one
        for(int i=0; i<pocketsLevel*4; ++i)
            this.slots.get(i).enabled = true;

        // Disabling all the other slots
        for(int i=pocketsLevel*4; i<20; ++i)
            this.slots.get(i).enabled = false;

        if(!world.isClient)
        {
            // Dropping the stacks in the pockets if the number of slots is decreased
            this.dropPocketsContents(pocketsLevel *4);
            // Syncing the client with the current level of the pockets
            ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
            new PocketsLevelMessage(player).sendToClient(player);
        }
    }

    @Unique
    private void dropPocketsContents(int startIndex)
    {
        // Dropping the stacks in the pockets if the number of slots is decreased
        for(int i=startIndex; i<20; ++i)
        {
            // Dropping the stacks in the pockets if the player decreases the number of slots
            ItemStack stack = this.getPockets().removeStack(i);
            if(!stack.isEmpty())
            {
                ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
                player.dropItem(stack, false, true);
            }
        }
    }

    @Override
    public void clone(_IPlayerPockets other)
    {
        for (int i = 0; i < this.getPockets().size(); ++i)
            this.getPockets().setStack(i, other.getPockets().getStack(i));
        this.arePocketsOpen = other.arePocketsOpen();
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
    private void updatePocketItems(CallbackInfo ci)
    {
        for(int i = 0; i<this.getPockets().size(); ++i)
        {
            if (this.getPockets().getStack(i).isEmpty())
                continue;

            PlayerEntity player = (PlayerEntity)(Object)this;
            PocketSlot slot = this.slots.get(i);
            slot.getStack().inventoryTick(player.getWorld(), player, slot.getIndex(), false);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writePocketsDataToNbt(NbtCompound nbt, CallbackInfo ci)
    {
        NbtCompound pockets = new NbtCompound();
        // Writing items
        NbtList list = new NbtList();
        for(int slot = 0; slot<this.getPockets().size(); ++slot)
        {
            ItemStack stack = this.getPockets().getStack(slot);
            if (stack.isEmpty())
                continue;

            NbtCompound compound = new NbtCompound();
            compound.putByte("slot", (byte)slot);
            list.add(stack.writeNbt(compound));
        }
        pockets.put("items", list);
        // Writing state
        pockets.putBoolean("open", this.arePocketsOpen);

        nbt.put(Resources.MOD_ID+".pockets", pockets);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci)
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

                this.getPockets().setStack(slot, itemStack);
            }
            // Reading state
            this.arePocketsOpen = pockets.getBoolean("open");
        }
    }

    @Mixin(PlayerScreenHandler.class)
    public abstract static class AddPocketSlotsToPlayerInventory
    {
        @Inject(method = "<init>", at = @At("TAIL"))
        private void addPocketSlotsToInventory(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci)
        {
            AccessorScreenHandler handler = (AccessorScreenHandler) this;
            _IPlayerPockets player = ((_IPlayerPockets)owner);
            Inventory pockets = player.getPockets();

            int id = 0;
            // The slots need to be always present but activated and visible only when necessary
            // Otherwise this might cause problems with iterating in the arrays
            for(int y=0; y<5; ++y)
            {
                // Adding pocket on the right
                for(int x=0; x<2; ++x)
                {
                    PocketSlot slot = new PocketSlot(pockets, id++, -46+18*x, 8+18*y);
                    player.updatePocketSlot(slot);
                    handler.addSlotAccess(slot);
                }
                // Adding pocket on the left
                for(int x=0; x<2; ++x)
                {
                    PocketSlot slot = new PocketSlot(pockets, id++, 189+18*x, 8+18*y);
                    player.updatePocketSlot(slot);
                    handler.addSlotAccess(slot);
                }
            }
        }
    }

    @Mixin(AbstractInventoryScreen.class)
    public abstract static class RemoveStatusEffectsRenderingWhenPocketsOpen
    {
        @Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
        private void removeStatusEffectsRendering(DrawContext context, int mouseX, int mouseY, CallbackInfo ci)
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

        @Unique private TexturedButtonWidget pocketsButton;

        @Inject(method = "init", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;initialize(IILnet/minecraft/client/MinecraftClient;ZLnet/minecraft/screen/AbstractRecipeScreenHandler;)V",
                shift = At.Shift.AFTER
        ))
        private void initPocketsButton(CallbackInfo ci)
        {
            _IPlayerPockets pockets = ((_IPlayerPockets)MinecraftClient.getInstance().player);

            this.pocketsButton = new TexturedButtonWidget(0, 0, 20, 18,
                    new ButtonTextures(Resources.POCKETS_BUTTON_NORMAL_TEXTURE, Resources.POCKETS_BUTTON_HIGHLIGHT_TEXTURE),
                    button -> pockets.setPocketsOpen(!pockets.arePocketsOpen()));
            this.updatePocketsButtonPos();

            ((AccessorScreen)this).getDrawables().add(this.pocketsButton);
            ((AccessorScreen)this).getChildren().add(this.pocketsButton);
            ((AccessorScreen)this).getSelectables().add(this.pocketsButton);
        }

        @Inject(method = "drawBackground", at = @At("TAIL"))
        private void renderPocketSlots(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci)
        {
            // Disabling the pockets button if the pockets are not open
            _IPlayerPockets pockets = ((_IPlayerPockets)MinecraftClient.getInstance().player);
            if(this.pocketsButton != null)
            {
                this.pocketsButton.visible = pockets.getPocketLevel() > 0;
                this.updatePocketsButtonPos();
            }

            // Do not render pockets if recipe book is open
            if(this.recipeBook.isOpen())
                return;

            if(pockets.getPocketLevel() == 0 || !pockets.arePocketsOpen())
                return;

            renderPockets(context, pockets.getPocketLevel(), true);
            renderPockets(context, pockets.getPocketLevel(), false);
        }


        @Unique
        @SuppressWarnings("unchecked")
        public void updatePocketsButtonPos()
        {
            int x = ((AccessorHandledScreen<PlayerScreenHandler>)this).getX();
            int height = ((AccessorScreen)this).getHeight();
            this.pocketsButton.setPosition(x+126, height/2-22);
        }

        @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
        private void addPocketsOutsideInventoryBounds(double mouseX, double mouseY, int left, int top, int button,
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
        private void disablePocketsWhenRecipeBookOpen(ButtonWidget button, CallbackInfo ci)
        {
            ((AccessorHandledScreen<?>)this).getHandler().slots.forEach((slot -> {
                if(slot instanceof PocketSlot pocketSlot)
                    pocketSlot.isRecipeBookOpen = this.recipeBook.isOpen();
            }));
        }

        @Unique
        private void renderPockets(DrawContext context, int level, boolean left)
        {
            AccessorHandledScreen<?> accessor = (AccessorHandledScreen<?>)this;

            int x = accessor.getX() + (left ? -55 : accessor.getBackgroundWidth()+4);
            int y = accessor.getY();
            int u = 51*(level-1);
            int v = 0;
            int sizeX = 51;
            int sizeY = 32+18*(level-1);
            context.drawTexture(Resources.POCKETS_TEXTURE, x, y, u, v, sizeX, sizeY);
        }
    }

    @Mixin(CreativeInventoryScreen.class)
    public abstract static class DisablePocketsInCreativeInventory
    {
        @Inject(method = "setSelectedTab", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/screen/slot/Slot;<init>(Lnet/minecraft/inventory/Inventory;III)V"
        ))
        private void removeAllPocketSlots(ItemGroup group, CallbackInfo ci)
        {
            // This is a fix which prevents using the @Redirect
            CreativeInventoryScreen screen = (CreativeInventoryScreen)(Object)this;
            screen.getScreenHandler().slots.removeIf((slot) -> slot instanceof PocketSlot);
        }
    }

    static
    {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, isEndTeleport) ->
        {
            if(!isEndTeleport)
                return;

            // Copying the pockets from the old player to the new player
            // Only if this is the end teleportation since when dying the player does not preserve the items
            ((_IPlayerPockets)newPlayer).clone((_IPlayerPockets)oldPlayer);
        });

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->
        {
            new PocketsToggleMessage(handler.player).sendToClient(handler.player);
        }));

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) ->
        {
            new PocketsLevelMessage(player).sendToClient(player);
            new PocketsToggleMessage(player).sendToClient(player);
        }));

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, isEndTeleport) ->
        {
            // The pockets contents are not copied after death because are dropped
            new PocketsLevelMessage(newPlayer).sendToClient(newPlayer);
            new PocketsToggleMessage(oldPlayer).sendToClient(newPlayer);
        }));
    }
}