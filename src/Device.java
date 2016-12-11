import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

class Device {
    String brand = "";
    String model = "";
    String formFactor = "";
    ArrayList<Attribute> attributes = new ArrayList<>();

    ArrayList<String> Validate() {
        ArrayList<String> errors = new ArrayList<>();
        if (brand.isEmpty() || brand.length() > 50) errors.add("Invalid brand : " + (brand.isEmpty() ? "NULL" : brand));

        if (model.isEmpty() || model.length() > 50) errors.add("Invalid model : " + (model.isEmpty() ? "NULL" : model));

        String[] allowedFormFactros = {"CANDYBAR", "SMARTPHONE", "PHABLET", "CLAMSHELL"};
        if (!Arrays.asList(allowedFormFactros).contains(formFactor))
            errors.add("Invalid form factor : " + (formFactor.isEmpty() ? "NULL" : formFactor));

        if (!attributes.isEmpty()) {
            for (Attribute a : attributes.stream()
                    .filter(a -> a.name.isEmpty() || a.name.length() > 20)
                    .collect(Collectors.toList())) {
                errors.add("Invalid attribute name : " + (a.name.isEmpty() ? "NULL" : a.name));
            }
            for (Attribute a : attributes.stream()
                    .filter(a -> a.value.isEmpty() || a.value.length() > 100)
                    .collect(Collectors.toList())) {
                errors.add("Invalid attribute value : " + (a.value.isEmpty() ? "NULL" : a.value));
            }
        }

        return errors;
    }
}

