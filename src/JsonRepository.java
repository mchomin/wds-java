import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

class JsonRepository {
    private ArrayList<Device> data = new ArrayList<>();

    JsonRepository(String pathToJsonFile, FileLogger log) {
        String json = "";
        try {
            json = FileUtils.readFileToString(new File(pathToJsonFile), "UTF8");
        } catch (java.io.IOException ex) {
            System.out.println("Could not read file with name: " + pathToJsonFile);
        }

        Gson gson = new Gson();
        Type castType = new TypeToken<ArrayList<Device>>() {
        }.getType();
        ArrayList<Device> results = gson.fromJson(json, castType);

        Map<String, Long> countedDevices = results.stream()
                .collect(Collectors.groupingBy(d -> d.brand + " " + d.model, Collectors.counting()));

        countedDevices.forEach((k, v) -> {
            if (v > 1) log.addError("Duplicate item : " + k + "\n");
        });

        results.forEach(d -> log.addError(d.Validate()));

        log.flushToFile();

        data = results.stream().filter(d -> d.Validate().isEmpty() && countedDevices.get(d.brand + " " + d.model) == 1)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<Device> getAll() {
        return data;
    }

    ArrayList<Device> getByName(String name) {
        return data.stream().filter(d -> (d.brand + " " + d.model).equals(name)).collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<Device> getByBrand(String brand) {
        return data.stream().filter(d -> d.brand.equals(brand)).collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<Device> getByModel(String model) {
        return data.stream().filter(d -> d.model.equals(model)).collect(Collectors.toCollection(ArrayList::new));
    }
}
