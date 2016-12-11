import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonRepository {
    private ArrayList<Device> data = new ArrayList<>();
    private String errors = "";
    public JsonRepository(String pathToJsonFile, String logPath){
        String json = "";
        try {
            json = FileUtils.readFileToString(new File(pathToJsonFile), "UTF8");
        }
        catch(java.io.IOException ex){
            System.out.println("Could not read file with name: " + pathToJsonFile);
        }

        Gson gson = new Gson();
        Type castType = new TypeToken<ArrayList<Device>>(){}.getType();
        ArrayList<Device> results = gson.fromJson(json, castType);

        Map<String, Long> countedDevices = results.stream()
                .collect(Collectors.groupingBy(d->d.brand + " " + d.model, Collectors.counting()));

        countedDevices.forEach((k,v) -> {if(v>1) addError("Duplicate item : " + k + "\n");});

        results.forEach(d->addError(d.Validate()));

        if (!errors.isEmpty()) logErrors(logPath);

        data = results.stream().filter(d->d.Validate().equals("") && countedDevices.get(d.brand + " " + d.model) == 1)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Device> getAll(){
        return data;
    }

    public ArrayList<Device> getByName(String name){
        return data.stream().filter(d->(d.brand + " " + d.model).equals(name)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Device> getByBrand(String brand){
        return data.stream().filter(d->d.brand.equals(brand)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Device> getByModel(String model){
        return data.stream().filter(d->d.model.equals(model)).collect(Collectors.toCollection(ArrayList::new));
    }

    private void addError(String error){
        errors += error;
    }

    private void logErrors(String path)
    {
        try {
            FileUtils.writeStringToFile(new File(path), errors, "UTF8");
        }
        catch (java.io.IOException ex){
            System.out.println("Could not write to file with name: " + path);
        }
    }
}
