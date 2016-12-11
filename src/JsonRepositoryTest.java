import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


class JsonRepositoryTest {
    private static JsonRepository validRepo;
    private static JsonRepository invalidRepo;
    private static String logPath;
    private static String errorLog;

    @BeforeAll
    static void prepareFaultyFile() {
        Device empty = new Device();
        empty.attributes.add(new Attribute());

        Device brandTooLong = new Device();
        brandTooLong.brand = StringUtils.repeat("a", 51);
        brandTooLong.model = "ok";
        brandTooLong.formFactor = "PHABLET";

        Device modelTooLong = new Device();
        modelTooLong.brand = "ok";
        modelTooLong.model = StringUtils.repeat("a", 51);
        modelTooLong.formFactor = "CLAMSHELL";

        Device invalidFormFactor = new Device();
        invalidFormFactor.brand = "ok";
        invalidFormFactor.model = "ok";
        invalidFormFactor.formFactor = "wrong";

        Device duplicate = new Device();
        duplicate.model = "duplicate";
        duplicate.brand = "duplicate";
        duplicate.formFactor = "SMARTPHONE";

        Device invalidAttributeName = new Device();
        invalidAttributeName.brand = "brand";
        invalidAttributeName.model = "model";
        invalidAttributeName.formFactor = "CANDYBAR";
        Attribute attr = new Attribute();
        attr.name = StringUtils.repeat("a", 21);
        attr.value = "ok";
        invalidAttributeName.attributes.add(attr);

        Device invalidAttributeValue = new Device();
        invalidAttributeValue.brand = "brand1";
        invalidAttributeValue.model = "model1";
        invalidAttributeValue.formFactor = "CANDYBAR";
        Attribute attr2 = new Attribute();
        attr2.name = "ok";
        attr2.value = StringUtils.repeat("a", 101);
        invalidAttributeValue.attributes.add(attr2);

        ArrayList<Device> devices = new ArrayList<>();
        devices.add(empty);
        devices.add(brandTooLong);
        devices.add(modelTooLong);
        devices.add(invalidFormFactor);
        devices.add(duplicate);
        devices.add(duplicate);
        devices.add(invalidAttributeName);
        devices.add(invalidAttributeValue);

        Gson gson = new Gson();
        String json = gson.toJson(devices);

        try {
            FileUtils.writeStringToFile(new File("Data/faulty.json"), json, "UTF8");
        } catch (java.io.IOException ex) {
            System.out.println("Could write to file with name: Data/faulty.json");
        }

        logPath = "Data/Log_" + UUID.randomUUID().toString() + ".txt";
        FileLogger logger = new FileLogger(logPath);
        invalidRepo = new JsonRepository("Data/faulty.json", logger);
        validRepo = new JsonRepository("Data/devices.json", logger);
        try {
            errorLog = FileUtils.readFileToString(new File(logPath), "UTF8");
        } catch (java.io.IOException ex) {
            System.out.println("Could not read file with name: " + logPath);
        }
    }

    @Test
    void faultyDevicesDoNotLoad() {
        Assertions.assertTrue(invalidRepo.getAll().isEmpty());
    }

    @Test
    void validDevicesDoLoad() {
        Assertions.assertTrue(validRepo.getAll().size() == 3);
    }

    @Test
    void getByNameReturnsOneCorrectDevice() {
        ArrayList<Device> results = validRepo.getByName("Mockia 5800");
        Assertions.assertTrue(results.size() == 1);
        Assertions.assertTrue(results.get(0).brand.equals("Mockia") && results.get(0).model.equals("5800"));
    }

    @Test
    void getByBrandReturnsOnlyDevicesWithThatBrand() {
        ArrayList<Device> results = validRepo.getByBrand("Phony");
        Assertions.assertTrue(results.stream().allMatch(d -> d.brand.equals("Phony")));
    }

    @Test
    void getByModelReturnsOnlyDevicesWithThatModel() {
        ArrayList<Device> results = validRepo.getByModel("Universe A1");
        Assertions.assertTrue(results.stream().allMatch(d -> d.model.equals("Universe A1")));
    }

    @Test
    void logIsNotEmpty() {
        Assertions.assertTrue(!errorLog.isEmpty());
    }

    @Test
    void invalidBrandIsLogged() {
        Assertions.assertTrue(errorLog.contains("Invalid brand : " + StringUtils.repeat("a", 51)));
        Assertions.assertTrue(errorLog.contains("Invalid brand : NULL"));
    }

    @Test
    void invalidModelIsLogged() {
        Assertions.assertTrue(errorLog.contains("Invalid model : " + StringUtils.repeat("a", 51)));
        Assertions.assertTrue(errorLog.contains("Invalid model : NULL"));
    }

    @Test
    void invalidFormFactorIsLogged() {
        Assertions.assertTrue(errorLog.contains("Invalid form factor : wrong"));
        Assertions.assertTrue(errorLog.contains("Invalid form factor : NULL"));
    }

    @Test
    void invalidAttributeNameIsLogged() {
        Assertions.assertTrue(errorLog.contains("Invalid attribute name : " + StringUtils.repeat("a", 21)));
        Assertions.assertTrue(errorLog.contains("Invalid attribute name : NULL"));
    }

    @Test
    void invalidAttributeValueIsLogged() {
        Assertions.assertTrue(errorLog.contains("Invalid attribute value : " + StringUtils.repeat("a", 101)));
        Assertions.assertTrue(errorLog.contains("Invalid attribute value : NULL"));
    }

    @Test
    void duplicateDevicesAreLogged() {
        Assertions.assertTrue(errorLog.contains("Duplicate item : duplicate duplicate"));
    }

    @AfterAll
    static void CleanUp() {
        FileUtils.deleteQuietly(new File("Data/faulty.json"));
        FileUtils.deleteQuietly(new File(logPath));
    }

}