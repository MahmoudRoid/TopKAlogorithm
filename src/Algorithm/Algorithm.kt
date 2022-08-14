package Algorithm

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.Arrays

class Algorithm(private val filePath: String) {

    private val dataModelList = mutableListOf<DataModel>()
    private lateinit var initialMatrix: Array<IntArray>
    private var initialMatrixRowCount = 0

    init {
        readFileUsingBufferedReader(filePath)
        makeInitialMatrix()
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
                                if (utilityList.size > initialMatrixRowCount)
                                    initialMatrixRowCount = utilityList.size
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

        if (dataModelList.isEmpty()){
            println("data model list is empty")
            return
        }

        initialMatrix = Array(initialMatrixRowCount) { IntArray(initialMatrixRowCount) }
    }
}