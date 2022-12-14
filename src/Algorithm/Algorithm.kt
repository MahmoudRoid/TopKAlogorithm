package Algorithm

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

class Algorithm(private val filePath: String, private val k: Int) {

    private val dataModelList = mutableListOf<DataModel>()
    private lateinit var initialMatrix: Array<Array<Pair<InternalUtility, CrossUtility>>>
    private var initialMatrixColumnCount = 0
    private val mainMatrix by lazy {
        Array(initialMatrixColumnCount) { IntArray(initialMatrixColumnCount) }
    }

    init {
        val startTime = System.currentTimeMillis()

        readFileUsingBufferedReader(filePath)
        makeInitialMatrix()
        calculateEachInitialMatrixColumnSum()
        reorderInitialMatrix()
        createMainMatrix()
        calculateTopK()

        println("time taken is:  ${System.currentTimeMillis() - startTime} ms")
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

    private fun  calculateEachInitialMatrixColumnSum(){

        var sum = 0
        for (i in 0 until initialMatrixColumnCount){
            for (j in 1 until initialMatrix.size){
                sum += initialMatrix[j][i].second.value
                /* initialMatrix[j][i] = Pair(InternalUtility(sum),CrossUtility(0))*/
            }
            initialMatrix[ initialMatrix.size - 1 ][i] = Pair(InternalUtility(sum),CrossUtility(i))
            sum = 0
        }

    }

    private fun reorderInitialMatrix() {

        val list = initialMatrix[dataModelList.size + 1].copyOf()
        val tmpList = initialMatrix[dataModelList.size + 1]
        val orderedList = list.sortedBy { it.first.value }

        for (i in list.indices){
            val elementIndexInTmpList = tmpList.indexOf(list[i])
            val elementIndexInOrderedList = orderedList.indexOf(list[i])

            if (elementIndexInTmpList != elementIndexInOrderedList)
                swapMatrixColumn(elementIndexInTmpList, elementIndexInOrderedList)
        }

    }

    private fun swapMatrixColumn(ci: Int, cj: Int){
        for (i in 0 ..  dataModelList.size + 1){
            val tmpPair = initialMatrix[i][ci]
            initialMatrix[i][ci] = initialMatrix[i][cj]
            initialMatrix[i][cj] = tmpPair

        }
    }

    private fun createMainMatrix(){

        fun calculateSumOfGivenColumnIndexes(list: List<Int>): Int {
            var sum = 0
            first@ for (i in 1 until initialMatrix.size -1){
                var tmp = 0
                second@ for (j in list.indices){
                    if (initialMatrix[i][list[j]].second.value != 0)
                        tmp += initialMatrix[i][list[j]].second.value
                    else{
                        tmp = 0
                        break@second
                    }
                }
                sum += tmp
            }
            return sum
        }

        fun getRow(rowIndex: Int){

            for (i in rowIndex + 1 until mainMatrix.size){
                val tmpList = mutableListOf<Int>()
                val listOfList: List<List<Int>> = Util.getIndexList(rowIndex,i)
                for (element in listOfList)
                    tmpList.add( calculateSumOfGivenColumnIndexes(element) )
                mainMatrix[rowIndex][i] = tmpList.maxOrNull() ?: 0
            }

        }

        // create hypotenuse of matrix
        for (i in mainMatrix.indices)
            mainMatrix[i][i] = initialMatrix[initialMatrix.size-1][i].first.value

        // create rest of matrix
        for (i in 0 until mainMatrix.size -1)
            getRow(i)

    }

    private fun calculateTopK() {

        val finalList = mutableListOf<Int>()

        for (i in mainMatrix.indices)
            for (j in i until mainMatrix.size)
                finalList.add(mainMatrix[i][j])

        finalList.sort()

        println( finalList.takeLast(k) )
    }

}