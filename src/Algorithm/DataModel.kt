package Algorithm

@JvmInline
value class InternalUtility(val value: Int)

@JvmInline
value class CrossUtility(val value: Int)

class DataModel{
   var utilityList: MutableList<Pair<InternalUtility,CrossUtility>> = mutableListOf()
   var sumUtility: Int = -1
}