# ILMUSU ENCHANTMENTS MOD
This mod adds <b>more enchantments</b> to Minecraft.  
It also includes a new mechanic, referred to as <b><span style="color:purple">Demonic Enchanting</span></b>, for making enchanting a little bit more interesting without twisting the game too much. Currently, there is no way of disabling this mechanic and I will consider making it possible based on users review.  

## DEMONIC ENCHANTING
While enchanting items or books, you might discover <b><span style="color:purple">demonic enchantments</span></b> : 

<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/demonic_enchanting_1.png?raw=true">
	<br>
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/demonic_enchanting_2.png?raw=true">
</p>

These enchantments require, not only experience levels and lapislazuli, but also a <b><span style="color:red">sacrifice</span></b>: this means that around the Enchanting Table (in a radius of 5 blocks) there should be enough living entities to drawn the health requested by the enchantment. For example in the second image, 40 hearts needs to be consumed and a sheep has only 4 hearts, this means that 10 sheeps are necessary to apply the <b><span style="color:purple">demonic enchantment</span></b> on the item.  
Notice that when there are not enough living entities aroung the enchanting table, <b>the health is drawn from the player</b> who might also get killed!  
  
If the health requirement is satisfied, you will obtain the <b><span style="color:purple">demonic enchantment</span></b>:  

<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/images/book_with_demonic_enchantment.png?raw=true">
</p>

## LACERATION ENCHANTMENT (V)
The <b><span style="color:purple">Laceration Enchantment</span></b> provides additional damage for weapon tools. Since it is a demonic enchantment, attacking an entity with an item containing this enchantment will damage also the player: at the maximum level, it will provide +37.5 additional damage and consume 25% of the player maxium health.  

<details>
<summary><b>Details here</b></summary>
This enchantment produces more damage the more the health it can consume from the player. <br />
At level 1, it consumes 10.00% of the player health and produces +6.5 additional damage. <br />
At level 2, it consumes 13.75% of the player health and produces +12.0 additional damage.  <br />
At level 3, it consumes 17.50% of the player health and produces +19.0 additional damage. <br />
At level 4, it consumes 21.25% of the player health and produces +27.5 additional damage. <br />
At level 5, it consumes 25.00% of the player health and produces +37.6 additional damage. <br />
An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/laceration.gif?raw=true">
</p>
</details>

This enchantment <b>is not compatible</b> with:
1. Sharpness Enchantment

## SKEWERING ENCHANTMENT (V)
The <b><span style="color:purple">Skewering Enchantment</span></b> provides additional damage for the trident both in the case of melee attack and ranged attack. Since it is a demonic enchantment, attacking an entity with an item containing this enchantment will damage also the player: at the maximum level, it will provide +37.5 additional damage and consume 25% of the player maxium health.  

<details>
<summary><b>Details here</b></summary>
This enchantment produces more damage the more the health it can consume from the player. <br />
At level 1, it consumes 10.00% of the player health and produces +6.5 additional damage. <br />
At level 2, it consumes 13.75% of the player health and produces +12.0 additional damage.  <br />
At level 3, it consumes 17.50% of the player health and produces +19.0 additional damage. <br />
At level 4, it consumes 21.25% of the player health and produces +27.5 additional damage. <br />
At level 5, it consumes 25.00% of the player health and produces +37.6 additional damage. <br />
An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/skewering.gif?raw=true">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Axe Item
2. Sword Item

This enchantment <b>is not compatible</b> with:
1. Impaling Enchantment
2. Overcharge Enchantment

## UNEARTHING ENCHANTMENT (V)
The <b><span style="color:purple">Unearthing Enchantment</span></b> makes a digger tool dig tunnels with the depth and area depending on the level of the enchantments. Since it is a demonic enchantment, digging with an item containing this enchantment will damage also the player: the tunnel will be complete only if the player has enough life.  

<details>
<summary><b>Details here</b></summary>
At level 1, the tunnel will have an area of 3x3 blocks and a depth of 1 block. <br />
At level 2, the tunnel will have an area of 3x3 blocks and a depth of 3 block. <br />
At level 3, the tunnel will have an area of 3x3 blocks and a depth of 4 block. <br />
At level 4, the tunnel will have an area of 3x3 blocks and a depth of 6 block. <br />
At level 5, the tunnel will have an area of 3x3 blocks and a depth of 7 block. <br />
An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/unearthing.gif?raw=true">
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
The <b><span style="color:purple">Overcharged Enchantment</span></b> makes the chargeable items to be overchargable and perform additional damage. Since it is a demonic enchantment, overcharging an item containing this enchantment will damage also the player.

<details>
<summary><b>Details here</b></summary>
The overcharging logic starts after the item has been fully charged: if the player continues to charge the item, he will start taking a heart of damage every 10 ticks (0.5 seconds). The projectile will make additional damage depending on the level of the enchantment and the total damage that the player endured. In particular, the final damage is computed as:  

```bash
additional_damage = player_endured_damage*(0.3 + enchantment_level * 0.2)
```

An important notice is that it never kills the player, <b>leaving him at half a heart</b>. <br />
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/overcharged.gif?raw=true">
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
The <b><span style="color:purple">Phasing Enchantment</span></b> makes the player able to teleport: it provides the ability of both teleporting where the player is looking at and teleporting behind walls.  Since it is a demonic enchantment, overcharging an item containing this enchantment will damage also the player.

<details>
<summary><b>Details here</b></summary>
The phasing enchantment ability is activated through the related keybinding. <br />
When the ability is activated, the logic performs a raycasting in the direction the player is looking at and, if a wall is found, it tries to teleport the player behind that wall. Instead, if a wall is not found, the player is teleported in the direction he was looking at with a distance depending on the current level of the enchantment.

An important notice is that <b>this kills the player if he does not have enough health!</b> <br />
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/phasing.gif?raw=true">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Leggings Item

## REACHING ENCHANTMENT (IV)
The <b>Reaching Enchantment</b> increases the reach of player making him able to interact with blocks and entities that would normally be out of reach.  

<details>
<summary><b>Details here</b></summary>
The reaching echantment increases the player reach by one block for every level. <br />
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/reaching.gif?raw=true">
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
The <b>Pocketed Enchantment</b> adds pockets to leggings to provide additional inventory storage for the player.

<details>
<summary><b>Details here</b></summary>
The size of the pockets increase with the level of the enchantment: at level 1, the pockets provide 4 slots and then 4 additional slots for every level.
<br />
<p align="center">
	<img src="https://github.com/IlMusu/IlMusuEnchantmentsMod/blob/documentation/gifs/pocketed.gif?raw=true">
</p>
</details>

This enchantment <b>can be applied to</b>:
1. Leggings Item
