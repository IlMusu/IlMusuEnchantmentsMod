# ILMUSU'S ENCHANTMENTS MOD
This mod adds <b>more enchantments</b> to Minecraft.  
It also includes a new mechanic, referred to as <b><span style="color:purple">Demonic Enchanting</span></b>, for making enchanting a little bit more interesting without twisting the game too much.

There is a configuration for the mod at the following path:
```bash
[minecraft_folder]/config/musuen/mod.properties
```

It is possible to disable the enchantments and modify the min and max levels singularly.  
This mod provides a configuration file for the enchantments located at the following path:  
```bash
[minecraft_folder]/config/musuen/enchantments.properties
```

## DEMONIC ENCHANTING
The demonic enchanting mechanic can be triggered by placing <b>three Wither Skeleton Skulls</b> (increasing the number of skulls increases the demonic enchantments probability), around the enchanting table as shown in the following images. Only after the skulls are placed correctly, red glyph particles start to move from the skulls to the enchanting table.

<p>
	<img width="80%" src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/skulls_positioning.png?raw=true" alt="">
	<br>
	<img width="80%" src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/skulls_red_glyph.png?raw=true" alt="">
</p>

Then, you might discover <b><span style="color:purple">demonic enchantments</span></b> while enchanting items or books: 

<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/demonic_enchanting_1.png?raw=true" alt="">
	<br>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/demonic_enchanting_2.png?raw=true" alt="">
</p>

These enchantments require, not only experience levels and lapislazuli, but also a <b><span style="color:red">sacrifice</span></b> in health: around the Enchanting Table, in a radius of 7 blocks, there should be enough living entities to drawn the health requested by the enchantment. <br />
For example in the second image, 40 hearts needs to be consumed and a sheep has only 4 hearts, this means that 10 sheep are necessary to apply the <b><span style="color:purple">demonic enchantment</span></b> on the item. <br />
Notice that when there are not enough living entities around the enchanting table, <b>the health is drawn from the player</b> who might also get killed! <br />
<br />
If the health requirement is satisfied, you will obtain the <b><span style="color:purple">demonic enchantment</span></b>: <br />

<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/book_with_demonic_enchantment.png?raw=true" alt="">
</p>

## LACERATION ENCHANTMENT (V)
The <b><span style="color:purple">Laceration Enchantment</span></b> provides additional damage for weapon tools. Since it is a demonic enchantment, attacking an entity with an item containing this enchantment <b>damages also the player</b>: at the maximum level, it provides +16.0 additional damage and consumes 25% of the player maximum health. <br />

<details>
<summary><b>Details here</b></summary>
This enchantment produces more damage the more health it can consume from the player: <br />
At level 1, it consumes 10.00% of the max health and produces +4.6 additional damage. <br />
At level 2, it consumes 13.75% of the max health and produces +7.0 additional damage.  <br />
At level 3, it consumes 17.50% of the max health and produces +9.7 additional damage. <br />
At level 4, it consumes 21.25% of the max health and produces +12.7 additional damage. <br />
At level 5, it consumes 25.00% of the max health and produces +16.0 additional damage. <br />
An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/laceration.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Axe Item
2. Sword Item

This enchantment <b>is not compatible</b> with:
1. Sharpness Enchantment
2. Berserker Enchantment

## SKEWERING ENCHANTMENT (V)
The <b><span style="color:purple">Skewering Enchantment</span></b> provides additional damage for the trident both in the case of melee attack and ranged attack. Since it is a demonic enchantment, attacking an entity with an item containing this enchantment <b>damages also the player</b>: at the maximum level, it provides +16.0 additional damage and consumes 25% of the player maximum health. <br />

<details>
<summary><b>Details here</b></summary>
This enchantment produces more damage the more health it can consume from the player: <br />
At level 1, it consumes 10.00% of the max health and produces +4.6 additional damage. <br />
At level 2, it consumes 13.75% of the max health and produces +7.0 additional damage.  <br />
At level 3, it consumes 17.50% of the max health and produces +9.7 additional damage. <br />
At level 4, it consumes 21.25% of the max health and produces +12.7 additional damage. <br />
At level 5, it consumes 25.00% of the max health and produces +16.0 additional damage. <br />
An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/skewering.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Trident Item

This enchantment <b>is not compatible</b> with:
1. Impaling Enchantment
2. Overcharge Enchantment

## BERSERKER ENCHANTMENT (V)
The <b><span style="color:purple">Berserker Enchantment</span></b> provides additional damage for weapon tools. When the player performs successive attacks, he fills a "rage" indicator that provides more damage. Since it is a demonic enchantment, attacking an entity with an item containing this enchantment <b>damages also the player</b>: at the maximum level and at the max rage, it provides +25.0 additional damage and consumes 10% of the player maximum health per attack.
<details>
<summary><b>Details here</b></summary>
This enchantment produces more damage the more health it can consume from the player: <br />
At level 1, it consumes 5.00% of the max health, 4 attacks for full rage, and produces +5.0 additional damage. <br />
At level 2, it consumes 6.25% of the max health, 6 attacks for full rage, and produces +10.0 additional damage.  <br />
At level 3, it consumes 7.50% of the max health, 7 attacks for full rage, and produces +15.0 additional damage. <br />
At level 4, it consumes 8.75% of the max health, 8 attacks for full rage, and produces +20.0 additional damage. <br />
At level 5, it consumes 10.00% of the max health, 9 attacks for full rage, and produces +25.0 additional damage. <br />
An important notice is that <b>this kills the player if he does not have enough health!</b> <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/berserker.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Axe Item
2. Sword Item

This enchantment <b>is not compatible</b> with:
1. Sharpness Enchantment
2. Laceration Enchantment

## UNEARTHING ENCHANTMENT (V)
The <b><span style="color:purple">Unearthing Enchantment</span></b> makes a digger tool dig tunnels with depth and area depending on the level of the enchantments. Since it is a demonic enchantment, digging with an item containing this enchantment <b>damages also the player</b>: the tunnel will be complete only if the player has enough life. <br />

<details>
<summary><b>Details here</b></summary>
This enchantment creates a bigger tunnel the more health it can consume from the player: <br />
At level 1, it consumes 20.0% of the max health and the tunnel will be of 3x3 blocks and 1 blocks depth. <br />
At level 2, it consumes 22.5% of the max health and the tunnel will be of 3x3 blocks and 3 blocks depth. <br />
At level 3, it consumes 25.0% of the max health and the tunnel will be of 3x3 blocks and 4 blocks depth. <br />
At level 4, it consumes 27.5% of the max health and the tunnel will be of 3x3 blocks and 6 blocks depth. <br />
At level 5, it consumes 30.0% of the max health and the tunnel will be of 5x5 blocks and 7 blocks depth. <br />
An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/unearthing.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Axe Item
2. Hoe Item
3. Pickaxe Item
4. Shovel Item

This enchantment <b>is not compatible</b> with:
1. Veinminer Enchantment

## OVERCHARGED ENCHANTMENT (V)
The <b><span style="color:purple">Overcharged Enchantment</span></b> makes the chargeable items to be overchargable and perform additional damage. Since it is a demonic enchantment, overcharging an item containing this enchantment <b>damages also the player</b>: after the item is normally charged, the player starts to take damage every 0.5 seconds and the projectile makes additional damage depending on the total health absorbed by the player. <br />

<details>
<summary><b>Details here</b></summary>
The projectile will make additional damage depending on the level of the enchantment and the health absorbed:

```bash
additional_damage = player_endured_damage*(0.3 + enchantment_level * 0.2)
```

An important notice is that <b>this kills the player if he does not have enough health!</b>  <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/overcharged.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Bow Item
2. Crossbow Item
3. Trident Item

This enchantment <b>is not compatible</b> with:
1. Skewering Enchantment
2. Laceration Enchantment
3. Impaling Enchantment
4. Power Enchantment
5. Damage Enchantments

## PHASING ENCHANTMENT (V)
The <b><span style="color:purple">Phasing Enchantment</span></b> makes the player able to teleport: it provides the ability of both teleporting where the player is looking at and teleporting behind walls. Since it is a demonic enchantment, activating this ability <b>damages also the player</b>. <br />
<b>The phasing enchantment ability is activated through the related keybinding.</b> <br />

<details>
<summary><b>Details here</b></summary>
When the ability is activated, the logic performs a raycasting in the direction the player is looking at and, if a wall is found, it tries to teleport the player behind that wall. Instead, if a wall is not found, the player is teleported in the direction he was looking at with a distance depending on the current level of the enchantment. <br />
An important notice is that <b>this kills the player if he does not have enough health!</b> <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/phasing.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Leggings Item

## GUILLOTINING ENCHANTMENT (III)
The <b><span style="color:purple">Guillotining Enchantment</span></b> adds a small probability of dropping a head when killing a mob or a player. Since it is a demonic enchantment, when a mob head is dropped, this enchantment <b>damages the player</b>. </br>
It is possible to add a custom head for a mob using data packs. Examples can be found [here](https://github.com/IlMusu/IlMusuEnchantmentsMod/tree/fabric_1.20.1/src/main/resources/data/musuen/head_recipes). </br>

<details>
<summary><b>Details here</b></summary>
The probability of dropping a head increases with the level of the enchantment: <br />
At level 1, the probability is 5.0%. <br />
At level 2, the probability is 10.0%. <br />
At level 3, the probability is 15.0%. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/guillotining.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Sword item

## DEMONCTION ENCHANTMENT (V)
The <b>Demonction Enchantment</b> is the only enchantment that is able to protect the user against demonic damage: this applies only to the demonic damage inflicted by the enchantments and not by demonic enchanting.

<details>
<summary><b>Details here</b></summary>
The protection provided by the demonction enchantment increases with the level: <br />
At level 1, the damage reduction is 0.25 hearts. <br />
At level 2, the damage reduction is 0.50 hearts. <br />
At level 3, the damage reduction is 0.75 hearts. <br />
At level 4, the damage reduction is 1.00 hearts. <br />
At level 5, the damage reduction is 1.25 hearts. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/demonction.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Helmet Item
2. Chestplate Item
3. Leggings Item
4. Boots Item

## REACHING ENCHANTMENT (IV)
The <b>Reaching Enchantment</b> increases player's reach making him able to interact with blocks and entities that would normally be out of reach: at the maximum level, it extends the player's reach by 7 blocks.  

<details>
<summary><b>Details here</b></summary>
The reaching enchantment increases the player reach by one block for every level: <br />
At level 1, the reach distance is 4 blocks. <br />
At level 2, the reach distance is 5 blocks. <br />
At level 3, the reach distance is 6 blocks. <br />
At level 4, the reach distance is 7 blocks. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/reaching.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Axe Item
2. Hoe Item
3. Pickaxe Item
4. Shovel Item
5. Sword Item
6. Triden Item

## POCKETED ENCHANTMENT (V)
The <b>Pocketed Enchantment</b> adds pockets to leggings to provide additional inventory storage for the player: at the maximum level, it increases the player's inventory by 24 slots.  

<details>
<summary><b>Details here</b></summary>
The size of the pockets increases with the level of the enchantment:  <br />
At level 1, the pockets provide 4 slots. <br />
At level 2, the pockets provide 8 slots. <br />
At level 3, the pockets provide 12 slots. <br />
At level 4, the pockets provide 16 slots. <br />
At level 5, the pockets provide 24 slots. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/pocketed.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Leggings Item

## VEIN MINER ENCHANTMENT (V)
The <b>Vein Miner Enchantment</b> increases the amout of blocks that the player breaks every time he digs a block: it allows the player to mine entire veins of the same type of block with just breaking a single block: at the maximum level, it breaks 110 additional blocks at maximum.  

<details>
<summary><b>Details here</b></summary>
The amount of blocks that will be broken increases with the level of the enchantment: <br />
At level 1, the enchantment will break 14 additional blocks. <br />
At level 2, the enchantment will break 26 additional blocks. <br />
At level 3, the enchantment will break 46 additional blocks. <br />
At level 4, the enchantment will break 74 additional blocks. <br />
At level 5, the enchantment will break 110 additional blocks. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/vein_miner.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Axe Item
2. Hoe Item
3. Pickaxe Item
4. Shovel Item

This enchantment <b>is not compatible</b> with:
1. Unearthing Enchantment

## ATTRACTION ENCHANTMENT (III)
The <b>Attraction Enchantment</b> makes the items, that are lying on the ground near the player, move toward him: at the maximum level, the attraction distance is 8 blocks.

<details>
<summary><b>Details here</b></summary>
The attraction distance increases with the level of the enchantment: <br />
At level 1, the attraction reach is 2 blocks. <br />
At level 2, the attraction reach is 4 blocks. <br />
At level 3, the attraction reach is 8 blocks. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/attraction.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Chestplate Item

## METEORITY ENCHANTMENT (III)
The <b>Meteority Enchantment</b> increases the maximim flyight speed of the elytra applied when using a firework. The speed increases with the level of the enchantment and notice that after the effect of the firework is expired, the maximum speed is reset to normal: normally, the maximum speed is 1.0 m/s, instead while using this enchantment at the maximum level, the speed is 3.1 m/s.  

<details>
<summary><b>Details here</b></summary>
The speed of the elytra increases differently at different levels of the enchantment: <br />
Normally, the maximum speed is 1.0 m/s. <br />
At level 1, the maximum speed is 1.7 m/s. <br />
At level 2, the masimum speed is 2.4 m/s. <br />
At level 3, the maximum speed is 3.1 m/s. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/meteority.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Elytra Item

## WINGSPAN ENCHANTMENT (III)
The <b>Wingspan Enchantment</b> applies knockback on all the living entities near the player after landing with the elytra. The force of the knockback and the range increases with the level of the enchantment: at the maximum level, the application distance is 6 blocks and the force depends on the velocity of the player.

<details>
<summary><b>Details here</b></summary>
The range of the enchantment increases with the level of the enchantment: <br />
At level 1, the reach distance is 3 blocks. <br />
At level 2, the reach distance is 4 blocks. <br />
At level 3, the reach distance is 6 blocks. <br />
The force of knockback increases with the level of the enchantment and the speed of the player when landing: <br />

```bash
power = (level * 0.25) + min(3.0, player_speed * 1.4)
```
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/wingspan.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Elytra Item

## ALIGHTING ENCHANTMENT (V)
The <b>Alighting Enchantment</b> reduces the fall damage taken by the player when the elytra are equipped: at the maximum level, it completely cancels the fall damage, making the landing completely safe.  
Notice that it <b>does not cancel wall damage!</b>

<details>
<summary><b>Details here</b></summary>
The damage reduction increases with the level of the enchantment: <br />
At level 1, the damage reduction is 1.5 half-hearts. <br />
At level 2, the damage reduction is 3.0 half-hearts. <br />
At level 3, the damage reduction is 4.5 half-hearts. <br />
At level 4, the damage reduction is 6.0 half-hearts. <br />
At level 5, the damage reduction completely cancels the damage. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/alighting.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Elytra Item

## TELEKINESIS ENCHANTMENT (I)
The <b>Telekinesis Enchantment</b> teleports the loot dropped by entities at the player position only if the player killed that entity using a weapon equipped with this enchantment: the range of this enchantment is unlimited.  

<details>
<summary><b>Details here</b></summary>
The range of this enchantment is unlimited, which means that every drop will be teleported at the player position. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/telekinesis.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Trident Item
2. Bow Item
3. Crossbow Item

## SKY JUMP ENCHANTMENT (V)
The <b>Sky Jump Enchantment</b> allows the player to perform multiple jumps while already in mid-air: the number of jumps increases with the level of the enchantment and a Leaping Effect can be used to perform higher jumps: at the maximum level, it allows the player to perform 10 mid-air jumps.  

<details>
<summary><b>Details here</b></summary>
The number of available jumps increases with the level of the enchantment: <br />
At level 1, the player can perform 2 mid-air jumps. <br />
At level 2, the player can perform 4 mid-air jumps. <br />
At level 3, the player can perform 6 mid-air jumps. <br />
At level 4, the player can perform 8 mid-air jumps. <br />
At level 5, the player can perform 10 mid-air jumps. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/moon_jump.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Boots Item

This enchantment <b>is not compatible</b> with:
1. Feather Falling Enchantment
2. Long Jump Enchantment

## LONG JUMP ENCHANTMENT (V)
The <b>Long Jump Enchantment</b> allows the player to perform longer jumps when jumping while sprinting: the distance of the jump increases with the level of the enchantment and a Leaping Effect can be used to perform higher jumps.  

<details>
<summary><b>Details here</b></summary>
The distance of the jumps increases with the level of the enchantment: <br />

```bash
strength = level*0.2  
player_velocity.add(normal_jump_velocity * strength)  
```
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/long_jump.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Boots Item

This enchantment <b>is not compatible</b> with:
1. Feather Falling Enchantment
2. Sky Jump Enchantment

## SKYHOOK ENCHANTMENT (IV)
The <b>Skyhook Enchantment</b> introduces a new movement mechanic which connects the player and an arrow with a lead: the player is attached to the arrow and flies following the arrow. Because of this reason, the player needs to have a lead in the inventory for this to work. The player is able to disconnect the lead by unequipping the crossbow or by shooting another arrow (but this requires having another lead inside the inventory). The amount of time the player is attached to the arrow increases with the level: at the maximum level, the duration is 8 seconds.  

<details>
<summary><b>Details here</b></summary>
The amount of time the player is attached to the arrow increases with the level: <br />
At level 1, the player is attached to the arrow for 2 seconds. <br />
At level 2, the player is attached to the arrow for 4 seconds. <br />
At level 3, the player is attached to the arrow for 6 seconds. <br />
At level 4, the player is attached to the arrow for 8 seconds. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/skyhook.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Crossbow Item

## REFLECTION ENCHANTMENT (III)
The <b>Reflection Enchantment</b> makes the shield more reflective towards the projectiles by reflecting them back toward the shooter entity: the reflection force of the shield increases with the level of the enchantment and at the maximum level it provides an almost complete reflection of the projectile.

<details>
<summary><b>Details here</b></summary>
The reflection force of the shield increases with the level of the enchantment: <br />
At level 1, the reflection force is 3 times the default one. <br />
At level 2, the reflection force is 6 times the default one. <br />
At level 3, the reflection force is 9 times the default one. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/reflection.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Shield Item

## SHOCKWAVE ENCHANTMENT (III)
The <b>Shockwave Enchantment</b> adds a new mechanic for the shield by creating a shockwave that starts from the player and propagates in the direction he is looking at. The shockwave pushes away and damages entities but is stopped when encountering solid blocks and non-solid ground: the size, duration, and damage of the shockwave increase with the level of the enchantment.  
<b>The shockwave enchantment ability is activated through the related keybinding</b> while the player is using the shield.  

<details>
<summary><b>Details here</b></summary>
The size, duration, and damage of the shockwave increase with the level of the enchantment: <br />

```bash
size = (1+level) [blocks]
damage = (1.0+level*0.7) [half-hearts]
duration = (1+level) [seconds]
```
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/shockwave.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Shield Item

## COVERAGE ENCHANTMENT (V)
The <b>Coverage Enchantment</b> increases the coverage ability of the shield by increasing the angles at which the shield is able to protect the player: at the maximum level, this enchantment provides full coverage from enemy attacks.

<details>
<summary><b>Details here</b></summary>
The coverage ability of the shield increases linearly with the level of the enchantment: <br />
Normally, the shield covers angles from -90° to +90° from the user's look direction. <br />
At level 1, the shield covers angles from -108° to +108° from the user's look direction. <br />
At level 2, the shield covers angles from -126° to +126° from the user's look direction. <br />
At level 3, the shield covers angles from -144° to +144° from the user's look direction. <br />
At level 4, the shield covers angles from -162° to +162° from the user's look direction. <br />
At level 5, the shield covers angles from -180° to +180° from the user's look direction. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/coverage.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Shield Item

## EVERLASTING ENCHANTMENT (I)
The <b>Everlasting Enchantment</b> makes the enchanted item never despawn.

<details>
<summary><b>Details here</b></summary>
The everlasting enchantment makes an item never despawn but it is still possible that the item might be destroyed in other ways (lava). <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/everlasting.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Any breakable item

## ZERO GRAVITY ENCHANTMENT (I)
The <b>Zero Gravity Enchantment</b> makes the shot projectiles have no gravity. </br>

<details>
<summary><b>Details here</b></summary>
Since the shot projectile has no gravity, they fly in a straight line allowing for more accuracy. Notice that Minecraft adds a bit of randomized inaccuracy to the shot projectiles which is the reason why they might not land exactly where the player is looking at. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/zero_gravity.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Hoe item

## DREAMLIKE ENCHANTMENT (III)
The <b>Dreamlike Enchantment</b> decreases the probability of Phantoms spawning because of player insomnia: the insomnia reduction increases with the level of the enchantments and at the maximum level prevents completely the spawning of Phantoms.

<details>
<summary><b>Details here</b></summary>
The insomnia amount decreases with the level of the enchantment. <br />
At the maximum level, the reduction is total and the Phantoms cannot spawn: <br />
At level 1, the insomnia reduction amount is 33.3%. <br />
At level 2, the insomnia reduction amount is 66.6%. <br />
At level 3, the insomnia reduction amount is 100.0%. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/dreamlike.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Helmet item

## GLUTTONY ENCHANTMENT (III)
The <b>Gluttony Enchantment</b> makes the player automatically eat food from the inventory when hungry: the level of enchantments determines the efficiency of enchantment in terms of consumed foot level. When food is not available, it makes a stomach-rumbling sound to inform the player.</br>

<details>
<summary><b>Details here</b></summary>
When the level of the enchantment is not the maximum, the food amount obtained by the food item is reduced. <br />
This means that if the food would give 10 food levels, at level 1, it will provide only 7 food levels. <br />
At level 1, the food amount obtained is 70.0%. <br />
At level 2, the food amount obtained is 85.0%. <br />
At level 3, the food amount obtained is 100.0%. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/gluttony.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Helmet item

## MULTI ARROW ENCHANTMENT (V)
The <b>Multi Arrow Enchantment</b> makes the bow fire multiple arrows in the same shot: the number of arrows that are shot simultaneously increases with the level of enchantment. The player must have the additional arrows in the inventory.</br>

<details>
<summary><b>Details here</b></summary>
The number of arrows per shot increases with the level of the enchantment: <br />
At level 1, there is 1 additional arrow. <br />
At level 2, there are 2 additional arrows. <br />
At level 3, there are 3 additional arrows. <br />
At level 4, there are 4 additional arrows. <br />
At level 5, there are 5 additional arrows. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/multi_arrow.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Bow item

## SCYTHING ENCHANTMENT (III)
The <b>Scything Enchantment</b> makes the enchanted tool break crops in a square: the side of the square increases with the level of the enchantment. </br>

<details>
<summary><b>Details here</b></summary>
The side of the square increases with the level of the enchantment: <br />
At level 1, the side of the square is 3 blocks. <br />
At level 2, the side of the square is 5 blocks. <br />
At level 3, the side of the square is 7 blocks. <br />
<br />
<p>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/scything.gif?raw=true" alt="">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Hoe item
