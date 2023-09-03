import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.hfad.ansormarket.models.ItemType

class FirestoreItemTypeConverter {

    @PropertyName("category")
    fun fromItemType(itemType: ItemType): String {
        return itemType.name
    }

    @Exclude
    fun toItemType(itemType: String): ItemType {
        return ItemType.valueOf(itemType)
    }
}