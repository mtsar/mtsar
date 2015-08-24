package mtsar;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ParamsUtils {
    public final static Set<String> extract(MultivaluedMap<String, String> params, String prefix) {
        final String regexp = "^" + Pattern.quote(prefix) + "(\\[\\d+\\]|)$";
        final Set<String> values = new HashSet<>();
        for (final Map.Entry<String, List<String>> entries : params.entrySet()) {
            if (!entries.getKey().matches(regexp)) continue;
            if (entries.getValue() == null || entries.getValue().isEmpty()) continue;
            for (final String answer : entries.getValue()) values.add(answer);
        }
        return values;
    }

    public final static void validate(Validator validator, Object... objects) throws ConstraintViolationException {
        final Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (Object object : objects) violations.addAll(validator.validate(object));
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
    }
}
