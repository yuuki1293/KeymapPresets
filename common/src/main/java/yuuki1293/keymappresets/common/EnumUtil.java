package yuuki1293.keymappresets.common;

public abstract class EnumUtil {
    public static <E extends Enum<E>> E next(E value) {
        var enumClass = value.getDeclaringClass();
        var enums = enumClass.getEnumConstants();

        if (value.ordinal() + 1 < enums.length) {
            return enumClass.getEnumConstants()[value.ordinal() + 1];
        }
        return enumClass.getEnumConstants()[0];
    }
}
