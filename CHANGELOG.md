# 3.1.1+1.20.1

> ### ⚠️ Read this before updating
>
> This release is a **major technical overhaul and is not backwards compatible.**
>
> - **Requires the matching Spell Engine and More RPG Library releases.** This version will not run on
>   Spell Engine **0.9.x**, and mods built against 0.9.x will not work alongside it.
> - **Update the whole set together.** Spell Engine, More RPG Library and every RPG Series mod must be on
>   matching versions. Mixing in an older add-on will break at startup or misbehave in play.
> - **Spell books must be re-obtained.** Spell books from an older world no longer carry valid
>   spell data. Re-craft them, or re-bind their spells at the Spell Binding Table.
>
> **Back up your world before updating.**

- Thanks to Daedelus for the PR!
- Ported to Minecraft 1.20.1 (Fabric + Forge 47). NeoForge is replaced by Forge on this line; the same
  Forge jar also loads on NeoForge 1.20.1.
- Requires the matching 1.20.1 releases of Spell Engine (1.10.5), Spell Power (1.6.0), More RPG Library
  (2.7.2) and Armor Model API (1.0.0).
- Every registry write goes through Forge's `RegisterEvent` window, so the mod also boots on Forge 47.0-47.3
  and on NeoForge 1.20.1, which never unlock the vanilla registries.
- The Norse Warlord armor is now always registered, so a server without Armory RPGs starts (its set bonus
  used to fail to load and abort the startup). Crafting it still requires Armory RPGs.

### Accepted 1.20.1 limitations

- Blood Sacrifice and Blood Reckoning lose their max-absorption modifiers (1.20.1 has no such attribute);
  absorption is uncapped there instead, and the amount each effect grants is unchanged.
- Blood Sacrifice now runs its full duration instead of ending as soon as the absorption is used up - the
  effect icon lingers, nothing else changes.
- Bloody Strike's tooltip states its absorption value directly instead of reading it back from the effect's
  modifier, so that number is no longer config-driven.
- The Berserker armour sets and raid axes are no longer listed in the 1.21-only armour-slot and
  enchantable item tags; on 1.20.1 enchantability comes from the enchantments themselves, so Unbreaking,
  Sharpness and friends still apply. The Warlord set opts into `#minecraft:trimmable_armor` explicitly to
  keep smithing-table trims working.
