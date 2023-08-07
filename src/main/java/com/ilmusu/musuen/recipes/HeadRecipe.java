package com.ilmusu.musuen.recipes;

import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeadRecipe
{
    // The mob associated to this recipe, there should be only one
    protected final Identifier mobIdentifier;
    // The heads associated to the mob, there could be more than one
    protected Set<Identifier> headsIdentifierSet;
    // The finalized list of heads
    protected List<Identifier> headsIdentifier;

    public HeadRecipe(Identifier mobIdentifier)
    {
        this.mobIdentifier = mobIdentifier;
        this.headsIdentifierSet = new HashSet<>();

    }

    public void freeze()
    {
        this.headsIdentifier = headsIdentifierSet.stream().toList();
        this.headsIdentifierSet = null;
    }

    public void addHead(Identifier headItemIdentifier)
    {
        this.headsIdentifierSet.add(headItemIdentifier);
    }

    public List<Identifier> getHeads()
    {
        return this.headsIdentifier;
    }
}
