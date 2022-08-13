package Algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    private List<DataModel> dataModelList;

    public Algorithm(String filePath){
        dataModelList = new ArrayList<>();
        readFileUsingBufferedReader(filePath);
    }


    private void readFileUsingBufferedReader(String filePath){

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for(String line; (line = br.readLine()) != null; ) {

                if (line.isEmpty() == true ||	line.charAt(0) == '#'
                        || line.charAt(0) == '%'
                        || line.charAt(0) == '@') {
                    continue;
                }

                DataModel dataModel = new DataModel();

                String split[] = line.split(":");
                String internalItems[] = split[0].split(" ");
                String crossItems[] = split[2].split(" ");

                dataModel.setSumUtility( Integer.parseInt( split[1] ) );
                
                // internal
                for (int i = 0; i < internalItems.length; i++)
                    dataModel.getInternalUtilityList().add(Integer.parseInt(internalItems[i]));

                // cross
                for (int i = 0; i < crossItems.length; i++)
                    dataModel.getInternalUtilityList().add(Integer.parseInt(crossItems[i]));

                dataModelList.add(dataModel);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
