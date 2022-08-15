package Algorithm

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

class Algorithm(private val filePath: String) {

    private val dataModelList = mutableListOf<DataModel>()
    private lateinit var initialMatrix: Array<Array<Pair<InternalUtility, CrossUtility>>>
    private var initialMatrixColumnCount = 0

    init {
        readFileUsingBufferedReader(filePath)
        makeInitialMatrix()
        calculateEachMatrixColumnSum()
        reorderInitialMatrix()
    }

    private fun readFileUsingBufferedReader(filePath: String) {
        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    if (line!!.isEmpty() || line!![0] == '#' || line!![0] == '%' || line!![0] == '@') {
                        continue
                    }

                    val split = line!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val internalItems = split[0].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val crossItems = split[2].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    dataModelList.add(
                        DataModel().apply {
                            sumUtility = split[1].toInt()

                            // internal
                            for (i in internalItems.indices){
                                utilityList.add(Pair(InternalUtility(internalItems[i].toInt()), CrossUtility(crossItems[i].toInt()) ))
                                if (utilityList.size > initialMatrixColumnCount)
                                    initialMatrixColumnCount = utilityList.size
                            }
                        }
                    )

                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }


    private fun makeInitialMatrix() {

        fun initialMatrixValue(i: Int) = Pair(InternalUtility(0),CrossUtility(0))


        if (dataModelList.isEmpty()){
            println("data model list is empty")
            return
        }

        val firstRowValueList = dataModelList.flatMap { it.utilityList.map { it.first.value } }.distinct().sorted()
        val initialMatrixFirstRowList = firstRowValueList.map { it-> Pair(InternalUtility(it), CrossUtility(0)) }
        initialMatrixColumnCount = initialMatrixFirstRowList.size
        initialMatrix = Array(dataModelList.size + 2) { Array(initialMatrixColumnCount, ::initialMatrixValue) }


        // add first row into matrix
        for (i in 0 until initialMatrixColumnCount)
            initialMatrix[0][i] = initialMatrixFirstRowList[i]

        // add other items based on first row
        for (i in 1 .. dataModelList.size)
            for (j in 0 until dataModelList[i-1].utilityList.size)
                initialMatrix[i][ firstRowValueList.indexOf(dataModelList[i-1].utilityList[j].first.value) ] = dataModelList[i-1].utilityList[j]

    }

    private fun  calculateEachMatrixColumnSum(){

        var sum = 0
        for (i in 0 until initialMatrixColumnCount){
            for (j in 1 until initialMatrix.size){
                sum += initialMatrix[j][i].second.value
                initialMatrix[j][i] = Pair(InternalUtility(sum),CrossUtility(0))
            }
            sum = 0
        }

    }

    private fun reorderInitialMatrix() {
        val list = initialMatrix[dataModelList.size].map { it.first.value }.toList()
        val orderedList = list.sorted()

        swapMatrixColumn(0,1)


     /*   for( i in list.indices){

        }*/


        println()
    }



    private fun swapMatrixColumn(ci: Int, cj: Int){
        for (i in 1 ..  dataModelList.size){
            val tmpPair = initialMatrix[i][ci]
            initialMatrix[i][ci] = initialMatrix[i][cj]
            initialMatrix[i][cj] = tmpPair

        }
        println()
    }

}