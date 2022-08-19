package Algorithm

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

class Algorithm(private val filePath: String) {

    private val k = 8
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

        println()
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

         fun calculateMainMatrixCell(ci: Int, cj: Int): Int{
            var sum = 0

            for (i in 1 until initialMatrix.size -1){
                if (initialMatrix[i][ci].second.value != 0 && initialMatrix[i][cj].second.value != 0)
                    sum += initialMatrix[i][ci].second.value + initialMatrix[i][cj].second.value
            }

            return sum
        }

        // create hypotenuse of matrix
        for (i in mainMatrix.indices)
            for (j in mainMatrix.indices)
                if ( i == j )
                    mainMatrix[i][j] = initialMatrix[initialMatrix.size-1][i].first.value

        // create rest of matrix
        for (i in mainMatrix.indices)
            for (j in i+1 until mainMatrix.size)
                    mainMatrix[i][j] = calculateMainMatrixCell(i,j)
    }

    private fun calculateTopK() {
        val maxAvailableK = (mainMatrix.size * mainMatrix.size) - (( (mainMatrix.size * mainMatrix.size) - mainMatrix.size ) / 2)
        if (k > maxAvailableK){
            println("Specify lower K !!!")
            return
        }
        else{

            val topKArray = IntArray(k){-1}
            var topKArrayIndex = 0
            var min_j = 0
            var max_i = mainMatrix.size - 1

            do {

                var j = min_j
                for (i in 0 .. max_i){

                    // fill topK array if it has not completed yet
                    if (topKArrayIndex < k){

                        topKArray[topKArrayIndex] = mainMatrix[i][j]
                        topKArrayIndex++

                    }
                    else {

                        // compare main matrix item with the lowest item in topK array
                        if ( mainMatrix[i][j] > topKArray[0] ){

                            topKArray[0] = mainMatrix[i][j]

                            // sort topk array
                            topKArray.sort()
                        }


                    }

                    j++
                }

                min_j++
                max_i--

            }while (min_j <= mainMatrix.size - 1  && max_i >= 0)

            print("Top $k is:  ")
            topKArray.forEach { print("$it ") }
        }
    }

}