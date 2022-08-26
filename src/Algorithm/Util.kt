package Algorithm

import org.paukov.combinatorics3.Generator

object Util {

    fun getIndexList_Recursive(firstIndex: Int, lastIndex: Int): List<List<Int>> {

        val list = mutableListOf<String>()
        val output = StringBuilder()

        fun createInputString(): String{
            return buildString {
                for (i in firstIndex+1 until lastIndex)
                    this.append(i.toString())
            }
        }

        fun findStringCombination(inputString: String, start: Int = 0){
            for (i in start until inputString.length) {
                output.append(inputString[i])
                list.add(firstIndex.toString() + output.toString() + lastIndex)
                if (i < inputString.length) findStringCombination(inputString, i + 1)
                output.setLength(output.length - 1)
            }
        }

        val x = createInputString()


        findStringCombination( createInputString() )
        return list.apply { add(firstIndex.toString() + lastIndex.toString()) }.map { string -> string.toList().map { it.toString().toInt() } }
    }

    fun getIndexList(firstIndex: Int,lastIndex: Int): List<List<Int>>{

        val list = mutableListOf<MutableList<Int>>()
        val initialList = (firstIndex..lastIndex).toList()

        for (i in 2 .. initialList.size){
            Generator.combination(initialList)
                .simple(i)
                .filter { it[0] == initialList[0] && it[i-1] == initialList[initialList.size-1] }
                .stream()
                .forEach { list.add(it) }
        }

        return list

    }


}