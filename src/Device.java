import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

class Device {
    String brand = "";
    String model = "";
    String formFactor = "";
    ArrayList<Attribute> attributes = new ArrayList<>();

    String Validate() {
        String errors = "";
        if (brand.isEmpty() || brand.length() > 50) errors += "Invalid brand : " + (brand.isEmpty() ? "NULL" : brand) + "\n";

        if (model.isEmpty() || model.length() > 50) errors += "Invalid model : " + (model.isEmpty() ? "NULL" : model) + "\n";

        String[] allowedFormFactros = {"CANDYBAR", "SMARTPHONE", "PHABLET", "CLAMSHELL"};
        if (!Arrays.asList(allowedFormFactros).contains(formFactor)) errors += "Invalid form factor : " + (formFactor.isEmpty() ? "NULL" : formFactor) + "\n";

        if (!attributes.isEmpty()) {
            for (Attribute a : attributes.stream()
                    .filter(a -> a.name.isEmpty() || a.name.length() > 20)
                    .collect(Collectors.toList())) {
                errors += "Invalid attribute name : " + (a.name.isEmpty() ? "NULL" : a.name)  + "\n";
            }
            for (Attribute a : attributes.stream()
                    .filter(a -> a.value.isEmpty() || a.value.length() > 100)
                    .collect(Collectors.toList())) {
                errors += "Invalid attribute value : " + (a.value.isEmpty() ? "NULL" : a.value) + "\n";
            }
        }

        return errors;
    }
}

