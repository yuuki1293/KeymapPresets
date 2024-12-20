package yuuki1293.keymappresets.common;

public enum SortOrder {
    ASC,
    DESC;

    public String getString() {
        return switch (this) {
            case ASC -> "⬆";
            case DESC -> "⬇";
        };
    }
}
