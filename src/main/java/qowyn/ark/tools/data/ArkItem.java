package qowyn.ark.tools.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.json.JsonObject;

import qowyn.ark.GameObject;
import qowyn.ark.PropertyContainer;
import qowyn.ark.arrays.ArkArrayUInt64;
import qowyn.ark.data.ExtraDataZero;
import qowyn.ark.properties.PropertyArray;
import qowyn.ark.properties.PropertyBool;
import qowyn.ark.properties.PropertyByte;
import qowyn.ark.properties.PropertyDouble;
import qowyn.ark.properties.PropertyFloat;
import qowyn.ark.properties.PropertyInt16;
import qowyn.ark.properties.PropertyInt;
import qowyn.ark.properties.PropertyObject;
import qowyn.ark.properties.PropertyStr;
import qowyn.ark.properties.PropertyStruct;
import qowyn.ark.properties.PropertyUInt16;
import qowyn.ark.properties.PropertyUInt32;
import qowyn.ark.structs.StructPropertyList;
import qowyn.ark.structs.StructVector;
import qowyn.ark.types.ArkByteValue;
import qowyn.ark.types.ArkName;
import qowyn.ark.types.ObjectReference;

public class ArkItem {

  private static final int COLOR_SLOT_COUNT = 6;
  
  public long itemId;

  public boolean canEquip;

  public boolean canSlot;

  public boolean isEngram;

  public boolean isBlueprint;

  public boolean canRemove;

  public boolean canRemoveFromCluster;

  public boolean isHidden;

  public ArkName className;

  public String blueprintGeneratedClass;

  public int quantity;

  public String customName;

  public String customDescription;

  public float durability;

  public float rating;

  public byte quality;

  public final short[] itemStatValues = new short[ItemStatDefinitions.size()];

  public final short[] itemColors = new short[COLOR_SLOT_COUNT];

  public final short[] preSkinItemColors = new short[COLOR_SLOT_COUNT];

  public final byte[] eggLevelups = new byte[AttributeNames.size()];

  public final byte[] eggColors = new byte[COLOR_SLOT_COUNT];

  public int uploadOffset;

  public ArkItem() {
    canEquip = true;
    canSlot = true;
    canRemove = true;
    canRemoveFromCluster = true;
    quantity = 1;
    customName = "";
    customDescription = "";
  }

  /**
   * From ArkSavegame
   */
  public ArkItem(GameObject item) {
    className = item.getClassName();

    canEquip = item.findPropertyValue("bAllowEquppingItem", Boolean.class).orElse(true);
    canSlot = item.findPropertyValue("bCanSlot", Boolean.class).orElse(true);
    isEngram = item.findPropertyValue("bIsEngram", Boolean.class).orElse(false);
    isBlueprint = item.findPropertyValue("bIsBlueprint", Boolean.class).orElse(false);
    canRemove = item.findPropertyValue("bAllowRemovalFromInventory", Boolean.class).orElse(true);
    canRemoveFromCluster = true;
    isHidden = item.findPropertyValue("bHideFromInventoryDisplay", Boolean.class).orElse(false);

    quantity = Math.max(1, item.findPropertyValue("ItemQuantity", Number.class).map(Number::intValue).orElse(1));

    customName = item.findPropertyValue("CustomItemName", String.class).orElse("");

    customDescription = item.findPropertyValue("CustomItemDescription", String.class).orElse("");

    durability = item.findPropertyValue("SavedDurability", Float.class).orElse(0.0f);

    rating = item.findPropertyValue("ItemRating", Float.class).orElse(0.0f);

    quality = item.findPropertyValue("ItemQualityIndex", ArkByteValue.class).map(ArkByteValue::getByteValue).orElse((byte) 0);

    for (int i = 0; i < ItemStatDefinitions.size(); i++) {
      itemStatValues[i] = item.findPropertyValue("ItemStatValues", Short.class, i).orElse((short) 0);
    }

    for (int i = 0; i < itemColors.length; i++) {
      itemColors[i] = item.findPropertyValue("ItemColorID", Short.class, i).orElse((short) 0);
    }

    for (int i = 0; i < preSkinItemColors.length; i++) {
      preSkinItemColors[i] = item.findPropertyValue("PreSkinItemColorID", Short.class, i).orElse((short) 0);
    }

    for (int i = 0; i < eggLevelups.length; i++) {
      eggLevelups[i] = item.findPropertyValue("EggNumberOfLevelUpPointsApplied", ArkByteValue.class, i).map(ArkByteValue::getByteValue).orElse((byte) 0);
    }

    for (int i = 0; i < eggColors.length; i++) {
      eggColors[i] = item.findPropertyValue("EggColorSetIndices", ArkByteValue.class, i).map(ArkByteValue::getByteValue).orElse((byte) 0);
    }
  }

  /**
   * From cluster storage
   */
  public ArkItem(PropertyContainer item) {
    blueprintGeneratedClass = item.getPropertyValue("ItemArchetype", ObjectReference.class).getObjectString().toString();
    className = ArkName.from(blueprintGeneratedClass.substring(blueprintGeneratedClass.lastIndexOf('.') + 1));

    canEquip = true;
    canSlot = item.findPropertyValue("bIsSlot", Boolean.class).orElse(true);
    isEngram = item.findPropertyValue("bIsEngram", Boolean.class).orElse(false);
    isBlueprint = item.findPropertyValue("bIsBlueprint", Boolean.class).orElse(false);
    canRemove = item.findPropertyValue("bAllowRemovalFromInventory", Boolean.class).orElse(true);
    canRemoveFromCluster = item.findPropertyValue("bAllowRemovalFromSteamInventory", Boolean.class).orElse(true);
    isHidden = item.findPropertyValue("bHideFromInventoryDisplay", Boolean.class).orElse(false);

    quantity = Math.max(1, item.findPropertyValue("ItemQuantity", Number.class).map(Number::intValue).orElse(1));

    customName = item.findPropertyValue("CustomItemName", String.class).orElse("");

    customDescription = item.findPropertyValue("CustomItemDescription", String.class).orElse("");

    durability = item.findPropertyValue("ItemDurability", Float.class).orElse(0.0f);

    rating = item.findPropertyValue("ItemRating", Float.class).orElse(0.0f);

    quality = item.findPropertyValue("ItemQualityIndex", ArkByteValue.class).map(ArkByteValue::getByteValue).orElse((byte) 0);

    for (int i = 0; i < itemStatValues.length; i++) {
      itemStatValues[i] = item.findPropertyValue("ItemStatValues", Short.class, i).orElse((short) 0);
    }

    for (int i = 0; i < itemColors.length; i++) {
      itemColors[i] = item.findPropertyValue("ItemColorID", Short.class, i).orElse((short) 0);
    }

    for (int i = 0; i < preSkinItemColors.length; i++) {
      preSkinItemColors[i] = item.findPropertyValue("PreSkinItemColorID", Short.class, i).orElse((short) 0);
    }

    for (int i = 0; i < eggLevelups.length; i++) {
      eggLevelups[i] = item.findPropertyValue("EggNumberOfLevelUpPointsApplied", ArkByteValue.class, i).map(ArkByteValue::getByteValue).orElse((byte) 0);
    }

    for (int i = 0; i < eggColors.length; i++) {
      eggColors[i] = item.findPropertyValue("EggColorSetIndices", ArkByteValue.class, i).map(ArkByteValue::getByteValue).orElse((byte) 0);
    }
  }

  /**
   * From JSON / ModificationFile
   */
  public ArkItem(JsonObject object) {
    className = ArkName.from(object.getString("className"));
    blueprintGeneratedClass = "BlueprintGeneratedClass " + object.getString("blueprintGeneratedClass", "");

    canEquip = object.getBoolean("canEquip", true);
    canSlot = object.getBoolean("canSlot", true);
    isEngram = object.getBoolean("isEngram", false);
    isBlueprint = object.getBoolean("isBlueprint", false);
    canRemove = object.getBoolean("canRemove", true);
    canRemoveFromCluster = object.getBoolean("canRemoveFromCluster", true);
    isHidden = object.getBoolean("isHidden", false);

    quantity = Math.max(1, object.getInt("quantity", 1));

    customName = object.getString("customName", "");

    customDescription = object.getString("customDescription", "");

    // Getting a float with a default value using JSR 353? Eaaaaasy
    durability = Optional.ofNullable(object.getJsonNumber("durability")).map(n -> n.bigDecimalValue().floatValue()).orElse(0.0f);
    rating = Optional.ofNullable(object.getJsonNumber("rating")).map(n -> n.bigDecimalValue().floatValue()).orElse(0.0f);

    quality = (byte) object.getInt("quality", 0);

    for (int i = 0; i < itemStatValues.length; i++) {
      itemStatValues[i] = (short) object.getInt("itemStatsValue_" + i, 0);
    }

    for (int i = 0; i < itemColors.length; i++) {
      itemColors[i] = (short) object.getInt("itemColor_" + i, 0);
    }

    for (int i = 0; i < preSkinItemColors.length; i++) {
      preSkinItemColors[i] = (short) object.getInt("preSkinItemColor_" + i, 0);
    }

    for (int i = 0; i < eggLevelups.length; i++) {
      eggLevelups[i] = (byte) object.getInt("eggLevelup_" + i, 0);
    }

    for (int i = 0; i < eggColors.length; i++) {
      eggColors[i] = (byte) object.getInt("eggColor_" + i, 0);
    }

    uploadOffset = object.getInt("uploadOffset", 0);
  }

  public StructPropertyList toClusterData() {
    if (blueprintGeneratedClass.equals("BlueprintGeneratedClass ")) {
      System.err.println("Item " + className + " is missing blueprintGeneratedClass.");
      return null;
    }

    StructPropertyList result = new StructPropertyList();
    StructPropertyList arkTributeItem = new StructPropertyList();

    result.getProperties().add(new PropertyStruct("ArkTributeItem", arkTributeItem, ArkName.from("ItemNetInfo")));

    ObjectReference itemArchetype = new ObjectReference();
    itemArchetype.setObjectType(ObjectReference.TYPE_PATH);
    itemArchetype.setObjectString(ArkName.from(blueprintGeneratedClass));
    arkTributeItem.getProperties().add(new PropertyObject("ItemArchetype", itemArchetype));

    Random random = new Random();
    long randomId = random.nextLong();

    StructPropertyList struct = new StructPropertyList();

    struct.getProperties().add(new PropertyUInt32("ItemID1", (int) (randomId >> 32)));
    struct.getProperties().add(new PropertyUInt32("ItemID2", (int) randomId));

    arkTributeItem.getProperties().add(new PropertyStruct("ItemId", struct, ArkName.from("ItemNetID")));

    arkTributeItem.getProperties().add(new PropertyBool("bIsBlueprint", isBlueprint));
    arkTributeItem.getProperties().add(new PropertyBool("bIsEngram", isEngram));
    arkTributeItem.getProperties().add(new PropertyBool("bIsCustomRecipe", false));
    arkTributeItem.getProperties().add(new PropertyBool("bIsFoodRecipe", false));
    arkTributeItem.getProperties().add(new PropertyBool("bIsRepairing", false));
    arkTributeItem.getProperties().add(new PropertyBool("bAllowRemovalFromInventory", canRemove));
    arkTributeItem.getProperties().add(new PropertyBool("bAllowRemovalFromSteamInventory", canRemoveFromCluster));
    arkTributeItem.getProperties().add(new PropertyBool("bHideFromInventoryDisplay", isHidden));
    arkTributeItem.getProperties().add(new PropertyBool("bFromSteamInventory", false));
    arkTributeItem.getProperties().add(new PropertyBool("bIsFromAllClustersInventory", false));
    arkTributeItem.getProperties().add(new PropertyBool("bIsEquipped", false));
    arkTributeItem.getProperties().add(new PropertyBool("bIsSlot", canSlot));
    arkTributeItem.getProperties().add(new PropertyUInt32("ExpirationTimeUTC", 0));
    arkTributeItem.getProperties().add(new PropertyUInt32("ItemQuantity", quantity - 1));
    arkTributeItem.getProperties().add(new PropertyFloat("ItemDurability", durability));
    arkTributeItem.getProperties().add(new PropertyFloat("ItemRating", rating));

    arkTributeItem.getProperties().add(new PropertyByte("ItemQualityIndex", quality));

    for (int i = 0; i < itemStatValues.length; i++) {
      arkTributeItem.getProperties().add(new PropertyUInt16("ItemStatValues", i, itemStatValues[i]));
    }

    for (int i = 0; i < itemColors.length; i++) {
      arkTributeItem.getProperties().add(new PropertyInt16("ItemColorID", i, itemColors[i]));
    }

    ObjectReference itemCustomClass = new ObjectReference();
    itemCustomClass.setLength(8);
    itemCustomClass.setObjectId(-1);
    itemCustomClass.setObjectType(ObjectReference.TYPE_ID);
    arkTributeItem.getProperties().add(new PropertyObject("ItemCustomClass", itemCustomClass));

    ObjectReference itemSkinTemplate = new ObjectReference();
    itemSkinTemplate.setLength(8);
    itemSkinTemplate.setObjectId(-1);
    itemSkinTemplate.setObjectType(ObjectReference.TYPE_ID);
    arkTributeItem.getProperties().add(new PropertyObject("ItemSkinTemplate", itemSkinTemplate));

    arkTributeItem.getProperties().add(new PropertyFloat("CraftingSkill", 0.0f));

    arkTributeItem.getProperties().add(new PropertyStr("CustomItemName", customName));
    arkTributeItem.getProperties().add(new PropertyStr("CustomItemDescription", customDescription));

    // TODO: add other values

    arkTributeItem.getProperties().add(new PropertyDouble("NextSpoilingTime", 0.0));
    arkTributeItem.getProperties().add(new PropertyDouble("LastSpoilingTime", 0.0));

    ObjectReference lastOwnerPlayer = new ObjectReference();
    lastOwnerPlayer.setLength(4);
    lastOwnerPlayer.setObjectId(-1);
    lastOwnerPlayer.setObjectType(ObjectReference.TYPE_ID);
    arkTributeItem.getProperties().add(new PropertyObject("LastOwnerPlayer", lastOwnerPlayer));

    arkTributeItem.getProperties().add(new PropertyDouble("LastAutoDurabilityDecreaseTime", 0.0));
    arkTributeItem.getProperties().add(new PropertyStruct("OriginalItemDropLocation", new StructVector(), ArkName.from("Vector")));

    for (int i = 0; i < preSkinItemColors.length; i++) {
      arkTributeItem.getProperties().add(new PropertyInt16("PreSkinItemColorID", i, preSkinItemColors[i]));
    }

    for (int i = 0; i < eggLevelups.length; i++) {
      arkTributeItem.getProperties().add(new PropertyByte("EggNumberOfLevelUpPointsApplied", i, eggLevelups[i]));
    }

    arkTributeItem.getProperties().add(new PropertyFloat("EggTamedIneffectivenessModifier", 0.0f));

    for (int i = 0; i < eggColors.length; i++) {
      arkTributeItem.getProperties().add(new PropertyByte("EggColorSetIndices", i, eggColors[i]));
    }

    arkTributeItem.getProperties().add(new PropertyByte("ItemVersion", (byte) 0));
    arkTributeItem.getProperties().add(new PropertyInt("CustomItemID", 0));
    arkTributeItem.getProperties().add(new PropertyArray("SteamUserItemID", new ArkArrayUInt64()));

    result.getProperties().add(new PropertyFloat("Version", 2.0f));
    result.getProperties().add(new PropertyInt("UploadTime", (int) Instant.now().plusSeconds(uploadOffset).getEpochSecond()));

    return result;
  }

  public GameObject toGameObject(Collection<GameObject> existingObjects, int ownerInventory) {
    GameObject object = new GameObject();

    object.setClassName(className);

    if (!canEquip) {
      object.getProperties().add(new PropertyBool("bAllowEquppingItem", canEquip));
    }

    if (!canSlot) {
      object.getProperties().add(new PropertyBool("bCanSlot", canSlot));
    }

    if (isEngram) {
      object.getProperties().add(new PropertyBool("bIsEngram", isEngram));
    }

    if (isBlueprint) {
      object.getProperties().add(new PropertyBool("bIsBlueprint", isBlueprint));
    }

    if (!canRemove) {
      object.getProperties().add(new PropertyBool("bAllowRemovalFromInventory", canRemove));
    }

    if (isHidden) {
      object.getProperties().add(new PropertyBool("bHideFromInventoryDisplay", isHidden));
    }

    if (quantity != 1) {
      object.getProperties().add(new PropertyInt("ItemQuantity", quantity));
    }

    if (!customName.isEmpty()) {
      object.getProperties().add(new PropertyStr("CustomItemName", customName));
    }

    if (!customDescription.isEmpty()) {
      object.getProperties().add(new PropertyStr("CustomItemDescription", customDescription));
    }

    if (durability > 0) {
      object.getProperties().add(new PropertyFloat("SavedDurability", durability));
    }

    if (quality > 0) {
      object.getProperties().add(new PropertyByte("ItemQualityIndex", quality));
    }

    for (int i = 0; i < itemStatValues.length; i++) {
      if (itemStatValues[i] != 0) {
        object.getProperties().add(new PropertyUInt16("ItemStatValues", i, itemStatValues[i]));
      }
    }

    Set<Long> itemIDs = new HashSet<>(); // Stored as StructPropertyList with 2 UInt32
    Set<ArkName> names = new HashSet<>();

    for (GameObject existingObject : existingObjects) {
      existingObject.getNames().forEach(names::add);

      PropertyContainer itemID = existingObject.getPropertyValue("ItemId", PropertyContainer.class);
      if (itemID != null) {
        Integer itemID1 = itemID.getPropertyValue("ItemID1", Integer.class);
        Integer itemID2 = itemID.getPropertyValue("ItemID2", Integer.class);
        if (itemID1 != null && itemID2 != null) {
          long id = (long) itemID1 << Integer.SIZE | (itemID2 & 0xFFFFFFFFL);
          itemIDs.add(id);
          continue;
        }
      }
    }

    Random random = new Random();

    Function<ArkName, ArkName> findFreeName = name -> {
      for (int i = 1; i < Integer.MAX_VALUE; i++) {
        ArkName tempName = ArkName.from(name.getName(), i);
        if (!names.contains(tempName)) {
          return tempName;
        }
      }

      throw new Error("This is insane.");
    };

    long randomId = random.nextLong();
    while (itemIDs.contains(randomId)) {
      randomId = random.nextLong();
    }

    StructPropertyList struct = new StructPropertyList();

    struct.getProperties().add(new PropertyUInt32("ItemID1", (int) (randomId >> 32)));
    struct.getProperties().add(new PropertyUInt32("ItemID2", (int) randomId));

    object.getProperties().add(new PropertyStruct("ItemId", struct, ArkName.from("ItemNetID")));

    object.setNames(new ArrayList<>());
    object.getNames().add(findFreeName.apply(className));

    object.setItem(true);

    ObjectReference ownerInventoryReference = new ObjectReference();
    ownerInventoryReference.setLength(8);
    ownerInventoryReference.setObjectId(ownerInventory);
    ownerInventoryReference.setObjectType(ObjectReference.TYPE_ID);

    object.getProperties().add(new PropertyObject("OwnerInventory", ownerInventoryReference));
    object.setExtraData(new ExtraDataZero());

    return object;
  }

}
